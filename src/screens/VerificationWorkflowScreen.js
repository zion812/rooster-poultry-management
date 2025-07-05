import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, FlatList, Alert, ActivityIndicator, ScrollView } from 'react-native';
import Button from '../components/common/Button'; // Assuming a common Button component
import { formatDate } from '../utils/helpers'; // Assuming a helper for date formatting

// Mock API functions - replace with actual API calls
const mockFetchVerificationSteps = (productId) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve([
        { id: 'step1', name: 'Initial Farm Inspection', status: 'pending', details: 'Verify farm conditions and practices.', assignedTo: 'Inspector A', dueDate: '2023-11-10' },
        { id: 'step2', name: 'Harvest Quality Check', status: 'pending', details: 'Assess quality of harvested produce.', assignedTo: 'QA Team Lead', dueDate: '2023-11-15' },
        { id: 'step3', name: 'Processing Facility Audit', status: 'pending', details: 'Audit processing unit for hygiene and standards.', assignedTo: 'Auditor B', dueDate: '2023-11-20' },
        { id: 'step4', name: 'Packaging Verification', status: 'pending', details: 'Ensure correct labeling and packaging integrity.', assignedTo: 'Packaging Supervisor', dueDate: '2023-11-25' },
        { id: 'step5', name: 'Final Product Approval', status: 'pending', details: 'Final sign-off before market release.', assignedTo: 'Compliance Officer', dueDate: '2023-11-30' },
      ]);
    }, 1000);
  });
};

const mockSubmitVerificationStep = (productId, stepId, verificationData) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      console.log('Submitting verification for:', { productId, stepId, verificationData });
      if (Math.random() > 0.1) { // Simulate 90% success rate
        resolve({ success: true, message: `Step ${stepId} verified successfully.` });
      } else {
        reject({ success: false, message: `Failed to verify step ${stepId}. Please try again.` });
      }
    }, 1500);
  });
};


const VerificationWorkflowScreen = ({ route }) => {
  // const { productId } = route.params; // Assuming productId is passed
  const productId = 'sampleProductIdForVerification'; // Sample Product ID

  const [steps, setSteps] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submittingStep, setSubmittingStep] = useState(null); // To track which step is being submitted
  const [error, setError] = useState(null);

  const fetchSteps = async () => {
    try {
      setLoading(true);
      setError(null);
      const fetchedSteps = await mockFetchVerificationSteps(productId);
      setSteps(fetchedSteps);
    } catch (e) {
      setError('Failed to load verification workflow.');
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (productId) {
      fetchSteps();
    } else {
        setError('Product ID is missing for verification workflow.');
        setLoading(false);
    }
  }, [productId]);

  const handleVerifyStep = async (stepId) => {
    // In a real app, you'd collect data for verification (e.g., from a form, camera, etc.)
    const verificationData = {
      verifiedBy: 'CurrentUser', // Replace with actual user
      timestamp: new Date().toISOString(),
      notes: `Verification for step ${stepId} completed.`,
      // Potentially add more specific data like photos, sensor readings, etc.
    };

    setSubmittingStep(stepId);
    setError(null);

    try {
      const result = await mockSubmitVerificationStep(productId, stepId, verificationData);
      Alert.alert('Success', result.message);
      // Update the step status locally or refetch
      setSteps(prevSteps =>
        prevSteps.map(step =>
          step.id === stepId ? { ...step, status: 'completed', verifiedData: verificationData } : step
        )
      );
    } catch (e) {
      Alert.alert('Error', e.message || 'An unknown error occurred during verification.');
      setError(e.message || 'Verification submission failed.');
    } finally {
      setSubmittingStep(null);
    }
  };

  const renderStepItem = ({ item }) => (
    <View style={[styles.stepItem, item.status === 'completed' && styles.completedStep]}>
      <Text style={styles.stepName}>{item.name}</Text>
      <Text style={styles.stepDetail}>Status: <Text style={item.status === 'completed' ? styles.statusCompleted : styles.statusPending}>{item.status.toUpperCase()}</Text></Text>
      <Text style={styles.stepDetail}>Details: {item.details}</Text>
      <Text style={styles.stepDetail}>Assigned To: {item.assignedTo}</Text>
      <Text style={styles.stepDetail}>Due Date: {formatDate(item.dueDate)}</Text>
      {item.status === 'pending' && (
        <Button
          title={submittingStep === item.id ? "Verifying..." : "Mark as Verified"}
          onPress={() => handleVerifyStep(item.id)}
          disabled={submittingStep === item.id}
          style={styles.verifyButton}
        />
      )}
      {item.status === 'completed' && item.verifiedData && (
        <View style={styles.verifiedInfo}>
          <Text style={styles.verifiedBy}>Verified by: {item.verifiedData.verifiedBy} on {formatDate(item.verifiedData.timestamp)}</Text>
          <Text style={styles.verifiedNotes}>Notes: {item.verifiedData.notes}</Text>
        </View>
      )}
    </View>
  );

  if (loading) {
    return <View style={styles.centered}><ActivityIndicator size="large" /><Text>Loading Workflow...</Text></View>;
  }

  if (error && steps.length === 0) { // Show error prominently if initial load fails
    return <View style={styles.centered}><Text style={styles.errorText}>{error}</Text><Button title="Retry" onPress={fetchSteps} /></View>;
  }

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Product Verification Workflow</Text>
      <Text style={styles.productIdText}>Product ID: {productId}</Text>
      {error && <Text style={styles.errorTextSmall}>{error}</Text>}
      {steps.length > 0 ? (
        <FlatList
          data={steps}
          renderItem={renderStepItem}
          keyExtractor={item => item.id}
          scrollEnabled={false} // Disable FlatList scrolling, use ScrollView's
        />
      ) : (
         <Text style={styles.noStepsText}>No verification steps found for this product.</Text>
      )}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f9f9f9',
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    textAlign: 'center',
    marginVertical: 20,
    color: '#333',
  },
  productIdText: {
    fontSize: 16,
    textAlign: 'center',
    marginBottom: 15,
    color: 'gray',
  },
  stepItem: {
    backgroundColor: '#fff',
    padding: 15,
    marginHorizontal: 16,
    marginBottom: 12,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#e0e0e0',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },
  completedStep: {
    backgroundColor: '#e6ffed', // Light green for completed steps
    borderColor: '#b2dfdb',
  },
  stepName: {
    fontSize: 18,
    fontWeight: '600',
    marginBottom: 8,
    color: '#444',
  },
  stepDetail: {
    fontSize: 14,
    color: '#666',
    marginBottom: 4,
  },
  statusPending: {
    color: '#ff9800', // Orange for pending
    fontWeight: 'bold',
  },
  statusCompleted: {
    color: '#4CAF50', // Green for completed
    fontWeight: 'bold',
  },
  verifyButton: {
    marginTop: 10,
  },
  verifiedInfo: {
    marginTop: 10,
    paddingTop: 8,
    borderTopWidth: 1,
    borderTopColor: '#eee',
  },
  verifiedBy: {
    fontSize: 13,
    fontStyle: 'italic',
    color: '#555',
  },
  verifiedNotes: {
    fontSize: 13,
    color: '#555',
    marginTop: 3,
  },
  errorText: {
    color: 'red',
    fontSize: 16,
    textAlign: 'center',
    marginBottom: 10,
  },
  errorTextSmall: {
    color: 'red',
    fontSize: 14,
    textAlign: 'center',
    marginHorizontal: 16,
    marginBottom: 10,
  },
  noStepsText: {
    textAlign: 'center',
    marginTop: 20,
    fontSize: 16,
    color: 'grey',
  }
});

export default VerificationWorkflowScreen;
