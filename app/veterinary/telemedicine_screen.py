import datetime
import random
import time # For simulating call duration

class TelemedicineScreen:
    def __init__(self):
        self.active_sessions = {} # session_id: {details}
        self.session_history = []
        # Assume vet_ids and user_ids (farmer_ids) are known from other systems
        self.known_vet_ids = [f"vet{str(i).zfill(3)}" for i in range(1, 4)]
        self.known_user_ids = [f"farmer{str(i).zfill(3)}" for i in range(1, 6)]
        self.patient_names_for_mock = ["Bessie", "Charlie", "Daisy", "Rocky", "Lucy", "Max"]


    def initiate_call(self, vet_id, user_id, patient_id, patient_name, appointment_id=None):
        if vet_id not in self.known_vet_ids or user_id not in self.known_user_ids:
            print("Error: Invalid vet_id or user_id.")
            return None

        session_id = f"telemed_{datetime.datetime.now().strftime('%Y%m%d%H%M%S')}_{random.randint(100,999)}"

        session_details = {
            "session_id": session_id,
            "vet_id": vet_id,
            "user_id": user_id,
            "patient_id": patient_id, # Link to patient record
            "patient_name": patient_name,
            "appointment_id": appointment_id, # Optional: link to a scheduled appointment
            "start_time": datetime.datetime.now(),
            "end_time": None,
            "status": "initiating", # initiating -> connecting -> active -> ended/disconnected
            "call_quality": None, # e.g., "good", "fair", "poor"
            "chat_log": [], # List of {"sender_id", "message", "timestamp"}
            "shared_files": [], # List of {"filename", "uploader_id", "timestamp"}
            "session_notes_vet": "", # Notes taken by vet during call
            "connection_url": f"https://telemed.example.com/join/{session_id}" # Mock URL
        }
        self.active_sessions[session_id] = session_details
        print(f"Telemedicine session {session_id} initiated between Vet {vet_id} and User {user_id} for Patient {patient_name}.")
        print(f"  Join URL: {session_details['connection_url']}")

        # Simulate connection process
        self._simulate_connection(session_id)
        return session_id

    def _simulate_connection(self, session_id):
        if session_id in self.active_sessions:
            # print(f"  Session {session_id}: Connecting...")
            # time.sleep(0.1) # Simulate network delay
            self.active_sessions[session_id]["status"] = "active" # Assume connection is successful
            self.active_sessions[session_id]["call_quality"] = random.choice(["good", "fair", "good"]) # More often good
            print(f"  Session {session_id}: Connected. Status: active, Quality: {self.active_sessions[session_id]['call_quality']}")

    def end_call(self, session_id, ended_by_id):
        if session_id in self.active_sessions:
            session = self.active_sessions[session_id]
            session["status"] = "ended"
            session["end_time"] = datetime.datetime.now()
            session["ended_by"] = ended_by_id

            duration_seconds = (session["end_time"] - session["start_time"]).total_seconds()
            session["duration_minutes"] = round(duration_seconds / 60, 2)

            print(f"Telemedicine session {session_id} ended by {ended_by_id}. Duration: {session['duration_minutes']} minutes.")
            self.session_history.append(session)
            del self.active_sessions[session_id]
            return True
        print(f"Error: Session {session_id} not found or already ended.")
        return False

    def send_chat_message(self, session_id, sender_id, message):
        if session_id in self.active_sessions and self.active_sessions[session_id]["status"] == "active":
            session = self.active_sessions[session_id]
            chat_message = {
                "sender_id": sender_id,
                "message": message,
                "timestamp": datetime.datetime.now()
            }
            session["chat_log"].append(chat_message)
            print(f"  [Chat - {session_id}] {sender_id}: {message}")
            return True
        print(f"Error: Cannot send chat. Session {session_id} not active.")
        return False

    def share_file(self, session_id, uploader_id, filename):
        if session_id in self.active_sessions and self.active_sessions[session_id]["status"] == "active":
            session = self.active_sessions[session_id]
            file_info = {
                "filename": filename,
                "uploader_id": uploader_id,
                "timestamp": datetime.datetime.now(),
                "file_url": f"https://files.example.com/{session_id}/{filename}" # Mock URL
            }
            session["shared_files"].append(file_info)
            print(f"  [File Share - {session_id}] {uploader_id} shared {filename} (URL: {file_info['file_url']})")
            return True
        print(f"Error: Cannot share file. Session {session_id} not active.")
        return False

    def update_vet_notes(self, session_id, notes, vet_id):
        if session_id in self.active_sessions and self.active_sessions[session_id]["vet_id"] == vet_id:
            self.active_sessions[session_id]["session_notes_vet"] = notes
            print(f"  Vet notes updated for session {session_id}.")
            return True
        print(f"Error: Cannot update notes. Session {session_id} not found or vet_id mismatch.")
        return False

    def get_active_session_details(self, session_id):
        return self.active_sessions.get(session_id)

    def get_session_history(self, user_id=None, vet_id=None, limit=5):
        results = self.session_history
        if user_id:
            results = [s for s in results if s["user_id"] == user_id]
        if vet_id:
            results = [s for s in results if s["vet_id"] == vet_id]
        return sorted(results, key=lambda x: x["start_time"], reverse=True)[:limit]

    def display_screen(self):
        print("---- Telemedicine Screen ----")

        print("\n-- Initiating a New Call (Example) --")
        vet_to_call = random.choice(self.known_vet_ids)
        user_to_call = random.choice(self.known_user_ids)
        patient_name_call = random.choice(self.patient_names_for_mock)
        patient_id_call = f"pet{random.randint(100,199)}" # Mock patient ID

        session_id = self.initiate_call(vet_to_call, user_to_call, patient_id_call, patient_name_call, appointment_id="appt_tele001")

        if session_id and session_id in self.active_sessions:
            print("\n-- Simulating Active Call Actions --")
            time.sleep(0.1) # Simulate some time passing
            self.send_chat_message(session_id, user_to_call, "Hello Dr, thanks for taking the call for Bessie.")
            time.sleep(0.1)
            self.send_chat_message(session_id, vet_to_call, f"Hello {user_to_call}, happy to help. What are the symptoms?")
            time.sleep(0.1)
            self.share_file(session_id, user_to_call, "bessie_rash_photo.jpg")
            time.sleep(0.1)
            self.update_vet_notes(session_id, "Patient presents with mild skin irritation on flank. Owner shared photo. Advised topical cream.", vet_to_call)

            print("\n-- Ending the Call --")
            time.sleep(0.1) # Simulate call duration
            self.end_call(session_id, vet_to_call)
        else:
            print("  Could not initiate call for demonstration.")

        print("\n-- Active Sessions (Should be empty if call ended) --")
        if not self.active_sessions:
            print("  No active telemedicine sessions.")
        else:
            for sid, details in self.active_sessions.items():
                print(f"  Session ID: {sid}, Vet: {details['vet_id']}, User: {details['user_id']}, Status: {details['status']}")

        print("\n-- Session History (Last 2) --")
        history = self.get_session_history(limit=2)
        if not history:
            print("  No session history found.")
        else:
            for s in history:
                print(f"  Session ID: {s['session_id']}, Vet: {s['vet_id']}, User: {s['user_id']}, Patient: {s['patient_name']}")
                print(f"    Started: {s['start_time']}, Ended: {s['end_time']}, Duration: {s.get('duration_minutes','N/A')} mins")
                print(f"    Ended By: {s.get('ended_by','N/A')}, Final Status: {s['status']}")
                if s['chat_log']: print(f"    Chat messages: {len(s['chat_log'])}")
                if s['shared_files']: print(f"    Files shared: {len(s['shared_files'])}")
                if s['session_notes_vet']: print(f"    Vet Notes: {s['session_notes_vet'][:50]}...")


        print("-----------------------------")

if __name__ == '__main__':
    screen = TelemedicineScreen()
    screen.display_screen()
