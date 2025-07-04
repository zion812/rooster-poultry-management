import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, FlatList, ActivityIndicator } from 'react-native';
import Button from '../components/common/Button'; // Assuming a common Button component

// Mock API functions - replace with actual API calls
const mockFetchSupplyChainData = (productId) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve([
        { id: '1', stage: 'Harvesting', location: 'Farm A', date: '2023-10-01', actor: 'Farmer John' },
        { id: '2', stage: 'Processing', location: 'Processing Unit B', date: '2023-10-03', actor: 'Processor Jane' },
        { id: '3', stage: 'Packaging', location: 'Packaging Center C', date: '2023-10-05', actor: 'Packager Bob' },
        { id: '4', stage: 'Shipping', location: 'Distributor D', date: '2023-10-07', actor: 'Shipper Alice' },
        { id: '5', stage: 'Retail', location: 'Retail Store E', date: '2023-10-10', actor: 'Retailer Carol' },
      ]);
    }, 1000);
  });
};

const ProductTraceabilityScreen = ({ route }) => {
  // const { productId } = route.params; // Assuming productId is passed via navigation
  const productId = 'sampleProductId'; // Using a sample productId for now

  const [supplyChain, setSupplyChain] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await mockFetchSupplyChainData(productId);
        setSupplyChain(data);
      } catch (e) {
        setError('Failed to fetch supply chain data.');
        console.error(e);
      } finally {
        setLoading(false);
      }
    };

    if (productId) {
      fetchData();
    } else {
      setError('Product ID is missing.');
      setLoading(false);
    }
  }, [productId]);

  const renderSupplyChainStep = ({ item }) => (
    <View style={styles.stepContainer}>
      <Text style={styles.stepStage}>{item.stage}</Text>
      <Text>Location: {item.location}</Text>
      <Text>Date: {item.date}</Text>
      <Text>Actor: {item.actor}</Text>
    </View>
  );

  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" />
        <Text>Loading Supply Chain...</Text>
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.centered}>
        <Text style={styles.errorText}>{error}</Text>
        <Button title="Retry" onPress={() => { /* Implement retry logic if needed */ }} />
      </View>
    );
  }

  if (supplyChain.length === 0) {
    return (
      <View style={styles.centered}>
        <Text>No supply chain data available for this product.</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Product Supply Chain</Text>
      <Text style={styles.productIdText}>Product ID: {productId}</Text>
      <FlatList
        data={supplyChain}
        renderItem={renderSupplyChainStep}
        keyExtractor={item => item.id}
        contentContainerStyle={styles.listContent}
      />
      {/* Add more visualization elements like a map or timeline if needed */}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    backgroundColor: '#f5f5f5',
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 16,
    textAlign: 'center',
  },
  productIdText: {
    fontSize: 16,
    textAlign: 'center',
    marginBottom: 20,
    color: 'gray',
  },
  stepContainer: {
    backgroundColor: '#fff',
    padding: 16,
    marginBottom: 12,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#ddd',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.2,
    shadowRadius: 1.41,
    elevation: 2,
  },
  stepStage: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 8,
    color: '#333',
  },
  listContent: {
    paddingBottom: 16,
  },
  errorText: {
    color: 'red',
    fontSize: 16,
    marginBottom: 10,
  },
});

export default ProductTraceabilityScreen;
