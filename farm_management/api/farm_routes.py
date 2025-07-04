 feature/python-rest-api-wrapper
from datetime import datetime # Standard library

from flask import Blueprint, request, jsonify, current_app # Third-party

# Local application imports
from farm_management.api.auth import token_required
# from farm_management.models import Farm # Model import not strictly needed for runtime if only using to_dict()

from flask import Blueprint, request, jsonify, current_app
from farm_management.api.auth import token_required
from farm_management.models import Farm # For type hinting, though not strictly necessary at runtime for dict conversion
from datetime import datetime
 main

farm_bp = Blueprint('farm_bp', __name__)

@farm_bp.route('/farms', methods=['POST'])
@token_required
def create_farm():
    """
    Creates a new farm.
    Endpoint: POST /api/farms
    Authentication: Required (Bearer Token)
    Request Body (JSON):
        {
            "name": "string (required)",
            "location": "string (required)",
            "owner": "string (required)",
            "capacity": "integer (required)",
            "established_date": "string (YYYY-MM-DD, optional)",
            "notes": "string (optional)"
        }
    Response (Success: 201 Created):
        {
            "farm_id": "string",
            "name": "string",
            ... (other farm properties)
        }
    Response (Error):
        400 Bad Request (e.g., missing fields, invalid data format)
        401 Unauthorized
        500 Internal Server Error
    """
    data = request.get_json()
    if not data:
        return jsonify({"message": "Request body must be JSON", "error": "Bad Request"}), 400

    required_fields = ['name', 'location', 'owner', 'capacity']
    missing_fields = [field for field in required_fields if field not in data]
    if missing_fields:
        return jsonify({"message": f"Missing required fields: {', '.join(missing_fields)}", "error": "Bad Request"}), 400

    try:
        # Handle optional established_date
        established_date_str = data.get('established_date')
        established_date_obj = None
        if established_date_str:
            try:
                established_date_obj = datetime.strptime(established_date_str, '%Y-%m-%d')
            except ValueError:
                return jsonify({"message": "Invalid established_date format. Use YYYY-MM-DD.", "error": "Bad Request"}), 400

        farm = current_app.farm_repo.add_farm(
            name=data['name'],
            location=data['location'],
            owner=data['owner'],
            capacity=int(data['capacity']), # Ensure capacity is int
            established_date=established_date_obj,
            notes=data.get('notes', "")
        )
        return jsonify(farm.to_dict()), 201
    except ValueError as e: # Catches issues like non-integer capacity if not handled before
        return jsonify({"message": str(e), "error": "Bad Request"}), 400
    except Exception as e:
        current_app.logger.error(f"Error creating farm: {e}")
        return jsonify({"message": "An unexpected error occurred", "error": "Internal Server Error"}), 500


@farm_bp.route('/farms', methods=['GET'])
@token_required
def get_farms():
    """
    Lists all farms or searches farms based on a query parameter.
    Endpoint: GET /api/farms
    Authentication: Required (Bearer Token)
    Query Parameters:
        q (string, optional): Search term for farm name, location, or owner.
                              If not provided, lists all farms.
    Response (Success: 200 OK):
        [
            {
                "farm_id": "string",
                "name": "string",
                ... (other farm properties)
            },
            ...
        ]
    Response (Error):
        401 Unauthorized
        500 Internal Server Error
    """
    search_query = request.args.get('q', None)
    if search_query is not None: # Check for None explicitly, empty string means search for "" (all)
        farms = current_app.farm_repo.search_farms(search_query)
    else:
        farms = current_app.farm_repo.get_all_farms()

    return jsonify([farm.to_dict() for farm in farms]), 200

@farm_bp.route('/farms/<string:farm_id>', methods=['GET'])
@token_required
def get_farm_by_id(farm_id: str):
    """
    Gets a specific farm by its ID.
    Endpoint: GET /api/farms/{farm_id}
    Authentication: Required (Bearer Token)
    Path Parameters:
        farm_id (string): The unique identifier for the farm.
    Response (Success: 200 OK):
        {
            "farm_id": "string",
            "name": "string",
            ... (other farm properties)
        }
    Response (Error):
        401 Unauthorized
        404 Not Found (if farm_id does not exist)
        500 Internal Server Error
    """
    farm = current_app.farm_repo.get_farm_by_id(farm_id)
    if farm:
        return jsonify(farm.to_dict()), 200
    else:
        return jsonify({"message": "Farm not found", "error": "Not Found"}), 404

@farm_bp.route('/farms/<string:farm_id>', methods=['PUT'])
@token_required
def update_farm(farm_id: str):
    """
    Updates an existing farm.
    Endpoint: PUT /api/farms/{farm_id}
    Authentication: Required (Bearer Token)
    Path Parameters:
        farm_id (string): The unique identifier for the farm to update.
    Request Body (JSON):
        {
            "name": "string (optional)",
            "location": "string (optional)",
            "owner": "string (optional)",
            "capacity": "integer (optional)",
            "established_date": "string (YYYY-MM-DD, optional)",
            "notes": "string (optional)"
        }
    Response (Success: 200 OK):
        {
            "farm_id": "string",
            "name": "string",
            ... (updated farm properties)
        }
    Response (Error):
        400 Bad Request (e.g., invalid data format)
        401 Unauthorized
        404 Not Found (if farm_id does not exist)
        500 Internal Server Error
    """
    farm = current_app.farm_repo.get_farm_by_id(farm_id)
    if not farm:
        return jsonify({"message": "Farm not found", "error": "Not Found"}), 404

    data = request.get_json()
    if not data:
        return jsonify({"message": "Request body must be JSON", "error": "Bad Request"}), 400

    update_data = {}
    allowed_fields = ['name', 'location', 'owner', 'capacity', 'notes', 'established_date']
    for field in allowed_fields:
        if field in data:
            if field == 'established_date':
                try:
                    update_data[field] = datetime.strptime(data[field], '%Y-%m-%d')
                except (ValueError, TypeError): #TypeError if data[field] is None
                    # If date is invalid or None, we might choose to ignore or error
                    # For PUT, usually means "set this value". If it's optional, client can omit.
                    # If it's an empty string, it's an invalid date.
                    if data[field] is not None and data[field] != "": # Allow unsetting by sending null? No, API should be clear.
                        return jsonify({"message": "Invalid established_date format. Use YYYY-MM-DD.", "error": "Bad Request"}), 400
                    # If None or empty string, and field is optional, we might allow it to clear the date.
                    # Farm model sets established_date to now() if None on creation, but update might allow None.
                    # For now, let's assume if provided, it must be valid. Or handle None specifically.
                    # The repository's update_farm handles None by not changing if key not in kwargs.
                    # Let's pass it as None if it's explicitly set to null by client.
                    if data[field] is None:
                         update_data[field] = None # Allow explicitly setting to None if model/repo supports
                    elif data[field] == "": # Treat empty string as "don't change" or error; error is safer.
                         return jsonify({"message": "established_date cannot be an empty string. Omit field or use YYYY-MM-DD.", "error": "Bad Request"}), 400

            elif field == 'capacity':
                try:
                    update_data[field] = int(data[field])
                except ValueError:
                    return jsonify({"message": "Capacity must be an integer.", "error": "Bad Request"}), 400
            else:
                update_data[field] = data[field]

    if not update_data:
        return jsonify({"message": "No update fields provided", "error": "Bad Request"}), 400

    updated_farm = current_app.farm_repo.update_farm(farm_id, **update_data)
    if updated_farm:
        return jsonify(updated_farm.to_dict()), 200
    else:
        # This case might not be reached if farm_id check at start is robust
        # and update_farm itself doesn't return None for other reasons.
        current_app.logger.error(f"Farm update failed unexpectedly for farm_id: {farm_id} with data: {update_data}")
        return jsonify({"message": "Update failed", "error": "Internal Server Error"}), 500


@farm_bp.route('/farms/<string:farm_id>', methods=['DELETE'])
@token_required
def delete_farm(farm_id: str):
    """
    Deletes a farm by its ID.
    Endpoint: DELETE /api/farms/{farm_id}
    Authentication: Required (Bearer Token)
    Path Parameters:
        farm_id (string): The unique identifier for the farm to delete.
    Response (Success: 204 No Content):
        (Empty response body)
    Response (Error):
        401 Unauthorized
        404 Not Found (if farm_id does not exist)
        409 Conflict (if the farm has associated flocks)
        500 Internal Server Error
    """
    farm = current_app.farm_repo.get_farm_by_id(farm_id)
    if not farm:
        return jsonify({"message": "Farm not found", "error": "Not Found"}), 404

    # Business logic: What happens to flocks on this farm?
    # Current FarmRepository.delete_farm does not handle this.
    # For now, we assume flocks might be orphaned or need to be deleted separately.
    # A more robust solution would involve cascading deletes or disallowing deletion if flocks exist.
    # AGENTS.md: "Repositories should handle data validation before persistence." - this might include such checks.
    # The current Farm model has farm.flocks (list of Flock objects) and FarmRepository._save_farms stores flock_ids.
    # Deleting a farm should probably also delete its associated flock_ids from the farm object,
    # and potentially delete the actual flock objects from FlockRepository (or prevent farm deletion if flocks exist).
    # Let's assume for now the Android app will handle this (e.g., delete flocks first).
    # For the API, we should ensure consistency. If farm is deleted, its flock_ids list should be empty.
    # The FarmRepository.delete_farm() handles removing the farm.
    # The FarmRepository._load_farms() does not load flocks, so this is more about data integrity.

    # Check if the farm has any flocks associated with it via FlockRepository
    flocks_on_farm = current_app.flock_repo.get_flocks_by_farm_id(farm_id)
    if flocks_on_farm:
        return jsonify({
            "message": f"Cannot delete farm. It has {len(flocks_on_farm)} associated flock(s). Please delete them first.",
            "error": "Conflict"
        }), 409 # 409 Conflict is appropriate here

    if current_app.farm_repo.delete_farm(farm_id):
        return '', 204  # No content, success
    else:
        # This case should ideally not be reached if the initial check for farm existence is done.
        return jsonify({"message": "Deletion failed unexpectedly", "error": "Internal Server Error"}), 500

# The search endpoint is handled by GET /farms with a query parameter 'q'
# @farm_bp.route('/farms/search', methods=['GET'])
# @token_required
# def search_farms_endpoint():
#     query = request.args.get('q')
#     if query is None: # If q is not provided, it's a bad request for a search-specific endpoint
#         return jsonify({"message": "Search query parameter 'q' is required.", "error": "Bad Request"}), 400
#     farms = current_app.farm_repo.search_farms(query)
#     return jsonify([farm.to_dict() for farm in farms]), 200

def register_farm_routes(app):
    """Registers the farm blueprint with the Flask app."""
    app.register_blueprint(farm_bp, url_prefix='/api')
