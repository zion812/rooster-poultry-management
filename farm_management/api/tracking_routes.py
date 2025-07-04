 feature/python-rest-api-wrapper
from datetime import datetime, date # Standard library

from flask import Blueprint, request, jsonify, current_app # Third-party

# Local application imports
from farm_management.api.auth import token_required
from farm_management.models.health_record import RecordType, DiseaseSymptoms, HealthRecord, DiseaseIncidentRecord, VaccinationRecord, MortalityRecord
from farm_management.models.production_record import ProductionRecord, FeedConsumptionRecord # ProductionRecord used by name
from farm_management.models.growth_record import GrowthRecord # GrowthRecord used by name

from flask import Blueprint, request, jsonify, current_app
from farm_management.api.auth import token_required
from farm_management.models.health_record import RecordType, DiseaseSymptoms, HealthRecord, DiseaseIncidentRecord, VaccinationRecord, MortalityRecord
from farm_management.models.production_record import ProductionRecord, FeedConsumptionRecord
from farm_management.models.growth_record import GrowthRecord
from datetime import datetime, date
 main

tracking_bp = Blueprint('tracking_bp', __name__)

def _parse_datetime(dt_str: str) -> datetime | None:
    if not dt_str: return None
    try:
        return datetime.fromisoformat(dt_str)
    except ValueError:
        try: # Attempt to parse common format if isoformat fails
            return datetime.strptime(dt_str, '%Y-%m-%d %H:%M:%S')
        except ValueError:
            try: # Attempt to parse common format if isoformat fails
                return datetime.strptime(dt_str, '%Y-%m-%d %H:%M')
            except ValueError:
                return None

def _parse_date(d_str: str) -> date | None:
    if not d_str: return None
    try:
        return date.fromisoformat(d_str)
    except ValueError:
        return None

# --- Health Record Routes ---

@tracking_bp.route('/flocks/<string:flock_id>/health', methods=['POST'])
@token_required
def add_health_record_for_flock(flock_id: str):
    """
    Adds a new health record for a specific flock.
    Endpoint: POST /api/flocks/{flock_id}/health
    Authentication: Required (Bearer Token)
    Path Parameters:
        flock_id (string): The ID of the flock.
    Request Body (JSON):
        Common fields:
        {
            "record_type": "string (required, one of: Disease Incident, Vaccination, Mortality, General Checkup)",
            "record_date": "string (ISO format or YYYY-MM-DD HH:MM, required)",
            "details": "string (required)",
            "veterinarian": "string (optional)",
            "cost": "float (optional)"
        }
        Type-specific fields:
        - If record_type == "Disease Incident":
            "disease_name": "string (required)",
            "symptoms": ["string" (array of symptom values from DiseaseSymptom enum), required],
            "treatment_administered": "string (optional)",
            "affected_count": "integer (optional)"
        - If record_type == "Vaccination":
            "vaccine_name": "string (required)",
            "administered_by": "string (required)",
            "dosage": "string (optional)",
            "vaccinated_count": "integer (optional)"
        - If record_type == "Mortality":
            "cause_of_death": "string (required)",
            "number_of_deaths": "integer (required, >0)",
            "post_mortem_findings": "string (optional)"
    Response (Success: 201 Created):
        { ... (the created health record object) ... }
    Response (Error):
        400 Bad Request (e.g., missing fields, invalid data, invalid record_type)
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

    record_type_str = data.get('record_type')
    record_date_str = data.get('record_date')
    details = data.get('details')

    if not all([record_type_str, record_date_str, details]):
        return jsonify({"message": "Missing required fields: record_type, record_date, details", "error": "Bad Request"}), 400

    record_date_obj = _parse_datetime(record_date_str)
    if not record_date_obj:
        return jsonify({"message": "Invalid record_date format. Use ISO format (YYYY-MM-DDTHH:MM:SS) or YYYY-MM-DD HH:MM.", "error": "Bad Request"}), 400

    try:
        record_type_enum = RecordType(record_type_str) # Validates record_type
    except ValueError:
        valid_types = [rt.value for rt in RecordType]
        return jsonify({"message": f"Invalid record_type. Must be one of: {', '.join(valid_types)}", "error": "Bad Request"}), 400

    common_args = {
        "flock_id": flock_id,
        "record_date": record_date_obj,
        "details": details,
        "veterinarian": data.get("veterinarian", ""),
        "cost": float(data.get("cost", 0.0))
    }

    try:
        record = None
        if record_type_enum == RecordType.DISEASE_INCIDENT:
            symptoms_list = data.get('symptoms', [])
            symptoms_enum = []
            for s_val in symptoms_list:
                try:
                    symptoms_enum.append(DiseaseSymptoms(s_val))
                except ValueError:
                    return jsonify({"message": f"Invalid symptom value: {s_val}", "error": "Bad Request"}), 400

            record = current_app.tracking_repo.add_disease_incident_record(
                **common_args,
                disease_name=data.get('disease_name', 'Unknown'),
                symptoms=symptoms_enum,
                treatment_administered=data.get('treatment_administered', ""),
                affected_count=int(data.get('affected_count', 0))
            )
        elif record_type_enum == RecordType.VACCINATION:
            record = current_app.tracking_repo.add_vaccination_record(
                **common_args,
                vaccine_name=data.get('vaccine_name', 'Unknown'),
                administered_by=data.get('administered_by', 'Unknown'),
                dosage=data.get('dosage', ""),
                vaccinated_count=int(data.get('vaccinated_count', 0))
            )
        elif record_type_enum == RecordType.MORTALITY:
            number_of_deaths = int(data.get('number_of_deaths', 0))
            if number_of_deaths <= 0:
                return jsonify({"message": "number_of_deaths must be positive for a mortality record.", "error": "Bad Request"}), 400

            record = current_app.tracking_repo.add_mortality_record(
                **common_args,
                cause_of_death=data.get('cause_of_death', 'Unknown'),
                number_of_deaths=number_of_deaths,
                post_mortem_findings=data.get('post_mortem_findings', "")
            )
            # Update flock count
            new_count = flock.current_count - number_of_deaths
            current_app.flock_repo.update_flock(flock_id, current_count=max(0, new_count)) # Ensure count doesn't go below 0

        elif record_type_enum == RecordType.GENERAL_CHECKUP:
             # Create a base HealthRecord instance for GENERAL_CHECKUP
            base_record = HealthRecord(
                record_id="", # Repo will generate
                **common_args,
                record_type=record_type_enum
            )
            record = current_app.tracking_repo.add_health_record(base_record)
        else:
            return jsonify({"message": "Unsupported health record type for direct creation via this endpoint.", "error": "Bad Request"}), 400

        return jsonify(record.to_dict()), 201

    except ValueError as e:
        return jsonify({"message": str(e), "error": "Bad Request"}), 400
    except Exception as e:
        current_app.logger.error(f"Error adding health record for flock {flock_id}: {e}")
        return jsonify({"message": "An unexpected error occurred", "error": "Internal Server Error"}), 500


@tracking_bp.route('/flocks/<string:flock_id>/health', methods=['GET'])
@token_required
def get_health_records_for_flock(flock_id: str):
    """
    Lists health records for a specific flock.
    Endpoint: GET /api/flocks/{flock_id}/health
    Authentication: Required (Bearer Token)
    Path Parameters:
        flock_id (string): The ID of the flock.
    Query Parameters:
        record_type (string, optional): Filter by record type (e.g., "Disease Incident", "Vaccination").
                                        Must match a value from the RecordType enum.
    Response (Success: 200 OK):
        [
            { ... (health record object) ... },
            ...
        ]
    Response (Error):
        400 Bad Request (if record_type filter is invalid)
        401 Unauthorized
        404 Not Found (if flock_id does not exist)
        500 Internal Server Error
    """
    flock = current_app.flock_repo.get_flock_by_id(flock_id)
    if not flock:
        return jsonify({"message": "Flock not found", "error": "Not Found"}), 404

    record_type_str = request.args.get('record_type')
    record_type_enum = None
    if record_type_str:
        try:
            record_type_enum = RecordType(record_type_str)
        except ValueError:
            return jsonify({"message": f"Invalid record_type filter value: {record_type_str}", "error": "Bad Request"}), 400

    records = current_app.tracking_repo.get_health_records_for_flock(flock_id, record_type=record_type_enum)
    return jsonify([rec.to_dict() for rec in records]), 200


@tracking_bp.route('/health-records/<string:record_id>', methods=['GET'])
@token_required
def get_health_record_by_id(record_id: str):
    """
    Gets a specific health record by its ID.
    Endpoint: GET /api/health-records/{record_id}
    Authentication: Required (Bearer Token)
    Path Parameters:
        record_id (string): The unique identifier for the health record.
    Response (Success: 200 OK):
        { ... (health record object) ... }
    Response (Error):
        401 Unauthorized
        404 Not Found (if record_id does not exist)
        500 Internal Server Error
    """
    record = current_app.tracking_repo.get_health_record_by_id(record_id)
    if record:
        return jsonify(record.to_dict()), 200
    else:
        return jsonify({"message": "Health record not found", "error": "Not Found"}), 404

@tracking_bp.route('/health-records/<string:record_id>', methods=['PUT'])
@token_required
def update_health_record(record_id: str):
    """
    Updates an existing health record.
    Endpoint: PUT /api/health-records/{record_id}
    Authentication: Required (Bearer Token)
    Path Parameters:
        record_id (string): The ID of the health record to update.
    Request Body (JSON):
        Should contain fields to be updated. Structure depends on the record_type.
        Common fields: "record_date", "details", "veterinarian", "cost".
        Type-specific fields as defined in POST /flocks/{flock_id}/health.
        The record_type itself cannot be changed.
    Response (Success: 200 OK):
        { ... (updated health record object) ... }
    Response (Error):
        400 Bad Request (e.g., invalid data format)
        401 Unauthorized
        404 Not Found (if record_id does not exist)
        500 Internal Server Error
    """
    record = current_app.tracking_repo.get_health_record_by_id(record_id)
    if not record:
        return jsonify({"message": "Health record not found", "error": "Not Found"}), 404

    data = request.get_json()
    if not data:
        return jsonify({"message": "Request body must be JSON", "error": "Bad Request"}), 400

    update_payload = {}
    # Common fields
    if 'record_date' in data:
        dt_obj = _parse_datetime(data['record_date'])
        if not dt_obj: return jsonify({"message": "Invalid record_date format", "error": "Bad Request"}), 400
        update_payload['record_date'] = dt_obj
    if 'details' in data: update_payload['details'] = data['details']
    if 'veterinarian' in data: update_payload['veterinarian'] = data['veterinarian']
    if 'cost' in data: update_payload['cost'] = float(data['cost'])

    original_deaths = 0
    if isinstance(record, MortalityRecord):
        original_deaths = record.number_of_deaths

    # Type-specific fields
    if isinstance(record, DiseaseIncidentRecord):
        if 'disease_name' in data: update_payload['disease_name'] = data['disease_name']
        if 'symptoms' in data:
            symptoms_enum = [DiseaseSymptoms(s) for s in data.get('symptoms', [])]
            update_payload['symptoms'] = symptoms_enum
        if 'treatment_administered' in data: update_payload['treatment_administered'] = data['treatment_administered']
        if 'affected_count' in data: update_payload['affected_count'] = int(data['affected_count'])
    elif isinstance(record, VaccinationRecord):
        if 'vaccine_name' in data: update_payload['vaccine_name'] = data['vaccine_name']
        if 'administered_by' in data: update_payload['administered_by'] = data['administered_by']
        if 'dosage' in data: update_payload['dosage'] = data['dosage']
        if 'vaccinated_count' in data: update_payload['vaccinated_count'] = int(data['vaccinated_count'])
    elif isinstance(record, MortalityRecord):
        if 'cause_of_death' in data: update_payload['cause_of_death'] = data['cause_of_death']
        if 'number_of_deaths' in data: update_payload['number_of_deaths'] = int(data['number_of_deaths'])
        if 'post_mortem_findings' in data: update_payload['post_mortem_findings'] = data['post_mortem_findings']

    if not update_payload:
        return jsonify({"message": "No update fields provided", "error": "Bad Request"}), 400

    updated_record = current_app.tracking_repo.update_health_record(record_id, **update_payload)
    if not updated_record:
        return jsonify({"message": "Update failed", "error": "Internal Server Error"}), 500

    # If mortality count changed, adjust flock count
    if isinstance(updated_record, MortalityRecord) and 'number_of_deaths' in update_payload:
        death_diff = updated_record.number_of_deaths - original_deaths
        flock = current_app.flock_repo.get_flock_by_id(updated_record.flock_id)
        if flock:
            new_flock_count = flock.current_count - death_diff
            current_app.flock_repo.update_flock(flock.flock_id, current_count=max(0, new_flock_count))

    return jsonify(updated_record.to_dict()), 200


@tracking_bp.route('/health-records/<string:record_id>', methods=['DELETE'])
@token_required
def delete_health_record(record_id: str):
    """
    Deletes a specific health record by its ID.
    If it's a mortality record, the flock's bird count is adjusted upwards.
    Endpoint: DELETE /api/health-records/{record_id}
    Authentication: Required (Bearer Token)
    Path Parameters:
        record_id (string): The unique identifier for the health record to delete.
    Response (Success: 204 No Content):
        (Empty response body)
    Response (Error):
        401 Unauthorized
        404 Not Found (if record_id does not exist)
        500 Internal Server Error
    """
    record = current_app.tracking_repo.get_health_record_by_id(record_id)
    if not record:
        return jsonify({"message": "Health record not found", "error": "Not Found"}), 404

    # If deleting a mortality record, adjust flock count back
    if isinstance(record, MortalityRecord):
        flock = current_app.flock_repo.get_flock_by_id(record.flock_id)
        if flock:
            new_flock_count = flock.current_count + record.number_of_deaths
            current_app.flock_repo.update_flock(flock.flock_id, current_count=new_flock_count)

    if current_app.tracking_repo.delete_health_record(record_id):
        return '', 204
    else:
        return jsonify({"message": "Deletion failed", "error": "Internal Server Error"}), 500


# --- Production Record (Eggs) Routes ---

@tracking_bp.route('/flocks/<string:flock_id>/production', methods=['POST'])
@token_required
def add_production_record_for_flock(flock_id: str):
    """
    Adds a new egg production record for a specific flock.
    Endpoint: POST /api/flocks/{flock_id}/production
    Authentication: Required (Bearer Token)
    Path Parameters:
        flock_id (string): The ID of the flock.
    Request Body (JSON):
        {
            "record_date": "string (YYYY-MM-DD, required)",
            "total_eggs_laid": "integer (required)",
            "damaged_eggs": "integer (optional, default 0)",
            "average_egg_weight_gm": "float (optional, default 0.0)",
            "notes": "string (optional)"
        }
    Response (Success: 201 Created):
        { ... (the created production record object) ... }
    Response (Error):
        400 Bad Request, 401 Unauthorized, 404 Not Found, 500 Internal Server Error
    """
    if not current_app.flock_repo.get_flock_by_id(flock_id):
        return jsonify({"message": "Flock not found", "error": "Not Found"}), 404
    data = request.get_json()
    if not data or 'record_date' not in data or 'total_eggs_laid' not in data:
        return jsonify({"message": "Missing required fields: record_date, total_eggs_laid", "error": "Bad Request"}), 400

    record_date_obj = _parse_date(data.get('record_date'))
    if not record_date_obj: return jsonify({"message": "Invalid record_date format. Use YYYY-MM-DD.", "error": "Bad Request"}), 400

    try:
        record = current_app.tracking_repo.add_production_record(
            flock_id=flock_id,
            record_date=record_date_obj,
            total_eggs_laid=int(data['total_eggs_laid']),
            damaged_eggs=int(data.get('damaged_eggs', 0)),
            average_egg_weight_gm=float(data.get('average_egg_weight_gm', 0.0)),
            notes=data.get('notes', "")
        )
        return jsonify(record.to_dict()), 201
    except (ValueError, KeyError) as e: # KeyError if required field missing after initial checks
        return jsonify({"message": str(e), "error": "Bad Request"}), 400
    except Exception as e:
        current_app.logger.error(f"Error adding production record: {e}")
        return jsonify({"message": "An unexpected error occurred", "error": "Internal Server Error"}), 500

@tracking_bp.route('/flocks/<string:flock_id>/production', methods=['GET'])
@token_required
def get_production_records_for_flock(flock_id: str):
    """
    Lists egg production records for a specific flock.
    Endpoint: GET /api/flocks/{flock_id}/production
    Authentication: Required (Bearer Token)
    Path Parameters:
        flock_id (string): The ID of the flock.
    Query Parameters:
        start_date (string, YYYY-MM-DD, optional): Filter records from this date.
        end_date (string, YYYY-MM-DD, optional): Filter records up to this date.
    Response (Success: 200 OK):
        [ { ... (production record object) ... }, ... ]
    Response (Error):
        401 Unauthorized, 404 Not Found, 500 Internal Server Error
    """
    if not current_app.flock_repo.get_flock_by_id(flock_id):
        return jsonify({"message": "Flock not found", "error": "Not Found"}), 404

    start_date_str = request.args.get('start_date')
    end_date_str = request.args.get('end_date')
    start_date = _parse_date(start_date_str) if start_date_str else None
    end_date = _parse_date(end_date_str) if end_date_str else None

    records = current_app.tracking_repo.get_production_records_for_flock(flock_id, start_date=start_date, end_date=end_date)
    return jsonify([rec.to_dict() for rec in records]), 200

@tracking_bp.route('/production-records/<string:record_id>', methods=['GET', 'PUT', 'DELETE'])
@token_required
def manage_production_record(record_id: str):
    """
    Manages a specific egg production record (Get, Update, Delete).
    Endpoints:
        GET /api/production-records/{record_id}
        PUT /api/production-records/{record_id}
        DELETE /api/production-records/{record_id}
    Authentication: Required (Bearer Token)
    Path Parameters:
        record_id (string): The ID of the production record.
    Request Body (for PUT, JSON):
        {
            "record_date": "string (YYYY-MM-DD, optional)",
            "total_eggs_laid": "integer (optional)",
            "damaged_eggs": "integer (optional)",
            "average_egg_weight_gm": "float (optional)",
            "notes": "string (optional)"
        }
    Response (Success):
        GET/PUT: 200 OK { ... (production record object) ... }
        DELETE: 204 No Content
    Response (Error):
        400 Bad Request, 401 Unauthorized, 404 Not Found, 500 Internal Server Error
    """
    record = current_app.tracking_repo.get_production_record_by_id(record_id)
    if not record: # Removed "and request.method != 'POST'" as POST is not handled here
        return jsonify({"message": "Production record not found", "error": "Not Found"}), 404

    if request.method == 'GET':
        return jsonify(record.to_dict()), 200

    elif request.method == 'PUT':
        data = request.get_json()
        if not data: return jsonify({"message": "Request body must be JSON", "error": "Bad Request"}), 400
        update_data = {}
        if 'record_date' in data:
            rd_obj = _parse_date(data['record_date'])
            if not rd_obj and data['record_date'] is not None: # Allow null to clear? No, date required.
                 return jsonify({"message": "Invalid record_date format. Use YYYY-MM-DD or null.", "error": "Bad Request"}), 400
            update_data['record_date'] = rd_obj

        for field in ['total_eggs_laid', 'damaged_eggs']:
            if field in data:
                try: update_data[field] = int(data[field])
                except (ValueError, TypeError): return jsonify({"message": f"{field} must be an integer", "error": "Bad Request"}), 400
        if 'average_egg_weight_gm' in data:
            try: update_data['average_egg_weight_gm'] = float(data['average_egg_weight_gm'])
            except (ValueError, TypeError): return jsonify({"message": "average_egg_weight_gm must be a float", "error": "Bad Request"}), 400
        if 'notes' in data:
            update_data['notes'] = data['notes']

        if not update_data: return jsonify({"message": "No update fields provided", "error": "Bad Request"}), 400
        try:
            updated = current_app.tracking_repo.update_production_record(record_id, **update_data)
            if updated: return jsonify(updated.to_dict()), 200
            else: return jsonify({"message": "Update failed internally"}), 500 # Should be caught by repo if ID invalid
        except ValueError as e:
            return jsonify({"message": str(e), "error": "Bad Request"}), 400

    elif request.method == 'DELETE':
        if current_app.tracking_repo.delete_production_record(record_id):
            return '', 204
        else:
            # This path should ideally not be reached if the initial record check is successful
            # and delete_production_record is robust.
            return jsonify({"message": "Deletion failed, record might have been already deleted or an internal error occurred", "error": "Internal Server Error"}), 500

# --- Feed Consumption Routes ---
@tracking_bp.route('/flocks/<string:flock_id>/feed', methods=['POST'])
@token_required
def add_feed_record_for_flock(flock_id: str):
    """
    Adds a new feed consumption record for a specific flock.
    Endpoint: POST /api/flocks/{flock_id}/feed
    Authentication: Required (Bearer Token)
    Path Parameters:
        flock_id (string): The ID of the flock.
    Request Body (JSON):
        {
            "record_date": "string (YYYY-MM-DD, required)",
            "feed_type": "string (required)",
            "quantity_kg": "float (required)",
            "cost_per_kg": "float (optional, default 0.0)",
            "notes": "string (optional)"
        }
    Response (Success: 201 Created):
        { ... (the created feed record object) ... }
    Response (Error):
        400 Bad Request, 401 Unauthorized, 404 Not Found, 500 Internal Server Error
    """
    if not current_app.flock_repo.get_flock_by_id(flock_id): return jsonify({"message":"Flock not found"}),404
    data = request.get_json()
    if not data or not all(k in data for k in ['record_date', 'feed_type', 'quantity_kg']):
        return jsonify({"message": "Missing required fields: record_date, feed_type, quantity_kg", "error": "Bad Request"}), 400

    record_date_obj = _parse_date(data.get('record_date'))
    if not record_date_obj: return jsonify({"message":"Invalid record_date format. Use YYYY-MM-DD."}),400
    try:
        record = current_app.tracking_repo.add_feed_consumption_record(
            flock_id=flock_id, record_date=record_date_obj, feed_type=data['feed_type'],
            quantity_kg=float(data['quantity_kg']), cost_per_kg=float(data.get('cost_per_kg',0.0)),
            notes=data.get('notes',"")
        )
        return jsonify(record.to_dict()), 201
    except (ValueError, KeyError) as e: return jsonify({"message":str(e), "error": "Bad Request"}),400

@tracking_bp.route('/flocks/<string:flock_id>/feed', methods=['GET'])
@token_required
def get_feed_records_for_flock(flock_id: str):
    """
    Lists feed consumption records for a specific flock.
    Endpoint: GET /api/flocks/{flock_id}/feed
    Authentication: Required (Bearer Token)
    Path Parameters:
        flock_id (string): The ID of the flock.
    Query Parameters:
        start_date (string, YYYY-MM-DD, optional): Filter records from this date.
        end_date (string, YYYY-MM-DD, optional): Filter records up to this date.
    Response (Success: 200 OK):
        [ { ... (feed record object) ... }, ... ]
    Response (Error):
        401 Unauthorized, 404 Not Found, 500 Internal Server Error
    """
    if not current_app.flock_repo.get_flock_by_id(flock_id): return jsonify({"message":"Flock not found"}),404
    start_date_str = request.args.get('start_date')
    end_date_str = request.args.get('end_date')
    start_date = _parse_date(start_date_str) if start_date_str else None
    end_date = _parse_date(end_date_str) if end_date_str else None
    records = current_app.tracking_repo.get_feed_consumption_records_for_flock(flock_id, start_date=start_date, end_date=end_date)
    return jsonify([r.to_dict() for r in records]), 200

@tracking_bp.route('/feed-records/<string:record_id>', methods=['GET', 'PUT', 'DELETE'])
@token_required
def manage_feed_record(record_id: str):
    """
    Manages a specific feed consumption record (Get, Update, Delete).
    Endpoints:
        GET /api/feed-records/{record_id}
        PUT /api/feed-records/{record_id}
        DELETE /api/feed-records/{record_id}
    Authentication: Required (Bearer Token)
    Path Parameters:
        record_id (string): The ID of the feed record.
    Request Body (for PUT, JSON):
        {
            "record_date": "string (YYYY-MM-DD, optional)",
            "feed_type": "string (optional)",
            "quantity_kg": "float (optional)",
            "cost_per_kg": "float (optional)",
            "notes": "string (optional)"
        }
    Response (Success):
        GET/PUT: 200 OK { ... (feed record object) ... }
        DELETE: 204 No Content
    Response (Error):
        400 Bad Request, 401 Unauthorized, 404 Not Found, 500 Internal Server Error
    """
    record = current_app.tracking_repo.get_feed_consumption_record_by_id(record_id)
    if not record: return jsonify({"message": "Feed record not found", "error": "Not Found"}), 404

    if request.method == 'GET': return jsonify(record.to_dict()), 200

    if request.method == 'PUT':
        data = request.get_json()
        if not data: return jsonify({"message": "Request body must be JSON", "error": "Bad Request"}), 400
        update_data = {}
        if 'record_date' in data:
            rd_obj = _parse_date(data['record_date'])
            if not rd_obj and data['record_date'] is not None:
                return jsonify({"message": "Invalid record_date format. Use YYYY-MM-DD.", "error": "Bad Request"}), 400
            update_data['record_date'] = rd_obj

        for field in ['feed_type', 'notes']:
            if field in data: update_data[field] = data[field]
        for field in ['quantity_kg', 'cost_per_kg']:
            if field in data:
                try: update_data[field] = float(data[field])
                except (ValueError, TypeError): return jsonify({"message": f"{field} must be a float", "error": "Bad Request"}), 400

        if not update_data: return jsonify({"message": "No update fields provided", "error": "Bad Request"}), 400
        try:
            updated = current_app.tracking_repo.update_feed_consumption_record(record_id, **update_data)
            if updated: return jsonify(updated.to_dict()), 200
            else: return jsonify({"message": "Update failed internally"}), 500
        except ValueError as e: return jsonify({"message":str(e), "error": "Bad Request"}),400

    if request.method == 'DELETE':
        if current_app.tracking_repo.delete_feed_consumption_record(record_id):
            return '', 204
        else:
            return jsonify({"message": "Deletion failed", "error": "Internal Server Error"}), 500

# --- Growth Record Routes ---
@tracking_bp.route('/flocks/<string:flock_id>/growth', methods=['POST'])
@token_required
def add_growth_record_for_flock(flock_id: str):
    """
    Adds a new growth record for a specific flock.
    Endpoint: POST /api/flocks/{flock_id}/growth
    Authentication: Required (Bearer Token)
    Path Parameters:
        flock_id (string): The ID of the flock.
    Request Body (JSON):
        {
            "record_date": "string (YYYY-MM-DD, required)",
            "average_weight_grams": "float (required)",
            "number_of_birds_weighed": "integer (required)",
            "feed_conversion_ratio": "float (optional)",
            "notes": "string (optional)"
        }
    Response (Success: 201 Created):
        { ... (the created growth record object) ... }
    Response (Error):
        400 Bad Request, 401 Unauthorized, 404 Not Found, 500 Internal Server Error
    """
    if not current_app.flock_repo.get_flock_by_id(flock_id): return jsonify({"message":"Flock not found"}),404
    data = request.get_json()
    if not data or not all(k in data for k in ['record_date', 'average_weight_grams', 'number_of_birds_weighed']):
        return jsonify({"message": "Missing required fields: record_date, average_weight_grams, number_of_birds_weighed", "error": "Bad Request"}), 400

    record_date_obj = _parse_date(data.get('record_date'))
    if not record_date_obj: return jsonify({"message":"Invalid record_date format. Use YYYY-MM-DD."}),400
    try:
        record = current_app.tracking_repo.add_growth_record(
            flock_id=flock_id, record_date=record_date_obj,
            average_weight_grams=float(data['average_weight_grams']),
            number_of_birds_weighed=int(data['number_of_birds_weighed']),
            feed_conversion_ratio=float(data.get('feed_conversion_ratio')) if data.get('feed_conversion_ratio') is not None else None,
            notes=data.get('notes',"")
        )
        return jsonify(record.to_dict()), 201
    except (ValueError, KeyError) as e: return jsonify({"message":str(e), "error": "Bad Request"}),400

@tracking_bp.route('/flocks/<string:flock_id>/growth', methods=['GET'])
@token_required
def get_growth_records_for_flock(flock_id: str):
    """
    Lists growth records for a specific flock.
    Endpoint: GET /api/flocks/{flock_id}/growth
    Authentication: Required (Bearer Token)
    Path Parameters:
        flock_id (string): The ID of the flock.
    Query Parameters:
        start_date (string, YYYY-MM-DD, optional): Filter records from this date.
        end_date (string, YYYY-MM-DD, optional): Filter records up to this date.
    Response (Success: 200 OK):
        [ { ... (growth record object) ... }, ... ]
    Response (Error):
        401 Unauthorized, 404 Not Found, 500 Internal Server Error
    """
    if not current_app.flock_repo.get_flock_by_id(flock_id): return jsonify({"message":"Flock not found"}),404
    start_date_str = request.args.get('start_date')
    end_date_str = request.args.get('end_date')
    start_date = _parse_date(start_date_str) if start_date_str else None
    end_date = _parse_date(end_date_str) if end_date_str else None
    records = current_app.tracking_repo.get_growth_records_for_flock(flock_id, start_date=start_date, end_date=end_date)
    return jsonify([r.to_dict() for r in records]), 200

@tracking_bp.route('/growth-records/<string:record_id>', methods=['GET', 'PUT', 'DELETE'])
@token_required
def manage_growth_record(record_id: str):
    """
    Manages a specific growth record (Get, Update, Delete).
    Endpoints:
        GET /api/growth-records/{record_id}
        PUT /api/growth-records/{record_id}
        DELETE /api/growth-records/{record_id}
    Authentication: Required (Bearer Token)
    Path Parameters:
        record_id (string): The ID of the growth record.
    Request Body (for PUT, JSON):
        {
            "record_date": "string (YYYY-MM-DD, optional)",
            "average_weight_grams": "float (optional)",
            "number_of_birds_weighed": "integer (optional)",
            "feed_conversion_ratio": "float (optional, or null to clear)",
            "notes": "string (optional)"
        }
    Response (Success):
        GET/PUT: 200 OK { ... (growth record object) ... }
        DELETE: 204 No Content
    Response (Error):
        400 Bad Request, 401 Unauthorized, 404 Not Found, 500 Internal Server Error
    """
    record = current_app.tracking_repo.get_growth_record_by_id(record_id)
    if not record: return jsonify({"message": "Growth record not found", "error": "Not Found"}), 404

    if request.method == 'GET': return jsonify(record.to_dict()), 200

    if request.method == 'PUT':
        data = request.get_json()
        if not data: return jsonify({"message": "Request body must be JSON", "error": "Bad Request"}), 400
        update_data = {}
        if 'record_date' in data:
            rd_obj = _parse_date(data['record_date'])
            if not rd_obj and data['record_date'] is not None:
                return jsonify({"message": "Invalid record_date format. Use YYYY-MM-DD.", "error": "Bad Request"}), 400
            update_data['record_date'] = rd_obj

        if 'average_weight_grams' in data:
            try: update_data['average_weight_grams'] = float(data['average_weight_grams'])
            except (ValueError, TypeError): return jsonify({"message": "average_weight_grams must be a float", "error": "Bad Request"}), 400
        if 'number_of_birds_weighed' in data:
            try: update_data['number_of_birds_weighed'] = int(data['number_of_birds_weighed'])
            except (ValueError, TypeError): return jsonify({"message": "number_of_birds_weighed must be an integer", "error": "Bad Request"}), 400
        if 'feed_conversion_ratio' in data: # Allows null to clear
            if data['feed_conversion_ratio'] is None:
                update_data['feed_conversion_ratio'] = None
            else:
                try: update_data['feed_conversion_ratio'] = float(data['feed_conversion_ratio'])
                except (ValueError, TypeError): return jsonify({"message": "feed_conversion_ratio must be a float or null", "error": "Bad Request"}), 400
        if 'notes' in data:
            update_data['notes'] = data['notes']

        if not update_data: return jsonify({"message": "No update fields provided", "error": "Bad Request"}), 400
        try:
            updated = current_app.tracking_repo.update_growth_record(record_id, **update_data)
            if updated: return jsonify(updated.to_dict()), 200
            else: return jsonify({"message": "Update failed internally"}), 500
        except ValueError as e: return jsonify({"message":str(e), "error": "Bad Request"}),400

    if request.method == 'DELETE':
        if current_app.tracking_repo.delete_growth_record(record_id):
            return '', 204
        else:
            return jsonify({"message": "Deletion failed", "error": "Internal Server Error"}), 500

# --- Alert System Routes (Conceptual) ---
@tracking_bp.route('/flocks/<string:flock_id>/health/alerts/mortality', methods=['GET'])
@token_required
def check_mortality_alert(flock_id: str):
    """
    Checks for high mortality alerts for a specific flock.
    Endpoint: GET /api/flocks/{flock_id}/health/alerts/mortality
    Authentication: Required (Bearer Token)
    Path Parameters:
        flock_id (string): The ID of the flock.
    Query Parameters:
        period_days (integer, optional, default 7): The period in days to check for mortality.
        threshold_deaths (integer, optional, default 5): The number of deaths to trigger an alert.
    Response (Success: 200 OK):
        {
            "alert": "boolean (true if alert triggered, false otherwise)",
            "message": "string (description of the alert or status)"
        }
    Response (Error):
        401 Unauthorized, 404 Not Found, 500 Internal Server Error
    """
    if not current_app.flock_repo.get_flock_by_id(flock_id):
        return jsonify({"message": "Flock not found", "error": "Not Found"}), 404

    period_days = request.args.get('period_days', default=7, type=int)
    threshold_deaths = request.args.get('threshold_deaths', default=5, type=int)

    alert_message = current_app.tracking_repo.check_high_mortality_events(flock_id, period_days, threshold_deaths)
    if alert_message:
        return jsonify({"alert": True, "message": alert_message}), 200
    else:
        return jsonify({"alert": False, "message": "No high mortality alert detected."}), 200

@tracking_bp.route('/flocks/<string:flock_id>/health/alerts/disease', methods=['GET'])
@token_required
def check_disease_alert(flock_id: str):
    """
    Checks for specific disease outbreak alerts for a flock.
    Endpoint: GET /api/flocks/{flock_id}/health/alerts/disease
    Authentication: Required (Bearer Token)
    Path Parameters:
        flock_id (string): The ID of the flock.
    Query Parameters:
        disease_name (string, required): The name of the disease to check for.
        period_days (integer, optional, default 14): The period in days to check for incidents.
        min_incidents (integer, optional, default 2): The minimum number of incidents to trigger an alert.
    Response (Success: 200 OK):
        {
            "alert": "boolean (true if alert triggered, false otherwise)",
            "message": "string (description of the alert or status)"
        }
    Response (Error):
        400 Bad Request (if disease_name is missing)
        401 Unauthorized, 404 Not Found, 500 Internal Server Error
    """
    if not current_app.flock_repo.get_flock_by_id(flock_id):
        return jsonify({"message": "Flock not found", "error": "Not Found"}), 404

    disease_name = request.args.get('disease_name')
    if not disease_name:
        return jsonify({"message": "Query parameter 'disease_name' is required.", "error": "Bad Request"}), 400

    period_days = request.args.get('period_days', default=14, type=int)
    min_incidents = request.args.get('min_incidents', default=2, type=int)

    alert_message = current_app.tracking_repo.check_disease_outbreak(flock_id, period_days, disease_name, min_incidents)
    if alert_message:
        return jsonify({"alert": True, "message": alert_message}), 200
    else:
        return jsonify({"alert": False, "message": f"No '{disease_name}' outbreak alert detected."}), 200


def register_tracking_routes(app):
    """Registers the tracking blueprint with the Flask app."""
    app.register_blueprint(tracking_bp, url_prefix='/api')
