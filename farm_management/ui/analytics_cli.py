from datetime import date, timedelta
from farm_management.repositories import TrackingRepository, FlockRepository
from farm_management.models import Flock

# Assuming tracking_repo and flock_repo are accessible
# For simplicity, try to import from other CLI modules or create new ones
try:
    from .health_cli import tracking_repo
except (ImportError, AttributeError):
    print("Warning: analytics_cli creating new TrackingRepository instance.")
    tracking_repo = TrackingRepository()

try:
    from .flock_cli import flock_repo
except (ImportError, AttributeError):
    print("Warning: analytics_cli creating new FlockRepository instance.")
    flock_repo = FlockRepository()


def calculate_feed_cost_per_egg(flock_id: str, start_date: date, end_date: date) -> dict:
    """Calculates feed cost per egg for a given period."""
    feed_records = tracking_repo.get_feed_consumption_records_for_flock(flock_id, start_date, end_date)
    egg_records = tracking_repo.get_production_records_for_flock(flock_id, start_date, end_date)

    total_feed_cost = sum(rec.total_cost for rec in feed_records)
    total_eggs = sum(rec.marketable_eggs for rec in egg_records) # Using marketable eggs

    if total_eggs == 0:
        cost_per_egg = float('inf') # Avoid division by zero
        if total_feed_cost > 0:
             print(f"Warning: Feed consumed ({total_feed_cost:.2f}) but no marketable eggs recorded in this period.")
        else:
             print("No feed or marketable eggs recorded in this period.")
    else:
        cost_per_egg = total_feed_cost / total_eggs

    return {
        "period_start": start_date.isoformat(),
        "period_end": end_date.isoformat(),
        "total_feed_cost": round(total_feed_cost, 2),
        "total_marketable_eggs": total_eggs,
        "cost_per_marketable_egg": round(cost_per_egg, 4) if total_eggs > 0 else "N/A" # .4 for cents/tenths of cents
    }

def calculate_average_fcr(flock_id: str, start_date: date, end_date: date) -> dict:
    """Calculates average FCR from growth records in a period."""
    growth_records = tracking_repo.get_growth_records_for_flock(flock_id, start_date, end_date)

    fcrs = [rec.feed_conversion_ratio for rec in growth_records if rec.feed_conversion_ratio is not None]
    avg_fcr = sum(fcrs) / len(fcrs) if fcrs else None

    return {
        "period_start": start_date.isoformat(),
        "period_end": end_date.isoformat(),
        "num_fcr_records": len(fcrs),
        "average_fcr": round(avg_fcr, 2) if avg_fcr is not None else "N/A"
    }


def view_feed_efficiency_insights_cli(flock_id: str):
    print(f"\n--- Feed Efficiency Insights for Flock ID: {flock_id} ---")
    print("This section provides data to help you assess feed efficiency.")
    print("True feed optimization requires detailed nutritional analysis and planning.")

    flock = flock_repo.get_flock_by_id(flock_id)
    if not flock:
        print(f"Flock {flock_id} not found.")
        return

    # Default period: last 30 days
    end_date = date.today()
    start_date = end_date - timedelta(days=30)

    print(f"\nMetrics for the period: {start_date.isoformat()} to {end_date.isoformat()}")

    # 1. Average FCR
    fcr_data = calculate_average_fcr(flock_id, start_date, end_date)
    print("\nFeed Conversion Ratio (FCR):")
    if fcr_data["num_fcr_records"] > 0:
        print(f"  Average FCR from {fcr_data['num_fcr_records']} record(s): {fcr_data['average_fcr']}")
    else:
        print("  No FCR records found for this period to calculate average.")
    print("  (Lower FCR is generally better, indicating less feed per unit of weight gain)")

    # 2. Feed Cost per Egg (if applicable, e.g., for Layer flocks)
    # We can infer if it's a layer flock by checking if there are any egg production records
    # or by looking at breed type if that info was more structured.
    # For now, we'll calculate if egg records exist.

    # Check if flock is likely a layer type by looking at its breed name (simple heuristic)
    # or if it has any egg production records at all
    is_layer_flock_heuristic = "layer" in flock.breed.lower() or \
                               len(tracking_repo.get_production_records_for_flock(flock_id, None, None)) > 0

    if is_layer_flock_heuristic: # Only makes sense for layers
        cost_per_egg_data = calculate_feed_cost_per_egg(flock_id, start_date, end_date)
        print("\nFeed Cost per Marketable Egg:")
        print(f"  Total Feed Cost in Period: {cost_per_egg_data['total_feed_cost']:.2f}")
        print(f"  Total Marketable Eggs in Period: {cost_per_egg_data['total_marketable_eggs']}")
        print(f"  Calculated Cost per Marketable Egg: {cost_per_egg_data['cost_per_marketable_egg']}")
    else:
        print("\nFeed Cost per Marketable Egg: Not typically calculated for non-layer flocks (e.g., broilers).")


    print("\nConsiderations for Optimization:")
    print("  - Compare FCR and cost metrics across different feed types (if you change feeds).")
    print("  - Monitor FCR against breed standards or targets.")
    print("  - Ensure feed quality and proper storage.")
    print("  - Minimize feed wastage.")
    print("  - Ensure birds have access to clean water, as it impacts feed intake.")

    input("\nPress Enter to continue...")


def calculate_simple_egg_projection(flock_id: str, past_days_for_avg: int, projection_days: int) -> dict:
    """Calculates a simple egg production projection."""
    today = date.today()
    start_date_for_avg = today - timedelta(days=past_days_for_avg)

    egg_records = tracking_repo.get_production_records_for_flock(flock_id, start_date_for_avg, today)

    if not egg_records:
        return {
            "error": f"No egg production records found in the last {past_days_for_avg} days to calculate an average."
        }

    total_eggs_in_period = sum(rec.marketable_eggs for rec in egg_records)
    # Calculate the actual number of days data was recorded for, within the period.
    # This handles gaps in recording.
    # More accurately, we should count distinct days with records, or sum up production over the actual days spanned by records.
    # For a simple average, total_eggs / number_of_records_days might be better than total_eggs / past_days_for_avg
    # if recording is not daily.

    # Let's use number of records as a proxy for days with production data, assuming one record per day.
    # A more robust way would be to find min/max date in records and use that duration if records are sparse.
    num_records = len(egg_records)
    if num_records == 0: # Should be caught by the check above, but defensive
        return {"error": "No records to calculate average daily production."}

    avg_daily_production = total_eggs_in_period / num_records # Average per recorded day

    projected_eggs = avg_daily_production * projection_days

    return {
        "flock_id": flock_id,
        "avg_period_days": past_days_for_avg,
        "num_data_points": num_records,
        "avg_daily_marketable_eggs": round(avg_daily_production, 2),
        "projection_for_next_days": projection_days,
        "projected_marketable_eggs": round(projected_eggs)
    }

def view_production_projection_cli(flock_id: str):
    print(f"\n--- Egg Production Projection (Conceptual) for Flock ID: {flock_id} ---")
    print("This is a basic projection based on recent average production.")
    print("It is NOT an AI-driven forecast and does not account for age, health, or environmental factors.\n")

    # Parameters for projection - could be user-configurable in a more advanced version
    PAST_DAYS_FOR_AVG = 14 # Use last 14 days for average
    PROJECTION_DAYS_FORWARD = 7 # Project for next 7 days

    projection_data = calculate_simple_egg_projection(flock_id, PAST_DAYS_FOR_AVG, PROJECTION_DAYS_FORWARD)

    if projection_data.get("error"):
        print(f"Could not generate projection: {projection_data['error']}")
    else:
        print(f"Based on average marketable egg production over the past {projection_data['avg_period_days']} days (using {projection_data['num_data_points']} data points):")
        print(f"  Average Daily Marketable Eggs: {projection_data['avg_daily_marketable_eggs']}")
        print(f"\nProjected marketable eggs for the next {projection_data['projection_for_next_days']} days: "
              f"~{projection_data['projected_marketable_eggs']} eggs")

    input("\nPress Enter to continue...")


if __name__ == "__main__":
    # Test functions
    # Need to create some dummy data in tracking_repo and flock_repo
    test_flock_id_analytics = "flock-analytics-test"
    today = date.today() # Define today here for use in __main__

    # Ensure flock exists for analytics functions
    if not flock_repo.get_flock_by_id(test_flock_id_analytics):
        flock_repo.add_flock(
            farm_id="farm-analytics",
            flock_id=test_flock_id_analytics, # Provide specific ID for predictability
            breed="Test Layer Analytics",
            acquisition_date=date(2023,1,1),
            initial_count=100
        )
        print(f"Created test flock: {test_flock_id_analytics}")


    # Add some sample data
    tracking_repo.add_feed_consumption_record(test_flock_id_analytics, today - timedelta(days=15), "Feed A", 50, 0.5)
    tracking_repo.add_feed_consumption_record(test_flock_id_analytics, today - timedelta(days=5), "Feed A", 55, 0.5)
    tracking_repo.add_production_record(test_flock_id_analytics, today - timedelta(days=15), 80, 5) # marketable 75
    tracking_repo.add_production_record(test_flock_id_analytics, today - timedelta(days=10), 82, 2) # marketable 80
    tracking_repo.add_production_record(test_flock_id_analytics, today - timedelta(days=5), 75, 3)  # marketable 72
    tracking_repo.add_growth_record(test_flock_id_analytics, today - timedelta(days=20), 1500, 10, 2.0)
    tracking_repo.add_growth_record(test_flock_id_analytics, today - timedelta(days=10), 1800, 10, 1.9)

    print("\n--- Testing Feed Efficiency Insights CLI ---")
    view_feed_efficiency_insights_cli(test_flock_id_analytics)

    print("\n--- Testing with a Broiler type (heuristic) ---")
    test_broiler_id = "flock-broiler-analytics"
    if not flock_repo.get_flock_by_id(test_broiler_id):
        flock_repo.add_flock(
            farm_id="farm-analytics",
            flock_id=test_broiler_id,
            breed="Test Broiler",
            acquisition_date=date(2023,6,1),
            initial_count=200
        )
        print(f"Created test broiler flock: {test_broiler_id}")
    tracking_repo.add_feed_consumption_record(test_broiler_id, today - timedelta(days=5), "Broiler Feed", 25, 0.6)
    # No egg production for broiler
    tracking_repo.add_growth_record(test_broiler_id, today - timedelta(days=5), 2000, 20, 1.7)
    view_feed_efficiency_insights_cli(test_broiler_id)

    print("\n--- Testing Production Projection CLI ---")
    view_production_projection_cli(test_flock_id_analytics) # Test with the layer flock

    # Test projection with no recent data
    test_flock_no_recent_eggs = "flock-no-recent-eggs"
    if not flock_repo.get_flock_by_id(test_flock_no_recent_eggs):
        flock_repo.add_flock(farm_id="farm-analytics", flock_id=test_flock_no_recent_eggs, breed="Old Layers", acquisition_date=date(2022,1,1), initial_count=50)
        tracking_repo.add_production_record(test_flock_no_recent_eggs, today - timedelta(days=100), 30, 1) # Old data
    view_production_projection_cli(test_flock_no_recent_eggs)
