from datetime import date

class GrowthRecord:
    def __init__(self, record_id: str, flock_id: str, record_date: date,
                 average_weight_grams: float, number_of_birds_weighed: int,
                 feed_conversion_ratio: float = None, notes: str = ""):
        self.record_id = record_id  # Unique ID for the growth record
        self.flock_id = flock_id    # ID of the flock this record pertains to
        self.record_date = record_date # Date of record
        self.average_weight_grams = average_weight_grams # Could be from IoT scales
        self.number_of_birds_weighed = number_of_birds_weighed # Relevant if avg_weight is from sample
        self.feed_conversion_ratio = feed_conversion_ratio # FCR = Total Feed Consumed / Total Weight Gain
        self.notes = notes
        # self.source_type: str = "manual" # Conceptual: 'manual' or 'iot_scale_DEF'


    def __repr__(self):
        return (f"<GrowthRecord {self.record_id} (Flock: {self.flock_id}, "
                f"Date: {self.record_date}, Avg Weight: {self.average_weight_grams}g)>")

    def to_dict(self):
        return {
            "record_id": self.record_id,
            "flock_id": self.flock_id,
            "record_date": self.record_date.isoformat() if self.record_date else None,
            "average_weight_grams": self.average_weight_grams,
            "number_of_birds_weighed": self.number_of_birds_weighed,
            "feed_conversion_ratio": self.feed_conversion_ratio,
            "notes": self.notes
        }
