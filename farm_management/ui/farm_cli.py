from farm_management.repositories import FarmRepository
from farm_management.models import Farm # For type hinting
from datetime import datetime

# Import flock management CLI functions and potentially its repository if needed globally
from .flock_cli import manage_flocks_for_farm_cli, flock_repo as global_flock_repo # Using global flock_repo from flock_cli

# Import flock management CLI functions and potentially its repository if needed globally
from .flock_cli import manage_flocks_for_farm_cli, flock_repo as global_flock_repo # Using global flock_repo from flock_cli
from farm_management.services import WeatherService # Import WeatherService

# Import flock management CLI functions and potentially its repository if needed globally
from .flock_cli import manage_flocks_for_farm_cli, flock_repo as global_flock_repo # Using global flock_repo from flock_cli
from farm_management.services import WeatherService, MarketService # Import WeatherService & MarketService

# Global repository instance for farms (for simplicity in CLI example)
# In a real app, this would be managed via dependency injection or a central app context
farm_repo = FarmRepository() # This is the main farm_repo instance for the application
weather_service_instance = WeatherService() # Instantiate weather service
market_service_instance = MarketService() # Instantiate market service

def _prompt_for_farm_details(is_update=False, existing_farm: Farm = None):
    """Helper function to gather farm details from user input."""
    details = {}

    if existing_farm:
        print(f"Updating details for: {existing_farm.name} (ID: {existing_farm.farm_id})")
        default_name = existing_farm.name
        default_location = existing_farm.location
        default_owner = existing_farm.owner
        default_capacity = str(existing_farm.capacity)
        default_notes = existing_farm.notes
        default_est_date = existing_farm.established_date.strftime('%Y-%m-%d') if existing_farm.established_date else ""
    else:
        default_name, default_location, default_owner, default_capacity, default_notes, default_est_date = "", "", "", "", "", ""

    details['name'] = input(f"Enter farm name [{default_name}]: ") or default_name
    details['location'] = input(f"Enter farm location [{default_location}]: ") or default_location
    details['owner'] = input(f"Enter farm owner [{default_owner}]: ") or default_owner

    while True:
        capacity_str = input(f"Enter farm capacity (max birds) [{default_capacity}]: ") or default_capacity
        try:
            details['capacity'] = int(capacity_str)
            break
        except ValueError:
            print("Invalid capacity. Please enter a number.")

    est_date_str = input(f"Enter established date (YYYY-MM-DD, optional) [{default_est_date}]: ") or default_est_date
    if est_date_str:
        try:
            details['established_date'] = datetime.strptime(est_date_str, '%Y-%m-%d')
        except ValueError:
            print("Invalid date format. Established date will be ignored or kept as is.")
            if is_update and existing_farm: # keep existing if update fails
                 details['established_date'] = existing_farm.established_date
            else:
                 details['established_date'] = None # or set to now for new farms if desired
    elif is_update and existing_farm: # if empty during update, keep existing
        details['established_date'] = existing_farm.established_date


    details['notes'] = input(f"Enter notes (optional) [{default_notes}]: ") or default_notes

    # Filter out unchanged values for update to avoid overwriting with empty strings if user just hits enter
    if is_update:
        final_details = {}
        for key, value in details.items():
            if value or (key == 'capacity' and value == 0): # capacity can be 0
                if hasattr(existing_farm, key) and getattr(existing_farm, key) == value and key != 'established_date': # special handling for date
                    continue # No change
                if key == 'established_date' and existing_farm and existing_farm.established_date == value:
                    continue
                final_details[key] = value
        return final_details

    return details

def add_new_farm_cli():
    """CLI function to add a new farm."""
    print("\n--- Add New Farm ---")
    details = _prompt_for_farm_details()

    if not details.get('name') or not details.get('location') or not details.get('owner') or details.get('capacity') is None:
        print("Farm name, location, owner, and capacity are required. Farm not added.")
        return

    farm = farm_repo.add_farm(
        name=details['name'],
        location=details['location'],
        owner=details['owner'],
        capacity=details['capacity'],
        established_date=details.get('established_date'), # Might be None
        notes=details.get('notes', "")
    )
    print(f"Successfully added farm: {farm.name} (ID: {farm.farm_id})")

def display_farm_list_cli():
    """CLI function to display list of farms and allow search."""
    print("\n--- Farm List & Search ---") # More descriptive title

    search_term = input("Search farms (by name, location, owner - leave empty to list all): ").strip()
    farms = farm_repo.search_farms(search_term)

    if not farms:
        print("No farms found." if not search_term else f"No farms found matching '{search_term}'.")
        return

    print(f"\nFound {len(farms)} farm(s):")
    for i, farm in enumerate(farms):
        print(f"{i+1}. {farm.name} (ID: {farm.farm_id}) - Location: {farm.location}, Owner: {farm.owner}")

    while True:
        choice = input("\nEnter farm number to view details, (A)dd new farm, or (B)ack: ").strip().lower()
        if choice == 'b' or choice == '':
            break
        elif choice == 'a':
            add_new_farm_cli()
            # After adding, refresh the list
            display_farm_list_cli()
            return # Exit current instance of display_farm_list_cli
        else:
            try:
                farm_index = int(choice) - 1
                if 0 <= farm_index < len(farms):
                    view_farm_details_cli(farms[farm_index].farm_id)
                    # After viewing, potentially refresh list if changes occurred or just show again
                    display_farm_list_cli()
                    return # Exit current instance
                else:
                    print("Invalid farm number.")
            except ValueError:
                print("Invalid input. Please enter a number, 'A', or 'B'.")


def view_farm_details_cli(farm_id: str):
    """CLI function to view details of a specific farm."""
    farm = farm_repo.get_farm_by_id(farm_id)
    if not farm:
        print(f"Farm with ID {farm_id} not found.")
        return

    print(f"\n--- Farm Details: {farm.name} ---")
    print(f"ID: {farm.farm_id}")
    print(f"Name: {farm.name}")
    print(f"Location: {farm.location}")
    print(f"Owner: {farm.owner}")
    print(f"Capacity: {farm.capacity} birds")
    print(f"Established: {farm.established_date.strftime('%Y-%m-%d') if farm.established_date else 'N/A'}")
    print(f"Notes: {farm.notes if farm.notes else 'N/A'}")
    print(f"Current number of flocks: {len(farm.flocks)}") # This count relies on farm.flocks being accurate

    # Displaying flock info here can be tricky due to how farm.flocks is populated (or not) on load.
    # For UX, it's better to direct to the dedicated flock management screen.
    if farm.flocks:
        print(f"  (Use 'Manage Flocks' option to view and manage the {len(farm.flocks)} flock(s) on this farm.)")
    else:
        print("  No flocks currently registered for this farm.")
    print("-" * 30) # Separator

    while True:
        action = input(f"\nActions for {farm.name}: (E)dit, (D)elete, (M)anage Flocks, (W)eather, or (B)ack to list: ").strip().lower()
        if action == 'b':
            break
        elif action == 'w':
            print(f"\nFetching weather for {farm.name} at location: {farm.location}...")
            weather_data = weather_service_instance.get_current_weather_for_farm(farm.location)
            if weather_data:
                if weather_data.get("error"):
                    print(f"  Could not get weather: {weather_data['error']}")
                else:
                    print("  Current Weather:")
                    for key, value in weather_data.items():
                        if key not in ["raw_weather_code", "latitude", "longitude", "timezone", "error"]: # Don't show these raw fields
                             print(f"    {key.replace('_', ' ').capitalize()}: {value}")
            else:
                print("  Failed to fetch weather data (no response).")
            input("  Press Enter to continue...")
        elif action == 'e':
            edit_farm_cli(farm_id)
            # Refresh details view after edit
            view_farm_details_cli(farm_id)
            return # Exit current instance to avoid loop issues
        elif action == 'd':
            if delete_farm_cli(farm_id): # If deletion successful
                return # Go back, farm no longer exists
            else: # Deletion cancelled or failed
                continue # Stay on details view
        elif action == 'm':
            # Navigate to FlockManagementScreen (CLI equivalent)
            # We pass the current farm_repo instance to flock_cli functions
            # so it can interact with farm data if needed (e.g., farm name)
            # and update the farm object with new/removed flocks.
            manage_flocks_for_farm_cli(farm_id, farm_repo)
            # After flock management, refresh farm details as flocks might have changed
            view_farm_details_cli(farm_id) # This re-fetches and re-displays the farm
            return # Exit current instance of view_farm_details_cli to avoid loops
        else:
            print("Invalid action.")

def edit_farm_cli(farm_id: str):
    """CLI function to edit an existing farm."""
    farm = farm_repo.get_farm_by_id(farm_id)
    if not farm:
        print(f"Farm with ID {farm_id} not found for editing.")
        return

    print(f"\n--- Edit Farm: {farm.name} ---")
    print("Leave fields blank to keep current values.")

    update_data = _prompt_for_farm_details(is_update=True, existing_farm=farm)

    if not update_data:
        print("No changes made.")
        return

    updated_farm = farm_repo.update_farm(farm_id, **update_data)
    if updated_farm:
        print(f"Farm '{updated_farm.name}' updated successfully.")
    else:
        print(f"Failed to update farm {farm_id}.")


def delete_farm_cli(farm_id: str) -> bool:
    """CLI function to delete a farm."""
    farm = farm_repo.get_farm_by_id(farm_id)
    if not farm:
        print(f"Farm with ID {farm_id} not found for deletion.")
        return False

    confirm = input(f"Are you sure you want to delete farm '{farm.name}' (ID: {farm_id})? This cannot be undone. (yes/no): ").strip().lower()
    if confirm == 'yes':
        if farm_repo.delete_farm(farm_id):
            print(f"Farm '{farm.name}' deleted successfully.")
            return True
        else:
            print(f"Failed to delete farm '{farm.name}'.")
            return False
    else:
        print("Deletion cancelled.")
        return False

# Main menu for farm management CLI
def display_market_prices_cli():
    """Displays conceptual market prices."""
    print("\n--- Conceptual Market Prices (Krishna District - Mock Data) ---")

    egg_prices = market_service_instance.get_egg_market_price("Krishna District")
    broiler_prices = market_service_instance.get_broiler_market_price("Krishna District")

    print("\nEgg Prices (per piece):")
    if egg_prices.get("message"):
        print(f"  {egg_prices['message']}")
    else:
        print(f"  Date: {egg_prices.get('date')}")
        print(f"  Wholesale: Rs. {egg_prices.get('wholesale_price_rupees', 'N/A')}")
        print(f"  Retail: Rs. {egg_prices.get('retail_price_rupees', 'N/A')}")
        print(f"  Source: {egg_prices.get('source')}")

    print("\nBroiler Prices (per Kg, live weight):")
    if broiler_prices.get("message"):
        print(f"  {broiler_prices['message']}")
    else:
        print(f"  Date: {broiler_prices.get('date')}")
        print(f"  Wholesale: Rs. {broiler_prices.get('wholesale_price_rupees', 'N/A')}")
        print(f"  Retail: Rs. {broiler_prices.get('retail_price_rupees', 'N/A')}")
        print(f"  Source: {broiler_prices.get('source')}")

    print("\nProfitability Insight (Conceptual):")
    print("  To estimate profitability, compare these market prices against your")
    print("  production costs (e.g., feed cost per egg/bird, labor, health expenses).")
    print("  The system helps track some of these costs (like feed).")
    input("\nPress Enter to continue...")


def farm_management_main_cli():
    """Main menu for the Farm Management CLI."""
    # Pre-populate with some data for easier testing
    # This now happens in repository __init__ if files don't exist (for farm_repo)
    # For CLI testing convenience, we can still add some if repo is empty.
    if not farm_repo.get_all_farms():
        print("No farms found in data files. Adding sample farms for demo...")
        farm_repo.add_farm("Krishna Poultry Paradise", "Machilipatnam", "Mr. Rao", 5000, notes="Layer farm")
        farm_repo.add_farm("Godavari Broilers", "Nuzvid area, Krishna", "Ms. Lakshmi", 10000, notes="Broiler specialist")
        # farm_repo.add_farm("Organic Feathers Farm", "Rural Gudivada", "Mr. Reddy", 2000, notes="Organic, free-range")


    while True:
        print("\n===== Farm Management System =====")
        print("1. Manage Farms (List/Search/Add)")
        print("2. View Market Prices (Conceptual)")
        print("3. Export Data")
        print("0. Exit")

        choice = input("Enter your choice: ").strip()

        if choice == '1':
            display_farm_list_cli() # This leads to add farm, view details etc.
        elif choice == '2':
            display_market_prices_cli()
        elif choice == '3':
            from .export_cli import data_export_main_cli # Import here to avoid circular dependencies at module level
            data_export_main_cli()
        elif choice == '0':
            print("Exiting Farm Management System.")
            break
        else:
            print("Invalid choice. Please try again.")

if __name__ == '__main__':
    # This allows running farm_cli.py directly for testing farm management features
    farm_management_main_cli()
