import datetime
import random

class CommissionManagementScreen:
    def __init__(self):
        self.commission_rates = {}
        self.transactions_for_commission = [] # Transactions that are subject to commission
        self._initialize_mock_data()

    def _initialize_mock_data(self):
        # Mock commission rates (can be by category, seller tier, etc.)
        self.commission_rates = {
            "default": {"rate": 0.05, "description": "Standard commission rate for all marketplace sales."},
            "electronics_category": {"rate": 0.08, "description": "Commission for electronics category."},
            "veterinary_services": {"rate": 0.10, "description": "Commission for services provided by vets via marketplace."},
            "premium_seller_tier": {"rate": 0.03, "description": "Reduced commission for premium sellers."}
        }

        # Mock transactions that would incur commission
        # These would typically come from a sales/order system
        seller_ids = [f"seller{str(i).zfill(3)}" for i in range(1, 10)]
        product_categories = ["general", "electronics_category", "veterinary_services", "books"]

        for i in range(20): # 20 sample transactions
            seller_id = random.choice(seller_ids)
            category = random.choice(product_categories)
            sale_amount = round(random.uniform(20.0, 300.0), 2)
            transaction_date = datetime.datetime.now() - datetime.timedelta(days=random.randint(0,30))

            # Determine applicable rate
            rate_key = "default"
            if category in self.commission_rates:
                rate_key = category
            # Example: some sellers might be premium
            if seller_id in ["seller001", "seller005"] and "premium_seller_tier" in self.commission_rates:
                # Let's say premium tier overrides category for these sellers if lower
                if self.commission_rates["premium_seller_tier"]["rate"] < self.commission_rates[rate_key]["rate"]:
                    rate_key = "premium_seller_tier"

            commission_rate_applied = self.commission_rates[rate_key]["rate"]
            commission_amount = round(sale_amount * commission_rate_applied, 2)

            self.transactions_for_commission.append({
                "transaction_id": f"sale_txn_{transaction_date.strftime('%Y%m%d')}_{str(i).zfill(3)}",
                "seller_id": seller_id,
                "sale_amount": sale_amount,
                "category": category,
                "transaction_date": transaction_date,
                "commission_rate_key": rate_key,
                "commission_rate_applied": commission_rate_applied,
                "commission_amount": commission_amount,
                "payout_status": random.choice(["pending", "paid_out"]) # Status of commission payout to platform
            })
        self.transactions_for_commission.sort(key=lambda x: x["transaction_date"], reverse=True)

    def get_commission_rates(self):
        return self.commission_rates

    def set_commission_rate(self, rate_key, rate_value, description, admin_id="admin"):
        if not (0 <= rate_value <= 1):
            print("Error: Rate value must be between 0 and 1 (e.g., 0.05 for 5%).")
            return False

        self.commission_rates[rate_key] = {
            "rate": rate_value,
            "description": description,
            "updated_at": datetime.datetime.now(),
            "updated_by": admin_id
        }
        print(f"Commission rate for '{rate_key}' set to {rate_value*100}% by {admin_id}.")
        # In a real system, you'd re-calculate future commissions or log this change carefully.
        return True

    def calculate_commission(self, sale_amount, category="general", seller_id=None):
        rate_key = "default"
        if category in self.commission_rates:
            rate_key = category

        # Example: Check for premium seller override
        if seller_id and "premium_seller_tier" in self.commission_rates:
             # Assuming premium tier is always better if applicable
            if self.commission_rates["premium_seller_tier"]["rate"] < self.commission_rates[rate_key]["rate"]:
                 rate_key = "premium_seller_tier"

        rate_details = self.commission_rates.get(rate_key)
        if not rate_details: # Fallback if somehow key is invalid
            rate_details = self.commission_rates.get("default")

        commission = sale_amount * rate_details["rate"]
        return round(commission, 2), rate_details["rate"], rate_key

    def get_commissioned_transactions(self, seller_id=None, payout_status=None):
        results = self.transactions_for_commission
        if seller_id:
            results = [tx for tx in results if tx["seller_id"] == seller_id]
        if payout_status:
            results = [tx for tx in results if tx["payout_status"] == payout_status]
        return results

    def display_screen(self):
        print("---- Commission Management Screen ----")

        print("\n-- Current Commission Rates --")
        for key, details in self.get_commission_rates().items():
            print(f"  Rate Key: {key}, Rate: {details['rate']*100:.2f}%, Description: {details['description']}")
            if "updated_at" in details:
                print(f"    Last Updated: {details['updated_at'].strftime('%Y-%m-%d %H:%M')} by {details['updated_by']}")

        print("\n-- Example Commission Calculation --")
        test_sale_amount = 150.00
        test_category = "electronics_category"
        commission, rate, key = self.calculate_commission(test_sale_amount, test_category)
        print(f"  Commission for a ${test_sale_amount:.2f} sale in '{test_category}' (rate key '{key}'): ${commission:.2f} (at {rate*100:.2f}%)")

        test_sale_amount_premium = 200.00
        test_category_premium = "general"
        test_seller_premium = "seller001" # Mock premium seller
        commission_p, rate_p, key_p = self.calculate_commission(test_sale_amount_premium, test_category_premium, test_seller_premium)
        print(f"  Commission for a ${test_sale_amount_premium:.2f} sale by '{test_seller_premium}' in '{test_category_premium}' (rate key '{key_p}'): ${commission_p:.2f} (at {rate_p*100:.2f}%)")


        print("\n-- Recent Commissioned Transactions (Top 5) --")
        recent_txs = self.get_commissioned_transactions()[:5]
        if not recent_txs:
            print("  No commissioned transactions found.")
        else:
            for tx in recent_txs:
                print(f"  ID: {tx['transaction_id']}, Seller: {tx['seller_id']}, Sale: ${tx['sale_amount']:.2f}, "
                      f"Commission: ${tx['commission_amount']:.2f} ({tx['commission_rate_applied']*100:.1f}%), "
                      f"Category: {tx['category']}, Payout Status: {tx['payout_status']}")

        print("\n-- Update Commission Rate (Example) --")
        self.set_commission_rate("default", 0.06, "Updated standard commission to 6%", "admin_jules")
        print(f"  New default rate: {self.commission_rates['default']['rate']*100}%")


        print("\n-- Pending Commission Payouts for seller002 --")
        pending_seller_txs = self.get_commissioned_transactions(seller_id="seller002", payout_status="pending")
        if not pending_seller_txs:
            print("  No pending commission payouts for seller002.")
        else:
            total_pending_commission = sum(tx['commission_amount'] for tx in pending_seller_txs)
            for tx in pending_seller_txs:
                print(f"  ID: {tx['transaction_id']}, Sale: ${tx['sale_amount']:.2f}, Commission: ${tx['commission_amount']:.2f}")
            print(f"  Total Pending Commission for seller002: ${total_pending_commission:.2f}")

        print("-----------------------------------")

if __name__ == '__main__':
    screen = CommissionManagementScreen()
    screen.display_screen()
