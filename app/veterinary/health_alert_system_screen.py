import datetime
import random

class HealthAlertSystemScreen:
    def __init__(self):
        self.reported_cases = [] # Each entry is a dict for a reported case/outbreak
        self.active_alerts = [] # Active alerts disseminated to users

        # Mock data for diseases, locations, species
        self.known_diseases = ["Avian Influenza", "Foot and Mouth Disease", "Rabies", "Swine Flu", "Bluetongue", "Equine Herpesvirus"]
        self.mock_locations = [ # Simplified geo-data: (City, State, Approx Lat/Lon for context)
            {"name": "Springfield, IL", "lat": 39.78, "lon": -89.65, "radius_km": 50},
            {"name": "Shelbyville, KY", "lat": 38.21, "lon": -85.22, "radius_km": 40},
            {"name": "Greenville, SC", "lat": 34.85, "lon": -82.39, "radius_km": 60},
            {"name": "Fresno, CA", "lat": 36.74, "lon": -119.77, "radius_km": 70},
            {"name": "Rural Area A, TX", "lat": 31.96, "lon": -99.90, "radius_km": 100} # Larger rural area
        ]
        self.affected_species_list = ["Cattle", "Poultry", "Swine", "Sheep", "Goats", "Horses", "Companion Animals"]

        self._initialize_mock_reported_cases(count=5)
        self._initialize_mock_active_alerts()

    def _initialize_mock_reported_cases(self, count=3):
        for i in range(count):
            disease = random.choice(self.known_diseases)
            location_info = random.choice(self.mock_locations)
            report_date = datetime.datetime.now() - datetime.timedelta(days=random.randint(1, 30))

            case = {
                "case_id": f"case_{report_date.strftime('%Y%m%d')}_{random.randint(100,999)}",
                "disease_name": disease,
                "location": location_info["name"],
                "geo_coordinates": {"latitude": location_info["lat"], "longitude": location_info["lon"]},
                "affected_radius_km": location_info["radius_km"] * random.uniform(0.5, 1.2), # Slight variation
                "date_reported": report_date.isoformat(),
                "number_of_confirmed_cases": random.randint(1, 20),
                "number_of_suspected_cases": random.randint(5, 50),
                "affected_species": random.sample(self.affected_species_list, k=random.randint(1,3)),
                "severity": random.choice(["Low", "Medium", "High", "Critical"]),
                "source_of_report": random.choice(["Local Vet Clinic", "State Agri Dept", "Farmer Cooperative"]),
                "status": random.choice(["under_investigation", "monitoring", "contained", "resolved"]),
                "updates": [{"timestamp": report_date.isoformat(), "note": "Initial report received."}]
            }
            self.reported_cases.append(case)
        self.reported_cases.sort(key=lambda x: x["date_reported"], reverse=True)

    def _initialize_mock_active_alerts(self):
        # Create alerts based on some of the high severity reported cases
        for case in self.reported_cases:
            if case["severity"] in ["High", "Critical"] and case["status"] not in ["resolved", "contained"] and random.random() > 0.3:
                alert_date = datetime.datetime.strptime(case["date_reported"], "%Y-%m-%dT%H:%M:%S.%f") + datetime.timedelta(hours=random.randint(1,6))
                alert = {
                    "alert_id": f"alert_{case['case_id']}",
                    "based_on_case_id": case["case_id"],
                    "disease_name": case["disease_name"],
                    "location_summary": case["location"],
                    "severity": case["severity"],
                    "date_issued": alert_date.isoformat(),
                    "target_audience": f"Users within {case['affected_radius_km']:.0f}km of {case['location']}", # Simplified
                    "alert_message": f"WARNING: {case['severity']} risk of {case['disease_name']} reported near {case['location']}. Affects: {', '.join(case['affected_species'])}. Take precautionary measures.",
                    "preventative_measures_link": f"https://example.com/alerts/{case['disease_name'].lower().replace(' ','-')}-prevention",
                    "status": "active" # active, superseded, cancelled
                }
                self.active_alerts.append(alert)
        self.active_alerts.sort(key=lambda x: x["date_issued"], reverse=True)


    def report_new_case(self, disease_name, location_name, lat, lon, radius_km, num_confirmed, num_suspected, species, severity, source, notes="Initial report."):
        report_date = datetime.datetime.now()
        case_id = f"case_{report_date.strftime('%Y%m%d%H%M%S')}_{random.randint(100,999)}"
        new_case = {
            "case_id": case_id,
            "disease_name": disease_name,
            "location": location_name,
            "geo_coordinates": {"latitude": lat, "longitude": lon},
            "affected_radius_km": radius_km,
            "date_reported": report_date.isoformat(),
            "number_of_confirmed_cases": num_confirmed,
            "number_of_suspected_cases": num_suspected,
            "affected_species": species if isinstance(species, list) else [species],
            "severity": severity,
            "source_of_report": source,
            "status": "under_investigation",
            "updates": [{"timestamp": report_date.isoformat(), "note": notes}]
        }
        self.reported_cases.insert(0, new_case)
        print(f"New case {case_id} for {disease_name} at {location_name} reported.")

        # Potentially trigger an alert if severity is high
        if severity in ["High", "Critical"]:
            self.create_alert_from_case(case_id)
        return case_id

    def update_case_status(self, case_id, new_status, notes, admin_id="system"):
        for case in self.reported_cases:
            if case["case_id"] == case_id:
                case["status"] = new_status
                update_note = {"timestamp": datetime.datetime.now().isoformat(), "note": notes, "updated_by": admin_id}
                case["updates"].append(update_note)
                print(f"Case {case_id} status updated to '{new_status}'. Note: {notes}")
                return True
        print(f"Error: Case {case_id} not found.")
        return False

    def create_alert_from_case(self, case_id, custom_message=None):
        case = next((c for c in self.reported_cases if c["case_id"] == case_id), None)
        if not case:
            print(f"Error: Case {case_id} not found to create alert from.")
            return None

        # Avoid duplicate alerts for the same case if one is already active
        if any(a["based_on_case_id"] == case_id and a["status"] == "active" for a in self.active_alerts):
            print(f"Alert for case {case_id} is already active.")
            return None

        alert_date = datetime.datetime.now()
        alert_id = f"alert_{case_id}_{alert_date.strftime('%H%M%S')}"

        default_message = f"URGENT HEALTH ALERT: {case['severity']} risk of {case['disease_name']} identified near {case['location']}. " \
                          f"Affects: {', '.join(case['affected_species'])}. " \
                          f"Confirmed cases: {case['number_of_confirmed_cases']}. " \
                          f"Please review biosecurity measures and report any suspected illness."

        new_alert = {
            "alert_id": alert_id,
            "based_on_case_id": case_id,
            "disease_name": case["disease_name"],
            "location_summary": case["location"],
            "severity": case["severity"],
            "date_issued": alert_date.isoformat(),
            "target_audience": f"Users and veterinarians within {case['affected_radius_km']:.0f}km of {case['location']}",
            "alert_message": custom_message if custom_message else default_message,
            "preventative_measures_link": f"https://example.com/alerts/{case['disease_name'].lower().replace(' ','-')}-prevention",
            "status": "active"
        }
        self.active_alerts.insert(0, new_alert)
        print(f"New alert {alert_id} created and disseminated for case {case_id}.")
        return alert_id

    def get_reported_cases(self, disease_filter=None, status_filter=None, limit=5):
        results = self.reported_cases
        if disease_filter:
            results = [c for c in results if c["disease_name"].lower() == disease_filter.lower()]
        if status_filter:
            results = [c for c in results if c["status"] == status_filter]
        return results[:limit]

    def get_active_alerts(self, limit=5):
        return [a for a in self.active_alerts if a["status"] == "active"][:limit]

    def display_screen(self):
        print("---- Health Alert System Screen ----")

        print("\n-- Recently Reported Cases (Max 3) --")
        recent_cases = self.get_reported_cases(limit=3)
        if not recent_cases:
            print("  No recent cases reported.")
        else:
            for case in recent_cases:
                print(f"  ID: {case['case_id']}, Disease: {case['disease_name']}, Location: {case['location']}")
                print(f"    Reported: {case['date_reported'][:10]}, Status: {case['status']}, Severity: {case['severity']}")
                print(f"    Confirmed: {case['number_of_confirmed_cases']}, Suspected: {case['number_of_suspected_cases']}, Species: {', '.join(case['affected_species'])}")

        print("\n-- Active Health Alerts (Max 3) --")
        active_alerts_list = self.get_active_alerts(limit=3)
        if not active_alerts_list:
            print("  No active health alerts.")
        else:
            for alert in active_alerts_list:
                print(f"  Alert ID: {alert['alert_id']}, Disease: {alert['disease_name']}, Location: {alert['location_summary']}")
                print(f"    Issued: {alert['date_issued'][:16]}, Severity: {alert['severity']}")
                print(f"    Message: {alert['alert_message'][:100]}...") # Truncate for display
                print(f"    More Info: {alert['preventative_measures_link']}")

        print("\n-- Report New Case (Example) --")
        new_case_id = self.report_new_case(
            disease_name="Test Disease X",
            location_name="Testville, Imaginaria",
            lat=0.0, lon=0.0, radius_km=20,
            num_confirmed=2, num_suspected=5,
            species=["Test Species A", "Test Species B"],
            severity="High", # This should trigger an alert
            source="Automated Test System",
            notes="This is a test case for demonstration."
        )
        if new_case_id:
            print(f"  Example case {new_case_id} reported. Check active alerts for a new entry.")
            self.update_case_status(new_case_id, "monitoring", "Initial investigation complete, moving to monitoring.", "admin_jules")

        print("\n-- Updated Active Health Alerts (Post New Case Report, Max 3) --")
        updated_alerts_list = self.get_active_alerts(limit=3)
        if not updated_alerts_list:
            print("  No active health alerts.")
        else:
            for alert in updated_alerts_list:
                print(f"  Alert ID: {alert['alert_id']}, Disease: {alert['disease_name']}")
                print(f"    Message: {alert['alert_message'][:100]}...")

        print("---------------------------------")

if __name__ == '__main__':
    screen = HealthAlertSystemScreen()
    screen.display_screen()
