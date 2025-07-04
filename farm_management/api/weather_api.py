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
 feature/dashboard-scaffolding-and-weather-api


# --- Farm Data API ---
from repositories.farm_repository import FarmRepository
from datetime import datetime, timedelta
import random

farm_repository = FarmRepository()

def get_mock_last_health_check_date():
    # Generates a random date string within the last 30 days for mock purposes
    days_ago = random.randint(1, 30)
    date_obj = datetime.now() - timedelta(days=days_ago)
    return date_obj.strftime("%d %b %Y")

@app.route('/farm/details/<string:farm_id>', methods=['GET'])
def get_farm_details(farm_id):
    farm = farm_repository.get_farm_by_id(farm_id)
    if not farm:
        return jsonify({"error": f"Farm with ID {farm_id} not found."}), 404

    # Adapt Python Farm model to Android FarmBasicInfo structure
    farm_basic_info = {
        "farmId": farm.farm_id,
        "farmName": farm.name,
        "location": farm.location,
        "ownerName": farm.owner, # Assuming farm.owner maps to ownerName
        "activeFlockCount": len(farm.flocks), # Assuming farm.flocks is populated
        "totalCapacity": farm.capacity,
        # lastHealthCheckDate is not in Python model, using mock for now
        "lastHealthCheckDate": get_mock_last_health_check_date()
    }
    return jsonify(farm_basic_info)

@app.route('/farms', methods=['GET']) # Optional: an endpoint to list all farm IDs or basic info
def get_all_farms_summary():
    all_farms = farm_repository.get_all_farms()
    summary_list = []
    for farm in all_farms:
        summary_list.append({
            "farmId": farm.farm_id,
            "farmName": farm.name,
            "location": farm.location,
            "ownerName": farm.owner
        })
    if not summary_list:
        # Initialize some default farms if none exist, for demo purposes
        # This ensures the API has some data if farms.json was empty or deleted
        print("No farms found in repository, initializing default farms for API demo.")
        farm_repo_path = os.path.join(os.path.dirname(__file__), '..', 'repositories', 'farms.json')
        if not os.path.exists(farm_repo_path) or os.path.getsize(farm_repo_path) < 5 : # check if file is empty or new
            farm_repository.add_farm("Green Valley Poultry (API Default)", "Rural Route 1, Krishna", "Mr. Patel (API)", 2000)
            farm_repository.add_farm("Sunrise Eggs (API Default)", "Near NH65, Krishna", "Mrs. Devi (API)", 10000)
            all_farms = farm_repository.get_all_farms() # re-fetch
            for farm in all_farms:
                 summary_list.append({
                    "farmId": farm.farm_id,
                    "farmName": farm.name,
                    "location": farm.location,
                    "ownerName": farm.owner
                })

    return jsonify(summary_list)


# --- Production Metrics API ---
# Assuming MarketService might be expanded or another service would handle this.
# For now, this will return mock data similar to the Android mock source.
from services.market_service import MarketService # Will be used if extended for real data
market_service = MarketService() # Currently provides market prices, not on-farm production

@app.route('/farm/production_summary/<string:farm_id>', methods=['GET'])
def get_production_summary(farm_id):
    farm = farm_repository.get_farm_by_id(farm_id)
    if not farm:
        return jsonify({"error": f"Farm with ID {farm_id} not found for production summary."}), 404

    # Mock data generation for production summary
    # This would ideally come from aggregating data from various records (production, health, feed)
    # associated with the farm and its flocks.

    active_birds_count = 0
    if farm.flocks: # Assuming farm.flocks is populated with Flock objects that have a current_bird_count attribute
        for flock_obj in farm.flocks:
            # The Flock model in farm_management.models.flock has initial_count,
            # but not an obvious 'current_bird_count' after mortality/sales.
            # Using initial_count as a proxy for now, or a random value.
            active_birds_count += getattr(flock_obj, 'initial_count', Random.randint(50, 500))


    mock_summary = {
        "totalFlocks": len(farm.flocks) if farm.flocks else random.randint(1,5), # Use actual if available, else random
        "activeBirds": active_birds_count if active_birds_count > 0 else random.randint(200, 1000),
        "overallEggProductionToday": random.randint(100, 1500),
        "weeklyMortalityRate": round(random.uniform(0.1, 3.0), 2),
        "metrics": [
            {
                "name": "Total Eggs (Last 7 Days)",
                "value": str(random.randint(700, 10000)),
                "unit": "eggs",
                "trend": random.choice(["UP", "DOWN", "STABLE", None]), # Match Android MetricTrend
                "period": "Last 7 Days"
            },
            {
                "name": "Avg. Egg Weight",
                "value": f"{random.uniform(50.0, 65.0):.1f}",
                "unit": "g",
                "trend": "STABLE",
                "period": "Last Batch"
            },
            {
                "name": "Feed Conversion Ratio (FCR)",
                "value": f"{random.uniform(1.8, 2.5):.2f}",
                "unit": "",
                "trend": random.choice(["UP", "DOWN", None]),
                "period": "Current Broiler Flock"
            }
            # Add more mock metrics if needed
        ]
    }
    return jsonify(mock_summary)


# --- Farm Health Alerts API ---
# This API will serve currently stored/mocked alerts.
# The actual generation of alerts would happen via other backend processes.

# Python equivalent for AlertSeverity enum on Android
class PyAlertSeverity:
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"
    CRITICAL = "CRITICAL"

# Mock storage for alerts for demonstration purposes
# In a real system, this would come from a database where alerts are recorded.
mock_farm_health_alerts_db = {
    "farm123": [
        {
            "id": "alert001", "flockId": "flockA", "farmId": "farm123", "title": "High Temperature Detected",
            "description": "Temperature in Hen House 1 exceeded 32°C for over an hour.",
            "severity": PyAlertSeverity.HIGH, "timestamp": int((datetime.now() - timedelta(hours=2)).timestamp() * 1000), # Milliseconds
            "recommendedAction": "Check ventilation immediately. Provide cool water.", "isRead": False
        },
        {
            "id": "alert002", "flockId": "flockB", "farmId": "farm123", "title": "Low Feed Alert",
            "description": "Automated feed sensor for Flock B indicates low levels (below 10%).",
            "severity": PyAlertSeverity.MEDIUM, "timestamp": int((datetime.now() - timedelta(days=1)).timestamp() * 1000),
            "recommendedAction": "Schedule feed refill for Flock B.", "isRead": True
        }
    ],
    "farm456": [
        {
            "id": "alert003", "flockId": None, "farmId": "farm456", "title": "Unusual Mortality Spike",
            "description": "3 birds died unexpectedly in the last 12 hours. Investigation needed.",
            "severity": PyAlertSeverity.CRITICAL, "timestamp": int((datetime.now() - timedelta(minutes=30)).timestamp() * 1000),
            "recommendedAction": "Isolate any sick birds. Contact veterinarian for consultation.", "isRead": False
        }
    ]
}


@app.route('/farm/health_alerts/<string:farm_id>', methods=['GET'])
def get_farm_health_alerts(farm_id):
    # In a real system, query a database for alerts related to farm_id
    alerts_for_farm = mock_farm_health_alerts_db.get(farm_id, [])

    # Sort by timestamp descending to show newest first (optional, client can also sort)
    # alerts_for_farm.sort(key=lambda x: x['timestamp'], reverse=True)

    return jsonify(alerts_for_farm)

@app.route('/farm/health_alerts/<string:farm_id>/<string:alert_id>/read', methods=['POST'])
def mark_alert_as_read(farm_id, alert_id):
    alerts_for_farm = mock_farm_health_alerts_db.get(farm_id)
    if alerts_for_farm:
        for alert in alerts_for_farm:
            if alert['id'] == alert_id:
                alert['isRead'] = True
                # TODO: Persist this change in a real database
                print(f"Marked alert {alert_id} for farm {farm_id} as read.")
                return jsonify({"success": True, "message": f"Alert {alert_id} marked as read."}), 200
    return jsonify({"success": False, "error": f"Alert {alert_id} not found for farm {farm_id}."}), 404

 main
