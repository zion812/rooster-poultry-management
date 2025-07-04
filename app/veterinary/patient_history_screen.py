import datetime
import random

class PatientHistoryScreen:
    def __init__(self):
        # patient_id: {details: {}, records: []}
        self.patient_database = {}
        self._initialize_mock_patient_data()

    def _initialize_mock_patient_data(self):
        # Shared across different mock initializations if needed
        self.vet_names = ["Dr. Alice Smith", "Dr. Bob Johnson", "Dr. Carol White"]
        self.farmer_user_ids = [f"farmer{str(i).zfill(3)}" for i in range(1, 6)]

        # Patient details
        patients_info = [
            {"patient_id": "pet001", "name": "Bessie", "species": "Cow", "breed": "Holstein", "dob": "2018-03-15", "owner_id": self.farmer_user_ids[0], "gender": "Female"},
            {"patient_id": "pet002", "name": "Charlie", "species": "Dog", "breed": "Labrador", "dob": "2020-07-22", "owner_id": self.farmer_user_ids[1], "gender": "Male"},
            {"patient_id": "pet003", "name": "Daisy", "species": "Cow", "breed": "Jersey", "dob": "2019-05-10", "owner_id": self.farmer_user_ids[0], "gender": "Female"},
            {"patient_id": "pet004", "name": "Rocky", "species": "Horse", "breed": "Quarter Horse", "dob": "2017-01-30", "owner_id": self.farmer_user_ids[2], "gender": "Male", "color": "Bay"},
            {"patient_id": "pet005", "name": "Whiskers", "species": "Cat", "breed": "Siamese", "dob": "2021-11-05", "owner_id": self.farmer_user_ids[3], "gender": "Female", "microchip_id": "A123456789"}
        ]

        for info in patients_info:
            self.patient_database[info["patient_id"]] = {
                "details": info,
                "records": [] # To be populated next
            }

        # Mock medical records for each patient
        for patient_id in self.patient_database.keys():
            num_records = random.randint(1, 5)
            for i in range(num_records):
                record_date = datetime.date.today() - datetime.timedelta(days=random.randint(10, 730)) # Records from past 2 years
                record = self._create_mock_medical_record(patient_id, record_date)
                self.patient_database[patient_id]["records"].append(record)
            # Sort records by date, most recent first
            self.patient_database[patient_id]["records"].sort(key=lambda x: x["visit_date"], reverse=True)

    def _create_mock_medical_record(self, patient_id, visit_date):
        species = self.patient_database[patient_id]["details"]["species"]

        # Common issues and treatments (can be expanded based on species)
        common_diagnoses = {
            "Cow": ["Mastitis", "Lameness", "Respiratory Infection", "Metritis"],
            "Dog": ["Otitis Externa", "Skin Allergy", "Gastroenteritis", "Arthritis"],
            "Horse": ["Colic", "Laminitis", "Equine Influenza", "Abscess"],
            "Cat": ["Feline URI", "Dental Disease", "UTI", "Hyperthyroidism"]
        }
        common_treatments = {
            "Cow": ["Antibiotics", "Anti-inflammatory", "Hoof Trim", "Fluid Therapy"],
            "Dog": ["Ear Cleaning Meds", "Steroids", "Diet Change", "Pain Medication"],
            "Horse": ["Banamine", "Stall Rest", "IV Fluids", "Wound Care"],
            "Cat": ["Antibiotics", "Dental Cleaning", "Urinary Diet", "Methimazole"]
        }
        vaccinations_data = {
            "Cow": ["BVD", "IBR", "Leptospirosis", "Clostridial"],
            "Dog": ["Rabies", "DHPP", "Bordetella"],
            "Horse": ["Tetanus", "EEE/WEE", "West Nile Virus", "Rabies"],
            "Cat": ["FVRCP", "Rabies", "FeLV"]
        }

        record_type = random.choice(["check_up", "vaccination", "illness", "injury", "procedure"])
        diagnosis = "N/A"
        treatment_plan = "N/A"
        medications_prescribed = []
        vaccinations_given = []
        notes = ""

        if record_type == "illness" or (record_type == "check_up" and random.random() < 0.3):
            diagnosis = random.choice(common_diagnoses.get(species, ["General Malaise"]))
            treatment_plan = random.choice(common_treatments.get(species, ["Rest and Monitor"]))
            if random.random() < 0.7:
                medications_prescribed.append({"medication": treatment_plan, "dosage": "As directed", "duration": f"{random.randint(3,14)} days"})
            notes += f"Presented with symptoms related to {diagnosis}. "

        if record_type == "vaccination" or (record_type == "check_up" and random.random() < 0.5):
            vacc_type = random.choice(vaccinations_data.get(species, ["General Booster"]))
            vaccinations_given.append({"vaccine_name": vacc_type, "batch_number": f"VB{random.randint(1000,9999)}", "next_due": (visit_date + datetime.timedelta(days=365)).isoformat()})
            notes += f"Administered {vacc_type}. "

        if record_type == "check_up":
            notes += "Routine examination. Overall health good."
            if not diagnosis or diagnosis == "N/A": diagnosis = "Healthy"
            if not treatment_plan or treatment_plan == "N/A": treatment_plan = "Preventative care discussed."


        return {
            "record_id": f"rec_{visit_date.strftime('%Y%m%d')}_{random.randint(100,999)}",
            "visit_date": visit_date.isoformat(),
            "record_type": record_type, # e.g., check-up, vaccination, illness, injury, procedure
            "attending_vet_name": random.choice(self.vet_names),
            "presenting_complaint": random.choice(["Coughing", "Not eating", "Lethargy", "Annual check", "Injury to leg"]) if record_type != "check_up" else "Routine Health Check",
            "diagnosis": diagnosis,
            "treatment_plan": treatment_plan,
            "medications_prescribed": medications_prescribed, # list of dicts {medication, dosage, duration}
            "vaccinations_given": vaccinations_given, # list of dicts {vaccine_name, batch_number, next_due}
            "weight_kg": round(random.uniform(3, 600), 1) if species != "Horse" else round(random.uniform(300, 1000),1),
            "temperature_celsius": round(random.uniform(37.5, 39.5), 1),
            "notes": notes.strip() or "No specific notes for this visit.",
            "follow_up_needed": random.choice([True, False]) if record_type not in ["vaccination"] else False
        }

    def get_patient_full_history(self, patient_id):
        return self.patient_database.get(patient_id)

    def get_patient_details(self, patient_id):
        return self.patient_database.get(patient_id, {}).get("details")

    def get_patient_medical_records(self, patient_id, limit=None):
        records = self.patient_database.get(patient_id, {}).get("records", [])
        if limit:
            return records[:limit]
        return records

    def add_medical_record(self, patient_id, record_data):
        if patient_id not in self.patient_database:
            print(f"Error: Patient {patient_id} not found.")
            return False

        # Basic validation for required fields in record_data (can be expanded)
        required_fields = ["visit_date", "attending_vet_name", "diagnosis", "treatment_plan"]
        if not all(field in record_data for field in required_fields):
            print("Error: Missing required fields in record data.")
            return False

        record_data["record_id"] = f"rec_{datetime.datetime.strptime(record_data['visit_date'], '%Y-%m-%d').strftime('%Y%m%d')}_{random.randint(1000,9999)}"
        self.patient_database[patient_id]["records"].append(record_data)
        self.patient_database[patient_id]["records"].sort(key=lambda x: x["visit_date"], reverse=True)
        print(f"Medical record added for patient {patient_id}.")
        return True

    def display_screen(self, patient_id_to_display="pet001"): # Default to Bessie
        print("---- Patient History Screen ----")

        if not self.patient_database:
            print("  No patient data loaded.")
            return

        # If default patient doesn't exist, pick the first one
        if patient_id_to_display not in self.patient_database:
            patient_id_to_display = list(self.patient_database.keys())[0]
            print(f"  Default patient not found, displaying for: {patient_id_to_display}")


        patient_data = self.get_patient_full_history(patient_id_to_display)
        if not patient_data:
            print(f"  No history found for patient ID: {patient_id_to_display}")
            return

        details = patient_data["details"]
        records = patient_data["records"]

        print(f"\n-- Patient Details: {details['name']} (ID: {details['patient_id']}) --")
        print(f"  Species: {details['species']}, Breed: {details['breed']}")
        print(f"  DOB: {details['dob']}, Gender: {details.get('gender', 'N/A')}")
        print(f"  Owner ID: {details['owner_id']}")
        if "color" in details: print(f"  Color: {details['color']}")
        if "microchip_id" in details: print(f"  Microchip: {details['microchip_id']}")

        print("\n-- Medical Records (Most Recent 2) --")
        if not records:
            print("  No medical records found for this patient.")
        else:
            for record in records[:2]: # Displaying only the most recent 2 for brevity
                print(f"  --- Record ID: {record['record_id']} (Date: {record['visit_date']}) ---")
                print(f"    Type: {record['record_type']}, Vet: {record['attending_vet_name']}")
                print(f"    Complaint: {record.get('presenting_complaint', 'N/A')}")
                print(f"    Diagnosis: {record['diagnosis']}")
                print(f"    Treatment: {record['treatment_plan']}")
                if record.get("medications_prescribed"):
                    print("    Medications:")
                    for med in record["medications_prescribed"]:
                        print(f"      - {med['medication']} ({med['dosage']}, {med['duration']})")
                if record.get("vaccinations_given"):
                    print("    Vaccinations:")
                    for vacc in record["vaccinations_given"]:
                        print(f"      - {vacc['vaccine_name']} (Batch: {vacc.get('batch_number','N/A')}, Next Due: {vacc.get('next_due','N/A')})")
                print(f"    Weight: {record.get('weight_kg','N/A')} kg, Temp: {record.get('temperature_celsius','N/A')} °C")
                print(f"    Notes: {record.get('notes', 'N/A')}")
                print(f"    Follow-up: {'Yes' if record.get('follow_up_needed') else 'No'}")

        print("\n-- Add Medical Record (Example - not actually added in display) --")
        # This is just to show what data would be needed
        sample_new_record = {
            "visit_date": datetime.date.today().isoformat(),
            "record_type": "check_up",
            "attending_vet_name": "Dr. Eve Foster",
            "presenting_complaint": "Annual wellness exam",
            "diagnosis": "Healthy",
            "treatment_plan": "Continue current diet and exercise. Monitor weight.",
            "medications_prescribed": [],
            "vaccinations_given": [{"vaccine_name": "Rabies Booster", "batch_number": "RB101123", "next_due": (datetime.date.today() + datetime.timedelta(days=365*3)).isoformat()}],
            "weight_kg": self.patient_database[patient_id_to_display]["details"].get("weight_kg", 55.5), # Mock based on some patient
            "temperature_celsius": 38.5,
            "notes": "Patient is bright, alert, and responsive. Good body condition.",
            "follow_up_needed": False
        }
        print(f"  Example data for new record for {patient_id_to_display}:")
        print(f"    Visit Date: {sample_new_record['visit_date']}, Vet: {sample_new_record['attending_vet_name']}")
        # self.add_medical_record(patient_id_to_display, sample_new_record) # Uncomment to actually add during demo run

        print("-----------------------------")

if __name__ == '__main__':
    screen = PatientHistoryScreen()
    # You can specify a patient ID to display, e.g., "pet002"
    screen.display_screen(patient_id_to_display="pet003")
