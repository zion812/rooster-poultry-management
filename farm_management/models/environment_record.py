from datetime import datetime
from typing import Optional

class EnvironmentRecord:
    """
    Represents an environmental reading for a specific flock.
    Conceptually, this data would often come from IoT sensors.
    """
    def __init__(self, record_id: str, flock_id: str,
                 record_date: datetime, temperature_celsius: Optional[float] = None,
                 humidity_percent: Optional[float] = None,
                 ammonia_ppm: Optional[float] = None,
                 carbon_dioxide_ppm: Optional[float] = None,
                 light_intensity_lux: Optional[float] = None,
                 notes: str = "",
                 sensor_id: Optional[str] = None): # ID of the sensor providing data

        if not flock_id:
            raise ValueError("flock_id cannot be empty for an EnvironmentRecord.")
        if not record_date:
            raise ValueError("record_date cannot be empty for an EnvironmentRecord.")


        self.record_id = record_id          # Unique ID for the environment record
        self.flock_id = flock_id            # ID of the flock this record pertains to
        self.record_date = record_date      # Timestamp of the reading

        self.temperature_celsius = temperature_celsius
        self.humidity_percent = humidity_percent
        self.ammonia_ppm = ammonia_ppm      # Ammonia level in parts per million
        self.carbon_dioxide_ppm = carbon_dioxide_ppm # Carbon Dioxide level in parts per million
        self.light_intensity_lux = light_intensity_lux # Light intensity in lux

        self.notes = notes
        self.sensor_id = sensor_id          # Identifier for the IoT sensor

    def __repr__(self):
        return (f"<EnvironmentRecord {self.record_id} (Flock: {self.flock_id}, "
                f"Date: {self.record_date.strftime('%Y-%m-%d %H:%M')}, Temp: {self.temperature_celsius}°C)>")

    def to_dict(self):
        return {
            "record_id": self.record_id,
            "flock_id": self.flock_id,
            "record_date": self.record_date.isoformat() if self.record_date else None,
            "temperature_celsius": self.temperature_celsius,
            "humidity_percent": self.humidity_percent,
            "ammonia_ppm": self.ammonia_ppm,
            "carbon_dioxide_ppm": self.carbon_dioxide_ppm,
            "light_intensity_lux": self.light_intensity_lux,
            "notes": self.notes,
            "sensor_id": self.sensor_id
        }

# To fully integrate:
# - Add to TrackingRepository (load, save, add, get methods)
# - Add API endpoints (done in openapi.yaml design)
# - Add CLI options to view this data (likely not manual entry if primarily IoT)
# - Consider how this data links to flock health/production for analysis.
