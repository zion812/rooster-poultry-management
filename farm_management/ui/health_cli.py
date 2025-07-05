from datetime import datetime
from farm_management.repositories import TrackingRepository, FlockRepository
from farm_management.models.health_record import RecordType, DiseaseSymptoms, HealthRecord, DiseaseIncidentRecord, VaccinationRecord, MortalityRecord
from farm_management.models import Flock # For type hinting

# Global repository instances
tracking_repo = TrackingRepository()
# flock_repo is used to update flock count on mortality.
# This assumes flock_repo is accessible, e.g. instantiated in a main app context or passed around.
# For simplicity, we'll use the one from flock_cli if this module is called from there,
# or instantiate a new one if run standalone for testing.
# This highlights the need for a proper dependency injection or service layer.

# Define default alert thresholds for CLI display
DEFAULT_MORTALITY_ALERT_PERIOD_DAYS = 7
DEFAULT_MORTALITY_ALERT_THRESHOLD_DEATHS = 5 # Example: 5 deaths in 7 days
DEFAULT_DISEASE_ALERT_PERIOD_DAYS = 14
DEFAULT_DISEASE_ALERT_MIN_INCIDENTS = 2 # Example: 2+ incidents of same disease in 14 days


def _get_flock_repo_instance():
    """ Helper to get flock_repo, trying from flock_cli first. """
    try:
        from .flock_cli import flock_repo as f_repo
        return f_repo
    except (ImportError, AttributeError):
        # Fallback if not available (e.g. testing health_cli directly)
        print("Warning: health_cli running standalone or flock_repo not found, creating new FlockRepository instance for health_cli.")
        return FlockRepository()

flock_repo_instance = _get_flock_repo_instance()


def _prompt_for_common_health_details(existing_record: HealthRecord = None):
    """Helper for common details: date, details, vet, cost."""
    data = {}
    default_date = existing_record.record_date.strftime('%Y-%m-%d %H:%M') if existing_record and existing_record.record_date else datetime.now().strftime('%Y-%m-%d %H:%M')
    default_details = existing_record.details if existing_record else ""
    default_vet = existing_record.veterinarian if existing_record else ""
    default_cost = str(existing_record.cost) if existing_record and existing_record.cost is not None else "0.0"

    while True:
        date_str = input(f"Enter record date and time (YYYY-MM-DD HH:MM) [{default_date}]: ") or default_date
        try:
            data['record_date'] = datetime.strptime(date_str, '%Y-%m-%d %H:%M')
            break
        except ValueError:
            print("Invalid date format. Please use YYYY-MM-DD HH:MM.")

    data['details'] = input(f"Enter details/notes [{default_details}]: ") or default_details
    data['veterinarian'] = input(f"Enter veterinarian name (optional) [{default_vet}]: ") or default_vet

    while True:
        cost_str = input(f"Enter cost (optional) [{default_cost}]: ") or default_cost
        try:
            data['cost'] = float(cost_str)
            break
        except ValueError:
            print("Invalid cost. Please enter a number.")
    return data

def add_disease_incident_cli(flock_id: str):
    print("\n--- Add Disease Incident Record ---")
    common_data = _prompt_for_common_health_details()

    disease_name = input("Enter disease name: ")
    if not disease_name:
        print("Disease name is required. Aborting.")
        return

    print("Select symptoms (comma-separated numbers):")
    symptom_options = list(DiseaseSymptoms)
    for i, symp in enumerate(symptom_options):
        print(f"  {i+1}. {symp.value}")

    symptoms_input = input("Symptoms: ")
    selected_symptoms = []
    if symptoms_input:
        try:
            indices = [int(x.strip()) - 1 for x in symptoms_input.split(',')]
            for index in indices:
                if 0 <= index < len(symptom_options):
                    selected_symptoms.append(symptom_options[index])
                else:
                    print(f"Warning: Invalid symptom number {index+1} ignored.")
        except ValueError:
            print("Invalid input for symptoms. Using 'Other' if no valid symptoms selected.")

    if not selected_symptoms: # Default if none or invalid input
        selected_symptoms.append(DiseaseSymptoms.OTHER)
        if not common_data.get('details'): # Add a note if details are empty
             common_data['details'] = (common_data.get('details',"") + " Symptoms not specified or invalid input, defaulted to Other.").strip()


    treatment = input("Enter treatment administered (optional): ")
    affected_str = input("Enter number of birds affected (optional, default 0): ") or "0"
    try:
        affected_count = int(affected_str)
    except ValueError:
        print("Invalid number for affected count, defaulting to 0.")
        affected_count = 0

    try:
        record = tracking_repo.add_disease_incident_record(
            flock_id=flock_id, **common_data, disease_name=disease_name,
            symptoms=selected_symptoms, treatment_administered=treatment, affected_count=affected_count
        )
        print(f"Disease incident record {record.record_id} added successfully.")
    except ValueError as e:
        print(f"Error adding disease record: {e}")


def add_vaccination_record_cli(flock_id: str):
    print("\n--- Add Vaccination Record ---")
    common_data = _prompt_for_common_health_details()

    vaccine_name = input("Enter vaccine name: ")
    if not vaccine_name:
        print("Vaccine name is required. Aborting.")
        return
    administered_by = input("Administered by: ")
    if not administered_by:
        print("Administered by is required. Aborting.")
        return

    dosage = input("Enter dosage (e.g., 0.5ml, optional): ")
    vaccinated_str = input("Enter number of birds vaccinated (optional, default 0): ") or "0"
    try:
        vaccinated_count = int(vaccinated_str)
    except ValueError:
        print("Invalid number for vaccinated count, defaulting to 0.")
        vaccinated_count = 0

    try:
        record = tracking_repo.add_vaccination_record(
            flock_id=flock_id, **common_data, vaccine_name=vaccine_name,
            administered_by=administered_by, dosage=dosage, vaccinated_count=vaccinated_count
        )
        print(f"Vaccination record {record.record_id} added successfully.")
    except ValueError as e:
        print(f"Error adding vaccination record: {e}")


def add_mortality_record_cli(flock_id: str):
    global flock_repo_instance # Ensure we are using the correct instance
    print("\n--- Add Mortality Record ---")
    common_data = _prompt_for_common_health_details()

    cause_of_death = input("Enter cause of death: ")
    if not cause_of_death:
        print("Cause of death is required. Aborting.")
        return

    while True:
        deaths_str = input("Enter number of deaths: ")
        try:
            number_of_deaths = int(deaths_str)
            if number_of_deaths <= 0:
                print("Number of deaths must be a positive integer.")
            else:
                break
        except ValueError:
            print("Invalid number. Please enter an integer.")

    post_mortem = input("Post-mortem findings (optional): ")

    try:
        record = tracking_repo.add_mortality_record(
            flock_id=flock_id, **common_data, cause_of_death=cause_of_death,
            number_of_deaths=number_of_deaths, post_mortem_findings=post_mortem
        )
        print(f"Mortality record {record.record_id} added successfully.")

        # Update flock count
        flock = flock_repo_instance.get_flock_by_id(flock_id)
        if flock:
            new_count = flock.current_count - number_of_deaths
            flock_repo_instance.update_flock(flock_id, current_count=new_count)
            print(f"Flock {flock_id} count updated from {flock.current_count + number_of_deaths} to {new_count}.")
        else:
            print(f"Warning: Could not find flock {flock_id} to update its count.")

    except ValueError as e:
        print(f"Error adding mortality record: {e}")


def view_health_record_details_cli(record_id: str):
    record = tracking_repo.get_health_record_by_id(record_id)
    if not record:
        print(f"Health record {record_id} not found.")
        return

    print(f"\n--- Health Record Details: {record.record_id} ---")
    print(f"Flock ID: {record.flock_id}")
    print(f"Record Type: {record.record_type.value}")
    print(f"Date: {record.record_date.strftime('%Y-%m-%d %H:%M')}")
    print(f"Details: {record.details}")
    print(f"Veterinarian: {record.veterinarian if record.veterinarian else 'N/A'}")
    print(f"Cost: {record.cost:.2f}")

    if isinstance(record, DiseaseIncidentRecord):
        print(f"Disease Name: {record.disease_name}")
        print(f"Symptoms: {', '.join([s.value for s in record.symptoms])}")
        print(f"Treatment: {record.treatment_administered if record.treatment_administered else 'N/A'}")
        print(f"Affected Count: {record.affected_count}")
    elif isinstance(record, VaccinationRecord):
        print(f"Vaccine Name: {record.vaccine_name}")
        print(f"Administered By: {record.administered_by}")
        print(f"Dosage: {record.dosage if record.dosage else 'N/A'}")
        print(f"Vaccinated Count: {record.vaccinated_count}")
    elif isinstance(record, MortalityRecord):
        print(f"Cause of Death: {record.cause_of_death}")
        print(f"Number of Deaths: {record.number_of_deaths}")
        print(f"Post-mortem Findings: {record.post_mortem_findings if record.post_mortem_findings else 'N/A'}")


def edit_health_record_cli(record_id: str):
    # This is a simplified edit function. A real one would be more granular.
    record = tracking_repo.get_health_record_by_id(record_id)
    if not record:
        print(f"Health record {record_id} not found for editing.")
        return

    print(f"\n--- Editing Health Record: {record_id} ({record.record_type.value}) ---")
    print("Provide new values or leave blank to keep current.")

    update_data = _prompt_for_common_health_details(existing_record=record) # Get common fields

    # Type-specific fields - very basic update for now
    if isinstance(record, DiseaseIncidentRecord):
        update_data['disease_name'] = input(f"Disease name [{record.disease_name}]: ") or record.disease_name
        # Symptoms update would be more complex (similar to add) - skipping detailed edit for brevity
        update_data['treatment_administered'] = input(f"Treatment [{record.treatment_administered}]: ") or record.treatment_administered
        affected_str = input(f"Affected count [{record.affected_count}]: ") or str(record.affected_count)
        update_data['affected_count'] = int(affected_str) if affected_str.isdigit() else record.affected_count

    elif isinstance(record, VaccinationRecord):
        update_data['vaccine_name'] = input(f"Vaccine name [{record.vaccine_name}]: ") or record.vaccine_name
        update_data['administered_by'] = input(f"Administered by [{record.administered_by}]: ") or record.administered_by
        # Vaccinated count update might need care if it affects other logic - simple update for now
        vaccinated_str = input(f"Vaccinated count [{record.vaccinated_count}]: ") or str(record.vaccinated_count)
        update_data['vaccinated_count'] = int(vaccinated_str) if vaccinated_str.isdigit() else record.vaccinated_count


    elif isinstance(record, MortalityRecord):
        update_data['cause_of_death'] = input(f"Cause of death [{record.cause_of_death}]: ") or record.cause_of_death
        # Note: Changing number_of_deaths here would require complex logic to revert/update flock count.
        # For simplicity, this CLI edit won't adjust flock count for changes in number_of_deaths.
        # This should be handled by a service layer or a more robust update mechanism.
        print(f"Original number of deaths: {record.number_of_deaths}. Editing this value here will NOT automatically adjust flock count.")
        new_deaths_str = input(f"Number of deaths (BE CAREFUL) [{record.number_of_deaths}]: ") or str(record.number_of_deaths)
        new_deaths = int(new_deaths_str) if new_deaths_str.isdigit() else record.number_of_deaths
        if new_deaths != record.number_of_deaths:
            print("WARNING: You've changed the number of deaths. Flock count is NOT automatically adjusted by this edit function. Manual adjustment might be needed.")
        update_data['number_of_deaths'] = new_deaths


    # Filter out truly unchanged values
    final_update_data = {}
    for key, value in update_data.items():
        if hasattr(record, key) and getattr(record, key) != value:
            final_update_data[key] = value
        elif not hasattr(record, key): # New key being added (less likely for typed records)
             final_update_data[key] = value


    if not final_update_data:
        print("No changes detected.")
        return

    updated = tracking_repo.update_health_record(record_id, **final_update_data)
    if updated:
        print(f"Health record {record_id} updated successfully.")
    else:
        print(f"Failed to update health record {record_id}.")


def delete_health_record_cli(record_id: str):
    record = tracking_repo.get_health_record_by_id(record_id)
    if not record:
        print(f"Health record {record_id} not found.")
        return False

    # Important: Deleting a mortality record should ideally prompt user about flock count.
    # For simplicity, this CLI delete won't auto-adjust flock count.
    if isinstance(record, MortalityRecord):
        print(f"WARNING: You are about to delete a mortality record for {record.number_of_deaths} bird(s).")
        print("This will NOT automatically adjust (increase) the flock's current bird count.")
        print("Manual adjustment of the flock count may be necessary.")

    confirm = input(f"Are you sure you want to delete health record {record_id} ({record.record_type.value})? (yes/no): ").strip().lower()
    if confirm == 'yes':
        if tracking_repo.delete_health_record(record_id):
            print(f"Health record {record_id} deleted successfully.")
            return True
        else:
            print(f"Failed to delete health record {record_id}.")
            return False
    else:
        print("Deletion cancelled.")
        return False

def manage_health_records_cli(flock_id: str, current_flock_repo_instance: FlockRepository):
    """CLI: HealthTrackingScreen - Manage health records for a specific flock."""
    global flock_repo_instance # Use the passed instance
    flock_repo_instance = current_flock_repo_instance

    flock = flock_repo_instance.get_flock_by_id(flock_id)
    if not flock:
        print(f"Flock {flock_id} not found.")
        return

    while True:
        # Refresh flock object in case count changed
        flock = flock_repo_instance.get_flock_by_id(flock_id)
        if not flock: # Should not happen if it existed initially, but good check
            print(f"Flock {flock_id} seems to have been deleted. Returning.")
            return

        title_farm_name = flock.farm_id # In case farm object not easily available to get name
        # If farm_repo_instance was passed and available, could get farm.name
        # For now, using flock_id is clear enough.

        print(f"\n--- Health Tracking: Flock {flock.flock_id} (Breed: {flock.breed}, Count: {flock.current_count}) ---") # Enhanced Title

        records = tracking_repo.get_health_records_for_flock(flock_id)
        if not records:
            print("No health records found for this flock.")
        else:
            print("Recent Health Records:")
            for i, rec in enumerate(records[:10]): # Show recent 10
                print(f"  {i+1}. ID: {rec.record_id}, Type: {rec.record_type.value}, Date: {rec.record_date.strftime('%Y-%m-%d')}, Details: {rec.details[:50]}...")
            if len(records) > 10:
                print(f"  ... and {len(records)-10} more records.")

        print("\nHealth Record Options:")
        print("  (LD) Add Disease Incident")
        print("  (LV) Add Vaccination Record")
        print("  (LM) Add Mortality Record")
        print("  (V) View/Edit/Delete Record by Number (from list above)")
        print("  (VA) View All Records for this Flock")
        print("  (S) Search Records (by ID or details)")
        print("  (T) View Recent Mortality Trend (7 days)")
        print("  (C)heck for Alerts Now") # New option
        print("  (B)ack to Flock Actions")

        choice = input("Enter your choice: ").strip().lower()

        if choice == 'b':
            break
        elif choice == 'ld':
            add_disease_incident_cli(flock_id)
            _check_and_display_alerts(flock_id) # Check alerts after adding
        elif choice == 'lv':
            add_vaccination_record_cli(flock_id)
            # Vaccinations don't typically trigger these alerts, but good practice
            _check_and_display_alerts(flock_id)
        elif choice == 'lm':
            add_mortality_record_cli(flock_id) # flock_repo_instance is now global to this module
            _check_and_display_alerts(flock_id) # Check alerts after adding
        elif choice == 'v':
            if not records:
                print("No records to select.")
                continue
            rec_num_str = input("Enter record number from the list to act on: ")
            try:
                rec_idx = int(rec_num_str) - 1
                if 0 <= rec_idx < min(len(records), 10): # only from displayed list
                    selected_rec_id = records[rec_idx].record_id
                    health_record_actions_cli(selected_rec_id)
                else:
                    print("Invalid record number from the displayed list. Use 'VA' to see all then 'S' to find specific.")
            except ValueError:
                print("Invalid input.")
        elif choice == 'va':
            display_all_flock_health_records(flock_id)
        elif choice == 's':
            term = input("Enter Record ID or search term in details: ")
            search_results = [r for r in records if term.lower() in r.record_id.lower() or term.lower() in r.details.lower()]
            if not search_results:
                print("No records found matching your search term.")
            else:
                print("\nMatching Records:")
                for i, rec in enumerate(search_results):
                     print(f"  {i+1}. ID: {rec.record_id}, Type: {rec.record_type.value}, Date: {rec.record_date.strftime('%Y-%m-%d')}, Details: {rec.details[:50]}...")
                sel_choice = input("Enter number to select a record from search results, or (B)ack: ").strip().lower()
                if sel_choice.isdigit() and 0 < int(sel_choice) <= len(search_results):
                    selected_rec = search_results[int(sel_choice)-1]
                    health_record_actions_cli(selected_rec.record_id)

        elif choice == 't':
            trend = tracking_repo.get_recent_mortality_trend(flock_id, days=7)
            print(f"\nMortality Trend for Flock {flock_id} (last 7 days):")
            print(f"  Total Deaths: {trend.get('total_deaths_in_period', 'N/A')}")
            print(f"  Number of Mortality Records: {trend.get('records_in_period', 'N/A')}")
        elif choice == 'c':
            _check_and_display_alerts(flock_id, True) # True for explicit check
        else:
            print("Invalid choice.")

def _check_and_display_alerts(flock_id: str, explicit_check: bool = False):
    """Helper to check and display health alerts."""
    if explicit_check:
        print("\n--- Checking for Health Alerts ---")

    mortality_alert = tracking_repo.check_high_mortality_events(
        flock_id,
        DEFAULT_MORTALITY_ALERT_PERIOD_DAYS,
        DEFAULT_MORTALITY_ALERT_THRESHOLD_DEATHS
    )
    if mortality_alert:
        print(f"\n{'*' * 10} HEALTH ALERT {'*' * 10}")
        print(mortality_alert)
        print(f"{'*' * 34}\n")

    # Example: Check for a common disease like "Infectious Bronchitis" or allow user input
    # For now, let's hardcode one or two common ones as an example or skip specific disease check by default.
    # To make it more dynamic, the system could maintain a list of "notifiable diseases".
    # For this CLI version, we might not check specific diseases automatically unless triggered.
    if explicit_check:
        # Only do specific disease check if explicitly asked, to avoid being too noisy.
        # Or, one could iterate through all unique disease names reported recently.
        # For simplicity, let's check for any disease with multiple recent incidents.
        all_disease_records = tracking_repo.get_health_records_for_flock(flock_id, RecordType.DISEASE_INCIDENT)
        recent_disease_names = {} # Store counts of disease names in the period
        cutoff_date = datetime.now() - datetime.timedelta(days=DEFAULT_DISEASE_ALERT_PERIOD_DAYS)

        for rec in all_disease_records:
            if isinstance(rec, DiseaseIncidentRecord) and rec.record_date >= cutoff_date:
                name = rec.disease_name
                recent_disease_names[name] = recent_disease_names.get(name, 0) + 1

        for disease_name, count in recent_disease_names.items():
            if count >= DEFAULT_DISEASE_ALERT_MIN_INCIDENTS:
                # Use the generic check_disease_outbreak or just print from here
                disease_alert = tracking_repo.check_disease_outbreak(
                    flock_id,
                    DEFAULT_DISEASE_ALERT_PERIOD_DAYS,
                    disease_name, # Check for this specific disease
                    DEFAULT_DISEASE_ALERT_MIN_INCIDENTS
                )
                if disease_alert:
                    print(f"\n{'*' * 10} HEALTH ALERT {'*' * 10}")
                    print(disease_alert)
                    print(f"{'*' * 34}\n")

    if explicit_check and not mortality_alert and not any(count >= DEFAULT_DISEASE_ALERT_MIN_INCIDENTS for count in recent_disease_names.values()):
        print("No specific high-priority alerts detected at this time.")


def display_all_flock_health_records(flock_id: str):
    records = tracking_repo.get_health_records_for_flock(flock_id)
    if not records:
        print(f"No health records found for flock {flock_id}.")
        return
    print(f"\n--- All Health Records for Flock {flock_id} ---")
    for i, rec in enumerate(records):
        print(f"  {i+1}. ID: {rec.record_id}, Type: {rec.record_type.value}, Date: {rec.record_date.strftime('%Y-%m-%d')}, Details: {rec.details[:50]}...")
    input("\nPress Enter to continue...")
    _check_and_display_alerts(flock_id) # Also check alerts after viewing all


def health_record_actions_cli(record_id: str):
    """Actions for a selected health record."""
    record = tracking_repo.get_health_record_by_id(record_id)
    if not record:
        print(f"Record {record_id} not found.")
        return

    while True:
        print(f"\n--- Actions for Health Record ID: {record.record_id} ---")
        view_health_record_details_cli(record_id)

        print("\nRecord Options:")
        print("  (E)dit Record")
        print("  (D)elete Record")
        print("  (B)ack to Health Records List")

        action_choice = input("Choose an action: ").strip().lower()

        if action_choice == 'b':
            break
        elif action_choice == 'e':
            edit_health_record_cli(record_id)
        elif action_choice == 'd':
            if delete_health_record_cli(record_id):
                return # Record deleted, go back
        else:
            print("Invalid action choice.")


if __name__ == '__main__':
    print("Health CLI Module - For testing individual functions if needed.")
    # Example: Create dummy flock repo and flock for testing
    # Note: This flock_repo_instance is specific to this __main__ block for testing.
    # The one at module level is what manage_health_records_cli will use if called from elsewhere.
    local_test_flock_repo = FlockRepository()

    # Check if farm_cli's farm_repo has farms, use one if available
    try:
        from .farm_cli import farm_repo as main_farm_repo
        if main_farm_repo.get_all_farms():
            test_farm_id = main_farm_repo.get_all_farms()[0].farm_id
        else:
            test_farm_id = "test-farm-health-direct"
            main_farm_repo.add_farm(test_farm_id, "Health Test Farm", "Loc", "Own", 100)
    except ImportError: # Running standalone
        main_farm_repo = None # Won't be used if flock exists
        test_farm_id = "test-farm-health-direct"


    test_flock = local_test_flock_repo.add_flock(test_farm_id, "Test Health Breed", datetime.now().date() , 100)
    print(f"Created test flock: {test_flock.flock_id} for farm {test_farm_id}")

    # Pre-populate some health data
    tracking_repo.add_disease_incident_record(test_flock.flock_id, datetime(2023,10,1), "Coughing", "IB", [DiseaseSymptoms.COUGHING])
    tracking_repo.add_vaccination_record(test_flock.flock_id, datetime(2023,10,5), "NDV Vaccine", "ND Lasota", "Staff")

    manage_health_records_cli(test_flock.flock_id, local_test_flock_repo)
