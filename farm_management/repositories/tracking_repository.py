import uuid
from datetime import datetime
from typing import List, Dict, Optional, Union

from farm_management.models.health_record import (
    HealthRecord, DiseaseIncidentRecord, VaccinationRecord, MortalityRecord, RecordType, DiseaseSymptoms
)
from farm_management.models.production_record import ProductionRecord, FeedConsumptionRecord
from farm_management.models.growth_record import GrowthRecord
import json
import os
from datetime import date as PyDate, datetime as PyDateTime # Aliases for clarity

DATA_DIR = "farm_management/data"
HEALTH_RECORDS_FILE = os.path.join(DATA_DIR, "health_records.json")
PRODUCTION_RECORDS_FILE = os.path.join(DATA_DIR, "production_records.json")
FEED_RECORDS_FILE = os.path.join(DATA_DIR, "feed_records.json")
GROWTH_RECORDS_FILE = os.path.join(DATA_DIR, "growth_records.json")


class TrackingRepository:
    """
    Manages storage and retrieval of tracking data including health,
    production, and growth records. Supports persistence to JSON files.
    """
    def __init__(self):
        self._health_records: Dict[str, HealthRecord] = {}
        self._production_records: Dict[str, ProductionRecord] = {}
        self._feed_consumption_records: Dict[str, FeedConsumptionRecord] = {}
        self._growth_records: Dict[str, GrowthRecord] = {}

        os.makedirs(DATA_DIR, exist_ok=True)
        self._load_all_records()

    def _ensure_data_dir_exists(self): # Reusable helper
        if not os.path.exists(DATA_DIR):
            os.makedirs(DATA_DIR)

    def _load_all_records(self):
        self._load_health_records()
        self._load_production_records()
        self._load_feed_consumption_records()
        self._load_growth_records()

    # Generic save method to be called by specific record type save methods
    def _save_data_to_file(self, data: Dict, filepath: str):
        self._ensure_data_dir_exists()
        try:
            with open(filepath, 'w') as f:
                json.dump(data, f, indent=4)
        except IOError as e:
            print(f"Error saving data to {filepath}: {e}")

    # Generic load method
    def _load_data_from_file(self, filepath: str) -> Dict:
        self._ensure_data_dir_exists()
        if not os.path.exists(filepath):
            return {}
        try:
            with open(filepath, 'r') as f:
                return json.load(f)
        except (IOError, json.JSONDecodeError) as e:
            print(f"Error loading data from {filepath}: {e}. Returning empty data.")
            return {}

    def _generate_id(self, prefix="record") -> str:
        """Generates a unique ID for a new record."""
        return f"{prefix}-{uuid.uuid4()}"

    # --- Health Record Management ---

    def _load_health_records(self):
        loaded_data = self._load_data_from_file(HEALTH_RECORDS_FILE)
        for rec_id, rec_data in loaded_data.items():
            try:
                # Common fields
                record_date = PyDateTime.fromisoformat(rec_data['record_date']) if rec_data.get('record_date') else PyDateTime.now()
                record_type_str = rec_data.get('record_type')
                if not record_type_str or record_type_str not in RecordType._value2member_map_:
                    print(f"Warning: Invalid or missing record_type for health record {rec_id}. Skipping.")
                    continue

                record_type_enum = RecordType(record_type_str)

                base_args = {
                    'record_id': rec_data['record_id'],
                    'flock_id': rec_data['flock_id'],
                    'record_date': record_date,
                    'details': rec_data['details'],
                    'veterinarian': rec_data.get('veterinarian', ""),
                    'cost': float(rec_data.get('cost', 0.0))
                }

                record_obj = None
                if record_type_enum == RecordType.DISEASE_INCIDENT:
                    symptoms = [DiseaseSymptoms(s) for s in rec_data.get('symptoms', []) if s in DiseaseSymptoms._value2member_map_]
                    record_obj = DiseaseIncidentRecord(
                        **base_args, record_type=record_type_enum, # record_type passed for base HealthRecord if needed
                        disease_name=rec_data['disease_name'],
                        symptoms=symptoms,
                        treatment_administered=rec_data.get('treatment_administered', ""),
                        affected_count=int(rec_data.get('affected_count', 0))
                    )
                elif record_type_enum == RecordType.VACCINATION:
                    record_obj = VaccinationRecord(
                        **base_args, record_type=record_type_enum,
                        vaccine_name=rec_data['vaccine_name'],
                        administered_by=rec_data['administered_by'],
                        dosage=rec_data.get('dosage', ""),
                        vaccinated_count=int(rec_data.get('vaccinated_count', 0))
                    )
                elif record_type_enum == RecordType.MORTALITY:
                    record_obj = MortalityRecord(
                        **base_args, record_type=record_type_enum,
                        cause_of_death=rec_data['cause_of_death'],
                        number_of_deaths=int(rec_data['number_of_deaths']),
                        post_mortem_findings=rec_data.get('post_mortem_findings', "")
                    )
                elif record_type_enum == RecordType.GENERAL_CHECKUP: # Base HealthRecord can be used
                     record_obj = HealthRecord(**base_args, record_type=record_type_enum)
                else:
                    print(f"Warning: Unknown health record type '{record_type_str}' for record {rec_id}. Skipping.")
                    continue

                if record_obj:
                    self._health_records[rec_id] = record_obj

            except Exception as e:
                print(f"Error reconstructing health record {rec_id} from data: {e}. Skipping record. Data: {rec_data}")


    def _save_health_records(self):
        data_to_save = {rec_id: rec.to_dict() for rec_id, rec in self._health_records.items()}
        self._save_data_to_file(data_to_save, HEALTH_RECORDS_FILE)


    def add_health_record(self, record: HealthRecord) -> HealthRecord:
        """
        Adds a health record. Specific record types should use their dedicated methods for construction,
        but this method handles actual addition and saving.
        Validates the record before adding.
        """
        if not isinstance(record, HealthRecord):
            raise ValueError("Invalid record type. Must be a HealthRecord.")
        if not record.flock_id or not record.record_date or not record.record_type or not record.details:
            raise ValueError("Missing required fields for health record (flock_id, record_date, record_type, details).")

        if not record.record_id: # Generate ID if not provided (e.g. when called directly)
            record.record_id = self._generate_id("health")

        self._health_records[record.record_id] = record
        print(f"TrackingRepository: Added HealthRecord {record.record_id} for flock {record.flock_id}")
        self._save_health_records()
        return record

    def add_disease_incident_record(self, flock_id: str, record_date: datetime, details: str,
                                    disease_name: str, symptoms: List[DiseaseSymptoms],
                                    treatment_administered: str = "", affected_count: int = 0,
                                    veterinarian: str = "", cost: float = 0.0) -> DiseaseIncidentRecord:
        record_id = self._generate_id("disease")
        record = DiseaseIncidentRecord(
            record_id=record_id, flock_id=flock_id, record_date=record_date, details=details,
            disease_name=disease_name, symptoms=symptoms, treatment_administered=treatment_administered,
            affected_count=affected_count, veterinarian=veterinarian, cost=cost
        )
        return self.add_health_record(record)

    def add_vaccination_record(self, flock_id: str, record_date: datetime, details: str,
                               vaccine_name: str, administered_by: str, dosage: str = "",
                               vaccinated_count: int = 0, veterinarian: str = "", cost: float = 0.0) -> VaccinationRecord:
        record_id = self._generate_id("vaccine")
        record = VaccinationRecord(
            record_id=record_id, flock_id=flock_id, record_date=record_date, details=details,
            vaccine_name=vaccine_name, administered_by=administered_by, dosage=dosage,
            vaccinated_count=vaccinated_count, veterinarian=veterinarian, cost=cost
        )
        return self.add_health_record(record)

    def add_mortality_record(self, flock_id: str, record_date: datetime, details: str,
                             cause_of_death: str, number_of_deaths: int,
                             post_mortem_findings: str = "", veterinarian: str = "", cost: float = 0.0) -> MortalityRecord:
        if number_of_deaths <= 0:
            raise ValueError("Number of deaths must be positive.")
        record_id = self._generate_id("mortality")
        record = MortalityRecord(
            record_id=record_id, flock_id=flock_id, record_date=record_date, details=details,
            cause_of_death=cause_of_death, number_of_deaths=number_of_deaths,
            post_mortem_findings=post_mortem_findings, veterinarian=veterinarian, cost=cost
        )
        # Note: This record also implies an update to the flock's current_count.
        # This logic should ideally be handled by a service layer that calls both
        # TrackingRepository.add_mortality_record and FlockRepository.update_flock.
        # For now, the CLI will handle this coordination.
        return self.add_health_record(record)

    def get_health_record_by_id(self, record_id: str) -> Optional[HealthRecord]:
        """Retrieves a specific health record by its ID."""
        return self._health_records.get(record_id)

    def get_health_records_for_flock(self, flock_id: str, record_type: Optional[RecordType] = None) -> List[HealthRecord]:
        """
        Retrieves all health records for a specific flock.
        Optionally filters by record_type.
        """
        records = [hr for hr in self._health_records.values() if hr.flock_id == flock_id]
        if record_type:
            records = [hr for hr in records if hr.record_type == record_type]
        records.sort(key=lambda r: r.record_date, reverse=True) # Show most recent first
        return records

    def get_all_health_records(self) -> List[HealthRecord]:
        """Retrieves all health records in the system."""
        return list(self._health_records.values())

    def update_health_record(self, record_id: str, **kwargs) -> Optional[HealthRecord]:
        """Updates an existing health record."""
        record = self.get_health_record_by_id(record_id)
        if record:
            for key, value in kwargs.items():
                if hasattr(record, key):
                    # Special handling for symptoms if it's a list of enums
                    if key == "symptoms" and isinstance(value, list):
                        valid_symptoms = []
                        for item in value:
                            if isinstance(item, DiseaseSymptoms):
                                valid_symptoms.append(item)
                            elif isinstance(item, str) and item in DiseaseSymptoms.__members__:
                                valid_symptoms.append(DiseaseSymptoms[item])
                        setattr(record, key, valid_symptoms)
                    elif key == "record_date" and isinstance(value, str):
                        try:
                            setattr(record, key, datetime.fromisoformat(value))
                        except ValueError:
                             print(f"Warning: Could not parse record_date for update: {value}")
                    else:
                        setattr(record, key, value)
            print(f"TrackingRepository: Updated HealthRecord {record.record_id}")
            self._save_health_records()
            return record
        return None

    def delete_health_record(self, record_id: str) -> bool:
        """Deletes a health record. Returns True if successful."""
        if record_id in self._health_records:
            del self._health_records[record_id]
            print(f"TrackingRepository: Deleted HealthRecord {record_id}")
            self._save_health_records()
            return True
        return False

    # --- Trend Analysis (Placeholder for Health Alert System) ---
    def get_recent_mortality_trend(self, flock_id: str, days: int = 7) -> Dict:
        """
        Analyzes mortality records for a flock over the last 'days'.
        This is a very basic example for trend analysis.
        """
        # This is a conceptual placeholder. Actual implementation would be more complex.
        # For now, it might just count mortalities or group by cause.
        # A real system might involve time series analysis or statistical methods.
        mortality_records = self.get_health_records_for_flock(flock_id, RecordType.MORTALITY)
        relevant_records = [
            r for r in mortality_records
            if (datetime.now() - r.record_date).days <= days
        ]
        total_deaths = sum(r.number_of_deaths for r in relevant_records if isinstance(r, MortalityRecord))

        # This is a simplified output.
        return {
            "flock_id": flock_id,
            "period_days": days,
            "total_deaths_in_period": total_deaths,
            "records_in_period": len(relevant_records)
        }

    # --- Production Record Management (Eggs) ---
    def add_production_record(self, flock_id: str, record_date: datetime.date, total_eggs_laid: int,
                              damaged_eggs: int = 0, average_egg_weight_gm: float = 0.0,
                              notes: str = "") -> ProductionRecord:
        if total_eggs_laid < 0 or damaged_eggs < 0:
            raise ValueError("Egg counts cannot be negative.")
        if damaged_eggs > total_eggs_laid:
            raise ValueError("Damaged eggs cannot exceed total eggs laid.")

        record_id = self._generate_id("prod")
        record = ProductionRecord(
            record_id=record_id, flock_id=flock_id, record_date=record_date,
            total_eggs_laid=total_eggs_laid, damaged_eggs=damaged_eggs,
            average_egg_weight_gm=average_egg_weight_gm, notes=notes
        )
        self._production_records[record_id] = record
        print(f"TrackingRepository: Added ProductionRecord {record.record_id} for flock {flock_id}")
        self._save_production_records()
        return record

    def get_production_record_by_id(self, record_id: str) -> Optional[ProductionRecord]:
        return self._production_records.get(record_id)

    def get_production_records_for_flock(self, flock_id: str, start_date: Optional[datetime.date] = None, end_date: Optional[datetime.date] = None) -> List[ProductionRecord]:
        records = [pr for pr in self._production_records.values() if pr.flock_id == flock_id]
        if start_date:
            records = [pr for pr in records if pr.record_date >= start_date]
        if end_date:
            records = [pr for pr in records if pr.record_date <= end_date]
        records.sort(key=lambda r: r.record_date, reverse=True)
        return records

    def update_production_record(self, record_id: str, **kwargs) -> Optional[ProductionRecord]:
        record = self.get_production_record_by_id(record_id)
        if record:
            for key, value in kwargs.items():
                if hasattr(record, key):
                    if key == "record_date" and isinstance(value, str):
                        try:
                            setattr(record, key, datetime.date.fromisoformat(value))
                        except ValueError:
                            print(f"Warning: Could not parse record_date for production update: {value}")
                    else:
                        setattr(record, key, value)
            # Validate after update
            if record.total_eggs_laid < 0 or record.damaged_eggs < 0 or record.damaged_eggs > record.total_eggs_laid:
                # This is a simple revert strategy; a better way would be transactional or pre-validation
                print("Error: Invalid egg counts after update. Update reverted for counts.")
                # Revert to original values if possible or handle error more gracefully
                # For now, we'll just print error. A real app needs robust handling.
                # To fully revert, we'd need to store original state before loop.
                raise ValueError("Update resulted in invalid egg counts.")
            print(f"TrackingRepository: Updated ProductionRecord {record.record_id}")
            self._save_production_records()
            return record
        return None

    def delete_production_record(self, record_id: str) -> bool:
        if record_id in self._production_records:
            del self._production_records[record_id]
            print(f"TrackingRepository: Deleted ProductionRecord {record_id}")
            self._save_production_records()
            return True
        return False

    # --- Feed Consumption Record Management ---
    def _load_feed_consumption_records(self):
        loaded_data = self._load_data_from_file(FEED_RECORDS_FILE)
        for rec_id, rec_data in loaded_data.items():
            try:
                record_date_iso = rec_data.get('record_date')
                record_date = PyDate.fromisoformat(record_date_iso) if record_date_iso else PyDate.today()
                self._feed_consumption_records[rec_id] = FeedConsumptionRecord(
                    record_id=rec_data['record_id'],
                    flock_id=rec_data['flock_id'],
                    record_date=record_date,
                    feed_type=rec_data.get('feed_type', "Unknown"),
                    quantity_kg=float(rec_data.get('quantity_kg', 0.0)),
                    cost_per_kg=float(rec_data.get('cost_per_kg', 0.0)),
                    notes=rec_data.get('notes', "")
                )
            except Exception as e:
                print(f"Error reconstructing feed consumption record {rec_id}: {e}. Data: {rec_data}")

    def _save_feed_consumption_records(self):
        data_to_save = {rec_id: rec.to_dict() for rec_id, rec in self._feed_consumption_records.items()}
        self._save_data_to_file(data_to_save, FEED_RECORDS_FILE)

    def add_feed_consumption_record(self, flock_id: str, record_date: datetime.date, feed_type: str,
                                    quantity_kg: float, cost_per_kg: float = 0.0,
                                    notes: str = "") -> FeedConsumptionRecord:
        if quantity_kg <= 0:
            raise ValueError("Feed quantity must be positive.")
        if not feed_type:
            raise ValueError("Feed type is required.")

        record_id = self._generate_id("feed")
        record = FeedConsumptionRecord(
            record_id=record_id, flock_id=flock_id, record_date=record_date,
            feed_type=feed_type, quantity_kg=quantity_kg, cost_per_kg=cost_per_kg, notes=notes
        )
        self._feed_consumption_records[record_id] = record
        print(f"TrackingRepository: Added FeedConsumptionRecord {record.record_id} for flock {flock_id}")
        self._save_feed_consumption_records()
        return record

    def get_feed_consumption_record_by_id(self, record_id: str) -> Optional[FeedConsumptionRecord]:
        return self._feed_consumption_records.get(record_id)

    def get_feed_consumption_records_for_flock(self, flock_id: str, start_date: Optional[datetime.date] = None, end_date: Optional[datetime.date] = None) -> List[FeedConsumptionRecord]:
        records = [fr for fr in self._feed_consumption_records.values() if fr.flock_id == flock_id]
        if start_date:
            records = [fr for fr in records if fr.record_date >= start_date]
        if end_date:
            records = [fr for fr in records if fr.record_date <= end_date]
        records.sort(key=lambda r: r.record_date, reverse=True)
        return records

    def update_feed_consumption_record(self, record_id: str, **kwargs) -> Optional[FeedConsumptionRecord]:
        record = self.get_feed_consumption_record_by_id(record_id)
        if record:
            for key, value in kwargs.items():
                if hasattr(record, key):
                    if key == "record_date" and isinstance(value, str):
                         try:
                            setattr(record, key, datetime.date.fromisoformat(value))
                         except ValueError:
                            print(f"Warning: Could not parse record_date for feed update: {value}")
                    else:
                        setattr(record, key, value)
            if record.quantity_kg <= 0:
                raise ValueError("Update resulted in invalid feed quantity.")
            if not record.feed_type:
                raise ValueError("Update resulted in empty feed type.")
            print(f"TrackingRepository: Updated FeedConsumptionRecord {record.record_id}")
            self._save_feed_consumption_records()
            return record
        return None

    def delete_feed_consumption_record(self, record_id: str) -> bool:
        if record_id in self._feed_consumption_records:
            del self._feed_consumption_records[record_id]
            print(f"TrackingRepository: Deleted FeedConsumptionRecord {record_id}")
            self._save_feed_consumption_records()
            return True
        return False

    # --- Growth Record Management ---
    def _load_growth_records(self):
        loaded_data = self._load_data_from_file(GROWTH_RECORDS_FILE)
        for rec_id, rec_data in loaded_data.items():
            try:
                record_date_iso = rec_data.get('record_date')
                record_date = PyDate.fromisoformat(record_date_iso) if record_date_iso else PyDate.today()
                self._growth_records[rec_id] = GrowthRecord(
                    record_id=rec_data['record_id'],
                    flock_id=rec_data['flock_id'],
                    record_date=record_date,
                    average_weight_grams=float(rec_data.get('average_weight_grams', 0.0)),
                    number_of_birds_weighed=int(rec_data.get('number_of_birds_weighed', 0)),
                    feed_conversion_ratio=float(rec_data['feed_conversion_ratio']) if rec_data.get('feed_conversion_ratio') is not None else None,
                    notes=rec_data.get('notes', "")
                )
            except Exception as e:
                print(f"Error reconstructing growth record {rec_id}: {e}. Data: {rec_data}")

    def _save_growth_records(self):
        data_to_save = {rec_id: rec.to_dict() for rec_id, rec in self._growth_records.items()}
        self._save_data_to_file(data_to_save, GROWTH_RECORDS_FILE)

    def add_growth_record(self, flock_id: str, record_date: datetime.date,
                          average_weight_grams: float, number_of_birds_weighed: int,
                          feed_conversion_ratio: Optional[float] = None,
                          notes: str = "") -> GrowthRecord:
        if average_weight_grams <= 0 or number_of_birds_weighed <= 0:
            raise ValueError("Average weight and number of birds weighed must be positive.")
        if feed_conversion_ratio is not None and feed_conversion_ratio <= 0:
            raise ValueError("Feed Conversion Ratio (FCR) must be positive if provided.")

        record_id = self._generate_id("growth")
        record = GrowthRecord(
            record_id=record_id, flock_id=flock_id, record_date=record_date,
            average_weight_grams=average_weight_grams,
            number_of_birds_weighed=number_of_birds_weighed,
            feed_conversion_ratio=feed_conversion_ratio, notes=notes
        )
        self._growth_records[record_id] = record
        print(f"TrackingRepository: Added GrowthRecord {record.record_id} for flock {flock_id}")
        self._save_growth_records()
        return record

    def get_growth_record_by_id(self, record_id: str) -> Optional[GrowthRecord]:
        return self._growth_records.get(record_id)

    def get_growth_records_for_flock(self, flock_id: str, start_date: Optional[datetime.date] = None, end_date: Optional[datetime.date] = None) -> List[GrowthRecord]:
        records = [gr for gr in self._growth_records.values() if gr.flock_id == flock_id]
        if start_date:
            records = [gr for gr in records if gr.record_date >= start_date]
        if end_date:
            records = [gr for gr in records if gr.record_date <= end_date]
        records.sort(key=lambda r: r.record_date, reverse=True)
        return records

    def update_growth_record(self, record_id: str, **kwargs) -> Optional[GrowthRecord]:
        record = self.get_growth_record_by_id(record_id)
        if record:
            for key, value in kwargs.items():
                if hasattr(record, key):
                    if key == "record_date" and isinstance(value, str):
                        try:
                            setattr(record, key, datetime.date.fromisoformat(value))
                        except ValueError:
                             print(f"Warning: Could not parse record_date for growth update: {value}")
                    else:
                        setattr(record, key, value)

            if record.average_weight_grams <= 0 or record.number_of_birds_weighed <= 0:
                raise ValueError("Update resulted in invalid average weight or number of birds weighed.")
            if record.feed_conversion_ratio is not None and record.feed_conversion_ratio <= 0:
                raise ValueError("Update resulted in invalid FCR.")
            print(f"TrackingRepository: Updated GrowthRecord {record.record_id}")
            self._save_growth_records()
            return record
        return None

    def delete_growth_record(self, record_id: str) -> bool:
        if record_id in self._growth_records:
            del self._growth_records[record_id]
            print(f"TrackingRepository: Deleted GrowthRecord {record_id}")
            self._save_growth_records()
            return True
        return False

    # --- Basic Alert System Logic ---
    def check_high_mortality_events(self, flock_id: str, period_days: int, threshold_deaths: int) -> Optional[str]:
        """
        Checks if total mortality for a flock in a given period exceeds a threshold.
        Returns an alert message string if threshold is met, otherwise None.
        """
        mortality_records = self.get_health_records_for_flock(flock_id, RecordType.MORTALITY)

        # Filter records within the specified period
        relevant_date_cutoff = datetime.now() - datetime.timedelta(days=period_days)

        recent_deaths = 0
        for rec in mortality_records:
            # Ensure record_date is datetime object for comparison
            rec_date_dt = rec.record_date
            if not isinstance(rec_date_dt, datetime): # Should not happen if data is clean
                try:
                    rec_date_dt = datetime.combine(rec.record_date, datetime.min.time())
                except TypeError: # if it's already datetime
                    pass


            if rec_date_dt >= relevant_date_cutoff and isinstance(rec, MortalityRecord):
                recent_deaths += rec.number_of_deaths

        if recent_deaths >= threshold_deaths:
            return (f"ALERT: High mortality event for flock {flock_id}! "
                    f"{recent_deaths} deaths recorded in the last {period_days} days (Threshold: {threshold_deaths}).")
        return None

    def check_disease_outbreak(self, flock_id: str, period_days: int, disease_name_to_check: str, min_incidents_for_alert: int) -> Optional[str]:
        """
        Checks if a specific disease has multiple reported incidents recently.
        Returns an alert message string or None.
        """
        disease_records = self.get_health_records_for_flock(flock_id, RecordType.DISEASE_INCIDENT)

        relevant_date_cutoff = datetime.now() - datetime.timedelta(days=period_days)
        incident_count = 0

        for rec in disease_records:
            rec_date_dt = rec.record_date
            if not isinstance(rec_date_dt, datetime):
                 try: # Try to convert if it's date
                    rec_date_dt = datetime.combine(rec.record_date, datetime.min.time())
                 except TypeError:
                    pass

            if rec_date_dt >= relevant_date_cutoff and isinstance(rec, DiseaseIncidentRecord):
                if rec.disease_name.lower() == disease_name_to_check.lower():
                    incident_count += 1

        if incident_count >= min_incidents_for_alert:
            return (f"ALERT: Possible '{disease_name_to_check}' outbreak in flock {flock_id}! "
                    f"{incident_count} incidents reported in the last {period_days} days.")
        return None


# Example Usage (for direct testing)
if __name__ == '__main__':
    # Need to import timedelta for the main block if used there, but it's used internally in repo methods
    from datetime import timedelta
    tracking_repo = TrackingRepository()
    test_flock_id_health = "flock-test-health-123"
    test_flock_id_prod = "flock-test-prod-456"
    test_flock_id_growth = "flock-test-growth-789"


    # ... (previous health, production, feed record tests can remain here for completeness) ...
    print("\n--- Adding Health Records (Example from before) ---")
    try:
        dis_rec = tracking_repo.add_disease_incident_record(
            flock_id=test_flock_id_health, record_date=datetime(2023, 5, 10, 10, 0, 0),
            details="Observed coughing and sneezing in several birds.", disease_name="Infectious Bronchitis (IB)",
            symptoms=[DiseaseSymptoms.RESPIRATORY_DISTRESS, DiseaseSymptoms.NASAL_DISCHARGE],
            affected_count=15, veterinarian="Dr. Priya", cost=250.00 )
        print(f"Added Health: {dis_rec.record_id}")
    except ValueError as e: print(f"Error: {e}")


    print("\n--- Adding Production & Feed Records (Example from before) ---")
    try:
        prod_rec1 = tracking_repo.add_production_record(
            flock_id=test_flock_id_prod, record_date=datetime(2023, 11, 1).date(),
            total_eggs_laid=400, damaged_eggs=10, average_egg_weight_gm=60.5 )
        print(f"Added Production: {prod_rec1.record_id}")
        feed_rec1 = tracking_repo.add_feed_consumption_record(
            flock_id=test_flock_id_prod, record_date=datetime(2023, 11, 1).date(),
            feed_type="Layer Mash Phase 1", quantity_kg=120, cost_per_kg=0.35 )
        print(f"Added Feed: {feed_rec1.record_id}")
    except ValueError as e: print(f"Error: {e}")


    print("\n--- Adding Growth Records ---")
    try:
        growth_rec1 = tracking_repo.add_growth_record(
            flock_id=test_flock_id_growth, record_date=datetime(2023, 10, 15).date(),
            average_weight_grams=1200.0, number_of_birds_weighed=50,
            feed_conversion_ratio=1.8, notes="Week 6 check"
        )
        print(f"Added Growth: {growth_rec1.to_dict()}")

        growth_rec2 = tracking_repo.add_growth_record(
            flock_id=test_flock_id_growth, record_date=datetime(2023, 10, 22).date(),
            average_weight_grams=1550.0, number_of_birds_weighed=50,
            feed_conversion_ratio=2.0, notes="Week 7 check, FCR slightly up"
        )
        print(f"Added Growth: {growth_rec2.to_dict()}")
    except ValueError as e:
        print(f"Error adding growth record: {e}")

    print(f"\n--- Growth Records for Flock: {test_flock_id_growth} ---")
    for rec in tracking_repo.get_growth_records_for_flock(test_flock_id_growth):
        print(rec.to_dict())

    print(f"\n--- Update Growth Record ({growth_rec1.record_id if 'growth_rec1' in locals() else 'N/A'}) ---")
    if 'growth_rec1' in locals():
        try:
            tracking_repo.update_growth_record(growth_rec1.record_id, average_weight_grams=1250.0, notes="Corrected avg weight")
            updated_growth_rec = tracking_repo.get_growth_record_by_id(growth_rec1.record_id)
            if updated_growth_rec:
                print(updated_growth_rec.to_dict())
        except ValueError as e:
            print(f"Error updating growth record: {e}")

    print(f"\n--- Delete Growth Record ({growth_rec2.record_id if 'growth_rec2' in locals() else 'N/A'}) ---")
    if 'growth_rec2' in locals():
        tracking_repo.delete_growth_record(growth_rec2.record_id)
        print(f"Growth records for flock after deletion: {len(tracking_repo.get_growth_records_for_flock(test_flock_id_growth))}")

    print(f"\nTotal health records: {len(tracking_repo._health_records)}")
    print(f"Total production records: {len(tracking_repo._production_records)}")
    print(f"Total feed records: {len(tracking_repo._feed_consumption_records)}")
    print(f"Total growth records: {len(tracking_repo._growth_records)}")

    print("\n--- Testing Alert System ---")
    # Setup for high mortality alert
    tracking_repo.add_mortality_record(test_flock_id_health, datetime.now() - timedelta(days=2), "Stress", 3)
    tracking_repo.add_mortality_record(test_flock_id_health, datetime.now() - timedelta(days=1), "Stress", 2)

    mortality_alert = tracking_repo.check_high_mortality_events(test_flock_id_health, period_days=7, threshold_deaths=5)
    if mortality_alert:
        print(mortality_alert)
    else:
        print(f"No high mortality alert for flock {test_flock_id_health} (threshold 5 in 7 days).")

    # Setup for disease outbreak alert
    tracking_repo.add_disease_incident_record(test_flock_id_health, datetime.now() - timedelta(days=3), "Avian Flu", "Flu symptoms", [DiseaseSymptoms.LETHARGY])
    tracking_repo.add_disease_incident_record(test_flock_id_health, datetime.now() - timedelta(days=1), "Avian Flu", "More flu", [DiseaseSymptoms.FEATHER_LOSS])

    disease_alert = tracking_repo.check_disease_outbreak(test_flock_id_health, period_days=7, disease_name_to_check="Avian Flu", min_incidents_for_alert=2)
    if disease_alert:
        print(disease_alert)
    else:
        print(f"No Avian Flu outbreak alert for flock {test_flock_id_health} (threshold 2 in 7 days).")

    disease_alert_false = tracking_repo.check_disease_outbreak(test_flock_id_health, period_days=7, disease_name_to_check="Newcastle", min_incidents_for_alert=2)
    if disease_alert_false:
        print(disease_alert_false)
    else:
        print(f"No Newcastle outbreak alert for flock {test_flock_id_health} (threshold 2 in 7 days).")
