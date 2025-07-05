from flask import Blueprint, request, jsonify, current_app
from farm_management.api.auth import token_required
from farm_management.models.environment_record import EnvironmentRecord # For type hinting and to_dict
from datetime import datetime, date
from typing import Optional

environment_bp = Blueprint('environment_bp', __name__)

def _parse_datetime_optional(dt_str: Optional[str]) -> Optional[datetime]:
    if not dt_str:
        return None
    try:
        return datetime.fromisoformat(dt_str)
    except ValueError:
        # Attempt to parse common format if isoformat fails, e.g. "YYYY-MM-DD HH:MM:SS" or "YYYY-MM-DD HH:MM"
        try:
            return datetime.strptime(dt_str, '%Y-%m-%d %H:%M:%S')
        except ValueError:
            try:
                return datetime.strptime(dt_str, '%Y-%m-%d %H:%M')
            except ValueError:
                 return None # Invalid format

@environment_bp.route('/flocks/<string:flock_id>/environment', methods=['POST'])
@token_required
def add_environment_record_for_flock(flock_id: str):
    """
    Adds a new environment record for a specific flock.
    """
    flock = current_app.flock_repo.get_flock_by_id(flock_id)
    if not flock:
        return jsonify({"message": "Flock not found", "error": "Not Found"}), 404

    data = request.get_json()
    if not data:
        return jsonify({"message": "Request body must be JSON", "error": "Bad Request"}), 400

    record_date_str = data.get('record_date')
    if not record_date_str:
        return jsonify({"message": "Missing required field: record_date", "error": "Bad Request"}), 400

    record_date_obj = _parse_datetime_optional(record_date_str)
    if not record_date_obj:
        return jsonify({"message": "Invalid record_date format. Use ISO format (YYYY-MM-DDTHH:MM:SS) or YYYY-MM-DD HH:MM.", "error": "Bad Request"}), 400

    try:
        # Optional float fields
        temp_c = data.get('temperature_celsius')
        humidity_p = data.get('humidity_percent')
        ammonia_ppm_val = data.get('ammonia_ppm')
        carbon_dioxide_ppm_val = data.get('carbon_dioxide_ppm')
        light_lux_val = data.get('light_intensity_lux')

        record = current_app.tracking_repo.add_environment_record(
            flock_id=flock_id,
            record_date=record_date_obj,
            temperature_celsius=float(temp_c) if temp_c is not None else None,
            humidity_percent=float(humidity_p) if humidity_p is not None else None,
            ammonia_ppm=float(ammonia_ppm_val) if ammonia_ppm_val is not None else None,
            carbon_dioxide_ppm=float(carbon_dioxide_ppm_val) if carbon_dioxide_ppm_val is not None else None,
            light_intensity_lux=float(light_lux_val) if light_lux_val is not None else None,
            notes=data.get('notes', ""),
            sensor_id=data.get('sensor_id')
        )
        return jsonify(record.to_dict()), 201
    except ValueError as e: # Catches issues like non-float conversion
        return jsonify({"message": str(e), "error": "Bad Request"}), 400
    except Exception as e:
        current_app.logger.error(f"Error adding environment record for flock {flock_id}: {e}", exc_info=True)
        return jsonify({"message": "An unexpected error occurred", "error": "Internal Server Error"}), 500


@environment_bp.route('/flocks/<string:flock_id>/environment', methods=['GET'])
@token_required
def get_environment_records_for_flock(flock_id: str):
    """
    Lists environment records for a specific flock, optionally filtered by date range.
    """
    flock = current_app.flock_repo.get_flock_by_id(flock_id)
    if not flock:
        return jsonify({"message": "Flock not found", "error": "Not Found"}), 404

    start_date_str = request.args.get('start_date')
    end_date_str = request.args.get('end_date')

    start_date_obj = _parse_datetime_optional(start_date_str)
    end_date_obj = _parse_datetime_optional(end_date_str)

    if start_date_str and not start_date_obj:
        return jsonify({"message": "Invalid start_date format.", "error": "Bad Request"}), 400
    if end_date_str and not end_date_obj:
        return jsonify({"message": "Invalid end_date format.", "error": "Bad Request"}), 400

    records = current_app.tracking_repo.get_environment_records_for_flock(
        flock_id, start_date=start_date_obj, end_date=end_date_obj
    )
    return jsonify([rec.to_dict() for rec in records]), 200


@environment_bp.route('/environment-records/<string:record_id>', methods=['GET'])
@token_required
def get_environment_record_by_id(record_id: str):
    """
    Gets a specific environment record by its ID.
    """
    record = current_app.tracking_repo.get_environment_record_by_id(record_id)
    if record:
        return jsonify(record.to_dict()), 200
    else:
        return jsonify({"message": "Environment record not found", "error": "Not Found"}), 404


@environment_bp.route('/environment-records/<string:record_id>', methods=['PUT'])
@token_required
def update_environment_record(record_id: str):
    """
    Updates an existing environment record.
    """
    record = current_app.tracking_repo.get_environment_record_by_id(record_id)
    if not record:
        return jsonify({"message": "Environment record not found", "error": "Not Found"}), 404

    data = request.get_json()
    if not data:
        return jsonify({"message": "Request body must be JSON", "error": "Bad Request"}), 400

    update_payload = {}
    if 'record_date' in data:
        dt_obj = _parse_datetime_optional(data['record_date'])
        if not dt_obj:
            return jsonify({"message": "Invalid record_date format", "error": "Bad Request"}), 400
        update_payload['record_date'] = dt_obj

    # Optional float fields
    for field_name in ['temperature_celsius', 'humidity_percent', 'ammonia_ppm', 'carbon_dioxide_ppm', 'light_intensity_lux']:
        if field_name in data:
            value = data[field_name]
            update_payload[field_name] = float(value) if value is not None else None

    if 'notes' in data:
        update_payload['notes'] = data['notes']
    if 'sensor_id' in data:
        update_payload['sensor_id'] = data.get('sensor_id')


    if not update_payload:
        return jsonify({"message": "No update fields provided", "error": "Bad Request"}), 400

    try:
        updated_record = current_app.tracking_repo.update_environment_record(record_id, **update_payload)
        if updated_record:
            return jsonify(updated_record.to_dict()), 200
        else:
            # Should be caught by initial record check, but as a fallback:
            return jsonify({"message": "Update failed, record not found or an internal error occurred", "error": "Internal Server Error"}), 500
    except ValueError as e:
        return jsonify({"message": str(e), "error": "Bad Request"}), 400
    except Exception as e:
        current_app.logger.error(f"Error updating environment record {record_id}: {e}", exc_info=True)
        return jsonify({"message": "An unexpected error occurred during update", "error": "Internal Server Error"}), 500


@environment_bp.route('/environment-records/<string:record_id>', methods=['DELETE'])
@token_required
def delete_environment_record(record_id: str):
    """
    Deletes a specific environment record by its ID.
    """
    record = current_app.tracking_repo.get_environment_record_by_id(record_id)
    if not record:
        return jsonify({"message": "Environment record not found", "error": "Not Found"}), 404

    if current_app.tracking_repo.delete_environment_record(record_id):
        return '', 204
    else:
        # This case should ideally not be reached if the initial record check is done.
        return jsonify({"message": "Deletion failed unexpectedly", "error": "Internal Server Error"}), 500

def register_environment_routes(app):
    """Registers the environment blueprint with the Flask app."""
    app.register_blueprint(environment_bp, url_prefix='/api')
