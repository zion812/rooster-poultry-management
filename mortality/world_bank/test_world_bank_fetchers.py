import unittest
from unittest.mock import patch, MagicMock
import pandas as pd
from io import StringIO # For testing logger output if needed, not used in current mocks

# Important: Adjust import path based on how tests are run.
# If tests are run from the project root directory (e.g., `python -m unittest discover`):
from mortality.world_bank.world_bank_api import WorldBankAPI
from mortality.world_bank.world_bank_data import (
    WorldBankDataFetcher,
    DeathRateFetcher,
    NeonatalMortalityFetcher,
    InfantMortalityFetcher,
    Under5MortalityFetcher
)
from mortality.world_bank import FETCHER_CLASSES

# Sample API response structure (simplified)
# data[0] is page info, data[1] is list of records
SAMPLE_API_PAGE_INFO = {"page": 1, "pages": 1, "per_page": "50", "total": 2}
SAMPLE_API_DATA_RECORDS = [
    {
        "indicator": {"id": "SH.DTH.INFT", "value": "Mortality rate, infant (per 1,000 live births)"},
        "country": {"id": "BR", "value": "Brazil"},
        "countryiso3code": "BRA",
        "date": "2020",
        "value": 12.8,
        "unit": "",
        "obs_status": "",
        "decimal": 1
    },
    {
        "indicator": {"id": "SH.DTH.INFT", "value": "Mortality rate, infant (per 1,000 live births)"},
        "country": {"id": "BR", "value": "Brazil"},
        "countryiso3code": "BRA",
        "date": "2019",
        "value": 13.1,
        "unit": "",
        "obs_status": "",
        "decimal": 1
    }
]
SAMPLE_API_RESPONSE_INFANT_MORTALITY_BRAZIL = [SAMPLE_API_PAGE_INFO, SAMPLE_API_DATA_RECORDS]

EMPTY_API_RESPONSE = [SAMPLE_API_PAGE_INFO, []] # Metadata + empty data list
NULL_VALUE_API_RESPONSE = [
    SAMPLE_API_PAGE_INFO,
    [
        {
            "indicator": {"id": "SH.DTH.MORT", "value": "Death rate, crude (per 1,000 people)"},
            "country": {"id": "US", "value": "United States"},
            "countryiso3code": "USA",
            "date": "2020",
            "value": None, # Null value
            "unit": "", "obs_status": "", "decimal": 1
        }
    ]
]


class TestWorldBankDataFetchers(unittest.TestCase):

    def test_base_fetcher_requires_indicator_id(self):
        """Test that WorldBankDataFetcher raises error if indicator_id is not set."""
        class TestFetcher(WorldBankDataFetcher):
            # No indicator_id defined
            pass
        with self.assertRaisesRegex(ValueError, "Subclasses must define an 'indicator_id'."):
            TestFetcher()

    @patch('mortality.world_bank.world_bank_data.WorldBankAPI')
    def test_infant_mortality_fetcher(self, MockWorldBankAPI):
        """Test InfantMortalityFetcher processing."""
        # Configure the mock API instance and its method
        mock_api_instance = MockWorldBankAPI.return_value
        mock_api_instance.get_data_for_indicator.return_value = SAMPLE_API_RESPONSE_INFANT_MORTALITY_BRAZIL

        fetcher = InfantMortalityFetcher(api=mock_api_instance)
        self.assertEqual(fetcher.indicator_id, "SH.DTH.INFT")
        self.assertEqual(fetcher.indicator_name, "Mortality rate, infant (per 1,000 live births)")

        df = fetcher.fetch_data(country_code="BRA")

        # Assertions
        mock_api_instance.get_data_for_indicator.assert_called_once_with(
            "SH.DTH.INFT", country_code="BRA", date_range=None
        )
        self.assertIsInstance(df, pd.DataFrame)
        self.assertFalse(df.empty)
        self.assertEqual(len(df), 2)
        self.assertListEqual(list(df.columns), ['Country Name', 'Country Code', 'Year', 'Indicator Name', 'Value'])
        self.assertEqual(df.iloc[0]['Country Name'], "Brazil")
        self.assertEqual(df.iloc[0]['Country Code'], "BRA")
        self.assertEqual(df.iloc[0]['Year'], 2019) # Sorted by year
        self.assertEqual(df.iloc[0]['Value'], 13.1)
        self.assertEqual(df.iloc[0]['Indicator Name'], "Mortality rate, infant (per 1,000 live births)")

    @patch('mortality.world_bank.world_bank_data.WorldBankAPI')
    def test_fetcher_with_no_api_data(self, MockWorldBankAPI):
        """Test fetcher behavior when API returns no data records."""
        mock_api_instance = MockWorldBankAPI.return_value
        mock_api_instance.get_data_for_indicator.return_value = EMPTY_API_RESPONSE # Metadata, but empty list of records

        fetcher = DeathRateFetcher(api=mock_api_instance) # Using DeathRateFetcher as an example
        df = fetcher.fetch_data(country_code="ALL")

        self.assertIsInstance(df, pd.DataFrame)
        self.assertTrue(df.empty)
        mock_api_instance.get_data_for_indicator.assert_called_once()

    @patch('mortality.world_bank.world_bank_data.WorldBankAPI')
    def test_fetcher_with_api_returning_none(self, MockWorldBankAPI):
        """Test fetcher behavior when API call returns None (e.g., HTTP error)."""
        mock_api_instance = MockWorldBankAPI.return_value
        mock_api_instance.get_data_for_indicator.return_value = None # API experienced an error

        fetcher = NeonatalMortalityFetcher(api=mock_api_instance)
        df = fetcher.fetch_data(country_code="ALL")

        self.assertIsInstance(df, pd.DataFrame)
        self.assertTrue(df.empty)
        mock_api_instance.get_data_for_indicator.assert_called_once()

    @patch('mortality.world_bank.world_bank_data.WorldBankAPI')
    def test_fetcher_with_null_values_in_data(self, MockWorldBankAPI):
        """Test that records with null values are filtered out."""
        mock_api_instance = MockWorldBankAPI.return_value
        mock_api_instance.get_data_for_indicator.return_value = NULL_VALUE_API_RESPONSE

        fetcher = DeathRateFetcher(api=mock_api_instance)
        df = fetcher.fetch_data(country_code="USA")

        self.assertIsInstance(df, pd.DataFrame)
        self.assertTrue(df.empty) # Since the only record had a null value

    def test_all_fetcher_classes_defined_and_configured(self):
        """Check that all fetchers in FETCHER_CLASSES have ID and name."""
        self.assertTrue(len(FETCHER_CLASSES) >= 4) # At least the 4 we defined
        for FetcherClass in FETCHER_CLASSES:
            self.assertIsNotNone(getattr(FetcherClass, 'indicator_id', None),
                                 f"{FetcherClass.__name__} is missing indicator_id.")
            self.assertNotEqual(getattr(FetcherClass, 'indicator_name', "Unknown Indicator"), "Unknown Indicator",
                                 f"{FetcherClass.__name__} is missing a proper indicator_name.")
            # Try to instantiate
            try:
                # We need to provide a mock api for instantiation if it creates one by default
                with patch('mortality.world_bank.world_bank_data.WorldBankAPI') as MockAPI:
                    fetcher_instance = FetcherClass(api=MockAPI.return_value)
                    self.assertIsInstance(fetcher_instance, WorldBankDataFetcher)
            except Exception as e:
                self.fail(f"Failed to instantiate {FetcherClass.__name__}: {e}")


    # Dynamically create test methods for each specific fetcher type
    # This reduces boilerplate if their core test logic is similar beyond ID/Name
    def _create_fetcher_test(fetcher_class_to_test):
        @patch('mortality.world_bank.world_bank_data.WorldBankAPI')
        def dynamic_test(self, MockWorldBankAPI):
            mock_api_instance = MockWorldBankAPI.return_value

            # Construct a sample response specific to this fetcher's indicator_id
            sample_records = [
                {
                    "indicator": {"id": fetcher_class_to_test.indicator_id, "value": fetcher_class_to_test.indicator_name},
                    "country": {"id": "XX", "value": "Testland"},
                    "countryiso3code": "TXL",
                    "date": "2021",
                    "value": 10.5, "unit": "", "obs_status": "", "decimal": 1
                }
            ]
            mock_api_instance.get_data_for_indicator.return_value = [SAMPLE_API_PAGE_INFO, sample_records]

            fetcher = fetcher_class_to_test(api=mock_api_instance)
            self.assertIsNotNone(fetcher.indicator_id)
            self.assertNotEqual(fetcher.indicator_name, "Unknown Indicator")

            df = fetcher.fetch_data(country_code="TXL")

            mock_api_instance.get_data_for_indicator.assert_called_once_with(
                fetcher.indicator_id, country_code="TXL", date_range=None
            )
            self.assertFalse(df.empty)
            self.assertEqual(len(df), 1)
            self.assertEqual(df.iloc[0]['Indicator Name'], fetcher.indicator_name)
            self.assertEqual(df.iloc[0]['Value'], 10.5)

        return dynamic_test

# Assign dynamic tests to the class
for idx, fetcher_cls in enumerate([DeathRateFetcher, NeonatalMortalityFetcher, Under5MortalityFetcher]):
    test_method = TestWorldBankDataFetchers._create_fetcher_test(fetcher_cls)
    setattr(TestWorldBankDataFetchers, f'test_specific_fetcher_{fetcher_cls.__name__}', test_method)


if __name__ == '__main__':
    unittest.main()
