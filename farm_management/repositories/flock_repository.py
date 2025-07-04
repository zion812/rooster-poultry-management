import uuid
import json
import os
from datetime import date # Keep datetime for type hints if necessary, but date is used for acquisition_date
from typing import Optional, List, Dict

from farm_management.models import Flock
from farm_management.models.flock import AgeGroup

DATA_DIR = "farm_management/data" # Consistent with FarmRepository
FLOCKS_FILE = os.path.join(DATA_DIR, "flocks.json")


class FlockRepository:
    """
    Manages storage and retrieval of Flock data.
    Supports persistence to a JSON file.
    """
    def __init__(self):
        self._flocks: Dict[str, Flock] = {}
        os.makedirs(DATA_DIR, exist_ok=True)
        self._load_flocks()

    def _ensure_data_dir_exists(self):
        if not os.path.exists(DATA_DIR):
            os.makedirs(DATA_DIR)

    def _load_flocks(self):
        self._ensure_data_dir_exists()
        if not os.path.exists(FLOCKS_FILE):
            self._flocks = {}
            return

        try:
            with open(FLOCKS_FILE, 'r') as f:
                flocks_data = json.load(f)

            for flock_id, flock_dict in flocks_data.items():
                # Reconstruct Flock object
                acq_date_iso = flock_dict.get("acquisition_date")
                acq_dt = None
                if acq_date_iso:
                    try:
                        acq_dt = date.fromisoformat(acq_date_iso)
                    except ValueError:
                        print(f"Warning: Could not parse acquisition_date for flock {flock_id}: {acq_date_iso}")
                        # Fallback or error handling - for now, might lead to issues if date is critical
                        acq_dt = date.today()

                # The age_group is calculated, so no need to load it directly if not stored.
                # If to_dict() stores it, we can ignore it on load and let _determine_age_group handle it.

                flock_obj = Flock(
                    flock_id=flock_dict['flock_id'],
                    farm_id=flock_dict['farm_id'], # Important for linking back to farm
                    breed=flock_dict['breed'],
                    acquisition_date=acq_dt,
                    initial_count=flock_dict.get('initial_count', 0),
                    source_supplier=flock_dict.get('source_supplier', ""),
                    parent_flock_id_male=flock_dict.get('parent_flock_id_male'),
                    parent_flock_id_female=flock_dict.get('parent_flock_id_female'),
                    notes=flock_dict.get('notes', "")
                )
                # current_count might be stored or recalculated based on mortality records later
                # For now, if to_dict stores it, use it.
                flock_obj.current_count = flock_dict.get('current_count', flock_obj.initial_count)
                flock_obj.age_group = flock_obj._determine_age_group() # Recalculate age group

                self._flocks[flock_id] = flock_obj
        except (IOError, json.JSONDecodeError) as e:
            print(f"Error loading flocks from {FLOCKS_FILE}: {e}. Starting with empty flock list.")
            self._flocks = {}

    def _save_flocks(self):
        """Saves the current state of flocks to the JSON file."""
        self._ensure_data_dir_exists()
        data_to_save = {}
        for flock_id, flock_obj in self._flocks.items():
            # Use flock_obj.to_dict() which should serialize necessary fields
            data_to_save[flock_id] = flock_obj.to_dict()
            # Ensure to_dict() handles date object to ISO string conversion for acquisition_date
            # and age_group is correctly represented if stored (e.g. as string value)

        try:
            with open(FLOCKS_FILE, 'w') as f:
                json.dump(data_to_save, f, indent=4)
        except IOError as e:
            print(f"Error saving flocks to {FLOCKS_FILE}: {e}")

    def _generate_id(self) -> str:
        """Generates a unique ID for a new flock."""
        return f"flock-{uuid.uuid4()}"

    def add_flock(self, farm_id: str, breed: str, acquisition_date: date,
                  initial_count: int, source_supplier: str = "",
                  parent_flock_id_male: Optional[str] = None,
                  parent_flock_id_female: Optional[str] = None,
                  notes: str = "") -> Flock:
        """
        Creates a new flock and adds it to the repository.
        """
        flock_id = self._generate_id()

        flock = Flock(
            flock_id=flock_id,
            farm_id=farm_id,
            breed=breed,
            acquisition_date=acquisition_date,
            initial_count=initial_count,
            source_supplier=source_supplier,
            parent_flock_id_male=parent_flock_id_male,
            parent_flock_id_female=parent_flock_id_female,
            notes=notes
        )
        self._flocks[flock_id] = flock
        print(f"FlockRepository: Added flock {flock.flock_id} for farm {farm_id}")
        self._save_flocks()
        return flock

    def get_flock_by_id(self, flock_id: str) -> Optional[Flock]:
        """Retrieves a flock by its ID."""
        return self._flocks.get(flock_id)

    def get_all_flocks(self) -> List[Flock]:
        """Retrieves all flocks in the system."""
        return list(self._flocks.values())

    def get_flocks_by_farm_id(self, farm_id: str) -> List[Flock]:
        """Retrieves all flocks associated with a specific farm ID."""
        return [flock for flock in self._flocks.values() if flock.farm_id == farm_id]

    def update_flock(self, flock_id: str, **kwargs) -> Optional[Flock]:
        """
        Updates an existing flock's attributes.
        kwargs can include breed, acquisition_date, source_supplier, initial_count, current_count, notes,
        parent_flock_id_male, parent_flock_id_female.
        Age group is determined automatically. Farm_id is generally not updated this way.
        """
        flock = self.get_flock_by_id(flock_id)
        if flock:
            for key, value in kwargs.items():
                if hasattr(flock, key):
                    if key == "acquisition_date" and isinstance(value, str):
                        try:
                            setattr(flock, key, date.fromisoformat(value))
                        except ValueError:
                            print(f"Warning: Could not parse acquisition_date: {value}")
                    else:
                        setattr(flock, key, value)

            # Recalculate age group if acquisition_date changed or for good measure
            flock.age_group = flock._determine_age_group()

            print(f"FlockRepository: Updated flock {flock.flock_id}")
            self._save_flocks()
            return flock
        return None

    def delete_flock(self, flock_id: str) -> bool:
        """
        Deletes a flock from the repository.
        Returns True if deletion was successful, False otherwise.
        """
        if flock_id in self._flocks:
            deleted_flock_id = self._flocks[flock_id].flock_id
            del self._flocks[flock_id]
            print(f"FlockRepository: Deleted flock {deleted_flock_id}")
            self._save_flocks()
            return True
        return False

    def search_flocks(self, farm_id: Optional[str] = None, search_term: str = "") -> List[Flock]:
        """
        Searches flocks by breed or notes.
        If farm_id is provided, search is scoped to that farm.
        Simple case-insensitive search.
        """
        flocks_to_search = []
        if farm_id:
            flocks_to_search = self.get_flocks_by_farm_id(farm_id)
        else:
            flocks_to_search = self.get_all_flocks()

        if not search_term:
            return flocks_to_search

        search_term_lower = search_term.lower()
        return [
            flock for flock in flocks_to_search
            if search_term_lower in flock.breed.lower() or \
               (flock.notes and search_term_lower in flock.notes.lower()) or \
               search_term_lower in flock.flock_id.lower()
        ]

# Example Usage (for testing repository functionality directly)
if __name__ == '__main__':
    flock_repo = FlockRepository()
    FARM_ID_EXAMPLE = "farm-test-123" # Example farm ID

    # Add flocks
    flock1 = flock_repo.add_flock(
        farm_id=FARM_ID_EXAMPLE,
        breed="White Leghorn",
        acquisition_date=date(2023, 1, 10),
        initial_count=500,
        source_supplier="Central Hatcheries"
    )
    flock2 = flock_repo.add_flock(
        farm_id=FARM_ID_EXAMPLE,
        breed="Rhode Island Red",
        acquisition_date=date(2023, 3, 15),
        initial_count=300,
        source_supplier="Local Breeders Inc.",
        notes="Parents are show quality"
    )
    flock_repo.add_flock(
        farm_id="farm-test-456", # Another farm
        breed="Cobb Broiler",
        acquisition_date=date(2023, 5, 20),
        initial_count=1000,
        notes="Fast growing batch"
    )

    print("\n--- All Flocks in System ---")
    for f in flock_repo.get_all_flocks():
        print(f.to_dict())

    print(f"\n--- Flocks for Farm ID: {FARM_ID_EXAMPLE} ---")
    for f in flock_repo.get_flocks_by_farm_id(FARM_ID_EXAMPLE):
        print(f.to_dict())

    print(f"\n--- Get Flock by ID ({flock1.flock_id}) ---")
    retrieved_flock = flock_repo.get_flock_by_id(flock1.flock_id)
    if retrieved_flock:
        print(retrieved_flock.to_dict())

    print(f"\n--- Update Flock ({flock1.flock_id}) ---")
    flock_repo.update_flock(flock1.flock_id, current_count=490, notes="First mortality recorded")
    if retrieved_flock:
        # Ensure current_count was updated
        print(retrieved_flock.to_dict())
        # Manually update initial_count for this demo if not done by update_flock logic
        # flock_repo.update_flock(flock1.flock_id, initial_count=495) # Example of other field

    print(f"\n--- Search Flocks (Farm: {FARM_ID_EXAMPLE}, Term: 'Red') ---")
    for f in flock_repo.search_flocks(farm_id=FARM_ID_EXAMPLE, search_term="Red"):
        print(f.to_dict())

    print(f"\n--- Search Flocks (All farms, Term: 'broiler') ---")
    for f in flock_repo.search_flocks(search_term="broiler"):
        print(f.to_dict())

    print(f"\n--- Delete Flock ({flock2.flock_id}) ---")
    flock_repo.delete_flock(flock2.flock_id)

    print("\n--- All Flocks After Deletion ---")
    for f in flock_repo.get_all_flocks():
        print(f.to_dict())

    # Test adding with parent IDs
    parent_m_id = "flock-male-parent-001"
    parent_f_id = "flock-female-parent-002"
    flock_with_parents = flock_repo.add_flock(
        farm_id=FARM_ID_EXAMPLE,
        breed="Special Cross",
        acquisition_date=date(2024,1,1),
        initial_count=50,
        parent_flock_id_male=parent_m_id,
        parent_flock_id_female=parent_f_id,
        notes="Experimental batch"
    )
    print("\n--- Flock with Parent Info ---")
    retrieved_parented_flock = flock_repo.get_flock_by_id(flock_with_parents.flock_id)
    if retrieved_parented_flock:
        print(retrieved_parented_flock.to_dict())

    print(f"\nTotal flocks in repository: {len(flock_repo.get_all_flocks())}")


    def get_flock_family_tree(self, flock_id: str, max_depth: int = 3) -> Dict:
        """
        Retrieves the family tree for a given flock up to a certain depth.
        Returns a dictionary representing the tree.
        Example: {'id': 'flock1', 'breed': 'Leghorn', 'male_parent': {'id': 'flockM', ...}, 'female_parent': {...}}
        """
        current_flock = self.get_flock_by_id(flock_id)
        if not current_flock:
            return {"id": flock_id, "error": "Flock not found"}

        def build_tree_recursive(current_id: Optional[str], depth: int) -> Optional[Dict]:
            if not current_id or depth > max_depth:
                return None

            flock = self.get_flock_by_id(current_id)
            if not flock:
                return {"id": current_id, "error": "Parent flock not found"}

            tree_node = {
                "id": flock.flock_id,
                "breed": flock.breed,
                "acquisition_date": flock.acquisition_date.isoformat() if flock.acquisition_date else None,
            }

            if depth < max_depth: # Only go deeper if not at max_depth
                male_parent_tree = build_tree_recursive(flock.parent_flock_id_male, depth + 1)
                if male_parent_tree:
                    tree_node["male_parent"] = male_parent_tree

                female_parent_tree = build_tree_recursive(flock.parent_flock_id_female, depth + 1)
                if female_parent_tree:
                    tree_node["female_parent"] = female_parent_tree

            return tree_node

        return build_tree_recursive(flock_id, 1) # Start at depth 1 for the target flock


if __name__ == '__main__':
    flock_repo = FlockRepository()
    FARM_ID_EXAMPLE = "farm-test-123" # Example farm ID

    # Add flocks
    grandpa_m = flock_repo.add_flock(FARM_ID_EXAMPLE, "Grandpa M Breed", date(2020,1,1), 10)
    grandma_m = flock_repo.add_flock(FARM_ID_EXAMPLE, "Grandma M Breed", date(2020,1,1), 10)
    grandpa_f = flock_repo.add_flock(FARM_ID_EXAMPLE, "Grandpa F Breed", date(2020,1,1), 10)
    grandma_f = flock_repo.add_flock(FARM_ID_EXAMPLE, "Grandma F Breed", date(2020,1,1), 10)

    parent_m = flock_repo.add_flock(FARM_ID_EXAMPLE, "Parent M Breed", date(2021,1,1), 20, parent_flock_id_male=grandpa_m.flock_id, parent_flock_id_female=grandma_m.flock_id)
    parent_f = flock_repo.add_flock(FARM_ID_EXAMPLE, "Parent F Breed", date(2021,1,1), 20, parent_flock_id_male=grandpa_f.flock_id, parent_flock_id_female=grandma_f.flock_id)

    child_flock = flock_repo.add_flock(
        farm_id=FARM_ID_EXAMPLE,
        breed="Child Special Cross",
        acquisition_date=date(2024,1,1),
        initial_count=50,
        parent_flock_id_male=parent_m.flock_id,
        parent_flock_id_female=parent_f.flock_id,
        notes="Experimental batch"
    )

    print("\n--- Family Tree for Child Flock ---")
    tree_data = flock_repo.get_flock_family_tree(child_flock.flock_id, max_depth=3)

    import json # For pretty printing the dict
    print(json.dumps(tree_data, indent=2))

    print("\n--- Family Tree for Parent M ( shallower depth )---")
    tree_data_parent = flock_repo.get_flock_family_tree(parent_m.flock_id, max_depth=2)
    print(json.dumps(tree_data_parent, indent=2))

    print("\n--- Family Tree for a flock with no parents ---")
    no_parent_flock = flock_repo.add_flock(FARM_ID_EXAMPLE, "No Parent Breed", date(2022,1,1), 5)
    tree_no_parents = flock_repo.get_flock_family_tree(no_parent_flock.flock_id)
    print(json.dumps(tree_no_parents, indent=2))

    print(f"\nTotal flocks in repository: {len(flock_repo.get_all_flocks())}")
