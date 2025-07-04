import datetime
import random

class EducationalContentScreen:
    def __init__(self):
        self.content_library = [] # List of content items
        # Mock data for categories and types
        self.species_categories = ["General", "Cattle", "Poultry", "Swine", "Sheep & Goats", "Horses", "Companion Animals"]
        self.topic_categories = ["Nutrition", "Disease Prevention", "Common Diseases", "Animal Welfare", "Farm Management", "Biosecurity", "First Aid"]
        self.content_types = ["Article", "Video", "FAQ", "Guide", "Webinar Recording"]
        self.author_names = ["Dr. Vet Expert", "AgriConsult Inc.", "Farmer Weekly Magazine", "University Extension Program"]

        self._initialize_mock_content(count=10)

    def _initialize_mock_content(self, count=5):
        for i in range(count):
            content_type = random.choice(self.content_types)
            title_verb = "Understanding" if content_type == "Article" else "How to Manage" if content_type == "Guide" else "Q&A on"
            topic = random.choice(self.topic_categories)
            species = random.choice(self.species_categories)

            content_id = f"edu_{datetime.datetime.now().strftime('%Y%m%d')}_{random.randint(1000,9999)}_{i}"

            # Mock content body or URL
            body_or_url = ""
            if content_type == "Article":
                body_or_url = f"This is a detailed article about {topic} in {species}. It covers A, B, and C. Lorem ipsum dolor sit amet..."
            elif content_type == "Video":
                body_or_url = f"https://videos.example.com/{species.lower()}_{topic.lower().replace(' ','_')}_{random.randint(100,999)}.mp4"
            elif content_type == "FAQ":
                body_or_url = f"Q1: What is {topic}?\nA1: It is...\nQ2: How to prevent?\nA2: By doing X, Y, Z."
            else: # Guide, Webinar
                body_or_url = f"Comprehensive guide on {topic} for {species}. See attached PDF or link."


            content_item = {
                "content_id": content_id,
                "title": f"{title_verb} {topic} in {species}",
                "content_type": content_type,
                "species_category": species,
                "topic_category": topic,
                "author": random.choice(self.author_names),
                "publish_date": (datetime.date.today() - datetime.timedelta(days=random.randint(1, 365))).isoformat(),
                "last_updated_date": (datetime.date.today() - datetime.timedelta(days=random.randint(0, 30))).isoformat(),
                "body_or_url": body_or_url, # Could be full text or a URL to the content
                "keywords": [species, topic, random.choice(["management", "health", "tips"])],
                "access_level": random.choice(["public", "registered_users", "vets_only"]), # For potential access control
                "view_count": random.randint(10, 1000)
            }
            self.content_library.append(content_item)
        self.content_library.sort(key=lambda x: x["publish_date"], reverse=True)

    def add_content(self, title, content_type, species_cat, topic_cat, author, body_or_url, keywords=None, access_level="public", vet_id_uploader="admin"):
        if content_type not in self.content_types:
            print(f"Error: Invalid content type '{content_type}'.")
            return None
        if species_cat not in self.species_categories:
            print(f"Warning: Species category '{species_cat}' is not standard, adding anyway.")
        if topic_cat not in self.topic_categories:
            print(f"Warning: Topic category '{topic_cat}' is not standard, adding anyway.")

        content_id = f"edu_{datetime.datetime.now().strftime('%Y%m%d%H%M%S')}_{random.randint(100,999)}"
        publish_date = datetime.date.today().isoformat()

        new_item = {
            "content_id": content_id,
            "title": title,
            "content_type": content_type,
            "species_category": species_cat,
            "topic_category": topic_cat,
            "author": author,
            "publish_date": publish_date,
            "last_updated_date": publish_date,
            "body_or_url": body_or_url,
            "keywords": keywords if keywords else [species_cat, topic_cat],
            "access_level": access_level,
            "uploaded_by": vet_id_uploader,
            "view_count": 0
        }
        self.content_library.insert(0, new_item) # Add to the beginning
        print(f"Content '{title}' (ID: {content_id}) added successfully by {vet_id_uploader}.")
        return content_id

    def get_content_by_id(self, content_id):
        for item in self.content_library:
            if item["content_id"] == content_id:
                item["view_count"] += 1 # Increment view count on access
                return item
        return None

    def search_content(self, query_term=None, species_filter=None, topic_filter=None, type_filter=None, limit=5):
        results = self.content_library

        if query_term:
            qt_lower = query_term.lower()
            results = [item for item in results if qt_lower in item["title"].lower() or
                       any(qt_lower in kw.lower() for kw in item.get("keywords",[])) or
                       (isinstance(item["body_or_url"], str) and qt_lower in item["body_or_url"].lower() and len(item["body_or_url"]) < 200) # Search body only if it's short text
                      ]
        if species_filter:
            results = [item for item in results if item["species_category"] == species_filter]
        if topic_filter:
            results = [item for item in results if item["topic_category"] == topic_filter]
        if type_filter:
            results = [item for item in results if item["content_type"] == type_filter]

        return results[:limit]

    def display_screen(self, current_user_type="vet"): # current_user_type can be "vet", "farmer", "guest" for access control demo
        print("---- Educational Content Screen ----")

        print("\n-- Available Categories --")
        print(f"  Species: {', '.join(self.species_categories[:4])}...")
        print(f"  Topics: {', '.join(self.topic_categories[:4])}...")
        print(f"  Types: {', '.join(self.content_types)}")

        print("\n-- Recently Added Content (Top 3) --")
        recent_content = self.search_content(limit=3)
        if not recent_content:
            print("  No content found in the library.")
        else:
            for item in recent_content:
                # Simple access control check for display
                if item["access_level"] == "vets_only" and current_user_type != "vet":
                    print(f"  Title: {item['title']} (Content restricted to Vets)")
                    continue
                print(f"  ID: {item['content_id']}, Title: {item['title']}")
                print(f"    Type: {item['content_type']}, Species: {item['species_category']}, Topic: {item['topic_category']}")
                print(f"    Author: {item['author']}, Published: {item['publish_date']}, Views: {item['view_count']}")
                if len(item['body_or_url']) < 100 : print(f"    Preview/URL: {item['body_or_url']}")
                else: print(f"    Preview: {item['body_or_url'][:100]}...")


        print("\n-- Search Example: 'Cattle Nutrition' Articles --")
        search_results = self.search_content(query_term="Nutrition", species_filter="Cattle", type_filter="Article")
        if not search_results:
            print("  No articles found matching 'Cattle Nutrition'.")
        else:
            for item in search_results[:2]: # Show max 2 results for brevity
                print(f"  Title: {item['title']} (ID: {item['content_id']})")
                print(f"    Author: {item['author']}, Topic: {item['topic_category']}")

        print("\n-- Add New Content (Example) --")
        if current_user_type == "vet": # Assume only vets can upload
            new_content_id = self.add_content(
                title="Advanced Biosecurity Protocols for Poultry Farms",
                content_type="Guide",
                species_cat="Poultry",
                topic_cat="Biosecurity",
                author="Dr. Avian Secure",
                body_or_url="This comprehensive guide outlines key biosecurity measures...",
                keywords=["poultry", "biosecurity", "disease prevention", "farm safety"],
                access_level="vets_only",
                vet_id_uploader="vet_jules_001"
            )
            if new_content_id:
                print(f"  Example content added with ID: {new_content_id}")
                # Display the newly added content
                added_item = self.get_content_by_id(new_content_id)
                if added_item:
                     print(f"    Newly added: {added_item['title']} ({added_item['access_level']})")
        else:
            print("  (Content addition is typically restricted to authorized users like veterinarians)")

        print("------------------------------------")

if __name__ == '__main__':
    screen = EducationalContentScreen()
    print("--- Displaying as a Veterinarian ---")
    screen.display_screen(current_user_type="vet")
    print("\n\n--- Displaying as a Farmer (some content might be restricted) ---")
    # Re-init or clear view counts if needed for clean demo runs, but for now it's cumulative
    # screen = EducationalContentScreen() # Uncomment to reset for farmer view
    screen.display_screen(current_user_type="farmer")
