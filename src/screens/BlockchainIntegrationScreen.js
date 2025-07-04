import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, FlatList, ActivityIndicator, TextInput, Alert, ScrollView } from 'react-native';
import Button from '../components/common/Button';
import { formatDate } from '../utils/helpers';

// --- Mock Blockchain Interaction ---
// This is a highly simplified mock. A real implementation would involve:
// - A library like ethers.js or web3.js for Ethereum.
// - Connection to a blockchain node (e.g., Infura, Alchemy, or a local node).
// - Smart contract ABIs and addresses.
// - Wallet integration for signing transactions (e.g., MetaMask or hardware wallet).

const mockBlockchain = {
  records: [],
  latestBlock: 1000,

  async writeRecord(data) {
    return new Promise((resolve) => {
      setTimeout(() => {
        this.latestBlock++;
        const newRecord = {
          id: `tx_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
          data: JSON.stringify(data), // Store as string for simplicity
          timestamp: new Date().toISOString(),
          blockNumber: this.latestBlock,
          status: 'confirmed', // Simulate immediate confirmation
        };
        this.records.unshift(newRecord); // Add to the beginning
        console.log('Blockchain: New record written', newRecord);
        resolve({ success: true, transactionHash: newRecord.id, blockNumber: newRecord.blockNumber });
      }, 1500);
    });
  },

  async fetchRecordsByProductId(productId, limit = 10) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const productRecords = this.records.filter(record => {
          try {
            const parsedData = JSON.parse(record.data);
            return parsedData.productId === productId;
          } catch (e) {
            return false;
          }
        });
        resolve(productRecords.slice(0, limit));
      }, 1000);
    });
  },

  async fetchAllRecords(limit = 20) {
     return new Promise((resolve) => {
      setTimeout(() => {
        resolve(this.records.slice(0, limit));
      }, 700);
    });
  }
};
// --- End Mock Blockchain Interaction ---

const BlockchainIntegrationScreen = ({ route }) => {
  // const { defaultProductId } = route.params || {}; // Optional: pre-fill product ID
  const defaultProductId = 'sampleProd123';

  const [productIdInput, setProductIdInput] = useState(defaultProductId || '');
  const [dataInput, setDataInput] = useState(''); // For custom data to write
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [filterProductId, setFilterProductId] = useState(defaultProductId || '');

  const handleWriteToBlockchain = async () => {
    if (!productIdInput.trim() || !dataInput.trim()) {
      Alert.alert('Error', 'Product ID and Data fields are required to write to blockchain.');
      return;
    }

    const recordData = {
      productId: productIdInput.trim(),
      customData: dataInput.trim(),
      recordedBy: 'AppUser_X', // Example: link to user or device
      action: 'PRODUCT_EVENT', // Example: type of event
    };

    setSubmitting(true);
    setError(null);
    try {
      const result = await mockBlockchain.writeRecord(recordData);
      if (result.success) {
        Alert.alert('Success', `Data written to blockchain.\nTransaction Hash: ${result.transactionHash}\nBlock: ${result.blockNumber}`);
        setDataInput(''); // Clear input after successful submission
        // Optionally, refetch records for the current product ID
        if (filterProductId === productIdInput.trim()) {
            handleFetchRecords();
        } else { // Or fetch all to see the new record
            handleFetchAllRecords();
        }
      } else {
        throw new Error(result.message || 'Failed to write to blockchain.');
      }
    } catch (e) {
      setError(`Error writing to blockchain: ${e.message}`);
      Alert.alert('Error', `Error writing to blockchain: ${e.message}`);
      console.error(e);
    } finally {
      setSubmitting(false);
    }
  };

  const handleFetchRecords = async () => {
    if (!filterProductId.trim()) {
      Alert.alert('Info', 'Please enter a Product ID to filter records.');
      // Optionally fetch all records if filter is cleared
      // handleFetchAllRecords();
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const fetchedRecords = await mockBlockchain.fetchRecordsByProductId(filterProductId.trim());
      setRecords(fetchedRecords);
      if(fetchedRecords.length === 0) {
        Alert.alert('Info', `No blockchain records found for Product ID: ${filterProductId.trim()}`);
      }
    } catch (e) {
      setError(`Error fetching records: ${e.message}`);
      Alert.alert('Error', `Error fetching records: ${e.message}`);
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleFetchAllRecords = async () => {
    setLoading(true);
    setError(null);
    try {
      const fetchedRecords = await mockBlockchain.fetchAllRecords();
      setRecords(fetchedRecords);
      setFilterProductId(''); // Clear product ID filter when fetching all
       if(fetchedRecords.length === 0) {
        Alert.alert('Info', `No blockchain records found yet.`);
      }
    } catch (e) {
      setError(`Error fetching all records: ${e.message}`);
      Alert.alert('Error', `Error fetching all records: ${e.message}`);
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  // Fetch initial records (e.g., all or for a default product)
  useEffect(() => {
    if (defaultProductId) {
        handleFetchRecords();
    } else {
        handleFetchAllRecords();
    }
  }, []);


  const renderRecordItem = ({ item }) => {
    let parsedData = {};
    try {
        parsedData = JSON.parse(item.data);
    } catch (e) {
        console.warn("Failed to parse record data:", item.data);
    }
    return (
        <View style={styles.recordItem}>
            <Text style={styles.recordTx}>Tx: {item.id}</Text>
            <Text>Block: {item.blockNumber}</Text>
            <Text>Timestamp: {formatDate(item.timestamp)}</Text>
            <Text>Status: <Text style={item.status === 'confirmed' ? styles.statusConfirmed : styles.statusPending}>{item.status.toUpperCase()}</Text></Text>
            <Text style={styles.recordDataLabel}>Data:</Text>
            <Text style={styles.recordDataContent}>  Product ID: {parsedData.productId || 'N/A'}</Text>
            <Text style={styles.recordDataContent}>  Action: {parsedData.action || 'N/A'}</Text>
            <Text style={styles.recordDataContent}>  Details: {parsedData.customData || 'N/A'}</Text>
            <Text style={styles.recordDataContent}>  Recorded By: {parsedData.recordedBy || 'N/A'}</Text>
        </View>
    );
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Blockchain Record Keeping</Text>

      <View style={styles.inputSection}>
        <Text style={styles.sectionTitle}>Write New Record</Text>
        <TextInput
          style={styles.input}
          placeholder="Product ID for new record"
          value={productIdInput}
          onChangeText={setProductIdInput}
        />
        <TextInput
          style={[styles.input, styles.multilineInput]}
          placeholder="Data to record (e.g., event details, sensor readings)"
          value={dataInput}
          onChangeText={setDataInput}
          multiline
          numberOfLines={3}
        />
        <Button
          title={submitting ? "Submitting..." : "Write to Blockchain"}
          onPress={handleWriteToBlockchain}
          disabled={submitting}
        />
      </View>

      <View style={styles.filterSection}>
        <Text style={styles.sectionTitle}>View Records</Text>
        <TextInput
          style={styles.input}
          placeholder="Filter by Product ID (or leave blank for all)"
          value={filterProductId}
          onChangeText={setFilterProductId}
        />
        <View style={styles.buttonRow}>
            <Button title="Fetch by Product ID" onPress={handleFetchRecords} style={styles.fetchButton} />
            <Button title="Fetch All Recent" onPress={handleFetchAllRecords} style={styles.fetchButton} />
        </View>
      </View>

      {loading && <View style={styles.centered}><ActivityIndicator size="large" /><Text>Loading Records...</Text></View>}
      {error && <Text style={styles.errorText}>{error}</Text>}

      {!loading && records.length === 0 && (
        <Text style={styles.noRecordsText}>No records to display. Try fetching or writing new data.</Text>
      )}

      {!loading && records.length > 0 && (
        <FlatList
          data={records}
          renderItem={renderRecordItem}
          keyExtractor={item => item.id}
          style={styles.list}
          scrollEnabled={false} // Parent ScrollView handles scrolling
        />
      )}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f4f6f8',
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    textAlign: 'center',
    marginVertical: 20,
    color: '#333',
  },
  inputSection: {
    paddingHorizontal: 16,
    marginBottom: 20,
    backgroundColor: 'white',
    paddingVertical: 15,
    borderRadius: 8,
    marginHorizontal:10,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },
  filterSection: {
    paddingHorizontal: 16,
    marginBottom: 20,
    backgroundColor: 'white',
    paddingVertical: 15,
    borderRadius: 8,
    marginHorizontal:10,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '600',
    marginBottom: 10,
    color: '#444',
  },
  input: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#ddd',
    padding: 10,
    borderRadius: 5,
    marginBottom: 12,
    fontSize: 15,
  },
  multilineInput: {
    height: 80,
    textAlignVertical: 'top',
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  fetchButton: {
    flex: 1, // Each button takes half the space
    marginHorizontal: 4, // Add some spacing between buttons
  },
  centered: {
    alignItems: 'center',
    paddingVertical: 20,
  },
  errorText: {
    color: 'red',
    textAlign: 'center',
    marginHorizontal: 16,
    marginBottom: 10,
  },
  list: {
    marginTop: 10,
  },
  recordItem: {
    backgroundColor: '#fff',
    padding: 15,
    marginHorizontal: 16,
    marginBottom: 12,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#e0e0e0',
  },
  recordTx: {
    fontSize: 13,
    color: '#666',
    fontWeight: 'bold',
    marginBottom: 4,
  },
  statusConfirmed: {
    color: 'green',
    fontWeight: 'bold',
  },
  statusPending: {
    color: 'orange',
    fontWeight: 'bold',
  },
  recordDataLabel: {
    marginTop: 6,
    fontWeight: '600',
    color: '#555',
  },
  recordDataContent: {
    fontSize: 13,
    color: '#333',
    marginLeft: 10,
    fontFamily: Platform.OS === 'ios' ? 'Courier New' : 'monospace', // Monospaced font for data
  },
  noRecordsText: {
    textAlign: 'center',
    marginTop: 20,
    fontSize: 16,
    color: 'grey',
    paddingHorizontal: 16,
  }
});

export default BlockchainIntegrationScreen;
