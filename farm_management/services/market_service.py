from datetime import date, timedelta
from typing import Dict, Optional, Union
import random

class MarketService:
    """
    Conceptual service for fetching market price data.
    In a real application, this would connect to an actual market data API or database.
    For now, it returns mock data or placeholder messages.
    """
    def __init__(self):
        # Mock data for demonstration for "Krishna District"
        self._mock_egg_prices = { # Price per egg
            "wholesale": round(random.uniform(3.5, 4.5), 2), # rupees
            "retail": round(random.uniform(5.0, 6.5), 2)    # rupees
        }
        self._mock_broiler_prices = { # Price per Kg live weight
            "wholesale": round(random.uniform(80, 120), 2), # rupees
            "retail": round(random.uniform(150, 200), 2)   # rupees
        }
        self._last_updated = date.today()

    def _refresh_mock_data_if_needed(self):
        """Refreshes mock data daily to simulate changing prices."""
        if date.today() > self._last_updated:
            self._mock_egg_prices = {
                "wholesale": round(random.uniform(3.5, 4.5), 2),
                "retail": round(random.uniform(5.0, 6.5), 2)
            }
            self._mock_broiler_prices = {
                "wholesale": round(random.uniform(80, 120), 2),
                "retail": round(random.uniform(150, 200), 2)
            }
            self._last_updated = date.today()
            print("(MarketService: Mock prices refreshed for today)")


    def get_egg_market_price(self, location: str, specific_date: Optional[date] = None) -> Dict[str, Union[str, float, None]]:
        """
        Conceptual: Fetches egg market prices.
        Args:
            location (str): The market location (e.g., "Krishna District").
            specific_date (Optional[date]): Date for historical prices (not implemented with mock).
        Returns:
            Dict with price data or an error/placeholder message.
        """
        self._refresh_mock_data_if_needed()

        if "krishna" in location.lower(): # Simplified location check
            if specific_date and specific_date < date.today() - timedelta(days=5): # Arbitrary limit for mock "history"
                return {
                    "product": "Eggs (per piece)",
                    "location": location,
                    "date": specific_date.isoformat(),
                    "message": "Historical data not available for this mock service beyond a few days."
                }
            return {
                "product": "Eggs (per piece)",
                "location": location,
                "date": date.today().isoformat(),
                "wholesale_price_rupees": self._mock_egg_prices["wholesale"],
                "retail_price_rupees": self._mock_egg_prices["retail"],
                "source": "Mock Data - For Demonstration Only"
            }
        else:
            return {
                "product": "Eggs",
                "message": f"Market price data not available for location: {location}. (Mock service supports 'Krishna District'.)"
            }

    def get_broiler_market_price(self, location: str, specific_date: Optional[date] = None) -> Dict[str, Union[str, float, None]]:
        """
        Conceptual: Fetches broiler chicken market prices.
        Args:
            location (str): The market location (e.g., "Krishna District").
            specific_date (Optional[date]): Date for historical prices (not implemented with mock).
        Returns:
            Dict with price data or an error/placeholder message.
        """
        self._refresh_mock_data_if_needed()

        if "krishna" in location.lower():
            if specific_date and specific_date < date.today() - timedelta(days=5):
                 return {
                    "product": "Broiler Chicken (per Kg, live weight)",
                    "location": location,
                    "date": specific_date.isoformat(),
                    "message": "Historical data not available for this mock service beyond a few days."
                }
            return {
                "product": "Broiler Chicken (per Kg, live weight)",
                "location": location,
                "date": date.today().isoformat(),
                "wholesale_price_rupees": self._mock_broiler_prices["wholesale"],
                "retail_price_rupees": self._mock_broiler_prices["retail"],
                "source": "Mock Data - For Demonstration Only"
            }
        else:
            return {
                "product": "Broiler Chicken",
                "message": f"Market price data not available for location: {location}. (Mock service supports 'Krishna District'.)"
            }

if __name__ == '__main__':
    market_service = MarketService()

    print("--- Egg Prices for Krishna District ---")
    egg_prices = market_service.get_egg_market_price("Krishna District")
    for key, value in egg_prices.items():
        print(f"  {key.replace('_', ' ').capitalize()}: {value}")

    print("\n--- Broiler Prices for a farm in Vijayawada, Krishna ---")
    broiler_prices = market_service.get_broiler_market_price("Vijayawada, Krishna")
    for key, value in broiler_prices.items():
        print(f"  {key.replace('_', ' ').capitalize()}: {value}")

    print("\n--- Egg Prices for Other Location (should show message) ---")
    other_egg_prices = market_service.get_egg_market_price("Hyderabad")
    print(f"  {other_egg_prices}")

    print("\n--- Historical Egg Prices (mock, should show message) ---")
    historical_prices = market_service.get_egg_market_price("Krishna", date.today() - timedelta(days=10))
    print(f"  {historical_prices}")

    print("\n--- Recent Historical Egg Prices (mock, should give current mock) ---")
    recent_historical_prices = market_service.get_egg_market_price("Krishna", date.today() - timedelta(days=1))
    for key, value in recent_historical_prices.items():
        print(f"  {key.replace('_', ' ').capitalize()}: {value}")
