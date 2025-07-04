from flask import Flask, jsonify, request
from werkzeug.exceptions import BadRequest, NotFound
import sys
import os

# Adjust path to import WeatherService from the parent directory's services package
# This assumes 'weather_api.py' is in 'farm_management/api/'
# and 'weather_service.py' is in 'farm_management/services/'
sys.path.append(os.path.join(os.path.dirname(__file__), '..'))

from services.weather_service import WeatherService

app = Flask(__name__)
weather_service = WeatherService()

@app.route('/weather/current_by_coords', methods=['GET'])
def get_weather_by_coords():
    """
    Fetches current weather based on latitude and longitude.
    Query parameters: lat (float), lon (float)
    """
    try:
        lat_str = request.args.get('lat')
        lon_str = request.args.get('lon')
        if lat_str is None or lon_str is None:
            raise BadRequest("Missing 'lat' or 'lon' query parameters.")

        lat = float(lat_str)
        lon = float(lon_str)
    except ValueError:
        raise BadRequest("Invalid latitude or longitude format. Must be float.")
    except BadRequest as e:
        return jsonify({"error": str(e)}), 400

    weather_data = weather_service.get_current_weather(latitude=lat, longitude=lon)
    if weather_data and weather_data.get("error"):
        # Pass through specific errors from the service if they exist
        return jsonify(weather_data), 404 # Or a more appropriate status code
    if not weather_data:
        return jsonify({"error": "Could not retrieve weather data."}), 500

    return jsonify(weather_data)

@app.route('/weather/current_by_location', methods=['GET'])
def get_weather_by_location_name():
    """
    Fetches current weather based on a location name string.
    Query parameter: location (string)
    """
    location_name = request.args.get('location')
    if not location_name:
        return jsonify({"error": "Missing 'location' query parameter."}), 400

    weather_data = weather_service.get_current_weather_for_farm(farm_location=location_name)
    if weather_data and weather_data.get("error"):
        # Pass through specific errors from the service
        return jsonify(weather_data), 404 # Or a more appropriate status code
    if not weather_data:
        return jsonify({"error": "Could not retrieve weather data for the specified location."}), 500

    return jsonify(weather_data)

@app.errorhandler(BadRequest)
def handle_bad_request(e):
    return jsonify(error=str(e.description)), 400

@app.errorhandler(NotFound) # Though direct NotFound isn't raised above, good practice
def handle_not_found(e):
    return jsonify(error=str(e.description)), 404

@app.errorhandler(Exception)
def handle_generic_exception(e):
    # Log the error for server-side review
    app.logger.error(f"An unexpected error occurred: {e}", exc_info=True)
    return jsonify(error="An unexpected internal server error occurred."), 500


if __name__ == '__main__':
    # Ensure the logger is configured for Flask development server
    if not app.debug: # if not in debug mode, add basic stream handler
        import logging
        stream_handler = logging.StreamHandler()
        stream_handler.setLevel(logging.INFO)
        app.logger.addHandler(stream_handler)
        app.logger.setLevel(logging.INFO)

    print("--- Flask Weather API Starting ---")
    print("Attempting to run on http://0.0.0.0:5000")
    print("Accessible from Android emulator via http://10.0.2.2:5000")
    print("Endpoints:")
    print("  GET /weather/current_by_coords?lat=<latitude>&lon=<longitude>")
    print("  GET /weather/current_by_location?location=<location_name>")
    print("--- Waiting for Flask to start ---")

    # The following print might only show after server stops if not flushed,
    # but the ones above should appear immediately.
    app.run(host='0.0.0.0', port=5000, debug=True)

    # This line will only be reached if the server is stopped.
    print("--- Flask Weather API Has Been Stopped ---")
