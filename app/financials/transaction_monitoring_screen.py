import datetime
import random

class TransactionMonitoringScreen:
    def __init__(self):
        self.transactions = []
        self._initialize_mock_transactions()

    def _initialize_mock_transactions(self, count=50):
        # Mock data for transactions
        payment_methods = ["credit_card", "paypal", "bank_transfer", "crypto"]
        statuses = ["completed", "pending", "failed", "refunded", "disputed"]
        transaction_types = ["consultation_fee", "marketplace_sale", "subscription", "payout_fee", "refund"]
        user_ids = [f"user{str(i).zfill(3)}" for i in range(1, 20)]
        product_ids = [f"prod{str(i).zfill(3)}" for i in range(1, 10)]

        for i in range(count):
            timestamp = datetime.datetime.now() - datetime.timedelta(days=random.randint(0, 30), hours=random.randint(0,23), minutes=random.randint(0,59))
            amount = round(random.uniform(5.0, 500.0), 2)
            status = random.choice(statuses)

            # Basic fraud flags
            fraud_flags = []
            if amount > 400 and random.random() < 0.3:
                fraud_flags.append("high_value_transaction")
            if status == "failed" and random.random() < 0.2:
                 fraud_flags.append("multiple_failed_attempts_suspected") # This would need more context in reality
            if random.random() < 0.05: # Small chance for any transaction
                fraud_flags.append("unusual_location_match") # Mock, real system would check IP/Geo

            transaction = {
                "transaction_id": f"txn_{timestamp.strftime('%Y%m%d')}_{str(i).zfill(4)}",
                "timestamp": timestamp,
                "user_id": random.choice(user_ids),
                "amount": amount,
                "currency": "USD",
                "payment_method": random.choice(payment_methods),
                "status": status,
                "transaction_type": random.choice(transaction_types),
                "description": f"{random.choice(transaction_types).replace('_', ' ').title()} for {random.choice(product_ids) if 'sale' in transaction_types[-1] else random.choice(user_ids)}",
                "fraud_flags": fraud_flags,
                "ip_address": f"192.168.1.{random.randint(1,254)}" # Mock IP
            }
            if status == "refunded":
                transaction["original_transaction_id"] = f"txn_{timestamp.strftime('%Y%m%d')}_{str(random.randint(0,i-1 if i>0 else 0)).zfill(4)}"

            self.transactions.append(transaction)

        # Sort by timestamp descending
        self.transactions.sort(key=lambda x: x["timestamp"], reverse=True)

    def get_all_transactions(self):
        return self.transactions

    def get_transaction_by_id(self, transaction_id):
        for tx in self.transactions:
            if tx["transaction_id"] == transaction_id:
                return tx
        return None

    def filter_transactions(self, status=None, user_id=None, min_amount=None, has_fraud_flags=None):
        results = self.transactions
        if status:
            results = [tx for tx in results if tx["status"] == status]
        if user_id:
            results = [tx for tx in results if tx["user_id"] == user_id]
        if min_amount is not None:
            results = [tx for tx in results if tx["amount"] >= min_amount]
        if has_fraud_flags is True:
            results = [tx for tx in results if len(tx["fraud_flags"]) > 0]
        if has_fraud_flags is False: # Explicitly filter for no flags
            results = [tx for tx in results if len(tx["fraud_flags"]) == 0]
        return results

    def flag_transaction_for_review(self, transaction_id, reason, admin_id):
        tx = self.get_transaction_by_id(transaction_id)
        if tx:
            if "manual_review_flags" not in tx:
                tx["manual_review_flags"] = []
            tx["manual_review_flags"].append({
                "reason": reason,
                "admin_id": admin_id,
                "timestamp": datetime.datetime.now()
            })
            print(f"Transaction {transaction_id} flagged for manual review by {admin_id}: {reason}")
            return True
        print(f"Transaction {transaction_id} not found.")
        return False

    def display_screen(self):
        print("---- Transaction Monitoring Screen ----")

        print("\n-- Recent Transactions (Top 5) --")
        for tx in self.get_all_transactions()[:5]:
            print(f"  ID: {tx['transaction_id']}, Time: {tx['timestamp'].strftime('%Y-%m-%d %H:%M')}, User: {tx['user_id']}, "
                  f"Amount: ${tx['amount']:.2f}, Status: {tx['status']}, Type: {tx['transaction_type']}")
            if tx['fraud_flags']:
                print(f"    Fraud Flags: {', '.join(tx['fraud_flags'])}")
            if tx.get('manual_review_flags'):
                print(f"    Manual Review: {tx['manual_review_flags'][-1]['reason']}")


        print("\n-- Transactions with Fraud Flags --")
        flagged_txns = self.filter_transactions(has_fraud_flags=True)
        if not flagged_txns:
            print("  No transactions with automatic fraud flags.")
        for tx in flagged_txns[:3]: # Display first 3
            print(f"  ID: {tx['transaction_id']}, User: {tx['user_id']}, Amount: ${tx['amount']:.2f}, Flags: {', '.join(tx['fraud_flags'])}")
            self.flag_transaction_for_review(tx['transaction_id'], "Automatic high value flag", "system_auto_flag")


        print("\n-- Filter Example: Failed transactions for user007 --")
        user_failed_txns = self.filter_transactions(status="failed", user_id="user007")
        if not user_failed_txns:
            print("  No matching transactions found for user007 with status failed.")
        for tx in user_failed_txns:
            print(f"  ID: {tx['transaction_id']}, Time: {tx['timestamp'].strftime('%Y-%m-%d %H:%M')}, Amount: ${tx['amount']:.2f}")

        print("------------------------------------")

if __name__ == '__main__':
    screen = TransactionMonitoringScreen()
    screen.display_screen()
