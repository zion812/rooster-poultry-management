import datetime
import random

class VeterinaryNetworkScreen:
    def __init__(self):
        self.vet_profiles = [] # List of veterinarian profiles
        self.referral_requests = [] # List of referral requests sent through the network

        # Mock data for specialties, locations, etc.
        self.specialties_list = [
            "General Practice", "Internal Medicine", "Surgery (Orthopedic)", "Surgery (Soft Tissue)",
            "Dermatology", "Ophthalmology", "Cardiology", "Oncology", "Neurology",
            "Radiology", "Anesthesiology", "Emergency & Critical Care",
            "Livestock Health Management", "Equine Specialist", "Poultry Veterinarian", "Aquatic Animal Health"
        ]
        self.mock_clinic_names = ["Advanced Vet Care", "Rural Animal Hospital", "City Paws Clinic", "The Equine Center", "State University Veterinary Teaching Hospital"]
        self.mock_cities = ["New York, NY", "Los Angeles, CA", "Chicago, IL", "Houston, TX", "Phoenix, AZ", "Philadelphia, PA", "Ruralville, IA"]

        self._initialize_mock_vet_profiles(count=15)

    def _initialize_mock_vet_profiles(self, count=10):
        base_names = ["Smith", "Jones", "Williams", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin"]
        first_names = ["Dr. Emily", "Dr. John", "Dr. Sarah", "Dr. Michael", "Dr. Jessica", "Dr. David", "Dr. Ashley", "Dr. Chris", "Dr. Amanda", "Dr. James"]

        for i in range(count):
            vet_id = f"vet_net_{str(i+1).zfill(4)}"
            name = f"{random.choice(first_names)} {random.choice(base_names)}"

            profile = {
                "vet_id": vet_id,
                "full_name": name,
                "licensed_state": random.choice(["NY", "CA", "TX", "FL", "IL", "PA", "OH"]),
                "license_number": f"VET{random.randint(10000,99999)}",
                "primary_specialty": random.choice(self.specialties_list),
                "secondary_specialties": random.sample(self.specialties_list, k=random.randint(0,2)),
                "clinic_name": random.choice(self.mock_clinic_names),
                "clinic_address": f"{random.randint(100,9999)} Main St, {random.choice(self.mock_cities)}",
                "contact_email": f"{name.lower().replace('dr. ','').replace(' ','_')}{random.randint(1,99)}@examplevet.com",
                "contact_phone": f"555-{random.randint(100,999)}-{random.randint(1000,9999)}",
                "years_of_experience": random.randint(1, 30),
                "profile_bio": f"Experienced veterinarian specializing in {random.choice(self.specialties_list)}. Committed to providing high-quality animal care.",
                "accepting_referrals": random.choice([True, True, False]), # Higher chance of true
                "areas_of_interest": random.sample(["diagnostic imaging", "preventative medicine", "exotic animal care", "herd health", "client education"], k=random.randint(1,3)),
                "publications_links": [f"https://example.com/pubs/{random.randint(100,999)}"] if random.random() > 0.7 else [],
                "profile_visibility": "all_vets", # "all_vets", "connections_only"
                "last_active_date": (datetime.date.today() - datetime.timedelta(days=random.randint(0,60))).isoformat()
            }
            # Ensure primary specialty is not in secondary
            profile["secondary_specialties"] = [s for s in profile["secondary_specialties"] if s != profile["primary_specialty"]]

            self.vet_profiles.append(profile)
        self.vet_profiles.sort(key=lambda x: x["full_name"])


    def search_vets(self, specialty=None, location_keyword=None, name_keyword=None, accepting_referrals=None, limit=5):
        results = self.vet_profiles

        if specialty:
            results = [vet for vet in results if specialty.lower() in vet["primary_specialty"].lower() or \
                                                 any(specialty.lower() in s.lower() for s in vet.get("secondary_specialties",[]))]
        if location_keyword:
            lk = location_keyword.lower()
            results = [vet for vet in results if lk in vet["clinic_address"].lower()]
        if name_keyword:
            nk = name_keyword.lower()
            results = [vet for vet in results if nk in vet["full_name"].lower()]

        if accepting_referrals is True:
            results = [vet for vet in results if vet["accepting_referrals"] is True]
        elif accepting_referrals is False: # Explicitly search for those not accepting
             results = [vet for vet in results if vet["accepting_referrals"] is False]

        return results[:limit]

    def get_vet_profile(self, vet_id):
        for profile in self.vet_profiles:
            if profile["vet_id"] == vet_id:
                return profile
        return None

    def send_referral_request(self, sending_vet_id, receiving_vet_id, patient_summary, reason_for_referral):
        sender_profile = self.get_vet_profile(sending_vet_id)
        receiver_profile = self.get_vet_profile(receiving_vet_id)

        if not sender_profile or not receiver_profile:
            print("Error: Sending or receiving vet profile not found.")
            return None

        if not receiver_profile.get("accepting_referrals", False):
            print(f"Warning: Dr. {receiver_profile['full_name']} is not currently listed as accepting referrals. Sending anyway...")

        request_id = f"ref_{datetime.datetime.now().strftime('%Y%m%d%H%M%S')}_{random.randint(100,999)}"
        referral = {
            "request_id": request_id,
            "sending_vet_id": sending_vet_id,
            "sending_vet_name": sender_profile["full_name"],
            "receiving_vet_id": receiving_vet_id,
            "receiving_vet_name": receiver_profile["full_name"],
            "patient_summary": patient_summary, # Could be brief text or link to a shared patient record (with consent)
            "reason_for_referral": reason_for_referral,
            "date_sent": datetime.datetime.now().isoformat(),
            "status": "pending_review", # pending_review, accepted, declined, information_requested
            "urgency": "Routine", # Routine, Urgent
            "attachments": [] # Placeholder for attached files/records
        }
        self.referral_requests.insert(0, referral)
        print(f"Referral request {request_id} sent from {sender_profile['full_name']} to {receiver_profile['full_name']}.")
        # Placeholder for actual messaging/notification system
        print(f"  (Placeholder: Notification sent to {receiver_profile['full_name']})")
        return request_id

    def update_referral_status(self, request_id, new_status, vet_id_acting, notes=""):
        for req in self.referral_requests:
            if req["request_id"] == request_id:
                if vet_id_acting != req["receiving_vet_id"] and vet_id_acting != req["sending_vet_id"]: # Allow sender to cancel perhaps
                    print(f"Error: Vet {vet_id_acting} not authorized to update this referral.")
                    return False

                req["status"] = new_status
                req["last_updated_by"] = vet_id_acting
                req["last_updated_at"] = datetime.datetime.now().isoformat()
                req["notes"] = notes
                print(f"Referral request {request_id} status updated to '{new_status}' by {vet_id_acting}.")
                # Placeholder: Notify other party
                other_party_id = req["sending_vet_id"] if vet_id_acting == req["receiving_vet_id"] else req["receiving_vet_id"]
                print(f"  (Placeholder: Notification of status change sent to vet {other_party_id})")
                return True
        print(f"Error: Referral request {request_id} not found.")
        return False

    def get_pending_referrals(self, vet_id, as_receiver=True, limit=5):
        if as_receiver:
            return [r for r in self.referral_requests if r["receiving_vet_id"] == vet_id and r["status"] == "pending_review"][:limit]
        else: # As sender
            return [r for r in self.referral_requests if r["sending_vet_id"] == vet_id and r["status"] != "accepted"][:limit] # Show non-accepted sent ones


    def display_screen(self, current_vet_id="vet_net_0001"): # Assume current_vet_id is one from the generated list
        print("---- Veterinary Network Screen ----")

        # Ensure current_vet_id is valid, if not pick one
        if not self.get_vet_profile(current_vet_id) and self.vet_profiles:
            current_vet_id = self.vet_profiles[0]['vet_id']
            print(f"  (Current vet ID defaulted to: {current_vet_id} - {self.get_vet_profile(current_vet_id)['full_name']})")


        print("\n-- Search for Specialists (Example: 'Cardiology' accepting referrals) --")
        cardiologists = self.search_vets(specialty="Cardiology", accepting_referrals=True, limit=2)
        if not cardiologists:
            print("  No cardiologists found matching criteria.")
        else:
            for vet in cardiologists:
                print(f"  Name: {vet['full_name']}, Clinic: {vet['clinic_name']}, Location: {vet['clinic_address']}")
                print(f"    Primary Specialty: {vet['primary_specialty']}, Experience: {vet['years_of_experience']} years")

        print("\n-- View Vet Profile (Example) --")
        if self.vet_profiles:
            profile_to_view = self.vet_profiles[random.randint(0, len(self.vet_profiles)-1)] # Pick a random profile
            profile_details = self.get_vet_profile(profile_to_view["vet_id"])
            if profile_details:
                print(f"  Viewing Profile for: {profile_details['full_name']} (ID: {profile_details['vet_id']})")
                print(f"    Specialty: {profile_details['primary_specialty']}")
                print(f"    Clinic: {profile_details['clinic_name']}, {profile_details['clinic_address']}")
                print(f"    Accepting Referrals: {'Yes' if profile_details['accepting_referrals'] else 'No'}")
                print(f"    Bio: {profile_details['profile_bio'][:70]}...") # Truncated bio
            else:
                print("  Could not load profile for display.")

        print("\n-- Send Referral Request (Example) --")
        if len(self.vet_profiles) >= 2:
            sender_vet = self.get_vet_profile(current_vet_id) # Current user sends
            # Find a different vet who is accepting referrals
            receiver_vet_profile = next((v for v in self.vet_profiles if v['vet_id'] != current_vet_id and v['accepting_referrals']), None)

            if sender_vet and receiver_vet_profile:
                patient_summary_mock = "Case: 7yr old MN Labrador, 'Max', with suspected mitral valve disease, requires echocardiogram and cardiology consult."
                reason_mock = "Advanced cardiac workup and management plan."
                referral_id = self.send_referral_request(sender_vet["vet_id"], receiver_vet_profile["vet_id"], patient_summary_mock, reason_mock)

                if referral_id:
                    print(f"  Example referral request sent with ID: {referral_id}")
                    # Simulate receiver accepting it
                    self.update_referral_status(referral_id, "accepted", receiver_vet_profile["vet_id"], "Happy to see the patient. Please send full records.")
            else:
                print("  Could not find suitable vets for referral demonstration (need at least two, one accepting referrals).")
        else:
            print("  Not enough vet profiles to demonstrate referral.")

        print(f"\n-- Pending Referrals for {self.get_vet_profile(current_vet_id)['full_name']} (as Receiver, max 2) --")
        pending_received = self.get_pending_referrals(current_vet_id, as_receiver=True, limit=2)
        if not pending_received:
            print("  No pending referrals to review.")
        else:
            for ref in pending_received:
                print(f"  From: {ref['sending_vet_name']}, Reason: {ref['reason_for_referral'][:50]}..., Status: {ref['status']}")

        print("---------------------------------")

if __name__ == '__main__':
    screen = VeterinaryNetworkScreen()
    # To test with a specific vet ID from the generated list, you can find one and pass it
    # For example, if screen.vet_profiles[0]['vet_id'] exists:
    # screen.display_screen(current_vet_id=screen.vet_profiles[0]['vet_id'])
    screen.display_screen() # Uses a default or the first vet if default is not found.
