# Placeholder for NotificationScreen
import datetime

class NotificationScreen:
    def __init__(self, user_id):
        self.user_id = user_id
        self.notifications = [] # In-memory list of notifications
        # In a real app, notifications would be fetched from a backend service

    def display_notifications(self):
        print(f"--- Notifications for User: {self.user_id} ---")
        if not self.notifications:
            print("No new notifications.")
        else:
            # Display newest first
            for notification in sorted(self.notifications, key=lambda x: x['timestamp'], reverse=True):
                status = "(Unread)" if not notification.get('is_read') else "(Read)"
                print(f"[{notification['timestamp'].strftime('%Y-%m-%d %H:%M')}] {status} {notification['type'].upper()}: {notification['message']}")
                if 'link' in notification:
                    print(f"  Link: {notification['link']}") # e.g., link to order, product, chat
        print("------------------------------------")

    def add_notification(self, type, message, link=None):
        # This method would typically be called by backend services or other system parts,
        # not directly by the user controlling this screen.
        # For simulation, we can call it to populate notifications.
        notification = {
            'notification_id': f"NOTIF{len(self.notifications) + 1:04d}", # Simple unique ID
            'user_id': self.user_id,
            'type': type, # e.g., "price_alert", "order_update", "promotion", "chat_message", "dispute_update"
            'message': message,
            'timestamp': datetime.datetime.now(),
            'is_read': False,
            'link': link # Optional link to relevant screen/item
        }
        self.notifications.append(notification)
        print(f"(System: New notification '{type}' added for user {self.user_id})")

    def mark_as_read(self, notification_id):
        # TODO: Implement marking a specific notification as read
        for notif in self.notifications:
            if notif['notification_id'] == notification_id:
                if not notif['is_read']:
                    notif['is_read'] = True
                    print(f"Notification {notification_id} marked as read.")
                else:
                    print(f"Notification {notification_id} was already read.")
                return
        print(f"Notification {notification_id} not found.")

    def mark_all_as_read(self):
        # TODO: Implement marking all notifications as read
        updated_count = 0
        for notif in self.notifications:
            if not notif['is_read']:
                notif['is_read'] = True
                updated_count +=1
        if updated_count > 0:
            print(f"{updated_count} notifications marked as read.")
        else:
            print("No unread notifications to mark.")

    def clear_notification(self, notification_id):
        # TODO: Implement removing a notification (e.g., after it's handled or dismissed)
        initial_len = len(self.notifications)
        self.notifications = [n for n in self.notifications if n['notification_id'] != notification_id]
        if len(self.notifications) < initial_len:
            print(f"Notification {notification_id} cleared.")
        else:
            print(f"Notification {notification_id} not found to clear.")

    def clear_all_notifications(self, only_read=False):
        # TODO: Implement clearing all (or all read) notifications
        initial_len = len(self.notifications)
        if only_read:
            self.notifications = [n for n in self.notifications if not n['is_read']]
            cleared_count = initial_len - len(self.notifications)
            print(f"{cleared_count} read notifications cleared.")
        else:
            self.notifications = []
            print(f"{initial_len} notifications cleared.")


    def get_unread_count(self):
        count = sum(1 for n in self.notifications if not n['is_read'])
        print(f"User {self.user_id} has {count} unread notifications.")
        return count

    # --- Methods to simulate receiving various notifications ---
    def simulate_price_alert(self, product_name, new_price, product_link):
        self.add_notification(
            type="price_alert",
            message=f"Price drop for {product_name}! Now ${new_price:.2f}.",
            link=product_link
        )

    def simulate_order_update(self, order_id, status, order_link):
        self.add_notification(
            type="order_update",
            message=f"Your order {order_id} has been updated to: {status}.",
            link=order_link
        )

    def simulate_promotion(self, promo_message, promo_link=None):
        self.add_notification(
            type="promotion",
            message=promo_message,
            link=promo_link
        )

    def simulate_new_chat_message(self, sender_name, chat_link):
        self.add_notification(
            type="chat_message",
            message=f"You have a new message from {sender_name}.",
            link=chat_link
        )

    def simulate_dispute_update(self, dispute_id, update_message, dispute_link):
        self.add_notification(
            type="dispute_update",
            message=f"Update on dispute {dispute_id}: {update_message}",
            link=dispute_link
        )
