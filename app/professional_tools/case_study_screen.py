import datetime
import random

class CaseStudyScreen:
    def __init__(self):
        self.case_studies = [] # List of case study dictionaries
        # Mock data - In a real system, these would come from other modules/DB
        self.known_vet_ids = [f"vet{str(i).zfill(3)}" for i in range(1, 4)]
        self.known_vet_names = {"vet001": "Dr. Alice Smith", "vet002": "Dr. Bob Johnson", "vet003": "Dr. Carol White"}
        self.species_list = ["Cow", "Dog", "Horse", "Cat", "Sheep", "Pig", "Chicken"]
        self.mock_diagnoses = ["Atypical Pneumonia", "Chronic Laminitis", "Resistant Mastitis", "Unusual Foreign Body Ingestion", "Complex Fracture Repair", "Rare Parasitic Infection"]

        self._initialize_mock_case_studies(count=5)

    def _initialize_mock_case_studies(self, count=3):
        for i in range(count):
            vet_id = random.choice(self.known_vet_ids)
            species = random.choice(self.species_list)
            diagnosis = random.choice(self.mock_diagnoses)
            date_of_presentation = datetime.date.today() - datetime.timedelta(days=random.randint(30, 365))
            date_resolved_or_closed = date_of_presentation + datetime.timedelta(days=random.randint(7, 90))

            study_id = f"cs_{date_of_presentation.strftime('%Y%m%d')}_{random.randint(100,999)}"

            # Mock patient signalment
            age_years = random.randint(1,10)
            age_months = random.randint(0,11)
            age_str = f"{age_years} years" if age_years > 0 else ""
            if age_months > 0 : age_str += f" {age_months} months" if age_years > 0 else f"{age_months} months"
            if not age_str: age_str = "Unknown"


            case_study = {
                "study_id": study_id,
                "title": f"Case Study: {diagnosis} in a {species}",
                "author_vet_id": vet_id,
                "author_vet_name": self.known_vet_names.get(vet_id, "Unknown Vet"),
                "date_created": (date_resolved_or_closed + datetime.timedelta(days=random.randint(1,10))).isoformat(), # Date study was written
                "status": random.choice(["draft", "published", "peer_review"]), # draft, published, peer_review, archived
                "confidentiality": random.choice(["anonymous_patient", "owner_consent_given"]), # For data privacy

                "patient_signalment": {
                    "species": species,
                    "breed": random.choice(["Angus", "Labrador", "Thoroughbred", "Siamese", "Merino", "Yorkshire", "Leghorn", "Mixed"]),
                    "age": age_str.strip(),
                    "sex": random.choice(["Male", "Female", "Male Castrated", "Female Spayed"]),
                    "weight_kg": round(random.uniform(3, 700),1)
                },
                "date_of_presentation": date_of_presentation.isoformat(),
                "presenting_complaint": f"Patient presented with {random.choice(['severe lethargy', 'non-weight bearing lameness', 'rapid weight loss', 'unresponsive fever', 'acute abdominal pain'])}.",
                "history": "Owner reported symptoms started X days ago. No significant prior medical history noted / Relevant prior history includes Y.",
                "diagnostic_workup": [ # List of procedures/tests
                    {"test": "Physical Examination", "findings": "Key findings A, B, C."},
                    {"test": "Bloodwork (CBC/Chemistry)", "findings": "Elevated WBC, other values WNL / Specific abnormalities noted."},
                    {"test": "Radiographs", "findings": "Evidence of X found in Y location."} if random.random() > 0.5 else None,
                    {"test": "Ultrasound", "findings": "Fluid accumulation observed."} if random.random() > 0.6 else None,
                ],
                "diagnosis": diagnosis,
                "differential_diagnoses": [f"DDx1: {random.choice(self.mock_diagnoses)}", f"DDx2: {random.choice(self.mock_diagnoses)}"],

                "treatment_protocol": f"Initial treatment involved {random.choice(['IV fluids and antibiotics', 'surgical intervention', 'strict stall rest and pain management'])}. " \
                                   f"Followed by {random.choice(['oral medication course', 'physiotherapy', 'dietary changes'])}.",
                "treatment_challenges": random.choice(["None", "Owner compliance", "Adverse drug reaction (minor)", "Slow initial response"]),

                "outcome": {
                    "status": random.choice(["Full Recovery", "Partial Recovery", "Condition Managed", "Euthanized (humane reasons)", "Died"]),
                    "date_resolved_or_closed": date_resolved_or_closed.isoformat(),
                    "long_term_follow_up": "Patient doing well at 6-month check-up / Requires ongoing medication."
                },
                "discussion_and_learning_points": "This case highlights the importance of A in diagnosing B. Key learning: always consider C when presented with D.",
                "attachments": [ # Placeholders for file links/references
                    {"type": "image", "filename": "radiograph_lesion.jpg", "caption": "Radiograph showing lesion"} if random.random() > 0.4 else None,
                    {"type": "lab_report", "filename": "bloodwork_results.pdf", "caption": "Full blood panel"} if random.random() > 0.5 else None,
                ],
                "keywords": [species, diagnosis.split(" ")[0], random.choice(["surgery", "medicine", "diagnostics"])],
                "citations": ["Reference to similar case or textbook (Author, Year)"] if random.random() > 0.7 else [],
                "view_count": random.randint(0, 200),
                "peer_comments": [] # For published studies
            }
            # Clean up None attachments
            case_study["attachments"] = [att for att in case_study["attachments"] if att is not None]
            case_study["diagnostic_workup"] = [dw for dw in case_study["diagnostic_workup"] if dw is not None]

            self.case_studies.append(case_study)
        self.case_studies.sort(key=lambda x: x["date_created"], reverse=True)

    def create_case_study(self, vet_id, title, patient_signalment, presentation_details, diagnosis, treatment, outcome, discussion, keywords=None, status="draft"):
        if vet_id not in self.known_vet_ids:
            print(f"Error: Vet {vet_id} not authorized to create case studies.")
            return None

        study_id = f"cs_{datetime.datetime.now().strftime('%Y%m%d%H%M%S')}_{random.randint(100,999)}"
        date_created = datetime.date.today().isoformat()

        new_study = {
            "study_id": study_id,
            "title": title,
            "author_vet_id": vet_id,
            "author_vet_name": self.known_vet_names.get(vet_id, "Unknown Vet"),
            "date_created": date_created,
            "status": status,
            "confidentiality": "anonymous_patient", # Default
            "patient_signalment": patient_signalment, # Expects a dict
            "date_of_presentation": presentation_details.get("date_of_presentation", date_created), # Expects dict
            "presenting_complaint": presentation_details.get("complaint", ""),
            "history": presentation_details.get("history", ""),
            "diagnostic_workup": presentation_details.get("workup", []),
            "diagnosis": diagnosis,
            "differential_diagnoses": presentation_details.get("ddx", []),
            "treatment_protocol": treatment.get("protocol", ""),
            "treatment_challenges": treatment.get("challenges", "None"),
            "outcome": outcome, # Expects a dict {status, date_resolved, follow_up}
            "discussion_and_learning_points": discussion,
            "attachments": [],
            "keywords": keywords if keywords else [patient_signalment.get("species","General"), diagnosis.split(" ")[0]],
            "citations": [],
            "view_count": 0,
            "peer_comments": []
        }
        self.case_studies.insert(0, new_study)
        print(f"Case study '{title}' (ID: {study_id}) created by {vet_id} with status '{status}'.")
        return study_id

    def get_case_study_by_id(self, study_id):
        for study in self.case_studies:
            if study["study_id"] == study_id:
                study["view_count"] +=1 # Increment view
                return study
        return None

    def search_case_studies(self, query_term=None, species_filter=None, diagnosis_filter=None, status_filter="published", limit=5):
        results = [study for study in self.case_studies if study["status"] == status_filter or status_filter is None] # Default to published

        if query_term:
            qt_lower = query_term.lower()
            results = [s for s in results if qt_lower in s["title"].lower() or \
                       qt_lower in s["diagnosis"].lower() or \
                       qt_lower in s["discussion_and_learning_points"].lower() or \
                       any(qt_lower in kw.lower() for kw in s.get("keywords",[]))]
        if species_filter:
            results = [s for s in results if s["patient_signalment"].get("species","").lower() == species_filter.lower()]
        if diagnosis_filter:
            results = [s for s in results if diagnosis_filter.lower() in s["diagnosis"].lower()]

        return results[:limit]

    def add_comment_to_study(self, study_id, vet_id, comment_text):
        study = self.get_case_study_by_id(study_id)
        if not study:
            print(f"Error: Case study {study_id} not found.")
            return False
        if study["status"] != "published":
            print(f"Error: Comments can only be added to 'published' case studies.")
            return False

        comment = {
            "comment_id": f"cmt_{datetime.datetime.now().strftime('%Y%m%d%H%M%S')}",
            "vet_id": vet_id,
            "vet_name": self.known_vet_names.get(vet_id, "Unknown Vet"),
            "comment_text": comment_text,
            "timestamp": datetime.datetime.now().isoformat()
        }
        study["peer_comments"].append(comment)
        print(f"Comment added to case study {study_id} by {vet_id}.")
        return True

    def display_screen(self, current_vet_id="vet001"):
        print("---- Case Study Screen ----")

        print("\n-- Recently Published Case Studies (Top 2) --")
        recent_studies = self.search_case_studies(status_filter="published", limit=2)
        if not recent_studies:
            print("  No published case studies found.")
        else:
            for study in recent_studies:
                print(f"  ID: {study['study_id']}, Title: {study['title']}")
                print(f"    Author: {study['author_vet_name']}, Species: {study['patient_signalment']['species']}, Diagnosis: {study['diagnosis']}")
                print(f"    Outcome: {study['outcome']['status']}, Views: {study['view_count']}")
                if study['peer_comments']: print(f"    Comments: {len(study['peer_comments'])}")

        print("\n-- Search Example: 'Laminitis' in 'Horse' --")
        search_results = self.search_case_studies(species_filter="Horse", diagnosis_filter="Laminitis")
        if not search_results:
            print("  No case studies found for 'Laminitis' in 'Horse'.")
        else:
            for study in search_results[:1]: # Show 1 for brevity
                print(f"  Title: {study['title']} (ID: {study['study_id']})")
                print(f"    Author: {study['author_vet_name']}, Published: {study['date_created']}")

        print("\n-- Create New Case Study (Example) --")
        # Mock data for a new case study
        mock_signalment = {"species": "Dog", "breed": "Golden Retriever", "age": "5 years", "sex": "Female Spayed", "weight_kg": 30.5}
        mock_presentation = {"date_of_presentation": (datetime.date.today() - datetime.timedelta(days=30)).isoformat(),
                             "complaint": "Sudden onset of severe abdominal pain and distension.", "history": "Previously healthy."}
        mock_treatment = {"protocol": "Emergency surgery for GDV, gastropexy performed.", "challenges": "Post-operative arrhythmia managed with medication."}
        mock_outcome = {"status": "Full Recovery", "date_resolved": (datetime.date.today() - datetime.timedelta(days=15)).isoformat(), "follow_up": "Excellent at 2-week post-op check."}

        new_study_id = self.create_case_study(
            vet_id=current_vet_id,
            title="Successful Surgical Management of GDV in a Golden Retriever",
            patient_signalment=mock_signalment,
            presentation_details=mock_presentation,
            diagnosis="Gastric Dilatation-Volvulus (GDV)",
            treatment=mock_treatment,
            outcome=mock_outcome,
            discussion="Rapid diagnosis and surgical intervention were key to the positive outcome. This case underscores the need for owner education on GDV risks.",
            keywords=["Dog", "GDV", "Surgery", "Emergency"],
            status="published" # Publish directly for demo
        )
        if new_study_id:
            print(f"  Example case study created with ID: {new_study_id}")
            self.add_comment_to_study(new_study_id, random.choice(self.known_vet_ids), "Great case, very informative. Thanks for sharing!")

            # Display the newly added study
            added_item = self.get_case_study_by_id(new_study_id)
            if added_item:
                 print(f"    Newly added: '{added_item['title']}' by {added_item['author_vet_name']}, Status: {added_item['status']}")
                 if added_item['peer_comments']: print(f"      Comment: '{added_item['peer_comments'][0]['comment_text']}'")


        print("-----------------------------")

if __name__ == '__main__':
    screen = CaseStudyScreen()
    screen.display_screen(current_vet_id="vet002")
