import requests
from typing import Dict, Optional, Tuple

# Simple geocoding lookup for demo purposes for Krishna District locations
# In a real app, use a proper geocoding service or ask user for lat/lon.
KRISHNA_DISTRICT_LOCATIONS: Dict[str, Tuple[float, float]] = {
    "machilipatnam": (16.17, 81.13),
    "vijayawada": (16.50, 80.64),
    "gudivada": (16.44, 80.99),
    "nuzvid": (16.78, 80.84),
    "krishna district center": (16.40, 80.80) # A general point
}

OPEN_METEO_API_URL = "https://api.open-meteo.com/v1/forecast"

class WeatherService:
    def __init__(self):
        pass

    def _get_lat_lon_for_location(self, location_name: str) -> Optional[Tuple[float, float]]:
        """
        Tries to find latitude and longitude for a given location name.
        Uses a predefined dictionary for common Krishna District locations.
        """
        normalized_location = location_name.lower()
        for key, coords in KRISHNA_DISTRICT_LOCATIONS.items():
            if key in normalized_location:
                return coords

        # Fallback if no specific match, use a general point for Krishna or indicate failure
        if "krishna" in normalized_location: # If "krishna" is anywhere in the location string
            return KRISHNA_DISTRICT_LOCATIONS["krishna district center"]

        print(f"Could not determine coordinates for location: {location_name}. Specific lat/lon might be needed.")
        return None

    def get_current_weather_for_farm(self, farm_location: str) -> Optional[Dict]:
        """
        Fetches current weather for a farm's location string.
        """
        coords = self._get_lat_lon_for_location(farm_location)
        if not coords:
            return {"error": f"Could not find coordinates for location '{farm_location}'. Weather data unavailable."}

        latitude, longitude = coords
        return self.get_current_weather(latitude, longitude)


    def get_current_weather(self, latitude: float, longitude: float) -> Optional[Dict]:
        """
        Fetches current weather data from Open-Meteo API.
        Includes temperature, humidity, precipitation, wind speed.
        """
        params = {
            "latitude": latitude,
            "longitude": longitude,
            "current": "temperature_2m,relative_humidity_2m,precipitation,wind_speed_10m,weather_code",
            "timezone": "Asia/Kolkata" # IST
        }
        try:
            response = requests.get(OPEN_METEO_API_URL, params=params, timeout=10)
            response.raise_for_status()  # Raises an HTTPError for bad responses (4XX or 5XX)
            data = response.json()

            if "current" not in data:
                return {"error": "Current weather data not found in API response."}

            current_weather = data["current"]
            # Add units from current_units for clarity
            current_units = data.get("current_units", {})

            # Map weather codes to descriptions (simplified)
            # Full WMO Weather interpretation codes: https://open-meteo.com/en/docs#weathervariables
            weather_code = current_weather.get("weather_code")
            weather_description = self.interpret_weather_code(weather_code)


            return {
                "temperature": f"{current_weather.get('temperature_2m')} {current_units.get('temperature_2m', '°C')}",
                "humidity": f"{current_weather.get('relative_humidity_2m')} {current_units.get('relative_humidity_2m', '%')}",
                "precipitation": f"{current_weather.get('precipitation')} {current_units.get('precipitation', 'mm')}",
                "wind_speed": f"{current_weather.get('wind_speed_10m')} {current_units.get('wind_speed_10m', 'km/h')}",
                "description": weather_description,
                "raw_weather_code": weather_code,
                "latitude": latitude,
                "longitude": longitude,
                "timezone": data.get("timezone")
            }

        except requests.exceptions.RequestException as e:
            print(f"Error fetching weather data: {e}")
            return {"error": f"Network error or API issue: {e}"}
        except KeyError as e:
            print(f"Error parsing weather data (KeyError): {e}. Response: {data}")
            return {"error": f"Could not parse weather API response (KeyError: {e})."}
        except Exception as e: # Catch any other unexpected errors
            print(f"An unexpected error occurred while fetching weather: {e}")
            return {"error": f"An unexpected error occurred: {e}"}

    def interpret_weather_code(self, code: Optional[int]) -> str:
        """Interprets WMO weather codes from Open-Meteo into readable descriptions."""
        if code is None: return "Unknown"
        # Simplified mapping
        if code == 0: return "Clear sky"
        if code in [1, 2, 3]: return "Mainly clear, partly cloudy, or overcast"
        if code in [45, 48]: return "Fog or depositing rime fog"
        if code in [51, 53, 55]: return "Drizzle (Light, moderate, dense)"
        if code in [56, 57]: return "Freezing Drizzle (Light, dense)"
        if code in [61, 63, 65]: return "Rain (Slight, moderate, heavy)"
        if code in [66, 67]: return "Freezing Rain (Light, heavy)"
        if code in [71, 73, 75]: return "Snow fall (Slight, moderate, heavy)"
        if code == 77: return "Snow grains"
        if code in [80, 81, 82]: return "Rain showers (Slight, moderate, violent)"
        if code in [85, 86]: return "Snow showers (Slight, heavy)"
        if code == 95: return "Thunderstorm (Slight or moderate)" # Open-Meteo groups 96 & 99 with 95 for current
        if code in [96, 99]: return "Thunderstorm with slight/heavy hail"
        return f"Weather code {code} (refer to WMO standards)"


if __name__ == '__main__':
    service = WeatherService()

    print("--- Weather for Machilipatnam ---")
    weather_mach = service.get_current_weather_for_farm("Machilipatnam")
    if weather_mach and not weather_mach.get("error"):
        for key, value in weather_mach.items():
            print(f"  {key.replace('_', ' ').capitalize()}: {value}")
    else:
        print(f"  Error: {weather_mach.get('error', 'Unknown error')}")

    print("\n--- Weather for Vijayawada ---")
    weather_vja = service.get_current_weather(16.50, 80.64) # Direct lat/lon
    if weather_vja and not weather_vja.get("error"):
        for key, value in weather_vja.items():
            print(f"  {key.replace('_', ' ').capitalize()}: {value}")
    else:
        print(f"  Error: {weather_vja.get('error', 'Unknown error')}")

    print("\n--- Weather for Unknown Location (should use fallback or fail) ---")
    weather_unknown = service.get_current_weather_for_farm("Some Random Village")
    if weather_unknown and not weather_unknown.get("error"):
        for key, value in weather_unknown.items():
            print(f"  {key.replace('_', ' ').capitalize()}: {value}")
    else:
        print(f"  Result: {weather_unknown}")

    print("\n--- Weather for 'krishna' (should use fallback) ---")
    weather_krishna = service.get_current_weather_for_farm("A farm in Krishna")
    if weather_krishna and not weather_krishna.get("error"):
        for key, value in weather_krishna.items():
            print(f"  {key.replace('_', ' ').capitalize()}: {value}")
    else:
        print(f"  Result: {weather_krishna}")

    print("\n--- Test interpret_weather_code ---")
    print(f"Code 0: {service.interpret_weather_code(0)}")
    print(f"Code 3: {service.interpret_weather_code(3)}")
    print(f"Code 61: {service.interpret_weather_code(61)}")
    print(f"Code 95: {service.interpret_weather_code(95)}")
    print(f"Code None: {service.interpret_weather_code(None)}")
    print(f"Code 100 (unknown): {service.interpret_weather_code(100)}")
