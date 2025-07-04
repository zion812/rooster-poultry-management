from datetime import datetime, date
from farm_management.repositories import TrackingRepository, FlockRepository
from farm_management.models.production_record import ProductionRecord, FeedConsumptionRecord
from farm_management.models import Flock # For type hinting

# Global repository instances
# tracking_repo is already instantiated in health_cli.py (if loaded) or can be new here.
# For simplicity, let's assume health_cli's instance is the one we want, or create a new one.
try:
    from .health_cli import tracking_repo
except (ImportError, AttributeError):
    print("Warning: production_cli creating new TrackingRepository instance.")
    tracking_repo = TrackingRepository()

# We don't directly modify flock from here, so FlockRepository isn't strictly needed
# unless we wanted to display flock details not passed in.

def _prompt_for_date(prompt_message: str, default_date_str: str) -> date:
    while True:
        date_str = input(f"{prompt_message} (YYYY-MM-DD) [{default_date_str}]: ") or default_date_str
        try:
            return date.fromisoformat(date_str)
        except ValueError:
            print("Invalid date format. Please use YYYY-MM-DD.")

def add_egg_production_cli(flock_id: str):
    print("\n--- Add Egg Production Record ---")
    record_date = _prompt_for_date("Enter record date", date.today().strftime('%Y-%m-%d'))

    while True:
        try:
            total_eggs_laid = int(input("Enter total eggs laid: "))
            if total_eggs_laid < 0: raise ValueError("Cannot be negative.")
            break
        except ValueError:
            print("Invalid number for total eggs.")

    while True:
        try:
            damaged_eggs_str = input("Enter number of damaged eggs (default 0): ") or "0"
            damaged_eggs = int(damaged_eggs_str)
            if damaged_eggs < 0: raise ValueError("Cannot be negative.")
            if damaged_eggs > total_eggs_laid: raise ValueError("Cannot exceed total eggs.")
            break
        except ValueError:
            print("Invalid number for damaged eggs.")

    while True:
        try:
            avg_weight_str = input("Enter average egg weight in grams (optional, default 0.0): ") or "0.0"
            average_egg_weight_gm = float(avg_weight_str)
            if average_egg_weight_gm < 0: raise ValueError("Cannot be negative.")
            break
        except ValueError:
            print("Invalid number for average weight.")

    notes = input("Enter notes (optional): ")

    try:
        record = tracking_repo.add_production_record(
            flock_id=flock_id, record_date=record_date, total_eggs_laid=total_eggs_laid,
            damaged_eggs=damaged_eggs, average_egg_weight_gm=average_egg_weight_gm, notes=notes
        )
        print(f"Egg production record {record.record_id} added successfully.")
    except ValueError as e:
        print(f"Error adding egg production record: {e}")


def add_feed_consumption_cli(flock_id: str):
    print("\n--- Add Feed Consumption Record ---")
    record_date = _prompt_for_date("Enter record date", date.today().strftime('%Y-%m-%d'))

    feed_type = input("Enter feed type/brand: ")
    if not feed_type:
        print("Feed type is required. Aborting.")
        return

    while True:
        try:
            quantity_kg = float(input("Enter quantity consumed in KG: "))
            if quantity_kg <= 0: raise ValueError("Must be positive.")
            break
        except ValueError:
            print("Invalid number for quantity.")

    while True:
        try:
            cost_per_kg_str = input("Enter cost per KG (optional, default 0.0): ") or "0.0"
            cost_per_kg = float(cost_per_kg_str)
            if cost_per_kg < 0: raise ValueError("Cannot be negative.")
            break
        except ValueError:
            print("Invalid number for cost per KG.")

    notes = input("Enter notes (optional): ")

    try:
        record = tracking_repo.add_feed_consumption_record(
            flock_id=flock_id, record_date=record_date, feed_type=feed_type,
            quantity_kg=quantity_kg, cost_per_kg=cost_per_kg, notes=notes
        )
        print(f"Feed consumption record {record.record_id} added successfully.")
    except ValueError as e:
        print(f"Error adding feed consumption record: {e}")


def view_production_record_details_cli(record_id: str):
    record = tracking_repo.get_production_record_by_id(record_id)
    if not record:
        print(f"Egg production record {record_id} not found.")
        return
    print(f"\n--- Egg Production Record Details: {record.record_id} ---")
    print(f"Flock ID: {record.flock_id}")
    print(f"Date: {record.record_date.strftime('%Y-%m-%d')}")
    print(f"Total Eggs Laid: {record.total_eggs_laid}")
    print(f"Damaged Eggs: {record.damaged_eggs}")
    print(f"Marketable Eggs: {record.marketable_eggs}")
    print(f"Average Egg Weight: {record.average_egg_weight_gm:.2f} gm")
    print(f"Notes: {record.notes if record.notes else 'N/A'}")

def view_feed_record_details_cli(record_id: str):
    record = tracking_repo.get_feed_consumption_record_by_id(record_id)
    if not record:
        print(f"Feed consumption record {record_id} not found.")
        return
    print(f"\n--- Feed Consumption Record Details: {record.record_id} ---")
    print(f"Flock ID: {record.flock_id}")
    print(f"Date: {record.record_date.strftime('%Y-%m-%d')}")
    print(f"Feed Type: {record.feed_type}")
    print(f"Quantity Consumed: {record.quantity_kg:.2f} KG")
    print(f"Cost per KG: {record.cost_per_kg:.2f}")
    print(f"Total Cost: {record.total_cost:.2f}")
    print(f"Notes: {record.notes if record.notes else 'N/A'}")

# Basic edit/delete functions (can be expanded)
def edit_production_record_cli(record_id: str):
    # Simplified: Re-prompt for all data. A more advanced version would show current values.
    record = tracking_repo.get_production_record_by_id(record_id)
    if not record:
        print(f"Egg production record {record_id} not found.")
        return
    print(f"--- Editing Egg Production Record {record_id} (Date: {record.record_date}) ---")
    print("Enter new details. Leave blank to attempt to keep old value (basic implementation).")
    # This is a very naive update; ideally, it would use _prompt helper with existing data.
    try:
        new_total_eggs = input(f"New total eggs [{record.total_eggs_laid}]: ") or str(record.total_eggs_laid)
        new_damaged_eggs = input(f"New damaged eggs [{record.damaged_eggs}]: ") or str(record.damaged_eggs)
        new_avg_weight = input(f"New avg weight [{record.average_egg_weight_gm}]: ") or str(record.average_egg_weight_gm)
        new_notes = input(f"New notes [{record.notes}]: ") # No default needed if it can be empty

        update_data = {
            "total_eggs_laid": int(new_total_eggs),
            "damaged_eggs": int(new_damaged_eggs),
            "average_egg_weight_gm": float(new_avg_weight),
            "notes": new_notes if new_notes != record.notes else record.notes # keep old if blank
        }
        # Date change is more complex, skipping for this basic edit
        updated = tracking_repo.update_production_record(record_id, **update_data)
        if updated: print("Record updated.")
        else: print("Update failed.")
    except ValueError as e:
        print(f"Error during update: {e}")


def delete_production_record_cli(record_id: str):
    if tracking_repo.delete_production_record(record_id):
        print("Egg production record deleted.")
    else:
        print("Failed to delete egg production record.")

def edit_feed_record_cli(record_id: str):
    record = tracking_repo.get_feed_consumption_record_by_id(record_id)
    if not record:
        print(f"Feed record {record_id} not found.")
        return
    print(f"--- Editing Feed Record {record_id} (Type: {record.feed_type}, Date: {record.record_date}) ---")
    try:
        new_feed_type = input(f"New feed type [{record.feed_type}]: ") or record.feed_type
        new_quantity = input(f"New quantity KG [{record.quantity_kg}]: ") or str(record.quantity_kg)
        new_cost_kg = input(f"New cost/KG [{record.cost_per_kg}]: ") or str(record.cost_per_kg)
        new_notes = input(f"New notes [{record.notes}]: ")

        update_data = {
            "feed_type": new_feed_type,
            "quantity_kg": float(new_quantity),
            "cost_per_kg": float(new_cost_kg),
            "notes": new_notes if new_notes != record.notes else record.notes
        }
        updated = tracking_repo.update_feed_consumption_record(record_id, **update_data)
        if updated: print("Record updated.")
        else: print("Update failed.")
    except ValueError as e:
        print(f"Error during update: {e}")

def delete_feed_record_cli(record_id: str):
    if tracking_repo.delete_feed_consumption_record(record_id):
        print("Feed consumption record deleted.")
    else:
        print("Failed to delete feed consumption record.")


def manage_production_records_cli(flock_id: str, flock_instance: Flock):
    """CLI: ProductionTrackingScreen - Manage production records for a specific flock."""
    if not flock_instance:
        print(f"Flock {flock_id} not found.")
        return

    while True:
        print(f"\n--- Production Tracking for Flock: {flock_instance.flock_id} (Breed: {flock_instance.breed}) ---")

        egg_records = tracking_repo.get_production_records_for_flock(flock_id)
        feed_records = tracking_repo.get_feed_consumption_records_for_flock(flock_id)

        print("\nRecent Egg Production Records (Max 5 shown):")
        if not egg_records:
            print("  No egg production records found.")
        else:
            for i, rec in enumerate(egg_records[:5]):
                print(f"  {i+1}. ID: {rec.record_id}, Date: {rec.record_date}, Eggs: {rec.total_eggs_laid}, Marketable: {rec.marketable_eggs}")

        print("\nRecent Feed Consumption Records (Max 5 shown):")
        if not feed_records:
            print("  No feed consumption records found.")
        else:
            for i, rec in enumerate(feed_records[:5]):
                print(f"  {i+1}. ID: {rec.record_id}, Date: {rec.record_date}, Type: {rec.feed_type}, Qty: {rec.quantity_kg}kg")

        print("\nProduction Record Options:")
        print("  (AE) Add Egg Production Record")
        print("  (AF) Add Feed Consumption Record")
        print("  (VE) View/Edit/Delete Egg Record (by number from list or ID)")
        print("  (VF) View/Edit/Delete Feed Record (by number from list or ID)")
        print("  (LAE) List All Egg Records")
        print("  (LAF) List All Feed Records")
        print("  (PJP) Production Projection (Conceptual)") # New Option
        print("  (B)ack to Flock Actions")

        choice = input("Enter your choice: ").strip().lower()

        if choice == 'b':
            break
        elif choice == 'ae':
            add_egg_production_cli(flock_id)
        elif choice == 'af':
            add_feed_consumption_cli(flock_id)
        elif choice == 've':
            act_on_specific_record_cli(flock_id, "egg", egg_records)
        elif choice == 'vf':
            act_on_specific_record_cli(flock_id, "feed", feed_records)
        elif choice == 'lae':
            display_all_records(flock_id, "egg")
        elif choice == 'laf':
            display_all_records(flock_id, "feed")
        elif choice == 'pjp':
            from .analytics_cli import view_production_projection_cli # Import here
            view_production_projection_cli(flock_id)
        else:
            print("Invalid choice.")

def display_all_records(flock_id: str, record_type: str):
    if record_type == "egg":
        records = tracking_repo.get_production_records_for_flock(flock_id)
        print(f"\n--- All Egg Production Records for Flock {flock_id} ---")
        if not records: print("No records found.")
        for i, rec in enumerate(records):
            print(f"  {i+1}. ID: {rec.record_id}, Date: {rec.record_date}, Eggs: {rec.total_eggs_laid}, Marketable: {rec.marketable_eggs}")
    elif record_type == "feed":
        records = tracking_repo.get_feed_consumption_records_for_flock(flock_id)
        print(f"\n--- All Feed Consumption Records for Flock {flock_id} ---")
        if not records: print("No records found.")
        for i, rec in enumerate(records):
            print(f"  {i+1}. ID: {rec.record_id}, Date: {rec.record_date}, Type: {rec.feed_type}, Qty: {rec.quantity_kg}kg, Cost: {rec.total_cost:.2f}")
    input("\nPress Enter to continue...")


def act_on_specific_record_cli(flock_id: str, record_type: str, displayed_records: list):
    """ Helper to view/edit/delete a specific production or feed record. """
    id_or_num = input(f"Enter {record_type} record number from list (if shown) or full ID: ").strip()
    record_id_to_act = None

    if id_or_num.isdigit() and displayed_records:
        try:
            idx = int(id_or_num) - 1
            if 0 <= idx < len(displayed_records):
                record_id_to_act = displayed_records[idx].record_id
            else:
                print("Invalid number from list.")
                return
        except ValueError:
            print("Invalid input for number.")
            return
    elif len(id_or_num) > 5: # Assume it's an ID
        record_id_to_act = id_or_num
    else:
        print("Invalid input. Please enter a number from the list or a full ID.")
        return

    # Fetch the record to confirm it exists and matches type
    if record_type == "egg":
        record = tracking_repo.get_production_record_by_id(record_id_to_act)
        if not record or record.flock_id != flock_id:
            print(f"Egg record {record_id_to_act} not found for this flock.")
            return
        view_production_record_details_cli(record_id_to_act)
        sub_choice = input("Actions: (E)dit, (D)elete, (B)ack: ").strip().lower()
        if sub_choice == 'e': edit_production_record_cli(record_id_to_act)
        elif sub_choice == 'd': delete_production_record_cli(record_id_to_act)
    elif record_type == "feed":
        record = tracking_repo.get_feed_consumption_record_by_id(record_id_to_act)
        if not record or record.flock_id != flock_id:
            print(f"Feed record {record_id_to_act} not found for this flock.")
            return
        view_feed_record_details_cli(record_id_to_act)
        sub_choice = input("Actions: (E)dit, (D)elete, (B)ack: ").strip().lower()
        if sub_choice == 'e': edit_feed_record_cli(record_id_to_act)
        elif sub_choice == 'd': delete_feed_record_cli(record_id_to_act)


if __name__ == '__main__':
    print("Production CLI Module - For testing individual functions if needed.")
    # For testing, we'd need a flock. Assume one exists or mock it.

    # This flock_repo is for testing standalone.
    # In the app, flock_cli passes its flock_repo instance.
    local_flock_repo = FlockRepository()
    try: # Try to use farm_repo from farm_cli for farm creation
        from .farm_cli import farm_repo as main_farm_repo
        if not main_farm_repo.get_all_farms():
             main_farm_repo.add_farm("prod-test-farm", "Test Loc", "Test Owner", 100)
        test_farm_id_for_prod = main_farm_repo.get_all_farms()[0].farm_id
    except ImportError:
        test_farm_id_for_prod = "default-farm-prod-test"


    test_flock_prod = local_flock_repo.add_flock(
        farm_id=test_farm_id_for_prod,
        breed="Test Production Breed",
        acquisition_date=date(2023,1,1),
        initial_count=100
    )
    print(f"Created test flock for production: {test_flock_prod.flock_id}")

    # Pre-populate some data
    tracking_repo.add_production_record(test_flock_prod.flock_id, date(2023,11,1), 80, 2)
    tracking_repo.add_feed_consumption_record(test_flock_prod.flock_id, date(2023,11,1), "Test Feed", 10, 0.5)

    manage_production_records_cli(test_flock_prod.flock_id, test_flock_prod)
