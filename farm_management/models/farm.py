from datetime import datetime

class Farm:
    def __init__(self, farm_id: str, name: str, location: str, owner: str, capacity: int, established_date: datetime = None, notes: str = ""):
        self.farm_id = farm_id  # Unique identifier for the farm
        self.name = name
        self.location = location  # Could be an address or GPS coordinates
        self.owner = owner
        self.capacity = capacity  # e.g., maximum number of birds
        self.established_date = established_date if established_date else datetime.now()
        self.notes = notes
        self.flocks = []  # List of Flock objects associated with this farm

    def __repr__(self):
        return f"<Farm {self.name} ({self.farm_id})>"

    def add_flock(self, flock):
        if flock not in self.flocks:
            self.flocks.append(flock)
            flock.farm_id = self.farm_id # Link flock to this farm

    def remove_flock(self, flock_id: str):
        self.flocks = [f for f in self.flocks if f.flock_id != flock_id]

    def to_dict(self):
        return {
            "farm_id": self.farm_id,
            "name": self.name,
            "location": self.location,
            "owner": self.owner,
            "capacity": self.capacity,
            "established_date": self.established_date.isoformat() if self.established_date else None,
            "notes": self.notes,
            "num_flocks": len(self.flocks)
        }
