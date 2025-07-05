import datetime
import random

class PrescriptionManagementScreen:
    def __init__(self):
        self.prescriptions = [] # List of all prescriptions
        # Mock data - In a real system, these would come from other modules/DB
        self.known_vet_ids = [f"vet{str(i).zfill(3)}" for i in range(1, 4)]
        self.known_vet_names = {"vet001": "Dr. Alice Smith", "vet002": "Dr. Bob Johnson", "vet003": "Dr. Carol White"}
        self.known_patient_ids = [f"pet{str(i).zfill(3)}" for i in range(1, 6)]
        self.known_patient_names = {"pet001": "Bessie", "pet002": "Charlie", "pet003": "Daisy", "pet004": "Rocky", "pet005": "Whiskers"}
        self.known_user_ids = [f"farmer{str(i).zfill(3)}" for i in range(1, 6)] # Owners

        self.medications_catalog = [ # Simplified catalog
            {"name": "Amoxicillin 250mg tablets", "unit": "tablet", "requires_vet_auth": True},
            {"name": "Meloxicam 1mg/ml Oral Suspension", "unit": "ml", "requires_vet_auth": True},
            {"name": "Ivermectin Pour-On", "unit": "ml", "requires_vet_auth": True},
            {"name": "Saline Eye Wash", "unit": "bottle", "requires_vet_auth": False},
            {"name": "Medicated Shampoo", "unit": "bottle", "requires_vet_auth": False},
            {"name": "Fenbendazole Granules 22.2%", "unit": "gram", "requires_vet_auth": True}
        ]
        self._initialize_mock_prescriptions()

    def _initialize_mock_prescriptions(self, count=5):
        for i in range(count):
            vet_id = random.choice(self.known_vet_ids)
            patient_id = random.choice(self.known_patient_ids)
            user_id = f"farmer{str(random.randint(1,5)).zfill(3)}" # Mock owner
            med_info = random.choice(self.medications_catalog)

            # Ensure only authorized meds are prescribed if vet auth is required
            if med_info["requires_vet_auth"]:
                status = random.choice(["issued", "dispensed", "cancelled"])
            else: # OTC meds might not always be "prescribed" but could be "recommended"
                status = random.choice(["recommended", "dispensed"])

            prescription_date = datetime.datetime.now() - datetime.timedelta(days=random.randint(0, 90))

            new_prescription = {
                "prescription_id": f"rx_{prescription_date.strftime('%Y%m%d')}_{random.randint(1000,9999)}",
                "vet_id": vet_id,
                "vet_name": self.known_vet_names.get(vet_id, "Unknown Vet"),
                "patient_id": patient_id,
                "patient_name": self.known_patient_names.get(patient_id, "Unknown Patient"),
                "user_id": user_id, # Owner
                "date_issued": prescription_date.isoformat(),
                "medication_name": med_info["name"],
                "dosage": f"{random.randint(1,3)} {med_info['unit']}(s)",
                "frequency": random.choice(["Once daily", "Twice daily", "As needed", "Every 8 hours"]),
                "duration": f"{random.randint(3, 14)} days" if med_info["requires_vet_auth"] else "Until symptoms resolve",
                "quantity_dispensed": None, # To be filled if status is 'dispensed'
                "dispensing_pharmacy_id": None, # If applicable
                "instructions": random.choice(["Administer with food.", "Keep refrigerated.", "Shake well before use.", "For external use only."]),
                "refills_allowed": random.randint(0,2) if med_info["requires_vet_auth"] else 0,
                "refills_remaining": 0, # Will be set based on allowed
                "status": status, # issued, dispensed, cancelled, expired, recommended
                "notes": "Ensure full course is completed." if med_info["requires_vet_auth"] else "Follow label directions."
            }
            new_prescription["refills_remaining"] = new_prescription["refills_allowed"]
            if status == "dispensed":
                new_prescription["quantity_dispensed"] = f"{random.randint(10,50)} {med_info['unit']}(s)" # Mock quantity
                new_prescription["dispensing_pharmacy_id"] = f"pharm{random.randint(1,3)}"
                new_prescription["date_dispensed"] = (prescription_date + datetime.timedelta(days=random.randint(0,2))).isoformat()

            self.prescriptions.append(new_prescription)
        self.prescriptions.sort(key=lambda x: x["date_issued"], reverse=True)

    def create_prescription(self, vet_id, patient_id, user_id, medication_name, dosage, frequency, duration, instructions, refills_allowed=0, notes=""):
        if vet_id not in self.known_vet_ids:
            print(f"Error: Veterinarian {vet_id} not authorized or found.")
            return None

        med_info = next((med for med in self.medications_catalog if med["name"] == medication_name), None)
        if not med_info:
            print(f"Error: Medication '{medication_name}' not found in catalog.")
            return None
        if med_info["requires_vet_auth"] and vet_id not in self.known_vet_ids : # Redundant check here, but good for logic
             print(f"Error: Veterinarian {vet_id} not authorized to prescribe {medication_name}.")
             return None

        prescription_date = datetime.datetime.now()
        new_rx = {
            "prescription_id": f"rx_{prescription_date.strftime('%Y%m%d%H%M%S')}_{random.randint(100,999)}",
            "vet_id": vet_id,
            "vet_name": self.known_vet_names.get(vet_id, "Unknown Vet"),
            "patient_id": patient_id,
            "patient_name": self.known_patient_names.get(patient_id, "Unknown Patient"),
            "user_id": user_id,
            "date_issued": prescription_date.isoformat(),
            "medication_name": medication_name,
            "dosage": dosage,
            "frequency": frequency,
            "duration": duration,
            "instructions": instructions,
            "refills_allowed": refills_allowed,
            "refills_remaining": refills_allowed,
            "status": "issued",
            "notes": notes,
            "quantity_dispensed": None,
            "dispensing_pharmacy_id": None
        }
        self.prescriptions.insert(0, new_rx) # Add to top of the list
        print(f"Prescription {new_rx['prescription_id']} created for {medication_name} for patient {patient_id} by {vet_id}.")
        return new_rx["prescription_id"]

    def get_prescription_details(self, prescription_id):
        for rx in self.prescriptions:
            if rx["prescription_id"] == prescription_id:
                return rx
        return None

    def get_prescriptions_for_patient(self, patient_id, status_filter=None):
        results = [rx for rx in self.prescriptions if rx["patient_id"] == patient_id]
        if status_filter:
            results = [rx for rx in results if rx["status"] == status_filter]
        return results

    def get_prescriptions_by_vet(self, vet_id, status_filter=None):
        results = [rx for rx in self.prescriptions if rx["vet_id"] == vet_id]
        if status_filter:
            results = [rx for rx in results if rx["status"] == status_filter]
        return results

    def update_prescription_status(self, prescription_id, new_status, admin_id_or_pharmacy_id, quantity_dispensed=None):
        rx = self.get_prescription_details(prescription_id)
        if not rx:
            print(f"Error: Prescription {prescription_id} not found.")
            return False

        valid_statuses = ["issued", "dispensed", "cancelled", "expired", "recommended"]
        if new_status not in valid_statuses:
            print(f"Error: Invalid status '{new_status}'.")
            return False

        rx["status"] = new_status
        rx["last_updated_by"] = admin_id_or_pharmacy_id
        rx["last_updated_at"] = datetime.datetime.now().isoformat()

        if new_status == "dispensed":
            if not quantity_dispensed:
                print("Error: Quantity dispensed must be provided for 'dispensed' status.")
                # Revert status if not provided, or handle as partial dispense
                # For now, just warn. A real system would be stricter.
            rx["quantity_dispensed"] = quantity_dispensed
            rx["dispensing_pharmacy_id"] = admin_id_or_pharmacy_id # Assuming pharmacy ID is passed
            rx["date_dispensed"] = datetime.datetime.now().isoformat()
            if rx["refills_remaining"] > 0 :
                rx["refills_remaining"] -=1 # This logic might be more complex (e.g. full vs partial)

        print(f"Prescription {prescription_id} status updated to '{new_status}' by {admin_id_or_pharmacy_id}.")
        return True

    def display_screen(self):
        print("---- Prescription Management Screen ----")

        print("\n-- Create New Prescription (Example) --")
        # Select a vet, patient, user for the new prescription
        demo_vet_id = self.known_vet_ids[0]
        demo_patient_id = self.known_patient_ids[0]
        demo_user_id = self.known_user_ids[0]
        demo_med_info = self.medications_catalog[0] # Amoxicillin

        new_rx_id = self.create_prescription(
            vet_id=demo_vet_id,
            patient_id=demo_patient_id,
            user_id=demo_user_id,
            medication_name=demo_med_info["name"],
            dosage=f"1 {demo_med_info['unit']}",
            frequency="Twice daily",
            duration="7 days",
            instructions="Administer with a small amount of food.",
            refills_allowed=1,
            notes="Re-evaluate if no improvement in 3 days."
        )
        if new_rx_id:
            print(f"  Successfully created prescription: {new_rx_id}")

        print("\n-- Prescriptions for Patient (e.g., pet001 - Bessie, Max 2) --")
        patient_rxs = self.get_prescriptions_for_patient(self.known_patient_ids[0])[:2]
        if not patient_rxs:
            print(f"  No prescriptions found for patient {self.known_patient_ids[0]}.")
        else:
            for rx in patient_rxs:
                print(f"  ID: {rx['prescription_id']}, Med: {rx['medication_name']}, Date: {rx['date_issued'][:10]}, Status: {rx['status']}")
                print(f"    Dosage: {rx['dosage']} {rx['frequency']} for {rx['duration']}. Refills Left: {rx['refills_remaining']}")

        print("\n-- Update Prescription Status (Example) --")
        if new_rx_id: # Use the newly created one if available
            target_rx_id_for_update = new_rx_id
        elif self.prescriptions: # Else pick first available
             target_rx_id_for_update = self.prescriptions[0]["prescription_id"]
        else:
            target_rx_id_for_update = None

        if target_rx_id_for_update:
            print(f"  Attempting to mark prescription {target_rx_id_for_update} as 'dispensed'.")
            # Find the unit for quantity_dispensed
            rx_to_update = self.get_prescription_details(target_rx_id_for_update)
            med_info_for_dispense = next((m for m in self.medications_catalog if m["name"] == rx_to_update["medication_name"]), {"unit":"units"})

            self.update_prescription_status(target_rx_id_for_update, "dispensed", "pharmacy001", quantity_dispensed=f"14 {med_info_for_dispense['unit']}")
            updated_rx = self.get_prescription_details(target_rx_id_for_update)
            if updated_rx:
                 print(f"  New status: {updated_rx['status']}, Dispensed Qty: {updated_rx['quantity_dispensed']}")
        else:
            print("  No prescription available to demonstrate status update.")

        print("------------------------------------")

if __name__ == '__main__':
    screen = PrescriptionManagementScreen()
    screen.display_screen()
