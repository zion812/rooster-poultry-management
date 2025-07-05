from flask import Blueprint, jsonify, request, current_app
from werkzeug.exceptions import BadRequest
import sys
import os

# Adjust path to import WeatherService from the parent directory's services package
# This assumes 'weather_api.py' is in 'farm_management/api/'
# and 'weather_service.py' is in 'farm_management/services/'
sys.path.append(os.path.join(os.path.dirname(__file__), '..'))

from services.weather_service import WeatherService

weather_bp = Blueprint('weather_bp', __name__)
weather_service = WeatherService() # Instantiate the service once

@weather_bp.route('/weather/current_by_coords', methods=['GET'])
def get_weather_by_coords():
    """
    Fetches current weather based on latitude and longitude.
    Query parameters: lat (float), lon (float)
    """
    lat_str = request.args.get('lat')
    lon_str = request.args.get('lon')
    if lat_str is None or lon_str is None:
        # Using current_app.aborter or raising specific exceptions handled by server.py
        return jsonify({"message": "Missing 'lat' or 'lon' query parameters.", "error": "Bad Request"}), 400

    try:
        lat = float(lat_str)
        lon = float(lon_str)
    except ValueError:
        return jsonify({"message": "Invalid latitude or longitude format. Must be float.", "error": "Bad Request"}), 400

    weather_data = weather_service.get_current_weather(latitude=lat, longitude=lon)

    if weather_data and weather_data.get("error"):
        status_code = weather_data.get("status_code", 404) # Use status_code from service if provided
        return jsonify(weather_data), status_code
    if not weather_data: # General failure from service
        return jsonify({"message": "Could not retrieve weather data.", "error": "Service Error"}), 500

    return jsonify(weather_data), 200

@weather_bp.route('/weather/current_by_location', methods=['GET'])
def get_weather_by_location_name():
    """
    Fetches current weather based on a location name string.
    Query parameter: location (string)
    """
    location_name = request.args.get('location')
    if not location_name:
        return jsonify({"message": "Missing 'location' query parameter.", "error": "Bad Request"}), 400

    weather_data = weather_service.get_current_weather_for_farm(farm_location=location_name)

    if weather_data and weather_data.get("error"):
        status_code = weather_data.get("status_code", 404)
        return jsonify(weather_data), status_code
    if not weather_data:
        return jsonify({"message": "Could not retrieve weather data for the specified location.", "error": "Service Error"}), 500

    return jsonify(weather_data), 200

# No need for app.errorhandler here as they are centralized in server.py
# No need for if __name__ == '__main__': block

def register_weather_routes(app):
    """Registers the weather blueprint with the Flask app."""
    app.register_blueprint(weather_bp, url_prefix='/api')
