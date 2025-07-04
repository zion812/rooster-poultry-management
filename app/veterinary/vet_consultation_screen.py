import datetime
import random

class VetConsultationScreen:
    def __init__(self):
        self.vets = {} # vet_id: {name, specialty, availability}
        self.appointments = [] # List of booked appointments
        self.patient_queue = [] # Patients waiting for immediate/walk-in consultation
        self._initialize_mock_data()

    def _initialize_mock_data(self):
        # Mock Veterinarians
        vet_names = ["Dr. Alice Smith", "Dr. Bob Johnson", "Dr. Carol White", "Dr. David Brown"]
        specialties = ["General Practice", "Surgery", "Dermatology", "Livestock Specialist"]
        for i, name in enumerate(vet_names):
            vet_id = f"vet{str(i+1).zfill(3)}"
            self.vets[vet_id] = {
                "name": name,
                "specialty": random.choice(specialties),
                "availability": self._generate_mock_availability(), # {date_str: [time_slots]}
                "consultation_fee": round(random.uniform(50, 150), 2)
            }

        # Mock some existing appointments
        farmer_user_ids = [f"farmer{str(i).zfill(3)}" for i in range(1, 10)]
        pet_names = ["Bessie", "Charlie", "Daisy", "Rocky", "Lucy", "Max"]
        for i in range(5): # Create 5 mock appointments
            vet_id = random.choice(list(self.vets.keys()))
            vet_avail_dates = list(self.vets[vet_id]["availability"].keys())
            if not vet_avail_dates: continue

            appt_date_str = random.choice(vet_avail_dates)
            time_slots = self.vets[vet_id]["availability"][appt_date_str]
            if not time_slots: continue

            appt_time_str = random.choice(time_slots)
            # Attempt to book this slot (simplified)
            if self._book_slot_internal(vet_id, appt_date_str, appt_time_str):
                self.appointments.append({
                    "appointment_id": f"appt_{datetime.datetime.now().strftime('%Y%m%d%H%M')}_{str(i).zfill(3)}",
                    "vet_id": vet_id,
                    "vet_name": self.vets[vet_id]["name"],
                    "user_id": random.choice(farmer_user_ids),
                    "patient_name": random.choice(pet_names),
                    "appointment_datetime": datetime.datetime.strptime(f"{appt_date_str} {appt_time_str}", "%Y-%m-%d %H:%M"),
                    "reason": random.choice(["General check-up", "Vaccination", "Skin issue", "Limping"]),
                    "status": "booked" # Other statuses: completed, cancelled, in_progress
                })
        self.appointments.sort(key=lambda x: x["appointment_datetime"])

        # Mock patient queue (for walk-ins or immediate online requests)
        for i in range(3):
            self.patient_queue.append({
                "queue_id": f"q_{str(i).zfill(3)}",
                "user_id": random.choice(farmer_user_ids),
                "patient_name": random.choice(pet_names),
                "arrival_time": datetime.datetime.now() - datetime.timedelta(minutes=random.randint(5,30)),
                "reason": "Urgent concern",
                "assigned_vet_id": None # Vet can pick from queue
            })
        self.patient_queue.sort(key=lambda x: x["arrival_time"])


    def _generate_mock_availability(self, num_days=7, slots_per_day=5):
        availability = {}
        today = datetime.date.today()
        for i in range(num_days):
            day = today + datetime.timedelta(days=i)
            if day.weekday() >= 5: continue # Skip weekends for this mock

            time_slots = []
            for j in range(slots_per_day):
                hour = 9 + j # 9 AM to 1 PM for example
                if random.random() > 0.3: # 70% chance slot is available
                    time_slots.append(f"{hour:02d}:00")
            if time_slots:
                availability[day.isoformat()] = time_slots
        return availability

    def _book_slot_internal(self, vet_id, date_str, time_str):
        """ Helper to remove a slot. True if successful."""
        if date_str in self.vets[vet_id]["availability"] and \
           time_str in self.vets[vet_id]["availability"][date_str]:
            self.vets[vet_id]["availability"][date_str].remove(time_str)
            return True
        return False

    def get_vet_availability(self, vet_id, date_str=None):
        vet = self.vets.get(vet_id)
        if not vet: return None
        if date_str:
            return {date_str: vet["availability"].get(date_str, [])}
        return vet["availability"]

    def book_appointment(self, vet_id, user_id, patient_name, date_str, time_str, reason):
        if vet_id not in self.vets:
            print(f"Error: Vet ID {vet_id} not found.")
            return None

        available_slots_on_date = self.vets[vet_id]["availability"].get(date_str, [])
        if time_str not in available_slots_on_date:
            print(f"Error: Slot {time_str} on {date_str} for Dr. {self.vets[vet_id]['name']} is not available or date is invalid.")
            return None

        if self._book_slot_internal(vet_id, date_str, time_str):
            new_appointment = {
                "appointment_id": f"appt_{datetime.datetime.now().strftime('%Y%m%d%H%M%S')}_{str(len(self.appointments)).zfill(3)}",
                "vet_id": vet_id,
                "vet_name": self.vets[vet_id]["name"],
                "user_id": user_id,
                "patient_name": patient_name,
                "appointment_datetime": datetime.datetime.strptime(f"{date_str} {time_str}", "%Y-%m-%d %H:%M"),
                "reason": reason,
                "status": "booked"
            }
            self.appointments.append(new_appointment)
            self.appointments.sort(key=lambda x: x["appointment_datetime"])
            print(f"Appointment booked for {patient_name} with {self.vets[vet_id]['name']} on {date_str} at {time_str}.")
            return new_appointment
        else:
            # This case should ideally be caught by the check above, but as a fallback
            print(f"Error: Could not book slot {time_str} on {date_str} for Dr. {self.vets[vet_id]['name']}. Slot may have just been taken.")
            return None

    def get_upcoming_appointments(self, user_id=None, vet_id=None, limit=10):
        results = self.appointments
        if user_id:
            results = [appt for appt in results if appt["user_id"] == user_id and appt["status"] == "booked"]
        if vet_id:
            results = [appt for appt in results if appt["vet_id"] == vet_id and appt["status"] == "booked"]
        return sorted([appt for appt in results if appt["appointment_datetime"] >= datetime.datetime.now()], key=lambda x: x["appointment_datetime"])[:limit]

    def add_to_patient_queue(self, user_id, patient_name, reason):
        queue_entry = {
            "queue_id": f"q_{datetime.datetime.now().strftime('%H%M%S')}_{str(len(self.patient_queue)).zfill(3)}",
            "user_id": user_id,
            "patient_name": patient_name,
            "arrival_time": datetime.datetime.now(),
            "reason": reason,
            "assigned_vet_id": None
        }
        self.patient_queue.append(queue_entry)
        self.patient_queue.sort(key=lambda x: x["arrival_time"])
        print(f"{patient_name} added to the patient queue.")
        return queue_entry

    def assign_queued_patient_to_vet(self, queue_id, vet_id):
        for entry in self.patient_queue:
            if entry["queue_id"] == queue_id and entry["assigned_vet_id"] is None:
                if vet_id in self.vets:
                    entry["assigned_vet_id"] = vet_id
                    entry["status"] = "assigned" # Or "waiting_for_vet"
                    print(f"Patient from queue ({queue_id}) assigned to Dr. {self.vets[vet_id]['name']}.")
                    # Potentially move to a different list or vet's personal queue here
                    return True
        print(f"Could not assign patient from queue {queue_id} to vet {vet_id}. Entry not found or already assigned.")
        return False

    def display_screen(self):
        print("---- Vet Consultation Screen ----")

        print("\n-- Veterinarians Available (Sample: Dr. Alice Smith) --")
        sample_vet_id = "vet001" # Assuming this vet exists from mock data
        if sample_vet_id in self.vets:
            print(f"  Dr. {self.vets[sample_vet_id]['name']}, Specialty: {self.vets[sample_vet_id]['specialty']}")
            availability = self.get_vet_availability(sample_vet_id)
            if availability:
                for date_str, slots in list(availability.items())[:2]: # Show for 2 days
                    if slots:
                        print(f"    Date: {date_str}, Available Slots: {', '.join(slots[:3])}{'...' if len(slots)>3 else ''}")
            else:
                print(f"    No availability shown for {self.vets[sample_vet_id]['name']}.")
        else:
            # Fallback if vet001 is not generated, pick first available vet
            if self.vets:
                first_vet_id = list(self.vets.keys())[0]
                print(f"  Showing sample for Dr. {self.vets[first_vet_id]['name']}, Specialty: {self.vets[first_vet_id]['specialty']}")
                availability = self.get_vet_availability(first_vet_id)
                if availability:
                    for date_str, slots in list(availability.items())[:2]:
                         if slots:
                            print(f"    Date: {date_str}, Available Slots: {', '.join(slots[:3])}{'...' if len(slots)>3 else ''}")
            else:
                print("  No veterinarians loaded.")


        print("\n-- Book Appointment (Example) --")
        # Find a vet with some availability to book for demo
        bookable_vet_id = None
        bookable_date = None
        bookable_time = None
        for vid, vdata in self.vets.items():
            for d, ts in vdata["availability"].items():
                if ts:
                    bookable_vet_id = vid
                    bookable_date = d
                    bookable_time = ts[0]
                    break
            if bookable_vet_id: break

        if bookable_vet_id and bookable_date and bookable_time:
            self.book_appointment(bookable_vet_id, "farmer010", "Speckles", bookable_date, bookable_time, "Routine check")
        else:
            print("  Could not find an available slot for booking demonstration.")


        print("\n-- Upcoming Appointments (Next 3) --")
        upcoming = self.get_upcoming_appointments(limit=3)
        if not upcoming:
            print("  No upcoming appointments.")
        else:
            for appt in upcoming:
                print(f"  ID: {appt['appointment_id']}, Patient: {appt['patient_name']}, Vet: {appt['vet_name']}, "
                      f"Time: {appt['appointment_datetime'].strftime('%Y-%m-%d %H:%M')}, Reason: {appt['reason']}")

        print("\n-- Patient Queue (First 3) --")
        if not self.patient_queue:
            print("  Patient queue is empty.")
        else:
            for entry in self.patient_queue[:3]:
                assign_status = f"Assigned to: {self.vets[entry['assigned_vet_id']]['name']}" if entry['assigned_vet_id'] else "Unassigned"
                print(f"  Queue ID: {entry['queue_id']}, Patient: {entry['patient_name']}, Arrived: {entry['arrival_time'].strftime('%H:%M:%S')}, "
                      f"Reason: {entry['reason']}, Status: {assign_status}")

        print("\n-- Assign Patient from Queue (Example) --")
        if self.patient_queue and self.patient_queue[0]['assigned_vet_id'] is None:
            q_id_to_assign = self.patient_queue[0]['queue_id']
            # Find a vet to assign to
            assign_to_vet_id = list(self.vets.keys())[0] if self.vets else None
            if assign_to_vet_id:
                self.assign_queued_patient_to_vet(q_id_to_assign, assign_to_vet_id)
            else:
                print("  No vets available to assign patient from queue.")
        else:
            print("  No unassigned patients in queue for assignment demonstration or queue is empty.")

        print("-----------------------------")

if __name__ == '__main__':
    screen = VetConsultationScreen()
    screen.display_screen()
