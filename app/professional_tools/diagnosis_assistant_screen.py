import datetime
import random

class DiagnosisAssistantScreen:
    def __init__(self):
        # This is a very simplified knowledge base. A real system would be vastly more complex.
        self.knowledge_base = {
            "Cow": {
                "symptoms": {
                    "coughing": [
                        {"condition": "Respiratory Infection (BRD Complex)", "confidence": 0.7, "urgency": "High", "next_steps": "Isolate animal, check temperature, consult vet for antibiotics/anti-inflammatory. Ensure good ventilation."},
                        {"condition": "Lungworm", "confidence": 0.4, "urgency": "Medium", "next_steps": "Consider fecal test, consult vet for dewormer. Manage pasture hygiene."},
                        {"condition": "Allergic Bronchitis (rare)", "confidence": 0.2, "urgency": "Low", "next_steps": "Identify and remove potential allergens. Consult vet if persistent."}
                    ],
                    "lameness": [
                        {"condition": "Foot Rot", "confidence": 0.6, "urgency": "Medium", "next_steps": "Clean and inspect foot, apply topical treatment, ensure dry footing. Consult vet if severe."},
                        {"condition": "Digital Dermatitis", "confidence": 0.5, "urgency": "Medium", "next_steps": "Regular footbaths, topical antibiotics. Consult vet for herd management plan."},
                        {"condition": "Injury (Sprain/Fracture)", "confidence": 0.3, "urgency": "High", "next_steps": "Immobilize if possible, consult vet immediately for diagnosis and treatment."}
                    ],
                    "reduced_milk_yield": [
                        {"condition": "Mastitis", "confidence": 0.8, "urgency": "High", "next_steps": "Perform CMT, check for udder inflammation, consult vet for culture and treatment. Review milking procedures."},
                        {"condition": "Ketosis", "confidence": 0.5, "urgency": "Medium", "next_steps": "Check urine/milk for ketones, provide oral glucose precursors. Adjust diet. Consult vet."},
                        {"condition": "General Stress/Illness", "confidence": 0.3, "urgency": "Varies", "next_steps": "Full clinical exam needed to identify underlying cause. Monitor other symptoms."}
                    ],
                    "diarrhea": [
                        {"condition": "Scours (Calves)", "confidence": 0.7, "urgency": "High", "next_steps": "Oral rehydration therapy, isolate calf, ensure hygiene. Consult vet, especially if bloody."},
                        {"condition": "Dietary Indiscretion/Change", "confidence": 0.4, "urgency": "Low-Medium", "next_steps": "Review feed, gradual diet changes. Provide probiotics. Consult vet if severe or prolonged."},
                        {"condition": "Parasitism (e.g., Coccidiosis)", "confidence": 0.5, "urgency": "Medium", "next_steps": "Fecal exam, consult vet for appropriate dewormer/coccidiostat."}
                    ]
                },
                "common_conditions_info": { # Brief info for when a condition is selected
                    "Respiratory Infection (BRD Complex)": "Common in stressed or young cattle. Bacterial/viral. Key signs: cough, fever, nasal discharge.",
                    "Mastitis": "Udder inflammation, usually bacterial. Reduces milk quality and yield. Key signs: clots in milk, swollen/hot udder.",
                }
            },
            "Dog": {
                "symptoms": {
                    "vomiting": [
                        {"condition": "Dietary Indiscretion", "confidence": 0.7, "urgency": "Low-Medium", "next_steps": "Withhold food for 12-24h, then bland diet. Consult vet if persistent, blood, or other signs."},
                        {"condition": "Gastroenteritis", "confidence": 0.5, "urgency": "Medium", "next_steps": "Consult vet for diagnosis and supportive care. May need fluids/meds."},
                        {"condition": "Foreign Body Obstruction", "confidence": 0.3, "urgency": "Critical", "next_steps": "IMMEDIATE vet attention if suspected (e.g., known ingestion, repeated unproductive vomiting)."}
                    ],
                    "lethargy": [
                        {"condition": "Various Infections (Viral/Bacterial)", "confidence": 0.6, "urgency": "Medium-High", "next_steps": "Check temperature, other symptoms. Consult vet for diagnosis."},
                        {"condition": "Pain (e.g., Arthritis, Injury)", "confidence": 0.4, "urgency": "Medium", "next_steps": "Observe for lameness or pain response. Consult vet."},
                        {"condition": "Systemic Disease (e.g., kidney, heart)", "confidence": 0.3, "urgency": "High", "next_steps": "Consult vet for diagnostics (bloodwork, imaging)."}
                    ],
                    "itching_scratching": [
                        {"condition": "Flea Allergy Dermatitis (FAD)", "confidence": 0.7, "urgency": "Medium", "next_steps": "Strict flea control on pet and environment. Consult vet for relief medication."},
                        {"condition": "Atopic Dermatitis (Environmental Allergies)", "confidence": 0.6, "urgency": "Medium", "next_steps": "Consult vet for diagnosis, allergy testing, and management plan (meds, diet, immunotherapy)."},
                        {"condition": "Food Allergy", "confidence": 0.4, "urgency": "Medium", "next_steps": "Consult vet for diet elimination trial."},
                        {"condition": "Sarcoptic Mange", "confidence": 0.3, "urgency": "High (Contagious)", "next_steps": "Consult vet for skin scrape and treatment. Isolate from other animals."}
                    ]
                },
                 "common_conditions_info": {
                    "Dietary Indiscretion": "Caused by eating inappropriate food items. Usually self-limiting.",
                    "Flea Allergy Dermatitis (FAD)": "Allergic reaction to flea saliva. Intense itching, often around tail base.",
                }
            }
            # Can add more species like Horse, Cat, Poultry, Swine etc.
        }
        self.current_session = None # To store inputs for a single diagnostic query

    def start_diagnostic_session(self, species, primary_symptoms, vet_id="vet_user"):
        if species not in self.knowledge_base:
            print(f"Warning: No detailed knowledge base for species: {species}. Results may be limited.")
            # Could offer generic advice or try to match symptoms across species with lower confidence
            return {"error": f"Knowledge base for {species} not available."}

        self.current_session = {
            "session_id": f"diag_{datetime.datetime.now().strftime('%Y%m%d%H%M%S')}",
            "vet_id": vet_id,
            "species": species,
            "primary_symptoms": primary_symptoms if isinstance(primary_symptoms, list) else [primary_symptoms],
            "potential_conditions": [],
            "timestamp": datetime.datetime.now()
        }

        species_kb = self.knowledge_base[species]["symptoms"]
        possible_matches = []

        for symptom in self.current_session["primary_symptoms"]:
            if symptom in species_kb:
                for condition_info in species_kb[symptom]:
                    # Check if this condition is already added from another symptom to adjust confidence
                    existing_match = next((m for m in possible_matches if m["condition"] == condition_info["condition"]), None)
                    if existing_match:
                        # Increase confidence if multiple symptoms point to same condition (very basic logic)
                        existing_match["confidence"] = min(1.0, existing_match["confidence"] + 0.1 * condition_info["confidence"])
                        existing_match["matched_symptoms"].append(symptom)
                    else:
                        match = condition_info.copy()
                        match["matched_symptoms"] = [symptom]
                        possible_matches.append(match)

        # Sort by confidence (higher first)
        possible_matches.sort(key=lambda x: x["confidence"], reverse=True)
        self.current_session["potential_conditions"] = possible_matches

        return self.current_session

    def get_condition_details(self, species, condition_name):
        if species in self.knowledge_base and "common_conditions_info" in self.knowledge_base[species]:
            return self.knowledge_base[species]["common_conditions_info"].get(condition_name, "No detailed information available for this condition.")
        return "Species or condition information not found."

    def display_screen(self):
        print("---- Diagnosis Assistant Screen ----")
        print("Disclaimer: This is a mock assistant and NOT a substitute for professional veterinary diagnosis.\n")

        # Example 1: Cow with coughing
        print("-- Example 1: Cow with Coughing --")
        session1_symptoms = ["coughing"]
        session1_species = "Cow"
        result1 = self.start_diagnostic_session(session1_species, session1_symptoms)

        if "error" in result1:
            print(f"  Error: {result1['error']}")
        elif result1 and result1["potential_conditions"]:
            print(f"  Species: {session1_species}, Symptoms: {', '.join(session1_symptoms)}")
            print("  Potential Conditions (Top 3):")
            for i, p_cond in enumerate(result1["potential_conditions"][:3]):
                print(f"    {i+1}. {p_cond['condition']} (Confidence: {p_cond['confidence']:.2f}, Urgency: {p_cond['urgency']})")
                print(f"       Matched on: {', '.join(p_cond['matched_symptoms'])}")
                print(f"       Next Steps: {p_cond['next_steps']}")
                # Display further info if available
                # print(f"       More Info: {self.get_condition_details(session1_species, p_cond['condition'])}")
        else:
            print("  No potential conditions found for the given symptoms.")

        # Example 2: Dog with itching and lethargy
        print("\n-- Example 2: Dog with Itching & Lethargy --")
        session2_symptoms = ["itching_scratching", "lethargy"]
        session2_species = "Dog"
        result2 = self.start_diagnostic_session(session2_species, session2_symptoms)

        if "error" in result2:
            print(f"  Error: {result2['error']}")
        elif result2 and result2["potential_conditions"]:
            print(f"  Species: {session2_species}, Symptoms: {', '.join(session2_symptoms)}")
            print("  Potential Conditions (Top 3):")
            for i, p_cond in enumerate(result2["potential_conditions"][:3]):
                print(f"    {i+1}. {p_cond['condition']} (Confidence: {p_cond['confidence']:.2f}, Urgency: {p_cond['urgency']})")
                print(f"       Matched on: {', '.join(p_cond['matched_symptoms'])}")
                print(f"       Next Steps: {p_cond['next_steps']}")
                # Display further info for the top condition
                if i == 0:
                    print(f"       More Info on '{p_cond['condition']}': {self.get_condition_details(session2_species, p_cond['condition'])}")
        else:
            print("  No potential conditions found for the given symptoms.")

        # Example 3: Unknown species (to show error handling)
        print("\n-- Example 3: Unknown Species --")
        session3_symptoms = ["coughing"]
        session3_species = "Sheep" # Assuming Sheep is not in KB
        result3 = self.start_diagnostic_session(session3_species, session3_symptoms)
        if "error" in result3:
            print(f"  Species: {session3_species}, Symptoms: {', '.join(session3_symptoms)}")
            print(f"  Result: {result3['error']}")


        print("----------------------------------")

if __name__ == '__main__':
    screen = DiagnosisAssistantScreen()
    screen.display_screen()
