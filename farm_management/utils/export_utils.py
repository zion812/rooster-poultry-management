import csv
import os
from typing import List, Dict, Any

EXPORT_DIR = "exports" # Top-level directory for exports

def export_to_csv(data_list_of_dicts: List[Dict[str, Any]], filename: str, fieldnames: List[str]) -> bool:
    """
    Exports a list of dictionaries to a CSV file.

    Args:
        data_list_of_dicts (List[Dict[str, Any]]): The data to export. Each dict is a row.
        filename (str): The name of the CSV file (e.g., "farms.csv").
        fieldnames (List[str]): An ordered list of dictionary keys to use as CSV headers
                                and to determine the order of columns.

    Returns:
        bool: True if export was successful, False otherwise.
    """
    if not data_list_of_dicts:
        print(f"No data provided to export to {filename}.")
        return False

    os.makedirs(EXPORT_DIR, exist_ok=True)
    filepath = os.path.join(EXPORT_DIR, filename)

    try:
        with open(filepath, 'w', newline='', encoding='utf-8') as csvfile:
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames, extrasaction='ignore')
            # extrasaction='ignore' means that if a dict in data_list_of_dicts has more keys
            # than specified in fieldnames, those extra keys/values are ignored.
            # If a key in fieldnames is missing in a dict, it writes an empty string.

            writer.writeheader()
            for row_data in data_list_of_dicts:
                # Ensure all fieldnames are present in row_data, defaulting to empty string if not
                # This helps if some dicts are missing optional keys defined in fieldnames
                # However, DictWriter handles missing keys by writing empty string by default if they are in fieldnames.
                # The main concern is that row_data should be a dict.
                writer.writerow(row_data)

        print(f"Data successfully exported to {filepath}")
        return True
    except IOError as e:
        print(f"Error writing to CSV file {filepath}: {e}")
        return False
    except Exception as e:
        print(f"An unexpected error occurred during CSV export to {filepath}: {e}")
        return False

if __name__ == '__main__':
    # Example Usage
    sample_data = [
        {'id': 1, 'name': 'Alice', 'age': 30, 'city': 'New York'},
        {'id': 2, 'name': 'Bob', 'age': 24, 'city': 'Los Angeles', 'occupation': 'Engineer'},
        {'id': 3, 'name': 'Charlie', 'age': 35, 'notes': 'Likes CSVs'} # Missing city, has extra notes
    ]
    # Define fieldnames - this determines header and column order
    # If 'occupation' or 'notes' are not in fieldnames, they will be ignored by extrasaction='ignore'
    # If a fieldname (e.g. 'city') is missing from a data row (like Charlie's), it will be an empty cell.
    fields = ['id', 'name', 'age', 'city', 'occupation']

    success = export_to_csv(sample_data, "test_export.csv", fields)
    print(f"Test export successful: {success}")

    success_empty = export_to_csv([], "empty_export.csv", fields)
    print(f"Test empty export successful (should indicate no data): {success_empty}")

    # Test with specific fields to ensure only those are exported
    specific_fields = ['name', 'city']
    success_specific = export_to_csv(sample_data, "specific_test_export.csv", specific_fields)
    print(f"Test specific fields export successful: {success_specific}")

    # Verify content of specific_test_export.csv (manual check or read back)
    # Expected:
    # name,city
    # Alice,New York
    # Bob,Los Angeles
    # Charlie,""

    if os.path.exists(os.path.join(EXPORT_DIR, "specific_test_export.csv")):
        with open(os.path.join(EXPORT_DIR, "specific_test_export.csv"), 'r') as f:
            print("\nContent of specific_test_export.csv:")
            print(f.read())

    # Test case where a fieldname might not be in any dict
    fields_with_nonexistent = ['id', 'name', 'non_existent_field']
    success_nonexistent = export_to_csv(sample_data, "nonexistent_field_export.csv", fields_with_nonexistent)
    print(f"Test export with non-existent field successful: {success_nonexistent}")
    if os.path.exists(os.path.join(EXPORT_DIR, "nonexistent_field_export.csv")):
        with open(os.path.join(EXPORT_DIR, "nonexistent_field_export.csv"), 'r') as f:
            print("\nContent of nonexistent_field_export.csv:")
            print(f.read())
    # Expected:
    # id,name,non_existent_field
    # 1,Alice,""
    # 2,Bob,""
    # 3,Charlie,""

    # Clean up test files
    # for f_name in ["test_export.csv", "empty_export.csv", "specific_test_export.csv", "nonexistent_field_export.csv"]:
    #     if os.path.exists(os.path.join(EXPORT_DIR, f_name)):
    #         os.remove(os.path.join(EXPORT_DIR, f_name))
    # if os.path.exists(EXPORT_DIR) and not os.listdir(EXPORT_DIR):
    #     os.rmdir(EXPORT_DIR)
