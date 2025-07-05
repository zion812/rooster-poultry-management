from farm_management.repositories import FarmRepository, FlockRepository, TrackingRepository
from farm_management.utils import export_to_csv
from farm_management.models.health_record import HealthRecord, DiseaseIncidentRecord, VaccinationRecord, MortalityRecord # For fieldnames

# Instantiate repositories - in a real app, these would likely be passed or accessed via a context
# For CLI simplicity, we create instances here. This means they will load data from files if they exist.
farm_repo = FarmRepository()
flock_repo = FlockRepository()
tracking_repo = TrackingRepository()

# Define fieldnames for each export type
# These should match keys in the dicts returned by model.to_dict() or custom dicts
FARM_FIELDNAMES = ["farm_id", "name", "location", "owner", "capacity", "established_date", "notes", "num_flocks", "flock_ids_concatenated"]
FLOCK_FIELDNAMES = ["flock_id", "farm_id", "breed", "acquisition_date", "source_supplier",
                    "initial_count", "current_count", "age_days", "age_group",
                    "parent_flock_id_male", "parent_flock_id_female", "notes"]

# For Health Records, it's more complex due to subclasses.
# One option is a generic export with common fields, another is to export each type separately,
# or a combined export with many optional columns.
# Let's aim for a combined export with specific fields for each type.
# This requires careful construction of the data dicts before export.
HEALTH_RECORD_FIELDNAMES = [
    "record_id", "flock_id", "record_date", "record_type", "details", "veterinarian", "cost",
    # DiseaseIncidentRecord specific
    "disease_name", "symptoms", "treatment_administered", "affected_count",
    # VaccinationRecord specific
    "vaccine_name", "administered_by", "dosage", "vaccinated_count",
    # MortalityRecord specific
    "cause_of_death", "number_of_deaths", "post_mortem_findings"
]
PRODUCTION_RECORD_FIELDNAMES = ["record_id", "flock_id", "record_date", "total_eggs_laid",
                                "damaged_eggs", "marketable_eggs", "average_egg_weight_gm", "notes"]
FEED_CONSUMPTION_FIELDNAMES = ["record_id", "flock_id", "record_date", "feed_type",
                               "quantity_kg", "cost_per_kg", "total_cost", "notes"]
GROWTH_RECORD_FIELDNAMES = ["record_id", "flock_id", "record_date", "average_weight_grams",
                            "number_of_birds_weighed", "feed_conversion_ratio", "notes"]


def export_farms_cli():
    print("\nExporting all farms...")
    farms = farm_repo.get_all_farms()
    if not farms:
        print("No farms to export.")
        return

    farm_data = []
    for farm in farms:
        f_dict = farm.to_dict() # Uses the model's to_dict
        # Add concatenated flock_ids for easier viewing in CSV if farm.flock_ids is available
        # farm.to_dict() already includes num_flocks. We need actual IDs.
        # The FarmRepository's _save_farms method saves 'flock_ids'.
        # When loading, farm.flocks itself might not be populated with objects yet.
        # The farm_repo._farms[farm_id].flocks list should have objects if linking occurs.
        # For robustness, let's try to get flock_ids from farm.flocks if populated, else from farm.flock_ids if that was loaded separately.
        # The Farm model itself has farm.flocks as a list of Flock objects.
        f_dict["flock_ids_concatenated"] = ", ".join([flk.flock_id for flk in farm.flocks])
        farm_data.append(f_dict)

    export_to_csv(farm_data, "farms_export.csv", FARM_FIELDNAMES)

def export_flocks_cli():
    print("\nExporting all flocks...")
    flocks = flock_repo.get_all_flocks()
    if not flocks:
        print("No flocks to export.")
        return
    flock_data = [flock.to_dict() for flock in flocks]
    export_to_csv(flock_data, "flocks_export.csv", FLOCK_FIELDNAMES)

def export_health_records_cli():
    print("\nExporting all health records...")
    # This could be very large. Consider options to filter by flock or date range in a real app.
    health_records = tracking_repo.get_all_health_records()
    if not health_records:
        print("No health records to export.")
        return

    health_data = []
    for record in health_records:
        rec_dict = record.to_dict() # This should handle subclass-specific fields
        if isinstance(record, DiseaseIncidentRecord) and record.symptoms:
            # Convert list of enums to comma-separated string for CSV
            rec_dict["symptoms"] = ", ".join([s.value for s in record.symptoms])
        health_data.append(rec_dict)

    export_to_csv(health_data, "health_records_export.csv", HEALTH_RECORD_FIELDNAMES)

def export_production_records_cli():
    print("\nExporting all egg production records...")
    production_records = tracking_repo._production_records.values() # Accessing internal dict for all
    if not production_records:
        print("No egg production records to export.")
        return
    prod_data = [record.to_dict() for record in production_records]
    export_to_csv(prod_data, "egg_production_export.csv", PRODUCTION_RECORD_FIELDNAMES)

def export_feed_records_cli():
    print("\nExporting all feed consumption records...")
    feed_records = tracking_repo._feed_consumption_records.values() # Accessing internal dict
    if not feed_records:
        print("No feed consumption records to export.")
        return
    feed_data = [record.to_dict() for record in feed_records]
    export_to_csv(feed_data, "feed_consumption_export.csv", FEED_CONSUMPTION_FIELDNAMES)

def export_growth_records_cli():
    print("\nExporting all growth records...")
    growth_records = tracking_repo._growth_records.values() # Accessing internal dict
    if not growth_records:
        print("No growth records to export.")
        return
    growth_data = [record.to_dict() for record in growth_records]
    export_to_csv(growth_data, "growth_records_export.csv", GROWTH_RECORD_FIELDNAMES)


def data_export_main_cli():
    """Main menu for Data Export CLI."""
    while True:
        print("\n--- Data Export Menu ---")
        print("Choose data to export (CSV format to 'exports/' directory):")
        print("1. Export All Farms")
        print("2. Export All Flocks")
        print("3. Export All Health Records")
        print("4. Export All Egg Production Records")
        print("5. Export All Feed Consumption Records")
        print("6. Export All Growth Records")
        print("0. Back to Main Menu")

        choice = input("Enter your choice: ").strip()

        if choice == '1':
            export_farms_cli()
        elif choice == '2':
            export_flocks_cli()
        elif choice == '3':
            export_health_records_cli()
        elif choice == '4':
            export_production_records_cli()
        elif choice == '5':
            export_feed_records_cli()
        elif choice == '6':
            export_growth_records_cli()
        elif choice == '0':
            break
        else:
            print("Invalid choice. Please try again.")

if __name__ == '__main__':
    # For testing export_cli directly
    # Note: This will create dummy data if json files are not present due to repo instantiation.
    print("Running Data Export CLI directly for testing...")
    # Pre-add some data if repositories are empty to test export
    if not farm_repo.get_all_farms():
        farm1 = farm_repo.add_farm("Test Export Farm", "Exportville", "Exporter", 100)
        if farm1:
            flock1 = flock_repo.add_flock(farm1.farm_id, "Export Breed", date(2023,1,1), 50)
            farm_repo.add_flock_to_farm(farm1.farm_id, flock1) # Link flock to farm for farm export
            if flock1:
                from datetime import datetime
                from farm_management.models.health_record import DiseaseSymptoms
                tracking_repo.add_health_record(DiseaseIncidentRecord(
                    record_id="hr001", flock_id=flock1.flock_id, record_date=datetime.now(),
                    details="Test disease", disease_name="Testitis", symptoms=[DiseaseSymptoms.LETHARGY]
                ))
                tracking_repo.add_production_record(flock1.flock_id, date.today(), 40, 2)
                tracking_repo.add_feed_consumption_record(flock1.flock_id, date.today(), "Test Feed", 5, 0.5)
                tracking_repo.add_growth_record(flock1.flock_id, date.today(), 1500, 10, 1.8)


    data_export_main_cli()
