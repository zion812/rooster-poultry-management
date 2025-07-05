from datetime import datetime, date

class ProductionRecord:
    def __init__(self, record_id: str, flock_id: str, record_date: date,
                 total_eggs_laid: int = 0, damaged_eggs: int = 0,
                 average_egg_weight_gm: float = 0.0, notes: str = ""):
        self.record_id = record_id # Unique ID for the production record
        self.flock_id = flock_id # ID of the flock this record pertains to
        self.record_date = record_date # Date of record
        self.total_eggs_laid = total_eggs_laid # Could be from IoT egg counter
        self.damaged_eggs = damaged_eggs
        self.average_egg_weight_gm = average_egg_weight_gm
        self.notes = notes
        # self.source_type: str = "manual" # Conceptual: 'manual' or 'iot_egg_counter_XYZ'

    @property
    def marketable_eggs(self):
        return self.total_eggs_laid - self.damaged_eggs

    def __repr__(self):
        return f"<ProductionRecord {self.record_id} (Flock: {self.flock_id}, Date: {self.record_date}, Eggs: {self.total_eggs_laid})>"

    def to_dict(self):
        return {
            "record_id": self.record_id,
            "flock_id": self.flock_id,
            "record_date": self.record_date.isoformat() if self.record_date else None,
            "total_eggs_laid": self.total_eggs_laid,
            "damaged_eggs": self.damaged_eggs,
            "marketable_eggs": self.marketable_eggs,
            "average_egg_weight_gm": self.average_egg_weight_gm,
            "notes": self.notes
        }


class FeedConsumptionRecord:
    def __init__(self, record_id: str, flock_id: str, record_date: date,
                 feed_type: str, quantity_kg: float, cost_per_kg: float = 0.0, notes: str = ""):
        self.record_id = record_id
        self.flock_id = flock_id
        self.record_date = record_date # Date of record
        self.feed_type = feed_type
        self.quantity_kg = quantity_kg # Could be from IoT silo sensor or automated feeder
        self.cost_per_kg = cost_per_kg
        self.notes = notes
        # self.source_type: str = "manual" # Conceptual: 'manual' or 'iot_feed_sensor_ABC'


    @property
    def total_cost(self):
        return self.quantity_kg * self.cost_per_kg

    def __repr__(self):
        return f"<FeedConsumptionRecord {self.record_id} (Flock: {self.flock_id}, Feed: {self.feed_type}, Qty: {self.quantity_kg}kg)>"

    def to_dict(self):
        return {
            "record_id": self.record_id,
            "flock_id": self.flock_id,
            "record_date": self.record_date.isoformat() if self.record_date else None,
            "feed_type": self.feed_type,
            "quantity_kg": self.quantity_kg,
            "cost_per_kg": self.cost_per_kg,
            "total_cost": self.total_cost,
            "notes": self.notes
        }
