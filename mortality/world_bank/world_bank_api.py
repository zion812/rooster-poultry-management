import requests
import logging

logger = logging.getLogger(__name__)

class WorldBankAPI:
    """
    A wrapper for the World Bank API to fetch indicator data.
    """
    BASE_URL = "http://api.worldbank.org/v2"

    def __init__(self, timeout=10):
        """
        Initializes the API wrapper.
        Args:
            timeout (int): Request timeout in seconds.
        """
        self.timeout = timeout

    def get_data_for_indicator(self, indicator_id, country_code="all", date_range=None, per_page=1000, **kwargs):
        """
        Fetches data for a specific indicator from the World Bank API.

        Args:
            indicator_id (str): The World Bank indicator ID (e.g., "SP.POP.TOTL").
            country_code (str): The country code or "all" for all countries.
                                Use "BRA;USA;CHN" for specific multiple countries.
            date_range (str, optional): A string representing the date range, e.g., "2000:2020".
                                        If None, fetches all available data.
            per_page (int): Number of records per page. Max is usually around 20000 for some APIs,
                            but World Bank's default is 50, max can be higher but let's keep it manageable.
            **kwargs: Additional query parameters to pass to the API.

        Returns:
            list: A list of data points (dictionaries) or None if an error occurs.
                  Each data point usually contains 'country', 'date', 'value', etc.
        """
        url = f"{self.BASE_URL}/country/{country_code}/indicator/{indicator_id}"
        params = {
            "format": "json",  # Request JSON format
            "per_page": str(per_page),
            **kwargs
        }
        if date_range:
            params["date"] = date_range

        all_data = []
        current_page = 1

        while True:
            params["page"] = str(current_page)
            try:
                response = requests.get(url, params=params, timeout=self.timeout)
                response.raise_for_status()  # Raise an exception for HTTP errors (4xx or 5xx)

                data = response.json()

                if not data or len(data) < 2: # data[0] is metadata, data[1] is the actual data array
                    logger.warning(f"Received empty or invalid data for indicator {indicator_id}, page {current_page}. Response: {data}")
                    break

                page_info = data[0]
                actual_data = data[1]

                if actual_data:
                    all_data.extend(actual_data)

                if current_page >= page_info.get('pages', 0) or not actual_data:
                    break  # Exit if all pages are fetched or no data on current page

                current_page += 1

            except requests.exceptions.HTTPError as e:
                logger.error(f"HTTP error occurred while fetching indicator {indicator_id}: {e}")
                return None
            except requests.exceptions.RequestException as e:
                logger.error(f"Request error occurred while fetching indicator {indicator_id}: {e}")
                return None
            except ValueError as e: # Handles JSON decoding errors
                logger.error(f"JSON decoding error for indicator {indicator_id}: {e}. Response text: {response.text if 'response' in locals() else 'N/A'}")
                return None
            except Exception as e:
                logger.error(f"An unexpected error occurred for indicator {indicator_id}: {e}")
                return None

        return all_data

if __name__ == '__main__':
    # Example Usage (for testing purposes)
    logging.basicConfig(level=logging.INFO)
    api = WorldBankAPI()

    # Test 1: Population data for all countries (might be large, so use a recent year for quick test)
    # print("\nFetching total population for all countries (recent year)...")
    # population_data = api.get_data_for_indicator("SP.POP.TOTL", date_range="2021:2021", per_page=100)
    # if population_data:
    #     print(f"Fetched {len(population_data)} data points for SP.POP.TOTL.")
    #     # print("First few entries:", population_data[:2])
    # else:
    #     print("Failed to fetch population data.")

    # Test 2: Mortality rate, infant (per 1,000 live births) for Brazil
    print("\nFetching infant mortality rate for Brazil...")
    infant_mortality_brazil = api.get_data_for_indicator("SH.DTH.INFT", country_code="BRA")
    if infant_mortality_brazil:
        print(f"Fetched {len(infant_mortality_brazil)} data points for SH.DTH.INFT for Brazil.")
        # Print the most recent non-null entry
        latest_entry = next((item for item in sorted(infant_mortality_brazil, key=lambda x: x['date'], reverse=True) if item['value'] is not None), None)
        if latest_entry:
            print(f"Most recent data for Brazil: Year {latest_entry['date']}, Value {latest_entry['value']}")
        else:
            print("No non-null data found for Brazil.")
    else:
        print("Failed to fetch infant mortality data for Brazil.")

    # Test 3: Non-existent indicator (should handle gracefully)
    # print("\nFetching data for a non-existent indicator...")
    # non_existent_data = api.get_data_for_indicator("NON.EXISTENT.IND")
    # if non_existent_data is None:
    #     print("Correctly handled non-existent indicator (returned None).")
    # else:
    #     print(f"Unexpectedly got data for non-existent indicator: {non_existent_data}")

    # Test 4: Multiple countries
    print("\nFetching GDP per capita for USA, China, and India...")
    gdp_data = api.get_data_for_indicator("NY.GDP.PCAP.CD", country_code="USA;CHN;IND", date_range="2020:2020")
    if gdp_data:
        print(f"Fetched {len(gdp_data)} data points for GDP per capita.")
        for item in gdp_data:
            print(f"{item['countryiso3code']} ({item['date']}): {item['value']}")
    else:
        print("Failed to fetch GDP data for multiple countries.")
