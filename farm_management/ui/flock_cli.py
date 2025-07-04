from datetime import date, datetime
from farm_management.repositories import FarmRepository, FlockRepository
from farm_management.models import Farm, Flock # For type hinting

# Global repository instances (for simplicity in CLI example)
# In a real app, these would be managed via dependency injection or a central app context.
# farm_repo is already instantiated in farm_cli.py, flock_repo needs one.
flock_repo = FlockRepository()
# To access farm details (like farm name), we might need farm_repo.
# For now, assuming farm_cli.py's farm_repo is the source of truth for farms.
# This highlights a need for better state/repo management in a larger app.


def _prompt_for_flock_details(farm_id: str, is_update=False, existing_flock: Flock = None):
    """Helper function to gather flock details from user input."""
    details = {}

    if existing_flock:
        print(f"Updating details for Flock ID: {existing_flock.flock_id}")
        default_breed = existing_flock.breed
        default_acq_date = existing_flock.acquisition_date.strftime('%Y-%m-%d')
        default_initial_count = str(existing_flock.initial_count)
        default_current_count = str(existing_flock.current_count)
        default_source = existing_flock.source_supplier
        default_parent_m = existing_flock.parent_flock_id_male or ""
        default_parent_f = existing_flock.parent_flock_id_female or ""
        default_notes = existing_flock.notes
    else:
        default_breed, default_acq_date, default_initial_count, default_current_count, \
        default_source, default_parent_m, default_parent_f, default_notes = "", "", "", "", "", "", "", ""

    details['breed'] = input(f"Enter flock breed [{default_breed}]: ") or default_breed

    while True:
        acq_date_str = input(f"Enter acquisition date (YYYY-MM-DD) [{default_acq_date}]: ") or default_acq_date
        try:
            details['acquisition_date'] = date.fromisoformat(acq_date_str)
            break
        except ValueError:
            print("Invalid date format. Please use YYYY-MM-DD.")
            if is_update and existing_flock: # allow keeping existing on error
                details['acquisition_date'] = existing_flock.acquisition_date
                break


    while True:
        initial_count_str = input(f"Enter initial bird count [{default_initial_count}]: ") or default_initial_count
        try:
            details['initial_count'] = int(initial_count_str)
            break
        except ValueError:
            print("Invalid count. Please enter a number.")

    if is_update: # Only ask for current_count if updating
        while True:
            current_count_str = input(f"Enter current bird count [{default_current_count}]: ") or default_current_count
            try:
                details['current_count'] = int(current_count_str)
                break
            except ValueError:
                print("Invalid count. Please enter a number.")
    else: # For new flocks, current_count is same as initial_count
        details['current_count'] = details['initial_count']


    details['source_supplier'] = input(f"Enter source/supplier (optional) [{default_source}]: ") or default_source
    details['parent_flock_id_male'] = input(f"Enter Male Parent Flock ID (optional) [{default_parent_m}]: ") or default_parent_m
    details['parent_flock_id_female'] = input(f"Enter Female Parent Flock ID (optional) [{default_parent_f}]: ") or default_parent_f
    details['notes'] = input(f"Enter notes (optional) [{default_notes}]: ") or default_notes

    # For updates, filter out unchanged values if user just hits enter
    if is_update:
        final_details = {}
        for key, value in details.items():
            # Special handling for date as it's converted to object
            if key == 'acquisition_date' and existing_flock and getattr(existing_flock, key) == value:
                continue
            if hasattr(existing_flock, key) and str(getattr(existing_flock, key)) == str(value) and (value or getattr(existing_flock,key) == ""): # ensure empty strings are compared correctly
                 if not (value == "" and getattr(existing_flock, key) is None): # if new value is empty string and old was None, it's a change
                    if not (value is None and getattr(existing_flock, key) == ""): # if new value is None and old was empty string
                        continue # No change

            final_details[key] = value
        return final_details

    return details


def add_new_flock_cli(farm_id: str, farm_repo_instance: FarmRepository):
    """CLI: AddFlockRegistryScreen - Add a new flock to a specific farm."""
    farm = farm_repo_instance.get_farm_by_id(farm_id)
    if not farm:
        print(f"Error: Farm with ID {farm_id} not found.")
        return

    print(f"\n--- Add New Flock to Farm: {farm.name} ---")
    details = _prompt_for_flock_details(farm_id)

    if not details.get('breed') or details.get('acquisition_date') is None or details.get('initial_count') is None:
        print("Flock breed, acquisition date, and initial count are required. Flock not added.")
        return

    new_flock = flock_repo.add_flock(
        farm_id=farm_id,
        breed=details['breed'],
        acquisition_date=details['acquisition_date'],
        initial_count=details['initial_count'],
        source_supplier=details.get('source_supplier', ""),
        parent_flock_id_male=details.get('parent_flock_id_male') or None, # ensure None if empty
        parent_flock_id_female=details.get('parent_flock_id_female') or None, # ensure None if empty
        notes=details.get('notes', "")
    )
    # Also add this flock to the farm object in FarmRepository
    farm_repo_instance.add_flock_to_farm(farm_id, new_flock)
    print(f"Successfully added Flock ID: {new_flock.flock_id} to Farm: {farm.name}")


def view_flock_details_cli(flock_id: str, farm_repo_instance: FarmRepository):
    """CLI: View details of a specific flock."""
    flock = flock_repo.get_flock_by_id(flock_id)
    if not flock:
        print(f"Flock with ID {flock_id} not found.")
        return

    farm = farm_repo_instance.get_farm_by_id(flock.farm_id)
    farm_name = farm.name if farm else "Unknown Farm"

    print(f"\n--- Flock Details: {flock.flock_id} ---")
    print(f"Belongs to Farm: {farm_name} (ID: {flock.farm_id})")
    print(f"Breed: {flock.breed}")
    print(f"Acquisition Date: {flock.acquisition_date.strftime('%Y-%m-%d')}")
    print(f"Age (days): {flock._calculate_age_days()}")
    print(f"Age Group: {flock.age_group.value}")
    print(f"Initial Count: {flock.initial_count}")
    print(f"Current Count: {flock.current_count}")
    print(f"Source/Supplier: {flock.source_supplier if flock.source_supplier else 'N/A'}")
    print(f"Male Parent Flock ID: {flock.parent_flock_id_male if flock.parent_flock_id_male else 'N/A'}")
    print(f"Female Parent Flock ID: {flock.parent_flock_id_female if flock.parent_flock_id_female else 'N/A'}")
    print(f"Notes: {flock.notes if flock.notes else 'N/A'}")

    # Placeholder for health, production, growth records navigation
    print("\nFurther actions (e.g., view health records) not yet implemented here.")


def edit_flock_cli(flock_id: str, farm_repo_instance: FarmRepository):
    """CLI: Edit an existing flock."""
    flock = flock_repo.get_flock_by_id(flock_id)
    if not flock:
        print(f"Flock with ID {flock_id} not found for editing.")
        return

    farm = farm_repo_instance.get_farm_by_id(flock.farm_id)
    print(f"\n--- Edit Flock: {flock.flock_id} (Farm: {farm.name if farm else 'Unknown'}) ---")
    print("Leave fields blank to keep current values, where applicable.")

    update_data = _prompt_for_flock_details(flock.farm_id, is_update=True, existing_flock=flock)

    if not update_data: # Check if any actual changes were made
        print("No changes detected to update.")
        return

    # Ensure acquisition_date is in correct date object format if changed
    if 'acquisition_date' in update_data and isinstance(update_data['acquisition_date'], str):
        try:
            update_data['acquisition_date'] = date.fromisoformat(update_data['acquisition_date'])
        except ValueError:
            print("Invalid acquisition date format during update. No changes made to date.")
            del update_data['acquisition_date']


    updated_flock = flock_repo.update_flock(flock_id, **update_data)
    if updated_flock:
        # If the flock is part of a farm in farm_repo, that farm's flock list might need an update
        # if the flock object itself is replaced by update_flock (depends on implementation).
        # Current implementation modifies in-place, so farm_repo's list should be fine.
        print(f"Flock '{updated_flock.flock_id}' updated successfully.")
    else:
        print(f"Failed to update flock {flock_id}.")


def delete_flock_cli(flock_id: str, farm_id: str, farm_repo_instance: FarmRepository) -> bool:
    """CLI: Delete a flock."""
    flock = flock_repo.get_flock_by_id(flock_id)
    if not flock:
        print(f"Flock with ID {flock_id} not found for deletion.")
        return False

    confirm = input(f"Are you sure you want to delete Flock ID '{flock.flock_id}' (Breed: {flock.breed})? This cannot be undone. (yes/no): ").strip().lower()
    if confirm == 'yes':
        if flock_repo.delete_flock(flock_id):
            # Also remove from the farm's list in FarmRepository
            farm_repo_instance.remove_flock_from_farm(farm_id, flock_id)
            print(f"Flock '{flock.flock_id}' deleted successfully.")
            return True
        else:
            print(f"Failed to delete flock '{flock.flock_id}' from flock repository.")
            return False
    else:
        print("Deletion cancelled.")
        return False


def manage_flocks_for_farm_cli(farm_id: str, farm_repo_instance: FarmRepository):
    """CLI: FlockManagementScreen - List and manage flocks for a given farm."""
    farm = farm_repo_instance.get_farm_by_id(farm_id)
    if not farm:
        print(f"Error: Farm with ID {farm_id} not found.")
        return

    while True:
        print(f"\n--- Flock Management: Farm '{farm.name}' (ID: {farm.farm_id}) ---") # Enhanced title

        # Fetch flocks using FlockRepository, then cross-reference with farm.flocks from FarmRepository
        # This ensures consistency if flocks can be managed independently or via farm.
        # For now, using flock_repo.get_flocks_by_farm_id which is simpler.
        # farm_flocks_from_farm_obj = farm.flocks # Flocks from farm object
        farm_flocks_from_repo = flock_repo.get_flocks_by_farm_id(farm_id)

        if not farm_flocks_from_repo:
            print("No flocks currently registered for this farm.")
        else:
            print("Flocks on this farm:")
            for i, flock_obj in enumerate(farm_flocks_from_repo):
                print(f"  {i+1}. ID: {flock_obj.flock_id}, Breed: {flock_obj.breed}, "
                      f"Count: {flock_obj.current_count}, Age: {flock_obj._calculate_age_days()} days ({flock_obj.age_group.value})")

        print("\nOptions:")
        print("  (A)dd New Flock")
        print("  (Number) View/Edit/Delete Flock")
        print("  (S)earch Flocks on this Farm")
        print("  (B)ack to Farm Details")

        choice = input("Enter your choice: ").strip().lower()

        if choice == 'b':
            break
        elif choice == 'a':
            add_new_flock_cli(farm_id, farm_repo_instance)
        elif choice == 's':
            search_term = input("Enter search term for flocks (breed, notes, ID): ")
            results = flock_repo.search_flocks(farm_id=farm_id, search_term=search_term)
            if not results:
                print("No flocks found matching your search.")
            else:
                print("\nSearch Results:")
                for i, flock_obj in enumerate(results):
                     print(f"  {i+1}. ID: {flock_obj.flock_id}, Breed: {flock_obj.breed}, Count: {flock_obj.current_count}")
                # Allow selection from search results
                sel_choice = input("Enter number to select a flock from search results, or (B)ack: ").strip().lower()
                if sel_choice.isdigit() and 0 < int(sel_choice) <= len(results):
                    selected_flock = results[int(sel_choice)-1]
                    flock_actions_cli(selected_flock.flock_id, farm_id, farm_repo_instance)
        elif choice.isdigit():
            try:
                flock_index = int(choice) - 1
                if 0 <= flock_index < len(farm_flocks_from_repo):
                    selected_flock_id = farm_flocks_from_repo[flock_index].flock_id
                    flock_actions_cli(selected_flock_id, farm_id, farm_repo_instance)
                else:
                    print("Invalid flock number.")
            except ValueError:
                print("Invalid input.")
        else:
            print("Invalid choice. Please try again.")

def flock_actions_cli(flock_id: str, farm_id: str, farm_repo_instance: FarmRepository):
    """Actions for a selected flock: View, Edit, Delete."""
    flock = flock_repo.get_flock_by_id(flock_id)
    if not flock:
        print(f"Flock {flock_id} not found.")
        return

# Import health CLI
from .health_cli import manage_health_records_cli
# Import production CLI
from .production_cli import manage_production_records_cli
# Import growth CLI
from .growth_cli import manage_growth_records_cli

    while True:
        # Refresh flock object before showing actions, as health records might change its count
        flock = flock_repo.get_flock_by_id(flock_id)
        if not flock:
            print(f"Flock {flock_id} no longer exists. Returning to flock list.")
            return

        print(f"\n--- Actions for Flock: {flock.flock_id} ---")
        print(f"  Breed: {flock.breed}, Acq.Date: {flock.acquisition_date}, Age: {flock._calculate_age_days()} days, Current Count: {flock.current_count}")
        print("-" * 30)


        print("\nFlock Options:")
        print("  (V)iew Full Details")
        print("  (E)dit Flock Details")
        print("  (H)ealth Records")
        print("  (P)roduction Records")
        print("  (G)rowth Records")
        print("  (T)ree View (Family Tree)")
        print("  (D)elete Flock")
        print("  (B)ack to Flock List")

        action_choice = input("Choose an action: ").strip().lower()

        if action_choice == 'b':
            break
        elif action_choice == 'v':
            view_flock_details_cli(flock_id, farm_repo_instance)
        elif action_choice == 'e':
            edit_flock_cli(flock_id, farm_repo_instance)
            # After edit, the loop will reprint details
        elif action_choice == 'h':
            manage_health_records_cli(flock_id, flock_repo) # Pass current flock_repo
            # Loop will refresh flock details after returning from health mgmt
        elif action_choice == 'p':
            manage_production_records_cli(flock_id, flock) # Pass flock_id and flock object
            # Loop will refresh flock details after returning
        elif action_choice == 'g':
            manage_growth_records_cli(flock_id, flock) # Pass flock_id and flock object
            # Loop will refresh flock details after returning
        elif action_choice == 't':
            display_text_family_tree(flock_id)
        elif action_choice == 'd':
            if delete_flock_cli(flock_id, farm_id, farm_repo_instance):
                return # Flock deleted, go back to flock list
        else:
            print("Invalid action choice.")


def display_text_family_tree(flock_id: str):
    """Displays a text-based family tree for the given flock."""
    print(f"\n--- Family Tree for Flock ID: {flock_id} ---")

    # flock_repo is the global instance in this module
    tree_data = flock_repo.get_flock_family_tree(flock_id, max_depth=4) # Max depth for CLI visualization

    if not tree_data or tree_data.get("error") == "Flock not found": # Check specific error for target flock
        print(f"Could not retrieve family tree: Flock ID {flock_id} not found or error processing.")
        return

    def print_node(node, indent_level=0, prefix="Flock: "):
        if not node:
            return

        flock_info = f"ID: {node.get('id', 'N/A')}, Breed: {node.get('breed', 'N/A')}"
        if node.get("error"): # This error is for parent flocks not found
            flock_info = f"ID: {node.get('id', 'N/A')} - Error: {node.get('error')}"

        print("  " * indent_level + prefix + flock_info)

        # Check if parents exist in the tree_data, not just by parent_flock_id_... on the node itself
        # as the tree_data is already structured by get_flock_family_tree
        male_parent_node = node.get("male_parent")
        if male_parent_node:
            print_node(male_parent_node, indent_level + 1, "  M-Parent: ")
        # If parent ID was present in original flock but not expanded (e.g. beyond depth or not found)
        # This part is tricky as the current tree_data from repo doesn't retain original parent_flock_ids if not found/expanded
        # For simplicity, we rely on the structure returned by get_flock_family_tree

        female_parent_node = node.get("female_parent")
        if female_parent_node:
            print_node(female_parent_node, indent_level + 1, "  F-Parent: ")

    print_node(tree_data)
    input("\nPress Enter to continue...")


# This file is not intended to be run directly as a main script,
# but to be called from farm_cli.py.
# If direct testing is needed:
if __name__ == '__main__':
    print("Flock CLI Module - For testing individual functions if needed.")
    # Example: Create dummy farm repo and farm for testing
    test_farm_repo = FarmRepository() # Main farm repo for farm context

    # Setup some flocks with parentage for testing tree directly
    gpa_m = flock_repo.add_flock("test-farm", "GPA-M", date(2020,1,1), 1)
    gma_m = flock_repo.add_flock("test-farm", "GMA-M", date(2020,1,1), 1)
    gpa_f = flock_repo.add_flock("test-farm", "GPA-F", date(2020,1,1), 1)
    gma_f = flock_repo.add_flock("test-farm", "GMA-F", date(2020,1,1), 1)

    parent_male = flock_repo.add_flock("test-farm", "Parent M", date(2021,1,1), 1, parent_flock_id_male=gpa_m.flock_id, parent_flock_id_female=gma_m.flock_id)
    parent_female = flock_repo.add_flock("test-farm", "Parent F", date(2021,1,1), 1, parent_flock_id_male=gpa_f.flock_id, parent_flock_id_female=gma_f.flock_id)

    child = flock_repo.add_flock("test-farm", "Child", date(2022,1,1), 1, parent_flock_id_male=parent_male.flock_id, parent_flock_id_female=parent_female.flock_id)

    print(f"Test flock setup complete. Child ID: {child.flock_id}")
    display_text_family_tree(child.flock_id)

    print("\n--- Testing tree for a flock with one parent missing in repo (simulated) ---")
    # Simulate a missing parent by providing a non-existent ID
    child_missing_parent = flock_repo.add_flock("test-farm", "ChildMissing", date(2022,2,1),1, parent_flock_id_male=parent_male.flock_id, parent_flock_id_female="non-existent-id")
    display_text_family_tree(child_missing_parent.flock_id)

    test_farm = test_farm_repo.add_farm("Test Farm for Flocks CLI", "Test Location", "Tester", 100)
    # Add the created flocks to the farm object as well for manage_flocks_for_farm_cli context
    test_farm_repo.add_flock_to_farm(test_farm.farm_id, gpa_m)
    test_farm_repo.add_flock_to_farm(test_farm.farm_id, gma_m)
    # ... and so on for other test flocks if needed by manage_flocks_for_farm_cli
    test_farm_repo.add_flock_to_farm(test_farm.farm_id, child)


    # Test manage_flocks_for_farm_cli to see if tree option appears
    # manage_flocks_for_farm_cli(test_farm.farm_id, test_farm_repo)
