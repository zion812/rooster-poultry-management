import datetime
import random

class PayoutSystemScreen:
    def __init__(self):
        self.payout_requests = []
        self.payout_history = []
        self.seller_balances = {} # Simplified: user_id -> available_for_payout
        self._initialize_mock_data()

    def _initialize_mock_data(self):
        seller_ids = [f"seller{str(i).zfill(3)}" for i in range(1, 10)]
        payout_methods = ["paypal", "bank_transfer", "payoneer"]

        # Initialize seller balances (these would be derived from sales minus commissions)
        for seller_id in seller_ids:
            self.seller_balances[seller_id] = round(random.uniform(50.0, 2000.0), 2)

        # Mock payout requests
        for i in range(5): # 5 pending requests
            seller_id = random.choice(seller_ids)
            available_balance = self.seller_balances.get(seller_id, 0)
            if available_balance < 20: # Min payout amount
                continue

            request_amount = round(random.uniform(20.0, min(available_balance, 500.0)), 2) # Request up to available or $500

            self.payout_requests.append({
                "request_id": f"payoutreq_{datetime.datetime.now().strftime('%Y%m%d')}_{str(i).zfill(3)}",
                "seller_id": seller_id,
                "amount_requested": request_amount,
                "currency": "USD",
                "requested_at": datetime.datetime.now() - datetime.timedelta(days=random.randint(0, 5)),
                "payout_method_details": {"method": random.choice(payout_methods), "account_id": f"acc_{random.randint(1000,9999)}"},
                "status": "pending_approval", # Other statuses: approved, processing, completed, rejected
                "notes": ""
            })
        self.payout_requests.sort(key=lambda x: x["requested_at"])

        # Mock payout history
        for i in range(10): # 10 historical payouts
            seller_id = random.choice(seller_ids)
            amount_paid = round(random.uniform(20.0, 500.0), 2)
            completion_date = datetime.datetime.now() - datetime.timedelta(days=random.randint(1, 60))
            self.payout_history.append({
                "payout_id": f"payout_{completion_date.strftime('%Y%m%d')}_{str(i).zfill(3)}",
                "seller_id": seller_id,
                "amount_paid": amount_paid,
                "currency": "USD",
                "processed_at": completion_date - datetime.timedelta(hours=random.randint(1,6)),
                "completed_at": completion_date,
                "payout_method_details": {"method": random.choice(payout_methods), "account_id": f"acc_{random.randint(1000,9999)}"},
                "status": "completed",
                "transaction_reference": f"ref_{random.randint(100000,999999)}"
            })
        self.payout_history.sort(key=lambda x: x["completed_at"], reverse=True)

    def get_pending_payout_requests(self):
        return [req for req in self.payout_requests if req["status"] in ["pending_approval", "approved"]]

    def get_seller_balance(self, seller_id):
        return self.seller_balances.get(seller_id, 0.0)

    def approve_payout_request(self, request_id, admin_id):
        for req in self.payout_requests:
            if req["request_id"] == request_id and req["status"] == "pending_approval":
                # Check if seller has enough balance
                if self.seller_balances.get(req["seller_id"], 0) >= req["amount_requested"]:
                    req["status"] = "approved"
                    req["approved_by"] = admin_id
                    req["approved_at"] = datetime.datetime.now()
                    print(f"Payout request {request_id} approved by {admin_id}.")
                    return True
                else:
                    req["status"] = "rejected"
                    req["notes"] = "Insufficient balance at time of approval."
                    req["processed_by"] = admin_id
                    print(f"Payout request {request_id} rejected due to insufficient balance.")
                    return False
        print(f"Payout request {request_id} not found or not pending approval.")
        return False

    def process_payout(self, request_id, admin_id, transaction_reference):
        # This would typically involve an external payment gateway integration
        for req in self.payout_requests:
            if req["request_id"] == request_id and req["status"] == "approved":
                req["status"] = "processing"
                print(f"Payout {request_id} is now processing...")

                # Simulate processing delay and completion
                # In real system, this would be asynchronous
                self.seller_balances[req["seller_id"]] -= req["amount_requested"]
                req["status"] = "completed"
                req["processed_by"] = admin_id
                req["processed_at"] = datetime.datetime.now()
                req["completed_at"] = datetime.datetime.now() + datetime.timedelta(minutes=random.randint(5,30)) # mock completion time
                req["transaction_reference"] = transaction_reference

                self.payout_history.insert(0, req) # Add to history
                self.payout_requests = [r for r in self.payout_requests if r["request_id"] != request_id] # Remove from active requests

                print(f"Payout {request_id} for seller {req['seller_id']} of ${req['amount_requested']:.2f} completed. Ref: {transaction_reference}")
                return True
        print(f"Payout request {request_id} not found or not approved for processing.")
        return False

    def reject_payout_request(self, request_id, reason, admin_id):
        for req in self.payout_requests:
            if req["request_id"] == request_id and req["status"] in ["pending_approval", "approved"]:
                req["status"] = "rejected"
                req["notes"] = reason
                req["processed_by"] = admin_id
                req["processed_at"] = datetime.datetime.now()
                print(f"Payout request {request_id} rejected by {admin_id}. Reason: {reason}")
                # Optionally add to history as rejected if needed, or just remove from requests
                # self.payout_history.insert(0, req)
                # self.payout_requests = [r for r in self.payout_requests if r["request_id"] != request_id]
                return True
        print(f"Payout request {request_id} not found or cannot be rejected.")
        return False

    def get_payout_history(self, seller_id=None):
        if seller_id:
            return [p for p in self.payout_history if p["seller_id"] == seller_id]
        return self.payout_history

    def display_screen(self, admin_id="finance_admin_jules"):
        print("---- Payout System Screen ----")

        print("\n-- Seller Balances (Sample) --")
        for i, (seller, balance) in enumerate(self.seller_balances.items()):
            if i >= 3: break # Show first 3
            print(f"  Seller ID: {seller}, Available Balance: ${balance:.2f}")

        print("\n-- Pending Payout Requests --")
        pending_reqs = self.get_pending_payout_requests()
        if not pending_reqs:
            print("  No pending payout requests.")
        else:
            for req in pending_reqs[:3]: # Show top 3
                print(f"  Req ID: {req['request_id']}, Seller: {req['seller_id']}, Amount: ${req['amount_requested']:.2f}, "
                      f"Method: {req['payout_method_details']['method']}, Status: {req['status']}")

        print("\n-- Actions (Examples) --")
        if pending_reqs:
            first_req_id = pending_reqs[0]['request_id']
            print(f"  Attempting to approve request: {first_req_id}")
            if self.approve_payout_request(first_req_id, admin_id):
                 # Find the approved request again to process it
                approved_req = next((r for r in self.payout_requests if r['request_id'] == first_req_id and r['status'] == 'approved'), None)
                if approved_req:
                    print(f"  Attempting to process approved request: {first_req_id}")
                    self.process_payout(first_req_id, admin_id, f"mock_txn_ref_{random.randint(1000,9999)}")

            if len(pending_reqs) > 1:
                second_req_id = pending_reqs[1]['request_id']
                print(f"  Attempting to reject request: {second_req_id}")
                self.reject_payout_request(second_req_id, "Manual review required due to high amount.", admin_id)


        print("\n-- Payout History (Last 5) --")
        history = self.get_payout_history()[:5]
        if not history:
            print("  No payout history.")
        else:
            for item in history:
                print(f"  Payout ID: {item.get('payout_id', item['request_id'])}, Seller: {item['seller_id']}, Amount: ${item.get('amount_paid', item['amount_requested']):.2f}, "
                      f"Status: {item['status']}, Completed: {item.get('completed_at', item.get('processed_at')).strftime('%Y-%m-%d %H:%M') if item.get('completed_at') or item.get('processed_at') else 'N/A'}")

        print("-----------------------------")

if __name__ == '__main__':
    screen = PayoutSystemScreen()
    screen.display_screen()
