import logging
from mortality.world_bank import FETCHER_CLASSES # Adjusted import path
# If running this script directly from its directory, and mortality is a top-level module:
# from . import FETCHER_CLASSES
# Or if mortality is in sys.path: from mortality.world_bank import FETCHER_CLASSES

# Setup basic logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(name)s - %(message)s')
logger = logging.getLogger(__name__)

def run_all_fetchers(country_code="all", date_range=None, output_dir=None):
    """
    Runs all registered World Bank data fetchers.

    Args:
        country_code (str): Country code(s) to fetch data for (e.g., "all", "USA", "USA;BRA").
        date_range (str, optional): Date range for the data (e.g., "2010:2020").
        output_dir (str, optional): If provided, saves each fetched DataFrame as a CSV file
                                    in this directory. Otherwise, prints a summary.
    """
    if not FETCHER_CLASSES:
        logger.warning("No fetcher classes found in FETCHER_CLASSES. Nothing to run.")
        return

    logger.info(f"Starting to run {len(FETCHER_CLASSES)} data fetchers for country/countries: '{country_code}'.")
    if date_range:
        logger.info(f"Date range: {date_range}")

    all_fetchers_summary = []

    for FetcherClass in FETCHER_CLASSES:
        try:
            logger.info(f"Running fetcher: {FetcherClass.__name__} for {FetcherClass.indicator_name}")
            fetcher_instance = FetcherClass()
            data_df = fetcher_instance.fetch_data(country_code=country_code, date_range=date_range)

            if not data_df.empty:
                summary = {
                    "fetcher": FetcherClass.__name__,
                    "indicator_name": fetcher_instance.indicator_name,
                    "records_fetched": len(data_df),
                    "countries": data_df["Country Code"].nunique(),
                    "min_year": data_df["Year"].min(),
                    "max_year": data_df["Year"].max(),
                }
                all_fetchers_summary.append(summary)
                logger.info(f"Successfully fetched data for {fetcher_instance.indicator_name}: {len(data_df)} records.")

                if output_dir:
                    import os
                    if not os.path.exists(output_dir):
                        os.makedirs(output_dir)
                    file_name = f"{FetcherClass.indicator_id.replace('.', '_')}_{country_code.replace(';', '_')}.csv"
                    file_path = os.path.join(output_dir, file_name)
                    data_df.to_csv(file_path, index=False)
                    logger.info(f"Saved data to {file_path}")
                else:
                    # Print a small sample if not saving to file
                    print(f"\n--- Data for {fetcher_instance.indicator_name} ---")
                    print(data_df.head())
                    if len(data_df) > 5:
                        print(f"... and {len(data_df) - 5} more rows.")

            else:
                logger.warning(f"No data returned for {fetcher_instance.indicator_name}.")
                all_fetchers_summary.append({
                    "fetcher": FetcherClass.__name__,
                    "indicator_name": fetcher_instance.indicator_name,
                    "records_fetched": 0,
                    "error": "No data returned"
                })

        except Exception as e:
            logger.error(f"Error running fetcher {FetcherClass.__name__}: {e}", exc_info=True)
            all_fetchers_summary.append({
                "fetcher": FetcherClass.__name__,
                "indicator_name": getattr(FetcherClass, 'indicator_name', 'N/A'),
                "records_fetched": 0,
                "error": str(e)
            })

    logger.info("\n--- Fetchers Run Summary ---")
    for summary_item in all_fetchers_summary:
        if 'error' in summary_item:
            logger.info(f"Fetcher: {summary_item['fetcher']} ({summary_item['indicator_name']}) - FAILED: {summary_item['error']}")
        else:
            logger.info(
                f"Fetcher: {summary_item['fetcher']} ({summary_item['indicator_name']}) - "
                f"Fetched {summary_item['records_fetched']} records for {summary_item['countries']} countries "
                f"({summary_item['min_year']}-{summary_item['max_year']})."
            )
    logger.info("All fetchers processed.")


if __name__ == "__main__":
    # Example: Run all fetchers for specific countries and a date range
    # To run this script directly for testing:
    # Ensure your PYTHONPATH includes the root of the project if 'mortality' is not directly in 'site-packages'
    # For example, if this script is in /path/to/project/mortality/world_bank/
    # and your main project root is /path/to/project/
    # you might run from /path/to/project/: python -m mortality.world_bank.fetch_all

    logger.info("Running fetch_all.py script example.")

    # Example 1: Fetch for a few countries and a specific date range, print to console
    # run_all_fetchers(country_code="USA;CAN;MEX", date_range="2018:2020")

    # Example 2: Fetch for a single country, all available years for indicators, and save to CSV
    # Make sure the 'data_output' directory exists or can be created.
    # run_all_fetchers(country_code="IND", output_dir="data_output/world_bank")

    # Example 3: Fetch for "all" countries (be cautious, this can be a lot of data and API calls)
    # for a very limited recent period to minimize load.
    run_all_fetchers(country_code="all", date_range="2021:2021", output_dir="data_output/world_bank_recent_all")

    # If you want to test fetching all data for a small country:
    # run_all_fetchers(country_code="LUX", output_dir="data_output/world_bank_lux")

    logger.info("fetch_all.py script example finished.")
