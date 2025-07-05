# Placeholder for NegotiationScreen
class NegotiationScreen:
    def __init__(self, buyer_id, seller_id, product_id):
        self.buyer_id = buyer_id
        self.seller_id = seller_id
        self.product_id = product_id
        self.messages = [] # Stores negotiation messages (offers, counter-offers, text)
        self.current_offer = None # Could be a dict: {'price_per_unit': X, 'quantity': Y, 'by_user': 'buyer/seller'}

    def display_negotiation_chat(self):
        # TODO: Implement display of negotiation messages and offer status
        print(f"--- Negotiation for Product {self.product_id} (Buyer: {self.buyer_id}, Seller: {self.seller_id}) ---")
        if not self.messages:
            print("No messages yet. Start the negotiation by making an offer or sending a message.")
        else:
            for msg in self.messages:
                sender = "You" if msg['sender_id'] == self.buyer_id else f"Seller ({self.seller_id})"
                if msg['type'] == 'text':
                    print(f"[{sender}]: {msg['content']}")
                elif msg['type'] == 'offer':
                    print(f"[{sender} made an offer]: Quantity: {msg['quantity']}, Price/unit: ${msg['price_per_unit']:.2f}")

        if self.current_offer:
            offerer = "Your" if self.current_offer['by_user'] == 'buyer' else "Seller's"
            print(f"\n--- Current {offerer} Offer ---")
            print(f"Quantity: {self.current_offer['quantity']}, Price/unit: ${self.current_offer['price_per_unit']:.2f}")
            print("-----------------------------")
        print("------------------------------------------------------------------")


    def send_message(self, text_content):
        # TODO: Implement sending a text message in the negotiation
        message = {'type': 'text', 'sender_id': self.buyer_id, 'receiver_id': self.seller_id, 'content': text_content}
        self.messages.append(message)
        print(f"Message sent: '{text_content}'")
        # In a real system, this would be sent to a backend and pushed to the seller

    def make_offer(self, quantity, price_per_unit):
        # TODO: Implement making an offer (price, quantity)
        offer = {
            'type': 'offer',
            'sender_id': self.buyer_id,
            'receiver_id': self.seller_id,
            'quantity': quantity,
            'price_per_unit': price_per_unit
        }
        self.messages.append(offer)
        self.current_offer = {'price_per_unit': price_per_unit, 'quantity': quantity, 'by_user': 'buyer'}
        print(f"Offer sent: Quantity {quantity} at ${price_per_unit:.2f} per unit.")
        # In a real system, this would be sent to a backend

    def accept_offer(self):
        # TODO: Implement accepting the current offer (if made by seller)
        if self.current_offer and self.current_offer['by_user'] == 'seller':
            print(f"Accepting seller's offer: Quantity {self.current_offer['quantity']} at ${self.current_offer['price_per_unit']:.2f} per unit.")
            # TODO: Logic to proceed to checkout/order creation with negotiated terms
            self.messages.append({'type': 'status', 'content': f"Buyer accepted offer."})
            # Clear current offer after acceptance or formalize it.
        else:
            print("No current offer from the seller to accept, or the current offer is yours.")

    def reject_offer(self):
        # TODO: Implement rejecting the current offer (if made by seller)
        if self.current_offer and self.current_offer['by_user'] == 'seller':
            print(f"Rejecting seller's offer.")
            self.messages.append({'type': 'status', 'content': f"Buyer rejected offer."})
            self.current_offer = None # Offer is off the table
        else:
            print("No current offer from the seller to reject, or the current offer is yours.")

    # Mock seller actions for testing purposes
    def _receive_seller_message(self, text_content):
        message = {'type': 'text', 'sender_id': self.seller_id, 'receiver_id': self.buyer_id, 'content': text_content}
        self.messages.append(message)
        print(f"Received message from seller: '{text_content}'")

    def _receive_seller_offer(self, quantity, price_per_unit):
        offer = {
            'type': 'offer',
            'sender_id': self.seller_id,
            'receiver_id': self.buyer_id,
            'quantity': quantity,
            'price_per_unit': price_per_unit
        }
        self.messages.append(offer)
        self.current_offer = {'price_per_unit': price_per_unit, 'quantity': quantity, 'by_user': 'seller'}
        print(f"Received offer from seller: Quantity {quantity} at ${price_per_unit:.2f} per unit.")
