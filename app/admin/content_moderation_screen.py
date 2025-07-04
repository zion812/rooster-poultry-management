import datetime

class ContentModerationScreen:
    def __init__(self):
        self.reported_content = {}
        self._initialize_mock_content()

    def _initialize_mock_content(self):
        # Mock data for reported content
        # Content types could be 'post', 'comment', 'user_profile', 'listing', etc.
        self.reported_content = {
            "report001": {
                "content_id": "post123",
                "content_type": "forum_post",
                "reporter_user_id": "user002",
                "reported_user_id": "user003",
                "reason": "Spam and unsolicited advertising.",
                "timestamp": datetime.datetime.now() - datetime.timedelta(hours=1),
                "status": "pending_review", # Other statuses: 'approved', 'rejected', 'action_taken'
                "content_preview": "Check out my amazing new product at spam.com!",
                "moderator_notes": ""
            },
            "report002": {
                "content_id": "comment456",
                "content_type": "article_comment",
                "reporter_user_id": "user001",
                "reported_user_id": "user004",
                "reason": "Offensive language.",
                "timestamp": datetime.datetime.now() - datetime.timedelta(hours=3),
                "status": "pending_review",
                "content_preview": "This article is terrible and the author is an idiot.",
                "moderator_notes": ""
            },
            "report003": {
                "content_id": "listing789",
                "content_type": "marketplace_listing",
                "reporter_user_id": "user003",
                "reported_user_id": "user002",
                "reason": "Misleading product description.",
                "timestamp": datetime.datetime.now() - datetime.timedelta(days=1),
                "status": "action_taken", # Example of an already handled report
                "action_taken": "listing_removed",
                "content_preview": "Miracle cure for all animal diseases! Guaranteed!",
                "moderator_notes": "Removed listing due to false claims. Warned user."
            }
        }

    def get_pending_reports(self):
        return {report_id: report_data for report_id, report_data in self.reported_content.items()
                if report_data["status"] == "pending_review"}

    def get_report_details(self, report_id):
        return self.reported_content.get(report_id)

    def review_content(self, report_id, action, moderator_id, notes=""):
        # Possible actions: 'approve_content', 'remove_content', 'warn_user', 'ban_user', 'reject_report'
        if report_id in self.reported_content:
            report = self.reported_content[report_id]
            report["status"] = "action_taken"
            report["action_taken"] = action
            report["moderator_id"] = moderator_id
            report["moderator_notes"] = notes
            report["review_timestamp"] = datetime.datetime.now()
            print(f"Report {report_id} reviewed. Action: {action}. Moderator: {moderator_id}.")
            # Here, you would trigger the actual action (e.g., call a service to remove content or warn user)
            return True
        print(f"Report {report_id} not found.")
        return False

    def display_screen(self, moderator_id="admin001"):
        print("---- Content Moderation Screen ----")
        print("\n-- Pending Reports --")
        pending_reports = self.get_pending_reports()
        if not pending_reports:
            print("  No pending reports.")
        else:
            for report_id, data in pending_reports.items():
                print(f"  Report ID: {report_id}")
                print(f"    Content ID: {data['content_id']} ({data['content_type']})")
                print(f"    Reported by: {data['reporter_user_id']} against {data['reported_user_id']}")
                print(f"    Reason: {data['reason']}")
                print(f"    Preview: '{data['content_preview']}'")
                print(f"    Reported at: {data['timestamp']}")

        print("\n-- Actions (Examples) --")
        # Example: Moderator reviews a report
        if "report001" in pending_reports:
            self.review_content("report001", "remove_content", moderator_id, "Content confirmed as spam.")
        if "report002" in pending_reports:
             self.review_content("report002", "warn_user", moderator_id, "User warned for offensive language. Comment hidden.")


        print("\n-- All Reports (Post Action) --")
        for report_id, data in self.reported_content.items():
            print(f"  Report ID: {report_id}, Status: {data['status']}")
            if data['status'] == 'action_taken':
                print(f"    Action: {data.get('action_taken')}, Moderator: {data.get('moderator_id')}, Notes: {data.get('moderator_notes')}")
        print("---------------------------------")

if __name__ == '__main__':
    screen = ContentModerationScreen()
    screen.display_screen(moderator_id="mod_jane")
