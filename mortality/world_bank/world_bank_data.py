import pandas as pd
from .world_bank_api import WorldBankAPI # Use relative import
import logging

logger = logging.getLogger(__name__)

class WorldBankDataFetcher:
    """
    Base class for fetching and processing data from the World Bank API
    for a specific indicator.
    """
    indicator_id = None
    indicator_name = "Unknown Indicator" # Default name

    def __init__(self, api=None):
        """
        Initializes the data fetcher.
        Args:
            api (WorldBankAPI, optional): An instance of WorldBankAPI.
                                          If None, a new one is created.
        """
        if self.indicator_id is None:
            raise ValueError("Subclasses must define an 'indicator_id'.")
        self.api = api if api else WorldBankAPI()

    def fetch_data(self, country_code="all", date_range=None, **kwargs):
        """
        Fetches data for the configured indicator and processes it into a DataFrame.

        Args:
            country_code (str): The country code or "all".
            date_range (str, optional): Date range like "2000:2020".
            **kwargs: Additional parameters for the API call.

        Returns:
            pandas.DataFrame: A DataFrame with columns like 'Country Name',
                              'Country Code', 'Year', 'Indicator Name', 'Value'.
                              Returns an empty DataFrame if fetching fails or no data.
        """
        logger.info(f"Fetching data for indicator: {self.indicator_name} ({self.indicator_id})")
        raw_data = self.api.get_data_for_indicator(
            self.indicator_id,
            country_code=country_code,
            date_range=date_range,
            **kwargs
        )

        if raw_data is None:
            logger.warning(f"No data received from API for indicator {self.indicator_name}.")
            return pd.DataFrame()

        if not raw_data: # Empty list
            logger.info(f"API returned no data points for indicator {self.indicator_name} with current parameters.")
            return pd.DataFrame()

        # Process data into a more usable format
        processed_data = []
        # raw_data from self.api.get_data_for_indicator is already the flat list of records.
        # Iterate directly over raw_data.

        for entry in raw_data: # Iterate directly over raw_data
            if entry and isinstance(entry, dict) and entry.get('value') is not None: # Explicitly check isinstance
                processed_data.append({
                    'Country Name': entry['country']['value'],
                    'Country Code': entry['countryiso3code'],
                    'Year': int(entry['date']),
                    'Indicator Name': self.indicator_name, # Use class attribute
                    'Value': entry['value']
                })

        if not processed_data:
            logger.info(f"No valid data points (with values) found after processing for indicator {self.indicator_name}.")
            return pd.DataFrame()

        df = pd.DataFrame(processed_data)
        # Optional: sort data for consistency
        df = df.sort_values(by=['Country Name', 'Year']).reset_index(drop=True)

        logger.info(f"Successfully fetched and processed {len(df)} records for {self.indicator_name}.")
        return df

    def __repr__(self):
        return f"<{self.__class__.__name__}(indicator_id='{self.indicator_id}', name='{self.indicator_name}')>"


# Specific Fetcher Implementations

class DeathRateFetcher(WorldBankDataFetcher):
    indicator_id = "SH.DTH.MORT"
    indicator_name = "Death rate, crude (per 1,000 people)"

class NeonatalMortalityFetcher(WorldBankDataFetcher):
    indicator_id = "SH.DTH.NMRT"
    indicator_name = "Neonatal mortality rate (per 1,000 live births)"

class InfantMortalityFetcher(WorldBankDataFetcher):
    indicator_id = "SH.DTH.INFT"
    indicator_name = "Mortality rate, infant (per 1,000 live births)"

class Under5MortalityFetcher(WorldBankDataFetcher):
    indicator_id = "SH.DTH.CHLD"
    indicator_name = "Mortality rate, under-5 (per 1,000 live births)"


if __name__ == '__main__':
    # Example Usage (for testing purposes)
    logging.basicConfig(level=logging.INFO)

    # Test with one of the fetchers
    print("\nTesting InfantMortalityFetcher for a few countries (USA, BRA, IND) for 2018-2020...")
    infant_fetcher = InfantMortalityFetcher()
    # Using specific country codes and a date range
    infant_data = infant_fetcher.fetch_data(country_code="USA;BRA;IND", date_range="2018:2020")

    if not infant_data.empty:
        print("\nInfant Mortality Data:")
        print(infant_data)
    else:
        print("No infant mortality data fetched or processed.")

    print("\nTesting DeathRateFetcher for Canada (last 5 years)...")
    # Test another fetcher, e.g., DeathRateFetcher for a single country
    # Note: The API might not have super recent data for all indicators/countries.
    # Let's try to get the last 10 years and see what we get.
    death_rate_fetcher = DeathRateFetcher()
    # For "last 5 years", we might need to be more dynamic, but for a simple test:
    # Let's assume current year is 2023, so 2019-2023.
    # However, World Bank data often lags. Let's try a broader recent range.
    death_rate_data_canada = death_rate_fetcher.fetch_data(country_code="CAN", date_range="2015:2022") # Wider range

    if not death_rate_data_canada.empty:
        print("\nCrude Death Rate Data for Canada (2015-2022):")
        print(death_rate_data_canada)
    else:
        print("No crude death rate data fetched for Canada in the specified range.")

    # Test fetching all data for a small country for NeonatalMortality
    print("\nTesting NeonatalMortalityFetcher for Luxembourg (all years)...")
    neonatal_fetcher = NeonatalMortalityFetcher()
    neonatal_data_lux = neonatal_fetcher.fetch_data(country_code="LUX")
    if not neonatal_data_lux.empty:
        print("\nNeonatal Mortality Data for Luxembourg:")
        print(neonatal_data_lux.head()) # Print head to keep output manageable
        print(f"... and {len(neonatal_data_lux) - 5} more rows.")
    else:
        print("No neonatal mortality data fetched for Luxembourg.")
