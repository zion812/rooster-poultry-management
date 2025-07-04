# Main application entry point

# Import the main CLI function for farm management
from farm_management.ui.farm_cli import farm_management_main_cli
# Import the global farm_repo instance from farm_cli to pass to other modules if needed,
# or initialize repositories here and pass them down.
# For now, farm_cli initializes its own repo instance.
# from farm_management.ui.farm_cli import farm_repo as global_farm_repo # Example

def main():
    # The farm_management_main_cli now handles the main interaction loop for farms.
    # It also pre-populates data if the repository is empty.
    farm_management_main_cli()

if __name__ == "__main__":
    main()
