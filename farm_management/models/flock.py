from datetime import date, datetime
from enum import Enum

class AgeGroup(Enum):
    CHICK = "Chick"
    GROWER = "Grower"
    LAYER = "Layer"
    BROILER = "Broiler"
    BREEDER = "Breeder"

class Flock:
    def __init__(self, flock_id: str, farm_id: str, breed: str, acquisition_date: date,
                 source_supplier: str = "", initial_count: int = 0, notes: str = "",
                 parent_flock_id_male: str = None, parent_flock_id_female: str = None):
        self.flock_id = flock_id  # Unique identifier for the flock
        self.farm_id = farm_id # Identifier of the farm this flock belongs to
        self.breed = breed
        self.acquisition_date = acquisition_date
        self.source_supplier = source_supplier
        self.initial_count = initial_count
        self.current_count = initial_count
        self.age_group = self._determine_age_group() # Calculated based on age
        self.notes = notes
        self.parent_flock_id_male = parent_flock_id_male # For family tree
        self.parent_flock_id_female = parent_flock_id_female # For family tree
        self.health_records = [] # List of HealthRecord objects
        self.production_records = [] # List of ProductionRecord objects
        self.growth_records = [] # List of GrowthRecord objects

    def _calculate_age_days(self):
        return (date.today() - self.acquisition_date).days

    def _determine_age_group(self):
        # This is a simplified logic, can be more sophisticated
        age_days = self._calculate_age_days()
        if age_days <= 42: # Up to 6 weeks
            return AgeGroup.CHICK
        elif age_days <= 140: # Up to 20 weeks
            return AgeGroup.GROWER
        # Further classification (Layer, Broiler, Breeder) might depend more on breed and purpose
        # For now, defaulting to Grower if older than chick
        return AgeGroup.GROWER


    def update_current_count(self, change: int):
        """
        Updates current count. Positive for additions, negative for removals/mortality.
        """
        self.current_count += change
        if self.current_count < 0:
            self.current_count = 0

    def add_health_record(self, record):
        if record not in self.health_records:
            self.health_records.append(record)

    def add_production_record(self, record):
        if record not in self.production_records:
            self.production_records.append(record)

    def add_growth_record(self, record):
        if record not in self.growth_records:
            self.growth_records.append(record)

    def __repr__(self):
        return f"<Flock {self.flock_id} (Breed: {self.breed}, Count: {self.current_count})>"

    def to_dict(self):
        return {
            "flock_id": self.flock_id,
            "farm_id": self.farm_id,
            "breed": self.breed,
            "acquisition_date": self.acquisition_date.isoformat() if self.acquisition_date else None,
            "source_supplier": self.source_supplier,
            "initial_count": self.initial_count,
            "current_count": self.current_count,
            "age_days": self._calculate_age_days(),
            "age_group": self.age_group.value,
            "notes": self.notes,
            "parent_flock_id_male": self.parent_flock_id_male,
            "parent_flock_id_female": self.parent_flock_id_female,
        }
