import React, { useState } from 'react';
import { View, Text, TextInput, StyleSheet, Alert, ScrollView, ActivityIndicator } from 'react-native';
import Button from '../components/common/Button';
// import { Picker } from '@react-native-picker/picker'; // For selecting a verifier

// Mock API for submitting a verification request
const mockSubmitVerificationRequest = async (requestDetails) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      console.log("Submitting verification request:", requestDetails);
      if (!requestDetails.productId || !requestDetails.verifierId || !requestDetails.requestDetails) {
        reject({ message: "Product ID, Verifier ID, and Request Details are required." });
        return;
      }
      resolve({
        success: true,
        message: `Verification request for ${requestDetails.productId} sent to ${requestDetails.verifierId}.`,
        requestId: `VR-${Date.now()}`
      });
    }, 1500);
  });
};

// Mock list of potential third-party verifiers
const mockVerifiers = [
  { id: 'verifier_001', name: 'AgriCertify Inc.', specialty: 'Organic Certification' },
  { id: 'verifier_002', name: 'QualityTrack Solutions', specialty: 'Supply Chain Audits' },
  { id: 'verifier_003', name: 'EthicaVerify Ltd.', specialty: 'Fair Trade Compliance' },
  { id: 'verifier_004', name: 'LabTest International', specialty: 'Product Quality Testing' },
];


const VerificationRequestScreen = ({ route }) => {
  // const { productIdToVerify } = route.params || {}; // Product ID passed from another screen
  const productIdToVerify = route?.params?.productIdToVerify || 'SAMPLE_PROD_789';

  const [productId, setProductId] = useState(productIdToVerify);
  const [verifierId, setVerifierId] = useState(''); // Could be an ID selected from a list/Picker
  const [verifierName, setVerifierName] = useState(''); // For display if selected from picker
  const [requestDetails, setRequestDetails] = useState('');
  const [requesterNotes, setRequesterNotes] = useState('');
  const [loading, setLoading] = useState(false);

  // State for a simple verifier selection (can be replaced with a Picker)
  const [selectedVerifier, setSelectedVerifier] = useState(null);


  const handleSubmitRequest = async () => {
    if (!productId.trim() || !selectedVerifier || !requestDetails.trim()) {
      Alert.alert('Validation Error', 'Please fill in Product ID, select a Verifier, and provide Request Details.');
      return;
    }

    const verificationRequest = {
      productId,
      verifierId: selectedVerifier.id,
      verifierName: selectedVerifier.name, // For logging/display
      requestDetails, // Specific checks or information requested
      requesterId: 'CurrentUser_ID', // Should come from auth context
      requesterNotes: requesterNotes.trim(),
      requestDate: new Date().toISOString(),
      status: 'pending_submission', // Initial status
    };

    setLoading(true);
    try {
      const result = await mockSubmitVerificationRequest(verificationRequest);
      Alert.alert('Request Submitted', result.message + ` (Request ID: ${result.requestId})`);
      // Clear form
      // setProductId(productIdToVerify || ''); // Reset to default or clear
      setVerifierId('');
      setVerifierName('');
      setSelectedVerifier(null);
      setRequestDetails('');
      setRequesterNotes('');
      // Optionally navigate to a request tracking screen
    } catch (error) {
      Alert.alert('Submission Failed', error.message || 'An unexpected error occurred.');
    } finally {
      setLoading(false);
    }
  };

  // Simple Verifier Selection UI (can be enhanced with a modal or dropdown)
  const renderVerifierSelection = () => (
    <View style={styles.verifierSelectionContainer}>
      <Text style={styles.label}>Select Third-Party Verifier:</Text>
      {mockVerifiers.map(verifier => (
        <TouchableOpacity
          key={verifier.id}
          style={[
            styles.verifierOption,
            selectedVerifier?.id === verifier.id && styles.verifierOptionSelected
          ]}
          onPress={() => setSelectedVerifier(verifier)}
        >
          <Text style={styles.verifierName}>{verifier.name}</Text>
          <Text style={styles.verifierSpecialty}>{verifier.specialty}</Text>
        </TouchableOpacity>
      ))}
      {selectedVerifier && <Text style={styles.selectedVerifierText}>Selected: {selectedVerifier.name}</Text>}
    </View>
  );


  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <Text style={styles.title}>Third-Party Verification Request</Text>

      <View style={styles.formGroup}>
        <Text style={styles.label}>Product ID to Verify</Text>
        <TextInput
          style={styles.input}
          value={productId}
          onChangeText={setProductId}
          placeholder="Enter Product ID"
          // editable={!productIdToVerify}
        />
      </View>

      {renderVerifierSelection()}
      {/* If using @react-native-picker/picker:
      <View style={styles.formGroup}>
        <Text style={styles.label}>Select Verifier</Text>
        <Picker
          selectedValue={verifierId}
          style={styles.picker}
          onValueChange={(itemValue, itemIndex) => {
            setVerifierId(itemValue);
            if (itemValue) {
                const verifier = mockVerifiers.find(v => v.id === itemValue);
                setVerifierName(verifier ? verifier.name : '');
            } else {
                setVerifierName('');
            }
          }}>
          <Picker.Item label="-- Choose a Verifier --" value="" />
          {mockVerifiers.map(v => <Picker.Item key={v.id} label={`${v.name} (${v.specialty})`} value={v.id} />)}
        </Picker>
      </View>
      */}


      <View style={styles.formGroup}>
        <Text style={styles.label}>Details of Verification Requested</Text>
        <TextInput
          style={[styles.input, styles.textArea]}
          value={requestDetails}
          onChangeText={setRequestDetails}
          placeholder="e.g., Verify organic status, confirm lab results for batch XYZ, audit storage conditions."
          multiline
          numberOfLines={4}
        />
      </View>

      <View style={styles.formGroup}>
        <Text style={styles.label}>Additional Notes for Verifier (Optional)</Text>
        <TextInput
          style={[styles.input, styles.textArea]}
          value={requesterNotes}
          onChangeText={setRequesterNotes}
          placeholder="e.g., Please expedite, contact person: John Doe (john@example.com)"
          multiline
          numberOfLines={3}
        />
      </View>

      {loading ? (
        <ActivityIndicator size="large" color="#007BFF" style={styles.loader} />
      ) : (
        <Button
          title="Submit Verification Request"
          onPress={handleSubmitRequest}
        />
      )}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f9fa',
  },
  contentContainer:{
    padding: 20,
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#343a40',
    textAlign: 'center',
    marginBottom: 25,
  },
  formGroup: {
    marginBottom: 20,
  },
  label: {
    fontSize: 16,
    color: '#495057',
    marginBottom: 8,
  },
  input: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#ced4da',
    borderRadius: 6,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 16,
    color: '#495057',
  },
  textArea: {
    height: 100,
    textAlignVertical: 'top',
  },
  picker: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#ced4da',
    borderRadius: 6,
  },
  loader: {
    marginTop: 20,
  },
  verifierSelectionContainer: {
    marginBottom: 20,
    padding: 10,
    backgroundColor: '#fff',
    borderRadius: 6,
    borderWidth:1,
    borderColor: '#e9ecef'
  },
  verifierOption: {
    paddingVertical: 12,
    paddingHorizontal: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#f1f3f5',
  },
  verifierOptionSelected: {
    backgroundColor: '#e6f7ff', // Light blue for selected
    borderRadius:4,
  },
  verifierName: {
    fontSize: 15,
    fontWeight: '500',
    color: '#212529',
  },
  verifierSpecialty: {
    fontSize: 13,
    color: '#6c757d',
  },
  selectedVerifierText: {
    marginTop:10,
    fontSize: 14,
    color: '#28a745', // Green
    fontWeight:'bold',
  }
});

export default VerificationRequestScreen;
