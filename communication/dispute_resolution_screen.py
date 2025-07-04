# Placeholder for DisputeResolutionScreen
import datetime

class DisputeResolutionScreen:
    def __init__(self, current_user_id, order_id):
        self.current_user_id = current_user_id
        self.order_id = order_id # The order in dispute
        self.dispute_id = None # Assigned when a dispute is formally created
        self.dispute_status = "Not Started" # e.g., "Open", "Under Investigation", "Resolved", "Closed"
        self.messages = [] # Communication log for this dispute (between buyer, seller, admin)
        self.evidence = [] # List of evidence submitted (e.g., file paths, image URLs, descriptions)

        # In a real system, this would fetch existing dispute data if any for this order_id
        self._load_dispute_details()

    def _load_dispute_details(self):
        # Mock: check if there's a mock dispute for this order
        # print(f"Checking for existing dispute for order {self.order_id}...")
        # For now, assumes a new dispute process each time this screen is initialized for an order.
        # A real system would load existing state.
        pass

    def display_dispute_status(self):
        print(f"--- Dispute Resolution for Order: {self.order_id} ---")
        print(f"Dispute ID: {self.dispute_id if self.dispute_id else 'Not yet created'}")
        print(f"Status: {self.dispute_status}")

        if self.messages:
            print("\nCommunication Log:")
            for msg in self.messages:
                sender_role = "You" if msg['sender_id'] == self.current_user_id else msg.get('sender_role', 'Other Party')
                timestamp = msg['timestamp'].strftime("%Y-%m-%d %H:%M:%S")
                print(f"  [{timestamp}] {sender_role}: {msg['text']}")

        if self.evidence:
            print("\nSubmitted Evidence:")
            for ev in self.evidence:
                submitter_role = "You" if ev['submitter_id'] == self.current_user_id else ev.get('submitter_role', 'Other Party')
                print(f"  - {ev['description']} (submitted by {submitter_role} on {ev['timestamp'].strftime('%Y-%m-%d')})")
        print("------------------------------------------------")

    def open_dispute(self, reason, desired_outcome):
        if self.dispute_id:
            print(f"A dispute ({self.dispute_id}) already exists for this order.")
            return

        self.dispute_id = f"DISPUTE-{self.order_id}-{datetime.datetime.now().strftime('%Y%m%d%H%M')}"
        self.dispute_status = "Open"
        initial_message = f"Dispute opened by buyer. Reason: {reason}. Desired Outcome: {desired_outcome}"
        self.add_message(initial_message, sender_role="Buyer (System)") # System message for initiation
        print(f"Dispute {self.dispute_id} opened for order {self.order_id}.")
        print(f"Reason: {reason}, Desired Outcome: {desired_outcome}")
        # In a real system, this would notify the seller and potentially an admin.

    def add_message(self, text, sender_role="Buyer"): # sender_role could be 'Buyer', 'Seller', 'Admin'
        if not self.dispute_id:
            print("Please open a dispute first before adding messages.")
            return

        message = {
            'sender_id': self.current_user_id if sender_role == "Buyer" else "SELLER_ID_PLACEHOLDER" if sender_role == "Seller" else "ADMIN_ID_PLACEHOLDER", # Placeholder IDs
            'sender_role': sender_role,
            'text': text,
            'timestamp': datetime.datetime.now()
        }
        self.messages.append(message)
        print(f"Message added to dispute {self.dispute_id}: '{text}'")
        # Notify other parties involved in the dispute.

    def submit_evidence(self, description, file_path_or_url=None):
        if not self.dispute_id:
            print("Please open a dispute first before submitting evidence.")
            return

        evidence_item = {
            'submitter_id': self.current_user_id,
            'submitter_role': 'Buyer', # Assuming buyer is using this instance
            'description': description,
            'file_path_or_url': file_path_or_url, # Could be a link to an uploaded file
            'timestamp': datetime.datetime.now()
        }
        self.evidence.append(evidence_item)
        print(f"Evidence submitted for dispute {self.dispute_id}: '{description}'")
        if file_path_or_url:
            print(f"File/Link: {file_path_or_url}")
        # Notify other parties.

    def resolve_dispute(self, resolution_details, resolved_by_role="Admin"):
        if not self.dispute_id or self.dispute_status not in ["Open", "Under Investigation"]:
            print("No active dispute to resolve or dispute already closed.")
            return

        self.dispute_status = "Resolved"
        resolution_message = f"Dispute resolved by {resolved_by_role}. Details: {resolution_details}"
        self.add_message(resolution_message, sender_role=resolved_by_role + " (System)")
        print(f"Dispute {self.dispute_id} has been marked as Resolved.")
        print(f"Resolution: {resolution_details}")
        # Implement actions based on resolution (e.g., process refund, etc.)

    # --- Mock methods for simulating other parties' actions ---
    def _receive_seller_message(self, text):
        if not self.dispute_id: return
        message = {
            'sender_id': "SELLER_MOCK_ID",
            'sender_role': "Seller",
            'text': text,
            'timestamp': datetime.datetime.now() + datetime.timedelta(minutes=5) # Simulate delay
        }
        self.messages.append(message)
        print(f"(Simulated) Seller message received for dispute {self.dispute_id}")

    def _receive_admin_decision(self, decision_text, resolution_details):
        if not self.dispute_id: return
        self.add_message(decision_text, sender_role="Admin")
        self.resolve_dispute(resolution_details, resolved_by_role="Admin")

    def escalate_to_admin(self):
        if not self.dispute_id or self.dispute_status != "Open":
            print("No open dispute to escalate or already escalated/resolved.")
            return

        self.dispute_status = "Under Investigation (Admin)"
        escalation_message = "Dispute escalated to admin by buyer."
        self.add_message(escalation_message, sender_role="Buyer (System)")
        print(f"Dispute {self.dispute_id} has been escalated to an administrator.")
        # Notify admin team.
