import datetime
import random

class FeatureFlagScreen:
    def __init__(self):
        self.feature_flags = {}
        self._initialize_mock_flags()

    def _initialize_mock_flags(self):
        # Mock data for feature flags
        self.feature_flags = {
            "new_telemedicine_interface": {
                "description": "Enable the redesigned telemedicine interface for video consultations.",
                "is_active": False,
                "rollout_percentage": 0, # Percentage of users this feature is active for
                "target_user_segment": "all", # e.g., 'all', 'veterinarians', 'farmers_premium', specific user_ids list
                "created_at": datetime.datetime.now() - datetime.timedelta(days=10),
                "updated_at": datetime.datetime.now() - datetime.timedelta(days=1),
                "created_by": "admin_jane"
            },
            "ai_diagnosis_assistant_beta": {
                "description": "Enable the AI-powered diagnosis assistant (Beta).",
                "is_active": True,
                "rollout_percentage": 10, # Active for 10% of the target segment
                "target_user_segment": "veterinarians_approved_beta",
                "created_at": datetime.datetime.now() - datetime.timedelta(days=30),
                "updated_at": datetime.datetime.now() - datetime.timedelta(hours=5),
                "created_by": "admin_john"
            },
            "marketplace_commission_increase_test": {
                "description": "A/B Test: Increase marketplace commission from 5% to 7% for a subset of new sellers.",
                "is_active": True, # The A/B test itself is active
                "rollout_percentage": 5, # 5% of new sellers get the 7% commission
                "target_user_segment": "new_sellers_last_7_days",
                "created_at": datetime.datetime.now() - datetime.timedelta(days=3),
                "updated_at": datetime.datetime.now() - datetime.timedelta(days=1),
                "created_by": "admin_jane"
            },
             "disable_legacy_reporting": {
                "description": "Disable the old reporting system for users who have migrated to the new one.",
                "is_active": False,
                "rollout_percentage": 0,
                "target_user_segment": "migrated_users_group_A",
                "created_at": datetime.datetime.now() - datetime.timedelta(days=5),
                "updated_at": datetime.datetime.now() - datetime.timedelta(days=5),
                "created_by": "admin_john"
            }
        }

    def get_all_flags(self):
        return self.feature_flags

    def get_flag_details(self, flag_name):
        return self.feature_flags.get(flag_name)

    def create_flag(self, flag_name, description, target_user_segment="all", created_by="system"):
        if flag_name in self.feature_flags:
            print(f"Error: Feature flag '{flag_name}' already exists.")
            return False
        self.feature_flags[flag_name] = {
            "description": description,
            "is_active": False,
            "rollout_percentage": 0,
            "target_user_segment": target_user_segment,
            "created_at": datetime.datetime.now(),
            "updated_at": datetime.datetime.now(),
            "created_by": created_by
        }
        print(f"Feature flag '{flag_name}' created successfully.")
        return True

    def update_flag_status(self, flag_name, is_active, updated_by="system"):
        if flag_name in self.feature_flags:
            self.feature_flags[flag_name]["is_active"] = is_active
            self.feature_flags[flag_name]["updated_at"] = datetime.datetime.now()
            # In a real system, you might want to log who updated it.
            print(f"Feature flag '{flag_name}' status set to {'active' if is_active else 'inactive'}.")
            return True
        print(f"Feature flag '{flag_name}' not found.")
        return False

    def update_rollout_percentage(self, flag_name, percentage, updated_by="system"):
        if flag_name in self.feature_flags:
            if 0 <= percentage <= 100:
                self.feature_flags[flag_name]["rollout_percentage"] = percentage
                self.feature_flags[flag_name]["updated_at"] = datetime.datetime.now()
                print(f"Feature flag '{flag_name}' rollout percentage set to {percentage}%.")
                return True
            else:
                print("Error: Percentage must be between 0 and 100.")
                return False
        print(f"Feature flag '{flag_name}' not found.")
        return False

    def update_target_segment(self, flag_name, segment_name, updated_by="system"):
        if flag_name in self.feature_flags:
            self.feature_flags[flag_name]["target_user_segment"] = segment_name
            self.feature_flags[flag_name]["updated_at"] = datetime.datetime.now()
            print(f"Feature flag '{flag_name}' target segment set to '{segment_name}'.")
            return True
        print(f"Feature flag '{flag_name}' not found.")
        return False

    def delete_flag(self, flag_name):
        if flag_name in self.feature_flags:
            del self.feature_flags[flag_name]
            print(f"Feature flag '{flag_name}' deleted.")
            return True
        print(f"Feature flag '{flag_name}' not found.")
        return False

    def display_screen(self, current_admin="admin_cli_user"):
        print("---- Feature Flag Management Screen ----")
        print("\n-- Current Feature Flags --")
        flags = self.get_all_flags()
        if not flags:
            print("  No feature flags configured.")
        else:
            for name, data in flags.items():
                print(f"  Flag Name: {name}")
                print(f"    Description: {data['description']}")
                print(f"    Active: {'Yes' if data['is_active'] else 'No'}")
                print(f"    Rollout: {data['rollout_percentage']}%")
                print(f"    Target Segment: {data['target_user_segment']}")
                print(f"    Last Updated: {data['updated_at'].strftime('%Y-%m-%d %H:%M')}")
                print(f"    Created By: {data['created_by']}")

        print("\n-- Actions (Examples) --")
        self.create_flag("new_dashboard_layout", "Enable a new layout for the main user dashboard.", "beta_testers_group", current_admin)
        self.update_flag_status("new_telemedicine_interface", True, current_admin)
        self.update_rollout_percentage("new_telemedicine_interface", 50, current_admin)
        self.update_target_segment("ai_diagnosis_assistant_beta", "all_veterinarians", current_admin)
        self.delete_flag("disable_legacy_reporting")

        print("\n-- Feature Flags After Actions --")
        flags_after = self.get_all_flags()
        for name, data in flags_after.items():
            print(f"  Flag Name: {name}, Active: {'Yes' if data['is_active'] else 'No'}, Rollout: {data['rollout_percentage']}%, Segment: {data['target_user_segment']}")

        print("------------------------------------")

if __name__ == '__main__':
    screen = FeatureFlagScreen()
    screen.display_screen()
