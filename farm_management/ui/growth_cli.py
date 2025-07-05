from datetime import datetime, date
from farm_management.repositories import TrackingRepository
from farm_management.models import Flock # For type hinting
from farm_management.models.growth_record import GrowthRecord

# Use the shared tracking_repo instance if available (e.g., from health_cli or production_cli)
try:
    from .health_cli import tracking_repo
except (ImportError, AttributeError):
    try:
        from .production_cli import tracking_repo
    except (ImportError, AttributeError):
        print("Warning: growth_cli creating new TrackingRepository instance.")
        tracking_repo = TrackingRepository()


def _prompt_for_date(prompt_message: str, default_date_str: str) -> date:
    while True:
        date_str = input(f"{prompt_message} (YYYY-MM-DD) [{default_date_str}]: ") or default_date_str
        try:
            return date.fromisoformat(date_str)
        except ValueError:
            print("Invalid date format. Please use YYYY-MM-DD.")

def add_growth_record_cli(flock_id: str):
    print("\n--- Add Growth Record ---")
    record_date = _prompt_for_date("Enter record date", date.today().strftime('%Y-%m-%d'))

    while True:
        try:
            avg_weight_grams = float(input("Enter average weight in grams: "))
            if avg_weight_grams <= 0: raise ValueError("Must be positive.")
            break
        except ValueError:
            print("Invalid input for average weight.")

    while True:
        try:
            num_birds_weighed = int(input("Enter number of birds weighed: "))
            if num_birds_weighed <= 0: raise ValueError("Must be positive.")
            break
        except ValueError:
            print("Invalid input for number of birds weighed.")

    fcr_str = input("Enter Feed Conversion Ratio (FCR) (optional, e.g., 1.75): ")
    feed_conversion_ratio = None
    if fcr_str:
        try:
            feed_conversion_ratio = float(fcr_str)
            if feed_conversion_ratio <=0:
                print("FCR must be positive. Ignoring value.")
                feed_conversion_ratio = None
        except ValueError:
            print("Invalid FCR value. It will be ignored.")

    notes = input("Enter notes (optional): ")

    try:
        record = tracking_repo.add_growth_record(
            flock_id=flock_id, record_date=record_date,
            average_weight_grams=avg_weight_grams,
            number_of_birds_weighed=num_birds_weighed,
            feed_conversion_ratio=feed_conversion_ratio, notes=notes
        )
        print(f"Growth record {record.record_id} added successfully.")
    except ValueError as e:
        print(f"Error adding growth record: {e}")


def view_growth_record_details_cli(record_id: str):
    record = tracking_repo.get_growth_record_by_id(record_id)
    if not record:
        print(f"Growth record {record_id} not found.")
        return
    print(f"\n--- Growth Record Details: {record.record_id} ---")
    print(f"Flock ID: {record.flock_id}")
    print(f"Date: {record.record_date.strftime('%Y-%m-%d')}")
    print(f"Average Weight: {record.average_weight_grams:.2f} grams")
    print(f"Number of Birds Weighed: {record.number_of_birds_weighed}")
    print(f"Feed Conversion Ratio (FCR): {record.feed_conversion_ratio if record.feed_conversion_ratio is not None else 'N/A'}")
    print(f"Notes: {record.notes if record.notes else 'N/A'}")


def edit_growth_record_cli(record_id: str):
    record = tracking_repo.get_growth_record_by_id(record_id)
    if not record:
        print(f"Growth record {record_id} not found.")
        return
    print(f"--- Editing Growth Record {record_id} (Date: {record.record_date}) ---")

    try:
        new_avg_weight = input(f"New average weight (grams) [{record.average_weight_grams}]: ") or str(record.average_weight_grams)
        new_num_weighed = input(f"New number of birds weighed [{record.number_of_birds_weighed}]: ") or str(record.number_of_birds_weighed)
        current_fcr = str(record.feed_conversion_ratio) if record.feed_conversion_ratio is not None else ""
        new_fcr = input(f"New FCR [{current_fcr}] (leave blank if N/A): ")

        new_notes = input(f"New notes [{record.notes}]: ") # No default needed if it can be empty

        update_data = {
            "average_weight_grams": float(new_avg_weight),
            "number_of_birds_weighed": int(new_num_weighed),
            "notes": new_notes if new_notes != record.notes else record.notes
        }
        if new_fcr: # Only update FCR if a value is provided
            update_data["feed_conversion_ratio"] = float(new_fcr)
        elif new_fcr == "" and record.feed_conversion_ratio is not None: # User wants to clear existing FCR
            update_data["feed_conversion_ratio"] = None


        updated = tracking_repo.update_growth_record(record_id, **update_data)
        if updated: print("Growth record updated successfully.")
        else: print("Growth record update failed.")
    except ValueError as e:
        print(f"Error during update: {e}")


def delete_growth_record_cli(record_id: str):
    if tracking_repo.delete_growth_record(record_id):
        print(f"Growth record {record_id} deleted successfully.")
    else:
        print(f"Failed to delete growth record {record_id}.")


def display_growth_analytics(flock_id: str):
    """Placeholder for displaying growth performance analytics."""
    print(f"\n--- Growth Analytics for Flock {flock_id} ---")
    records = tracking_repo.get_growth_records_for_flock(flock_id)
    if not records or len(records) < 2:
        print("Not enough data for detailed analytics (need at least 2 records).")
        if records:
             print(f"Latest Weight: {records[0].average_weight_grams}g on {records[0].record_date}")
        return

    # Sort by date to calculate gain
    records.sort(key=lambda r: r.record_date)

    print("Growth Trend (Weight in grams):")
    for i in range(len(records)):
        rec = records[i]
        gain_str = ""
        if i > 0:
            prev_rec = records[i-1]
            days_diff = (rec.record_date - prev_rec.record_date).days
            weight_gain = rec.average_weight_grams - prev_rec.average_weight_grams
            if days_diff > 0:
                daily_gain = weight_gain / days_diff
                gain_str = f" (Gain: {weight_gain:.0f}g in {days_diff}d, ~{daily_gain:.1f}g/day)"
            else:
                gain_str = f" (Gain: {weight_gain:.0f}g)"
        print(f"  - {rec.record_date}: {rec.average_weight_grams:.0f}g {gain_str}")
        if rec.feed_conversion_ratio is not None:
            print(f"    FCR: {rec.feed_conversion_ratio}")

    # Basic overall analytics
    first_rec = records[0]
    last_rec = records[-1]
    total_days = (last_rec.record_date - first_rec.record_date).days
    total_weight_gain = last_rec.average_weight_grams - first_rec.average_weight_grams

    if total_days > 0:
        avg_daily_gain = total_weight_gain / total_days
        print(f"\nOverall Average Daily Gain ({total_days} days): {avg_daily_gain:.2f} g/day")
    else:
        print("\nOverall gain cannot be calculated with single day data or no time difference.")

    avg_fcr = [r.feed_conversion_ratio for r in records if r.feed_conversion_ratio is not None]
    if avg_fcr:
        print(f"Average Recorded FCR: {sum(avg_fcr)/len(avg_fcr):.2f}")

    input("\nPress Enter to continue...")


def manage_growth_records_cli(flock_id: str, flock_instance: Flock):
    """CLI: GrowthMonitoringScreen - Manage growth records for a specific flock."""
    if not flock_instance:
        print(f"Flock {flock_id} not found.")
        return

    while True:
        print(f"\n--- Growth Monitoring for Flock: {flock_instance.flock_id} (Breed: {flock_instance.breed}) ---")

        records = tracking_repo.get_growth_records_for_flock(flock_id)

        print("\nRecent Growth Records (Max 5 shown):")
        if not records:
            print("  No growth records found.")
        else:
            for i, rec in enumerate(records[:5]):
                fcr_info = f", FCR: {rec.feed_conversion_ratio}" if rec.feed_conversion_ratio is not None else ""
                print(f"  {i+1}. ID: {rec.record_id}, Date: {rec.record_date}, Avg Wt: {rec.average_weight_grams:.0f}g{fcr_info}")

        print("\nGrowth Record Options:")
        print("  (A) Add Growth Record")
        print("  (V) View/Edit/Delete Record (by number from list or ID)")
        print("  (L) List All Growth Records")
        print("  (N) View Growth Analytics/Trend")
        print("  (F)eed Efficiency Insights (Conceptual)") # New Option
        print("  (B)ack to Flock Actions")

        choice = input("Enter your choice: ").strip().lower()

        if choice == 'b':
            break
        elif choice == 'a':
            add_growth_record_cli(flock_id)
        elif choice == 'v':
            act_on_specific_growth_record_cli(flock_id, records[:5]) # Pass only displayed records for num selection
        elif choice == 'l':
            display_all_growth_records(flock_id)
        elif choice == 'n':
            display_growth_analytics(flock_id)
        elif choice == 'f':
            # Need to import this function
            from .analytics_cli import view_feed_efficiency_insights_cli
            view_feed_efficiency_insights_cli(flock_id)
        else:
            print("Invalid choice.")


def display_all_growth_records(flock_id: str):
    records = tracking_repo.get_growth_records_for_flock(flock_id)
    print(f"\n--- All Growth Records for Flock {flock_id} ---")
    if not records:
        print("No records found.")
    else:
        for i, rec in enumerate(records):
            fcr_info = f", FCR: {rec.feed_conversion_ratio}" if rec.feed_conversion_ratio is not None else ""
            print(f"  {i+1}. ID: {rec.record_id}, Date: {rec.record_date}, Avg Wt: {rec.average_weight_grams:.0f}g, Weighed: {rec.number_of_birds_weighed}{fcr_info}")
    input("\nPress Enter to continue...")


def act_on_specific_growth_record_cli(flock_id: str, displayed_records: list):
    id_or_num = input("Enter growth record number from list (if shown) or full ID: ").strip()
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

    record = tracking_repo.get_growth_record_by_id(record_id_to_act)
    if not record or record.flock_id != flock_id:
        print(f"Growth record {record_id_to_act} not found for this flock.")
        return

    view_growth_record_details_cli(record_id_to_act)
    sub_choice = input("Actions: (E)dit, (D)elete, (B)ack: ").strip().lower()
    if sub_choice == 'e': edit_growth_record_cli(record_id_to_act)
    elif sub_choice == 'd': delete_growth_record_cli(record_id_to_act)


if __name__ == '__main__':
    print("Growth CLI Module - For testing individual functions if needed.")
    # For testing, we'd need a flock.
    from farm_management.repositories import FlockRepository # For standalone testing
    local_flock_repo = FlockRepository()
    try:
        from .farm_cli import farm_repo as main_farm_repo
        if not main_farm_repo.get_all_farms():
             main_farm_repo.add_farm("growth-test-farm", "Test Loc", "Test Owner", 100)
        test_farm_id_for_growth = main_farm_repo.get_all_farms()[0].farm_id
    except ImportError:
        test_farm_id_for_growth = "default-farm-growth-test"

    test_flock_growth = local_flock_repo.add_flock(
        farm_id=test_farm_id_for_growth,
        breed="Test Growth Breed",
        acquisition_date=date(2023,1,1),
        initial_count=100
    )
    print(f"Created test flock for growth: {test_flock_growth.flock_id}")

    # Pre-populate data
    tracking_repo.add_growth_record(test_flock_growth.flock_id, date(2023,10,1), 500.0, 50, 1.5)
    tracking_repo.add_growth_record(test_flock_growth.flock_id, date(2023,10,8), 750.0, 48, 1.6)

    manage_growth_records_cli(test_flock_growth.flock_id, test_flock_growth)
