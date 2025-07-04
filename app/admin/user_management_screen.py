import datetime

class UserManagementScreen:
    def __init__(self):
        self.users = {}
        self._initialize_mock_users()

    def _initialize_mock_users(self):
        # Mock data for users
        self.users = {
            "user001": {
                "email": "alice@example.com",
                "is_verified": True,
                "roles": ["veterinarian", "admin"],
                "account_status": "active",
                "last_login": datetime.datetime.now() - datetime.timedelta(hours=2),
                "profile": {"name": "Dr. Alice Smith", "specialty": "General Practice"}
            },
            "user002": {
                "email": "bob@example.com",
                "is_verified": False,
                "roles": ["farmer"],
                "account_status": "pending_verification",
                "last_login": None,
                "profile": {"name": "Bob Johnson", "farm_size": "100 acres"}
            },
            "user003": {
                "email": "charlie@example.com",
                "is_verified": True,
                "roles": ["farmer", "seller"],
                "account_status": "active",
                "last_login": datetime.datetime.now() - datetime.timedelta(days=1),
                "profile": {"name": "Charlie Brown", "store_name": "Brown's Farm Produce"}
            },
            "user004": {
                "email": "diana@example.com",
                "is_verified": True,
                "roles": ["veterinarian"],
                "account_status": "suspended",
                "last_login": datetime.datetime.now() - datetime.timedelta(weeks=1),
                "profile": {"name": "Dr. Diana Prince", "specialty": "Surgery"}
            }
        }

    def get_user_details(self, user_id):
        return self.users.get(user_id)

    def list_users(self, filter_by=None):
        # In a real system, filter_by would be more complex (e.g., role, status)
        return self.users

    def verify_user(self, user_id):
        if user_id in self.users:
            self.users[user_id]["is_verified"] = True
            self.users[user_id]["account_status"] = "active" # Assuming verification activates
            print(f"User {user_id} verified.")
            return True
        print(f"User {user_id} not found.")
        return False

    def assign_role(self, user_id, role):
        if user_id in self.users:
            if role not in self.users[user_id]["roles"]:
                self.users[user_id]["roles"].append(role)
                print(f"Role '{role}' assigned to user {user_id}.")
                return True
            else:
                print(f"User {user_id} already has role '{role}'.")
                return False
        print(f"User {user_id} not found.")
        return False

    def revoke_role(self, user_id, role):
        if user_id in self.users and role in self.users[user_id]["roles"]:
            self.users[user_id]["roles"].remove(role)
            print(f"Role '{role}' revoked from user {user_id}.")
            return True
        print(f"User {user_id} not found or does not have role '{role}'.")
        return False

    def change_account_status(self, user_id, new_status):
        # new_status could be "active", "suspended", "banned", "deleted" etc.
        if user_id in self.users:
            self.users[user_id]["account_status"] = new_status
            print(f"Account status for user {user_id} changed to '{new_status}'.")
            return True
        print(f"User {user_id} not found.")
        return False

    def display_screen(self):
        print("---- User Management Screen ----")
        print("\n-- All Users --")
        for user_id, data in self.list_users().items():
            print(f"  ID: {user_id}, Email: {data['email']}, Verified: {data['is_verified']}, "
                  f"Roles: {', '.join(data['roles'])}, Status: {data['account_status']}")
            if data.get('profile'):
                profile_info = ", ".join([f"{k}: {v}" for k,v in data['profile'].items()])
                print(f"    Profile: {profile_info}")

        print("\n-- Actions (Examples) --")
        self.verify_user("user002")
        self.assign_role("user003", "admin")
        self.revoke_role("user001", "admin")
        self.change_account_status("user004", "active")

        print("\n-- Updated User List --")
        for user_id, data in self.list_users().items():
            print(f"  ID: {user_id}, Email: {data['email']}, Verified: {data['is_verified']}, "
                  f"Roles: {', '.join(data['roles'])}, Status: {data['account_status']}")

        print("-----------------------------")

if __name__ == '__main__':
    screen = UserManagementScreen()
    screen.display_screen()
