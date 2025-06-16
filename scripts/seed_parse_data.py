```python
import requests
import json
import random
from datetime import datetime, timedelta

# --- Configuration ---
PARSE_SERVER_URL = "YOUR_PARSE_SERVER_URL"  # e.g., https://api.back4app.com
PARSE_APP_ID = "YOUR_PARSE_APP_ID"
PARSE_REST_API_KEY = "YOUR_PARSE_REST_API_KEY"
# PARSE_MASTER_KEY = "YOUR_PARSE_MASTER_KEY" # Use Master Key for schema changes or protected fields

HEADERS = {
    "X-Parse-Application-Id": PARSE_APP_ID,
    "X-Parse-REST-API-Key": PARSE_REST_API_KEY,
    # "X-Parse-Master-Key": PARSE_MASTER_KEY, # Uncomment if master key is needed
    "Content-Type": "application/json"
}

# --- Helper Functions ---
def create_object(class_name, data):
    """Creates a Parse object and returns its objectId."""
    try:
        response = requests.post(f"{PARSE_SERVER_URL}/classes/{class_name}", headers=HEADERS, json=data)
        response.raise_for_status() # Raises an exception for bad status codes
        print(f"Created {class_name}: {response.json().get('objectId')}")
        return response.json().get('objectId')
    except requests.exceptions.RequestException as e:
        print(f"Error creating {class_name}: {e}")
        if e.response is not None:
            print(f"Response content: {e.response.text}")
        return None

def get_random_user_id(user_type="Farmer"):
    """Fetches a random user ID of a specific type (conceptual)."""
    # In a real script, query the _User table for actual user IDs.
    # For this stub, we'll return a placeholder or a pre-defined list.
    # Example: query Parse for users with role 'farmer'
    # For now, returning a mock ID
    # user_query_url = f"{PARSE_SERVER_URL}/users?where={json.dumps({'role': user_type})}"
    # users = requests.get(user_query_url, headers=HEADERS).json().get('results', [])
    # if users: return random.choice(users)['objectId']
    return f"mock_{user_type.lower()}_user_{random.randint(1,100)}"

# --- Data Generation Functions ---

def generate_test_users(count=20):
    print("\n--- Generating Test Users ---")
    roles = ["Farmer", "General", "High-Level"]
    for i in range(count):
        username = f"testuser{i}_{datetime.now().strftime('%Y%m%d%H%M%S%f')}"
        password = "password123"
        role = random.choice(roles)
        user_data = {
            "username": username,
            "password": password,
            "email": f"{username}@example.com",
            "role": role,
            "name": f"Test {role} User {i}"
        }
        # For User class, endpoint is /users not /classes/_User for creation via REST
        try:
            response = requests.post(f"{PARSE_SERVER_URL}/users", headers=HEADERS, json=user_data)
            response.raise_for_status()
            print(f"Created User ({role}): {response.json().get('objectId')}")
        except requests.exceptions.RequestException as e:
            print(f"Error creating User: {e}")
            if e.response is not None: print(f"Response: {e.response.text}")

def generate_orders(count=50):
    print("\n--- Generating Test Orders ---")
    statuses = ["PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"]
    for i in range(count):
        user_id = get_random_user_id("General")
        farmer_id = get_random_user_id("Farmer")
        order_data = {
            "userId": user_id,
            "farmerId": farmer_id, # Assuming you have a farmerId field
            "items": [
                {"productId": f"product_{random.randint(1,100)}", "quantity": random.randint(1,5), "price": random.uniform(100, 500)}
            ],
            "totalAmount": random.uniform(200, 2000),
            "status": random.choice(statuses),
            "shippingAddress": {"street": f"{i+1} Main St", "city": "Testville", "zip": f"{random.randint(10000,99999)}"},
            "paymentMode": random.choice(["COD", "ONLINE"]),
            "createdAt": {"__type": "Date", "iso": (datetime.now() - timedelta(days=random.randint(0,30))).isoformat() + "Z"}
        }
        create_object("UserOrder", order_data) # Assuming UserOrder is your Order class name

def generate_auctions(count=25):
    print("\n--- Generating Test Auctions ---")
    fowl_types = ["Rooster", "Hen", "Chick"]
    breeds = ["Kadaknath", "Asil", "Brahma", "Country Chicken"]
    statuses = ["ACTIVE", "PENDING_APPROVAL", "CLOSED_NO_BIDS", "CLOSED_BID_ACCEPTED"]
    for i in range(count):
        farmer_id = get_random_user_id("Farmer")
        auction_data = {
            "farmerId": farmer_id,
            "title": f"{random.choice(breeds)} {random.choice(fowl_types)} Auction {i}",
            "description": f"High-quality {random.choice(breeds)} for auction.",
            "startPrice": random.uniform(500, 1500),
            "currentHighestBid": random.uniform(600, 2000) if random.choice([True, False]) else None,
            "endTime": {"__type": "Date", "iso": (datetime.now() + timedelta(days=random.randint(1,7))).isoformat() + "Z"},
            "status": random.choice(statuses),
            "fowlType": random.choice(fowl_types),
            "breed": random.choice(breeds),
            "ageWeeks": random.randint(10, 52),
            "mediaUrls": [f"https://picsum.photos/seed/{random.randint(1,1000)}/300/200" for _ in range(random.randint(0,3))]
        }
        # Assuming your Auction class is named 'AuctionListing' or similar
        create_object("AuctionListing", auction_data)

def generate_farms_and_fowl(farm_count=15, avg_fowl_per_farm=10):
    print("\n--- Generating Test Farms & Fowl ---")
    breeds = ["Kadaknath", "Asil", "Brahma", "Leghorn", "Country Chicken"]
    genders = ["Male", "Female"]

    for i in range(farm_count):
        farmer_id = get_random_user_id("Farmer")
        farm_data = {
            "farmerId": farmer_id,
            "farmName": f"Farm_{farmer_id.replace('mock_farmer_user_', '')}",
            "location": {"__type": "GeoPoint", "latitude": random.uniform(16, 18), "longitude": random.uniform(78, 80)},
            "sizeAcres": random.uniform(1, 10)
        }
        farm_id = create_object("FarmProfile", farm_data) # Assuming FarmProfile class name

        if farm_id:
            for _ in range(avg_fowl_per_farm):
                birth_date = datetime.now() - timedelta(weeks=random.randint(1, 100))
                fowl_data = {
                    "ownerId": farmer_id, # Or link to FarmProfile pointer
                    "farmId": {"__type": "Pointer", "className": "FarmProfile", "objectId": farm_id},
                    "name": f"{random.choice(breeds)}_{random.randint(1,500)}",
                    "breed": random.choice(breeds),
                    "gender": random.choice(genders),
                    "dateOfBirth": {"__type": "Date", "iso": birth_date.isoformat() + "Z"},
                    "healthStatus": random.choice(["Healthy", "Vaccinated", "Minor Sickness"]),
                    "photos": [f"https://picsum.photos/seed/{random.randint(1,1000)}/200/200" for _ in range(random.randint(0,2))]
                    # Add parent pointers if your lineage system needs it
                }
                create_object("FowlRecord", fowl_data) # Assuming FowlRecord class name

def generate_broadcasts(count=10):
    print("\n--- Generating Test Broadcasts ---")
    categories = ["Showcase", "Education", "Breeding Tips", "Health Consultation", "Competitions"]
    for i in range(count):
        caster_id = get_random_user_id(random.choice(["Farmer", "High-Level"]))
        broadcast_data = {
            "casterId": caster_id,
            "title": f"{random.choice(categories)} Broadcast Session {i}",
            "description": "Live discussion and Q&A.",
            "category": random.choice(categories),
            "status": random.choice(["UPCOMING", "LIVE", "ENDED"]),
            "startTime": {"__type": "Date", "iso": (datetime.now() + timedelta(minutes=random.randint(-60, 120))).isoformat() + "Z"},
            "isRecorded": random.choice([True, False])
        }
        create_object("BroadcastEvent", broadcast_data) # Assuming BroadcastEvent class name

# --- Main Execution ---
if __name__ == "__main__":
    if PARSE_SERVER_URL == "YOUR_PARSE_SERVER_URL" or \
       PARSE_APP_ID == "YOUR_PARSE_APP_ID" or \
       PARSE_REST_API_KEY == "YOUR_PARSE_REST_API_KEY":
        print("ERROR: Please configure PARSE_SERVER_URL, PARSE_APP_ID, and PARSE_REST_API_KEY before running.")
    else:
        print(f"Starting data generation for Parse Server: {PARSE_SERVER_URL}")
        generate_test_users(20)
        generate_orders(50)
        generate_auctions(25)
        generate_farms_and_fowl(farm_count=15, avg_fowl_per_farm=10)
        generate_broadcasts(10)
        print("\n--- Data Generation Complete ---")
        print("NOTE: This script uses mock User IDs. For real data linkage, query actual User objectIds.")
        print("Remember to adjust Class Names (e.g., UserOrder, AuctionListing, FowlRecord) to match your Parse schema.")

```
