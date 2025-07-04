from flask import Blueprint, request, jsonify, current_app
from farm_management.api.auth import token_required
from farm_management.models import Flock # For type hinting and to_dict
from datetime import date, datetime

flock_bp = Blueprint('flock_bp', __name__)

def _parse_date_from_string(date_str: str) -> date | None:
    """Helper to parse YYYY-MM-DD string to date object."""
    if not date_str:
        return None
    try:
        return date.fromisoformat(date_str)
    except ValueError:
        return None # Indicates parsing error

@flock_bp.route('/farms/<string:farm_id>/flocks', methods=['POST'])
@token_required
def create_flock(farm_id: str):
    """
    Creates a new flock for a given farm.
    Endpoint: POST /api/farms/{farm_id}/flocks
    Authentication: Required (Bearer Token)
    Path Parameters:
        farm_id (string): The ID of the farm to which this flock will belong.
    Request Body (JSON):
        {
            "breed": "string (required)",
            "acquisition_date": "string (YYYY-MM-DD, required)",
            "initial_count": "integer (required, >0)",
            "source_supplier": "string (optional)",
            "parent_flock_id_male": "string (optional, ID of an existing flock)",
            "parent_flock_id_female": "string (optional, ID of an existing flock)",
            "notes": "string (optional)"
        }
    Response (Success: 201 Created):
        {
            "flock_id": "string",
            "farm_id": "string",
            "breed": "string",
            ... (other flock properties)
        }
    Response (Error):
        400 Bad Request (e.g., missing fields, invalid data format, initial_count <=0)
        401 Unauthorized
        404 Not Found (if farm_id does not exist)
        500 Internal Server Error
    """
    farm = current_app.farm_repo.get_farm_by_id(farm_id)
    if not farm:
        return jsonify({"message": "Farm not found to add flock to", "error": "Not Found"}), 404

    data = request.get_json()
    if not data:
        return jsonify({"message": "Request body must be JSON", "error": "Bad Request"}), 400

    required_fields = ['breed', 'acquisition_date', 'initial_count']
    missing_fields = [field for field in required_fields if field not in data]
    if missing_fields:
        return jsonify({"message": f"Missing required fields: {', '.join(missing_fields)}", "error": "Bad Request"}), 400

    acquisition_date_obj = _parse_date_from_string(data['acquisition_date'])
    if not acquisition_date_obj:
        return jsonify({"message": "Invalid acquisition_date format. Use YYYY-MM-DD.", "error": "Bad Request"}), 400

    try:
        initial_count = int(data['initial_count'])
        if initial_count <= 0:
             return jsonify({"message": "initial_count must be a positive integer.", "error": "Bad Request"}), 400
    except ValueError:
        return jsonify({"message": "initial_count must be an integer.", "error": "Bad Request"}), 400


    try:
        new_flock = current_app.flock_repo.add_flock(
            farm_id=farm_id,
            breed=data['breed'],
            acquisition_date=acquisition_date_obj,
            initial_count=initial_count,
            source_supplier=data.get('source_supplier', ""),
            parent_flock_id_male=data.get('parent_flock_id_male') or None,
            parent_flock_id_female=data.get('parent_flock_id_female') or None,
            notes=data.get('notes', "")
        )
        # Link flock to farm object in FarmRepository (as done in CLI)
        # Note: FarmRepository._save_farms() saves flock_ids from farm.flocks
        current_app.farm_repo.add_flock_to_farm(farm_id, new_flock)

        return jsonify(new_flock.to_dict()), 201
    except ValueError as e:
        return jsonify({"message": str(e), "error": "Bad Request"}), 400
    except Exception as e:
        current_app.logger.error(f"Error creating flock for farm {farm_id}: {e}")
        return jsonify({"message": "An unexpected error occurred", "error": "Internal Server Error"}), 500


@flock_bp.route('/farms/<string:farm_id>/flocks', methods=['GET'])
@token_required
def get_flocks_for_farm(farm_id: str):
    """
    Lists all flocks for a specific farm.
    Can also search within these flocks using query param 'q'.
    Endpoint: GET /api/farms/{farm_id}/flocks
    Authentication: Required (Bearer Token)
    Path Parameters:
        farm_id (string): The ID of the farm.
    Query Parameters:
        q (string, optional): Search term for flock breed, notes, or ID.
                              If not provided, lists all flocks for the farm.
    Response (Success: 200 OK):
        [
            {
                "flock_id": "string",
                "farm_id": "string",
                ... (other flock properties)
            },
            ...
        ]
    Response (Error):
        401 Unauthorized
        404 Not Found (if farm_id does not exist)
        500 Internal Server Error
    """
    farm = current_app.farm_repo.get_farm_by_id(farm_id)
    if not farm:
        return jsonify({"message": "Farm not found", "error": "Not Found"}), 404

    search_query = request.args.get('q', None)
    if search_query is not None:
        flocks = current_app.flock_repo.search_flocks(farm_id=farm_id, search_term=search_query)
    else:
        flocks = current_app.flock_repo.get_flocks_by_farm_id(farm_id)

    return jsonify([flock.to_dict() for flock in flocks]), 200


@flock_bp.route('/flocks/<string:flock_id>', methods=['GET'])
@token_required
def get_flock_by_id(flock_id: str):
    """
    Gets a specific flock by its ID.
    Endpoint: GET /api/flocks/{flock_id}
    Authentication: Required (Bearer Token)
    Path Parameters:
        flock_id (string): The unique identifier for the flock.
    Response (Success: 200 OK):
        {
            "flock_id": "string",
            "farm_id": "string",
            ... (other flock properties)
        }
    Response (Error):
        401 Unauthorized
        404 Not Found (if flock_id does not exist)
        500 Internal Server Error
    """
    flock = current_app.flock_repo.get_flock_by_id(flock_id)
    if flock:
        return jsonify(flock.to_dict()), 200
    else:
        return jsonify({"message": "Flock not found", "error": "Not Found"}), 404


@flock_bp.route('/flocks/<string:flock_id>', methods=['PUT'])
@token_required
def update_flock(flock_id: str):
    """
    Updates an existing flock. Farm_id cannot be changed via this endpoint.
    Endpoint: PUT /api/flocks/{flock_id}
    Authentication: Required (Bearer Token)
    Path Parameters:
        flock_id (string): The ID of the flock to update.
    Request Body (JSON):
        {
            "breed": "string (optional)",
            "acquisition_date": "string (YYYY-MM-DD, optional)",
            "initial_count": "integer (optional, >0)",
            "current_count": "integer (optional, >=0)",
            "source_supplier": "string (optional)",
            "parent_flock_id_male": "string (optional, ID of an existing flock or null)",
            "parent_flock_id_female": "string (optional, ID of an existing flock or null)",
            "notes": "string (optional)"
        }
    Response (Success: 200 OK):
        {
            "flock_id": "string",
            "farm_id": "string",
            ... (updated flock properties)
        }
    Response (Error):
        400 Bad Request (e.g., invalid data format)
        401 Unauthorized
        404 Not Found (if flock_id does not exist)
        500 Internal Server Error
    """
    flock = current_app.flock_repo.get_flock_by_id(flock_id)
    if not flock:
        return jsonify({"message": "Flock not found", "error": "Not Found"}), 404

    data = request.get_json()
    if not data:
        return jsonify({"message": "Request body must be JSON", "error": "Bad Request"}), 400

    update_data = {}
    allowed_fields = [
        'breed', 'acquisition_date', 'initial_count', 'current_count',
        'source_supplier', 'parent_flock_id_male', 'parent_flock_id_female', 'notes'
    ]

    for field in allowed_fields:
        if field in data:
            if field == 'acquisition_date':
                parsed_date = _parse_date_from_string(data[field])
                if not parsed_date and data[field] is not None: # Allow null to clear? No, date is required for flock.
                     return jsonify({"message": f"Invalid {field} format. Use YYYY-MM-DD.", "error": "Bad Request"}), 400
                update_data[field] = parsed_date # Will be None if data[field] was None or empty
            elif field in ['initial_count', 'current_count']:
                try:
                    count_val = int(data[field])
                    if count_val < 0:
                        return jsonify({"message": f"{field} cannot be negative.", "error": "Bad Request"}), 400
                    update_data[field] = count_val
                except (ValueError, TypeError):
                    return jsonify({"message": f"{field} must be an integer.", "error": "Bad Request"}), 400
            else:
                update_data[field] = data[field]

    if not update_data:
        return jsonify({"message": "No update fields provided", "error": "Bad Request"}), 400

    # Ensure farm_id is not in update_data as it shouldn't be changed here
    if 'farm_id' in update_data:
        del update_data['farm_id']

    updated_flock = current_app.flock_repo.update_flock(flock_id, **update_data)
    if updated_flock:
        # If flock details that are part of the Farm's representation change (e.g. if Farm stored full flock objects),
        # the Farm object might need invalidation/update. But FarmRepository stores only flock_ids.
        # FlockRepository._save_flocks() persists the changes.
        return jsonify(updated_flock.to_dict()), 200
    else:
        current_app.logger.error(f"Flock update failed unexpectedly for flock_id: {flock_id} with data: {update_data}")
        return jsonify({"message": "Update failed", "error": "Internal Server Error"}), 500


@flock_bp.route('/flocks/<string:flock_id>', methods=['DELETE'])
@token_required
def delete_flock(flock_id: str):
    """
    Deletes a flock by its ID.
    Also removes the flock from its associated farm object.
    Endpoint: DELETE /api/flocks/{flock_id}
    Authentication: Required (Bearer Token)
    Path Parameters:
        flock_id (string): The ID of the flock to delete.
    Response (Success: 204 No Content):
        (Empty response body)
    Response (Error):
        401 Unauthorized
        404 Not Found (if flock_id does not exist)
        409 Conflict (if the flock has associated tracking records like health records)
        500 Internal Server Error
    """
    flock = current_app.flock_repo.get_flock_by_id(flock_id)
    if not flock:
        return jsonify({"message": "Flock not found", "error": "Not Found"}), 404

    farm_id = flock.farm_id # Get farm_id before deleting flock from flock_repo

    # Business Logic: Check for dependent records (health, production, etc.)
    # For simplicity, this version will allow deletion. A production system might prevent or archive.
    # Example check:
    health_records = current_app.tracking_repo.get_health_records_for_flock(flock_id)
    if health_records:
         return jsonify({
            "message": f"Cannot delete flock. It has {len(health_records)} associated health record(s). Please delete them first or implement cascading delete.",
            "error": "Conflict"
        }), 409
    # Similar checks for production, growth, feed records should be added.
    # production_records = current_app.tracking_repo.get_production_records_for_flock(flock_id) etc.


    if current_app.flock_repo.delete_flock(flock_id):
        # Remove flock from farm's list in FarmRepository
        farm = current_app.farm_repo.get_farm_by_id(farm_id)
        if farm:
            current_app.farm_repo.remove_flock_from_farm(farm_id, flock_id)
        else:
            current_app.logger.warning(f"Farm {farm_id} not found when trying to remove deleted flock {flock_id} from its list.")

        return '', 204  # No content, success
    else:
        return jsonify({"message": "Deletion failed unexpectedly", "error": "Internal Server Error"}), 500


@flock_bp.route('/flocks/<string:flock_id>/family_tree', methods=['GET'])
@token_required
def get_flock_family_tree(flock_id: str):
    """
    Gets the family tree for a given flock.
    Endpoint: GET /api/flocks/{flock_id}/family_tree
    Authentication: Required (Bearer Token)
    Path Parameters:
        flock_id (string): The ID of the flock for which to retrieve the family tree.
    Query Parameters:
        max_depth (integer, optional): The maximum depth of the tree to retrieve. Defaults to 3.
    Response (Success: 200 OK):
        {
            "id": "string (flock_id)",
            "breed": "string",
            "acquisition_date": "string (YYYY-MM-DD)",
            "male_parent": { ... (recursive structure) ... },
            "female_parent": { ... (recursive structure) ... },
            "error": "string (if a parent flock in the tree is not found)"
        }
    Response (Error):
        401 Unauthorized
        404 Not Found (if the primary flock_id does not exist)
        500 Internal Server Error
    """
    flock = current_app.flock_repo.get_flock_by_id(flock_id)
    if not flock:
        return jsonify({"message": "Flock not found", "error": "Not Found"}), 404

    try:
        max_depth_str = request.args.get('max_depth')
        max_depth = int(max_depth_str) if max_depth_str and max_depth_str.isdigit() else 3 # Default to 3
    except ValueError:
        max_depth = 3

    tree_data = current_app.flock_repo.get_flock_family_tree(flock_id, max_depth=max_depth)

    if tree_data.get("error") == "Flock not found" and tree_data.get("id") == flock_id: # Should be caught by above check
        return jsonify({"message": "Flock not found", "error": "Not Found"}), 404

    return jsonify(tree_data), 200


# Search endpoint for flocks within a farm is: GET /api/farms/<farm_id>/flocks?q=<query>
# Handled by get_flocks_for_farm route.

def register_flock_routes(app):
    """Registers the flock blueprint with the Flask app."""
    app.register_blueprint(flock_bp, url_prefix='/api')
