from datetime import datetime
from typing import Optional

class EnvironmentRecord:
    """
    Represents an environmental reading for a specific farm or shed.
    Conceptually, this data would often come from IoT sensors.
    """
    def __init__(self, record_id: str, farm_id: str, shed_id: Optional[str] = None,
                 record_date: datetime, temperature_celsius: Optional[float] = None,
                 humidity_percent: Optional[float] = None,
                 ammonia_ppm: Optional[float] = None,
                 co2_ppm: Optional[float] = None,
                 light_lux: Optional[float] = None,
                 notes: str = "",
                 sensor_id: Optional[str] = None): # ID of the sensor providing data

        self.record_id = record_id          # Unique ID for the environment record
        self.farm_id = farm_id              # ID of the farm
        self.shed_id = shed_id              # Optional: Specific shed/house ID within the farm
        self.record_date = record_date      # Timestamp of the reading

        self.temperature_celsius = temperature_celsius
        self.humidity_percent = humidity_percent
        self.ammonia_ppm = ammonia_ppm      # Ammonia level in parts per million
        self.co2_ppm = co2_ppm              # Carbon Dioxide level in parts per million
        self.light_lux = light_lux          # Light intensity in lux

        self.notes = notes
        self.sensor_id = sensor_id          # Identifier for the IoT sensor

    def __repr__(self):
        return (f"<EnvironmentRecord {self.record_id} (Farm: {self.farm_id}, Shed: {self.shed_id or 'N/A'}, "
                f"Date: {self.record_date.strftime('%Y-%m-%d %H:%M')}, Temp: {self.temperature_celsius}°C)>")

    def to_dict(self):
        return {
            "record_id": self.record_id,
            "farm_id": self.farm_id,
            "shed_id": self.shed_id,
            "record_date": self.record_date.isoformat() if self.record_date else None,
            "temperature_celsius": self.temperature_celsius,
            "humidity_percent": self.humidity_percent,
            "ammonia_ppm": self.ammonia_ppm,
            "co2_ppm": self.co2_ppm,
            "light_lux": self.light_lux,
            "notes": self.notes,
            "sensor_id": self.sensor_id
        }

# Note: This model is conceptual for IoT integration.
# It's not currently integrated into the CLI data entry or repository saving/loading
# as that would require significant additions to the UI and TrackingRepository.
# It serves as a definition for data that could be collected via IoT.
# To fully integrate:
# - Add to TrackingRepository (load, save, add, get methods)
# - Add CLI options to view this data (likely not manual entry if primarily IoT)
# - Consider how this data links to flock health/production for analysis.
