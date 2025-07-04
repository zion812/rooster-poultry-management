import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, FlatList, ActivityIndicator, TextInput } from 'react-native';
import Button from '../components/common/Button'; // Assuming a common Button component
import { formatDate } from '../utils/helpers'; // Assuming date formatting helper

// Mock API to fetch ownership history for a product
const mockFetchOwnershipHistory = (productId) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (!productId) {
        reject(new Error("Product ID is required."));
        return;
      }
      // Sample history. In a real app, this would come from a blockchain or secure ledger.
      const historyData = {
        'PROD001': [
          { transferId: 'T001', from: 'FarmOrigin', to: 'FarmerA', timestamp: '2023-01-10T10:00:00Z', quantity: 100, transactionDetails: 'Initial Harvest Registration' },
          { transferId: 'T002', from: 'FarmerA', to: 'ProcessorB', timestamp: '2023-01-12T14:30:00Z', quantity: 98, transactionDetails: 'Sale for Processing' },
          { transferId: 'T003', from: 'ProcessorB', to: 'DistributorC', timestamp: '2023-01-15T09:15:00Z', quantity: 95, transactionDetails: 'Processed Goods Transfer' },
          { transferId: 'T004', from: 'DistributorC', to: 'RetailerD', timestamp: '2023-01-18T16:45:00Z', quantity: 90, transactionDetails: 'Distribution to Retail' },
          { transferId: 'T005', from: 'RetailerD', to: 'ConsumerX', timestamp: '2023-01-20T11:05:00Z', quantity: 1, transactionDetails: 'Final Sale (example item)' },
        ],
        'PROD002': [
          { transferId: 'T101', from: 'ManufacturerY', to: 'WarehouseZ', timestamp: '2023-02-01T08:00:00Z', quantity: 500, transactionDetails: 'Bulk Shipment' },
          { transferId: 'T102', from: 'WarehouseZ', to: 'RetailerD', timestamp: '2023-02-05T13:20:00Z', quantity: 100, transactionDetails: 'Stock Replenishment' },
        ],
        // Add more product histories as needed
      };

      if (historyData[productId]) {
        resolve(historyData[productId]);
      } else {
        resolve([]); // No history found for this product ID
      }
    }, 1200);
  });
};


const OwnershipHistoryScreen = ({ route }) => {
  // const { productId } = route.params; // Get productId from navigation
  const initialProductId = route?.params?.productId || ''; // Allow pre-filling

  const [productIdInput, setProductIdInput] = useState(initialProductId);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchedProductId, setSearchedProductId] = useState('');


  const fetchHistory = async (idToFetch) => {
    if (!idToFetch.trim()) {
      setError("Please enter a Product ID to view its history.");
      setHistory([]);
      return;
    }
    setLoading(true);
    setError(null);
    setSearchedProductId(idToFetch.trim());
    try {
      const data = await mockFetchOwnershipHistory(idToFetch.trim());
      setHistory(data);
      if (data.length === 0) {
        setError(`No ownership history found for Product ID: ${idToFetch.trim()}.`);
      }
    } catch (e) {
      setError(e.message || 'Failed to fetch ownership history.');
      setHistory([]);
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (initialProductId) {
      fetchHistory(initialProductId);
    }
  }, [initialProductId]);

  const handleSearch = () => {
    fetchHistory(productIdInput);
  };

  const renderHistoryItem = ({ item, index }) => (
    <View style={styles.historyItem}>
      <View style={styles.timelineConnector}>
        <View style={styles.timelineDot} />
        {index < history.length -1 && <View style={styles.timelineLine} />}
      </View>
      <View style={styles.itemContent}>
        <Text style={styles.itemTimestamp}>{formatDate(item.timestamp)}</Text>
        <Text style={styles.itemTransfer}>
          <Text style={styles.ownerLabel}>From:</Text> {item.from} ➔ <Text style={styles.ownerLabel}>To:</Text> {item.to}
        </Text>
        <Text style={styles.itemDetail}>Quantity: {item.quantity}</Text>
        <Text style={styles.itemDetail}>Transaction ID: {item.transferId}</Text>
        <Text style={styles.itemDetail}>Details: {item.transactionDetails}</Text>
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Product Ownership History</Text>

      <View style={styles.searchContainer}>
        <TextInput
          style={styles.input}
          placeholder="Enter Product ID"
          value={productIdInput}
          onChangeText={setProductIdInput}
          onSubmitEditing={handleSearch} // Allow search on keyboard submit
        />
        <Button title="Search" onPress={handleSearch} style={styles.searchButton} textStyle={styles.searchButtonText} />
      </View>

      {loading && (
        <View style={styles.centered}>
          <ActivityIndicator size="large" color="#007AFF" />
          <Text>Loading History...</Text>
        </View>
      )}

      {error && !loading && (
        <View style={styles.centered}>
          <Text style={styles.errorText}>{error}</Text>
        </View>
      )}

      {!loading && history.length > 0 && (
        <>
        <Text style={styles.historyForText}>Displaying history for: {searchedProductId}</Text>
        <FlatList
          data={history}
          renderItem={renderHistoryItem}
          keyExtractor={item => item.transferId}
          contentContainerStyle={styles.listContent}
        />
        </>
      )}

      {!loading && !error && history.length === 0 && searchedProductId && (
         <View style={styles.centered}>
            {/* This case is covered by error state if API returns empty and sets error */}
            {/* <Text>No history found for Product ID: {searchedProductId}.</Text> */}
         </View>
      )}
       {!loading && !error && history.length === 0 && !searchedProductId && !initialProductId && (
         <View style={styles.centered}>
            <Text>Enter a Product ID to see its ownership chain.</Text>
         </View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f9f9f9',
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    textAlign: 'center',
    color: '#333',
    paddingVertical: 20,
    backgroundColor: 'white',
    borderBottomWidth: 1,
    borderBottomColor: '#eee'
  },
  searchContainer: {
    flexDirection: 'row',
    padding: 15,
    backgroundColor: 'white',
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
    alignItems: 'center',
  },
  input: {
    flex: 1,
    height: 45,
    borderColor: '#ccc',
    borderWidth: 1,
    borderRadius: 8,
    paddingHorizontal: 12,
    marginRight: 10,
    backgroundColor: '#fff',
    fontSize: 16,
  },
  searchButton: {
    paddingHorizontal: 15,
    height: 45,
    justifyContent: 'center',
  },
  searchButtonText: {
    fontSize: 16,
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  errorText: {
    color: '#D32F2F', // Material Red 700
    fontSize: 16,
    textAlign: 'center',
  },
  historyForText: {
    textAlign:'center',
    fontSize: 16,
    fontWeight: '500',
    color: '#555',
    paddingVertical: 10,
    backgroundColor: '#eef',
  },
  listContent: {
    paddingHorizontal: 10,
    paddingVertical: 10,
  },
  historyItem: {
    flexDirection: 'row',
    marginBottom: 5, // Reduced margin for tighter timeline
    alignItems: 'flex-start',
  },
  timelineConnector: {
    width: 30, // Width for the dot and line
    alignItems: 'center',
    marginRight: 10,
  },
  timelineDot: {
    width: 14,
    height: 14,
    borderRadius: 7,
    backgroundColor: '#007AFF', // iOS Blue
    zIndex: 1, // Ensure dot is above the line
  },
  timelineLine: {
    flex: 1,
    width: 2,
    backgroundColor: '#007AFF',
    marginTop: -2, // Overlap slightly with dot for continuous feel
  },
  itemContent: {
    flex: 1,
    backgroundColor: '#fff',
    padding: 15,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#e0e0e0',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 2,
    elevation: 1,
    marginBottom:10, // Space between cards
  },
  itemTimestamp: {
    fontSize: 13,
    color: '#888',
    marginBottom: 6,
    fontWeight: '500',
  },
  itemTransfer: {
    fontSize: 15,
    color: '#333',
    marginBottom: 4,
  },
  ownerLabel: {
    fontWeight: 'bold',
    color: '#444'
  },
  itemDetail: {
    fontSize: 14,
    color: '#555',
    marginBottom: 2,
  },
});

export default OwnershipHistoryScreen;
