# Farm-to-Flock Management System (CLI Version)

This project is a command-line interface (CLI) application designed to help poultry farmers in Krishna District manage their farms and flocks, track health and production, and gain insights into their operations.

## Features

*   **Farm Management**:
    *   Add, list, search, view, edit, and delete farms.
    *   Store farm details like name, location, owner, capacity.
*   **Flock Management**:
    *   Add, list, view, edit, and delete flocks associated with farms.
    *   Track flock details: breed, acquisition date, count, source.
    *   Record and view flock parentage for family tree traceability.
*   **Tracking Systems**:
    *   **Health Tracking**: Record and manage disease incidents, vaccinations, and mortality. Includes a basic alert system for high mortality or disease outbreaks.
    *   **Production Tracking**: Log egg production (total, damaged, average weight) and feed consumption (type, quantity, cost).
    *   **Growth Monitoring**: Record average bird weight, number weighed, and Feed Conversion Ratio (FCR). View basic growth trends.
*   **Advanced Features (Conceptual/Basic CLI Implementation)**:
    *   **Family Tree Visualization**: Text-based display of a flock's lineage.
    *   **Health Alerts**: Simple reactive alerts for high mortality or disease incidents.
    *   **Feed Optimization Insights**: Calculates and displays average FCR and feed cost per egg to aid decision-making.
    *   **Production Forecasting (Conceptual)**: Basic projection of future egg production based on recent averages.
*   **Data Persistence (Offline Capability)**:
    *   All data (farms, flocks, tracking records) is saved locally in JSON files within the `farm_management/data/` directory, ensuring data persists across application sessions.
*   **Data Integration (Conceptual/Basic)**:
    *   **Weather API**: Fetches and displays current weather for a farm's location using the Open-Meteo API.
    *   **Market Price Data (Conceptual)**: Displays mock market prices for eggs and broilers to illustrate how this data could be used for profitability analysis.
*   **Data Export**:
    *   Export farm, flock, and various tracking records to CSV files, saved in the `exports/` directory.
*   **User Interface**:
    *   Menu-driven CLI designed for ease of use.

## Setup and Running the Application

1.  **Prerequisites**:
    *   Python 3.7+

2.  **Clone the Repository (if applicable)**:
    ```bash
    # git clone <repository_url>
    # cd <repository_directory>
    ```

3.  **Install Dependencies**:
    The application uses the `requests` library for weather API calls.
    ```bash
    pip install -r requirements.txt
    ```

4.  **Run the Application**:
    ```bash
    python farm_management/main.py
    ```
    This will start the CLI application, and you can navigate through the menus.

## Directory Structure

```
.
├── farm_management/
│   ├── data/                 # Stores JSON data files (farms.json, flocks.json, etc.)
│   ├── models/               # Data model classes (Farm, Flock, HealthRecord, etc.)
│   ├── repositories/         # Data access logic (FarmRepository, FlockRepository, etc.)
│   ├── services/             # Business logic services (WeatherService, MarketService)
│   ├── ui/                   # Command-line interface modules (farm_cli.py, flock_cli.py, etc.)
│   ├── utils/                # Utility functions (e.g., export_utils.py)
│   ├── __init__.py
│   └── main.py               # Main application entry point
├── exports/                  # Default directory for CSV exports
├── AGENTS.md                 # Instructions and guidelines for AI agent development
├── README.md                 # This file
└── requirements.txt          # Python package dependencies
```

## Notes

*   **Data Storage**: Data is stored in JSON format in the `farm_management/data/` directory. If this directory or its files are deleted, the data will be lost (unless backed up). The application will recreate empty files if they are missing on startup if a write operation triggers directory creation.
*   **Offline Use**: Thanks to local JSON storage, the application works fully offline for core data management. Weather integration requires an internet connection.
*   **Conceptual Features**: Some advanced features like "AI Insights" or full "IoT Integration" are discussed conceptually in `AGENTS.md` or implemented with mock data/basic calculations to show potential, as a full implementation would require significant additional infrastructure (backend, databases, ML models, specific hardware).

## Focus

The system is designed with a focus on:
*   User-friendly interfaces (for CLI) for farmers with potentially limited technical experience.
*   Robust offline functionality for core data management.
*   Providing actionable insights through collected data and basic analytics.
```
