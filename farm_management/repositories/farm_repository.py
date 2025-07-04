import uuid
import json
import os
from datetime import datetime
from typing import Optional, List, Dict

from farm_management.models import Farm
from farm_management.models.flock import Flock

DATA_DIR = "farm_management/data"
FARMS_FILE = os.path.join(DATA_DIR, "farms.json")

class FarmRepository:
    """
    Manages storage and retrieval of Farm data.
    Supports persistence to a JSON file.
    """
    def __init__(self):
        self._farms: Dict[str, Farm] = {}
        os.makedirs(DATA_DIR, exist_ok=True) # Ensure data directory exists
        self._load_farms()

    def _ensure_data_dir_exists(self):
        if not os.path.exists(DATA_DIR):
            os.makedirs(DATA_DIR)

    def _load_farms(self):
        self._ensure_data_dir_exists()
        if not os.path.exists(FARMS_FILE):
            self._farms = {} # No file, start empty
            return

        try:
            with open(FARMS_FILE, 'r') as f:
                farms_data = json.load(f)

            for farm_id, farm_dict in farms_data.items():
                # Reconstruct Farm object
                # Flocks will be more complex as they are separate objects.
                # For now, farm.flocks will be empty on load from farm_repo;
                # flock_repo will handle loading its own flocks and they can be linked up by a service layer or UI.
                # Or, we can store flock IDs and reconstruct basic Flock shells if needed here.
                # For simplicity here, farm.flocks will be populated by other means (e.g. flock_repo loading)

                # Convert established_date back from ISO string
                established_date_iso = farm_dict.get("established_date")
                established_dt = None
                if established_date_iso:
                    try:
                        established_dt = datetime.fromisoformat(established_date_iso)
                    except ValueError:
                        print(f"Warning: Could not parse established_date for farm {farm_id}: {established_date_iso}")
                        established_dt = datetime.now() # Fallback or handle as error

                self._farms[farm_id] = Farm(
                    farm_id=farm_dict['farm_id'],
                    name=farm_dict['name'],
                    location=farm_dict['location'],
                    owner=farm_dict['owner'],
                    capacity=farm_dict['capacity'],
                    established_date=established_dt,
                    notes=farm_dict.get('notes', "")
                )
                # Note: farm.flocks are NOT loaded here. They are managed by FlockRepository.
                # A higher level service or the UI layer would be responsible for associating loaded flocks
                # from FlockRepository with these farm objects.
        except (IOError, json.JSONDecodeError) as e:
            print(f"Error loading farms from {FARMS_FILE}: {e}. Starting with empty farm list.")
            self._farms = {}

    def _save_farms(self):
        """Saves the current state of farms to the JSON file."""
        self._ensure_data_dir_exists()
        data_to_save = {}
        for farm_id, farm_obj in self._farms.items():
            # We need to serialize Farm objects to dicts.
            # The farm_obj.to_dict() method is useful here.
            # However, farm.flocks contains Flock objects. For saving farms,
            # we should only store flock IDs, or not store them at all if FlockRepository is solely responsible.
            # For now, let's use a simplified farm.to_dict() that doesn't try to serialize full Flock objects.
            farm_data_serializable = {
                "farm_id": farm_obj.farm_id,
                "name": farm_obj.name,
                "location": farm_obj.location,
                "owner": farm_obj.owner,
                "capacity": farm_obj.capacity,
                "established_date": farm_obj.established_date.isoformat() if farm_obj.established_date else None,
                "notes": farm_obj.notes,
                # Storing only flock_ids to avoid circular dependencies or complex object serialization here.
                # The actual Flock objects are managed by FlockRepository.
                "flock_ids": [flock.flock_id for flock in farm_obj.flocks]
            }
            data_to_save[farm_id] = farm_data_serializable

        try:
            with open(FARMS_FILE, 'w') as f:
                json.dump(data_to_save, f, indent=4)
        except IOError as e:
            print(f"Error saving farms to {FARMS_FILE}: {e}")


    def _generate_id(self) -> str:
        """Generates a unique ID for a new farm."""
        return f"farm-{uuid.uuid4()}"

    def add_farm(self, name: str, location: str, owner: str, capacity: int,
                 established_date: Optional[datetime] = None, notes: str = "") -> Farm:
        """
        Creates a new farm and adds it to the repository.
        """
        farm_id = self._generate_id()
        if not established_date:
            established_date = datetime.now()

        farm = Farm(
            farm_id=farm_id,
            name=name,
            location=location,
            owner=owner,
            capacity=capacity,
            established_date=established_date,
            notes=notes
        )
        self._farms[farm_id] = farm
        print(f"Repository: Added farm {farm.name} with ID {farm.farm_id}") # For logging/debugging
        self._save_farms()
        return farm

    def get_farm_by_id(self, farm_id: str) -> Optional[Farm]:
        """Retrieves a farm by its ID."""
        return self._farms.get(farm_id)

    def get_all_farms(self) -> List[Farm]:
        """Retrieves all farms."""
        return list(self._farms.values())

    def update_farm(self, farm_id: str, **kwargs) -> Optional[Farm]:
        """
        Updates an existing farm's attributes.
        kwargs can include name, location, owner, capacity, notes.
        """
        farm = self.get_farm_by_id(farm_id)
        if farm:
            for key, value in kwargs.items():
                if hasattr(farm, key):
                    setattr(farm, key, value)
                elif key == "established_date_iso" and value: # Handle date string update
                    try:
                        setattr(farm, "established_date", datetime.fromisoformat(value))
                    except ValueError:
                        print(f"Warning: Could not parse established_date_iso: {value}")
            self._save_farms()
            print(f"Repository: Updated farm {farm.name} with ID {farm.farm_id}") # For logging/debugging
            return farm
        return None

    def delete_farm(self, farm_id: str) -> bool:
        """
        Deletes a farm from the repository.
        Returns True if deletion was successful, False otherwise.
        """
        if farm_id in self._farms:
            deleted_farm_name = self._farms[farm_id].name
            del self._farms[farm_id]
            self._save_farms()
            print(f"Repository: Deleted farm {deleted_farm_name} with ID {farm_id}") # For logging/debugging
            return True
        return False

    # --- Flock Management within a Farm Context (Basic) ---
    # More advanced flock management might go into a dedicated FlockRepository
    # or be expanded here if requirements are simple.

    def add_flock_to_farm(self, farm_id: str, flock: Flock) -> bool:
        """
        Adds a flock to a specific farm.
        Assumes flock object is already created.
        """
        farm = self.get_farm_by_id(farm_id)
        if farm:
            if flock.flock_id not in [f.flock_id for f in farm.flocks]:
                farm.add_flock(flock) # Farm model's method handles linking
                flock.farm_id = farm_id # Ensure flock is linked to farm
                self._save_farms() # Save farm because its flock list changed
                print(f"Repository: Added flock {flock.flock_id} to farm {farm.name}")
                return True
            else:
                print(f"Repository: Flock {flock.flock_id} already exists in farm {farm.name}")
                return False # Flock already exists in this farm
        return False # Farm not found

    def get_flocks_for_farm(self, farm_id: str) -> List[Flock]:
        """Retrieves all flocks associated with a specific farm."""
        farm = self.get_farm_by_id(farm_id)
        if farm:
            return farm.flocks
        return []

    def remove_flock_from_farm(self, farm_id: str, flock_id: str) -> bool:
        """Removes a flock from a specific farm."""
        farm = self.get_farm_by_id(farm_id)
        if farm:
            initial_flock_count = len(farm.flocks)
            farm.remove_flock(flock_id)
            if len(farm.flocks) < initial_flock_count:
                self._save_farms() # Save farm because its flock list changed
                print(f"Repository: Removed flock {flock_id} from farm {farm.name}")
                return True
            else:
                print(f"Repository: Flock {flock_id} not found in farm {farm.name}")
                return False # Flock not found in this farm
        return False # Farm not found

    def search_farms(self, search_term: str) -> List[Farm]:
        """
        Searches farms by name or location.
        Simple case-insensitive search.
        """
        if not search_term:
            return self.get_all_farms()

        search_term_lower = search_term.lower()
        return [
            farm for farm in self._farms.values()
            if search_term_lower in farm.name.lower() or \
               search_term_lower in farm.location.lower() or \
               search_term_lower in farm.owner.lower()
        ]

# Example Usage (for testing repository functionality directly)
if __name__ == '__main__':
    repo = FarmRepository()

    # Add farms
    farm1 = repo.add_farm("Green Valley Poultry", "Rural Route 1, Krishna", "Mr. Patel", 2000)
    farm2 = repo.add_farm("Sunrise Eggs", "Near NH65, Krishna", "Mrs. Devi", 10000, notes="Focus on organic eggs")
    repo.add_farm("Modern Poultry Farm", "Industrial Area, Vijayawada", "Krishna AgroVet Ltd", 50000)


    print("\n--- All Farms ---")
    for f in repo.get_all_farms():
        print(f.to_dict())

    print("\n--- Get Farm by ID (farm1's ID) ---")
    retrieved_farm = repo.get_farm_by_id(farm1.farm_id)
    if retrieved_farm:
        print(retrieved_farm.to_dict())

    print("\n--- Update Farm (farm1) ---")
    repo.update_farm(farm1.farm_id, capacity=2500, notes="Upgraded housing")
    if retrieved_farm:
        print(retrieved_farm.to_dict())

    print("\n--- Search Farms (Krishna) ---")
    for f in repo.search_farms("Krishna"):
        print(f.to_dict())

    print("\n--- Search Farms (Organic) ---")
    for f in repo.search_farms("organic"): # testing notes search if implemented or part of general search
        print(f.to_dict())


    # Example of adding a flock (Flock model would need to be defined and imported)
    # from farm_management.models.flock import Flock # Ensure this import is correct
    # from datetime import date
    #
    # if retrieved_farm:
    #     flock_example = Flock(flock_id="temp-flock-001", farm_id=retrieved_farm.farm_id, breed="Broiler", acquisition_date=date.today(), initial_count=500)
    #     repo.add_flock_to_farm(retrieved_farm.farm_id, flock_example)
    #     print(f"\n--- Flocks for {retrieved_farm.name} ---")
    #     for flk in repo.get_flocks_for_farm(retrieved_farm.farm_id):
    #         print(flk.to_dict())


    print("\n--- Delete Farm (farm2's ID) ---")
    repo.delete_farm(farm2.farm_id)

    print("\n--- All Farms After Deletion ---")
    for f in repo.get_all_farms():
        print(f.to_dict())

    print("\n--- Test Search (Empty string - should return all) ---")
    all_farms = repo.search_farms("")
    print(f"Found {len(all_farms)} farms.")

    print("\n--- Test Search (Non-existent term) ---")
    no_farms = repo.search_farms("NonExistentXYZ")
    print(f"Found {len(no_farms)} farms.")

    # Test adding a farm with specific established date
    from datetime import datetime
    specific_date = datetime(2020, 5, 10)
    farm_with_date = repo.add_farm("Historic Farm", "Old Town", "Legacy Group", 500, established_date=specific_date)
    retrieved_historic = repo.get_farm_by_id(farm_with_date.farm_id)
    if retrieved_historic:
        print(f"\n--- Farm with specific established date: {retrieved_historic.to_dict()} ---")

    # Test updating established date via ISO string (if implemented in update_farm)
    # repo.update_farm(farm_with_date.farm_id, established_date_iso="2019-01-15T00:00:00")
    # if retrieved_historic:
    #     print(f"Updated established date: {retrieved_historic.established_date}")

    print(f"\nTotal farms in repository: {len(repo.get_all_farms())}")
