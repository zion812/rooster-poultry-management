import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, Alert, ActivityIndicator, TextInput, TouchableOpacity } from 'react-native';
import Button from '../components/common/Button';
import { formatDate } from '../utils/helpers';
// For actual digital signatures, you'd need a crypto library (e.g., ethers.js for Ethereum-like signatures, or a platform-specific secure enclave API)
// For this mock, we'll simulate the process.

// Mock API for fetching transfer details and confirming/rejecting
const mockFetchTransferDetails = async (transferId) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      // Simulate fetching details of a pending transfer
      if (transferId === 'T-12345PENDING') {
        resolve({
          id: transferId,
          productId: 'PROD_XYZ_789',
          fromOwner: 'UserA_WalletAddr',
          toRecipient: 'UserB_WalletAddr',
          quantity: 10,
          initiatedDate: '2023-11-15T10:30:00Z',
          status: 'pending_confirmation', // or 'pending_recipient_approval'
          notes: 'Transfer for order #ORD991',
          // In real scenario, might include a hash of transfer data to be signed
          dataToSign: `Transfer_PROD_XYZ_789_Qty10_To_UserB_WalletAddr_Nonce_${Date.now()}`
        });
      } else if (transferId === 'T-67890CONFIRMED') {
         resolve({
          id: transferId,
          productId: 'PROD_ABC_123',
          fromOwner: 'UserC_WalletAddr',
          toRecipient: 'UserD_WalletAddr',
          quantity: 5,
          initiatedDate: '2023-11-14T18:00:00Z',
          status: 'confirmed',
          notes: 'Payment received.',
          confirmationSignature: '0xabc123...', // Example
          confirmedDate: '2023-11-14T18:05:00Z',
        });
      }
      else {
        reject(new Error(`Transfer ID ${transferId} not found or already processed.`));
      }
    }, 1000);
  });
};

const mockSubmitTransferConfirmation = async (transferId, action, signatureOrOtp, userPin) => {
  // action can be 'confirm' or 'reject'
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      console.log("Submitting transfer confirmation:", { transferId, action, signatureOrOtp, userPin });
      if (!userPin || userPin.length < 4) { // Basic PIN check
          reject({message: "Invalid or missing PIN."});
          return;
      }
      if (action === 'confirm' && !signatureOrOtp) {
         reject({message: "Signature/OTP is required for confirmation."});
         return;
      }

      if (transferId === 'T-12345PENDING') {
        if (action === 'confirm') {
          resolve({ success: true, message: `Transfer ${transferId} confirmed successfully with signature ${signatureOrOtp}.` });
        } else if (action === 'reject') {
          resolve({ success: true, message: `Transfer ${transferId} rejected.` });
        } else {
          reject({message: "Invalid action."});
        }
      } else {
        reject({ message: `Transfer ${transferId} cannot be ${action}ed or does not exist.` });
      }
    }, 1500);
  });
};


const TransferVerificationScreen = ({ route }) => {
  // const { transferId } = route.params; // Expected to be passed to this screen
  const transferId = route?.params?.transferId || 'T-12345PENDING'; // Default for example

  const [transferDetails, setTransferDetails] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [actionLoading, setActionLoading] = useState(false);

  // For mock signature/OTP and PIN
  const [mockSignature, setMockSignature] = useState('');
  const [userPin, setUserPin] = useState('');

  useEffect(() => {
    const loadTransferDetails = async () => {
      if (!transferId) {
        setError("Transfer ID is missing.");
        setLoading(false);
        return;
      }
      setLoading(true);
      setError(null);
      try {
        const details = await mockFetchTransferDetails(transferId);
        setTransferDetails(details);
      } catch (e) {
        setError(e.message || 'Failed to load transfer details.');
        console.error(e);
      } finally {
        setLoading(false);
      }
    };
    loadTransferDetails();
  }, [transferId]);

  const handleAction = async (actionType) => { // 'confirm' or 'reject'
    if (!transferDetails || transferDetails.status !== 'pending_confirmation') {
      Alert.alert("Invalid State", "This transfer cannot be actioned at this time.");
      return;
    }

    if (actionType === 'confirm' && !mockSignature.trim()) {
        Alert.alert("Input Required", "Please enter a mock signature/OTP to confirm.");
        return;
    }
    if (!userPin.trim() || userPin.length < 4) {
        Alert.alert("Authentication Required", "Please enter your 4-digit PIN.");
        return;
    }

    setActionLoading(true);
    setError(null);
    try {
      const result = await mockSubmitTransferConfirmation(transferId, actionType, mockSignature, userPin);
      Alert.alert(actionType === 'confirm' ? 'Transfer Confirmed' : 'Transfer Rejected', result.message);
      // Update local state or navigate away
      setTransferDetails(prev => ({ ...prev, status: actionType === 'confirm' ? 'confirmed_by_user' : 'rejected_by_user' }));
      // In a real app: navigation.goBack() or navigate to a success/status screen.
    } catch (e) {
      Alert.alert('Action Failed', e.message || 'An unexpected error occurred.');
      setError(e.message);
    } finally {
      setActionLoading(false);
      setUserPin(''); // Clear PIN after attempt
    }
  };

  // Simulate generating a signature (in reality, this would involve a crypto wallet/library)
  const handleGenerateMockSignature = () => {
      if (transferDetails && transferDetails.dataToSign) {
          const pseudoSignature = `signed(${transferDetails.dataToSign.substring(0,15)}...${Date.now()%10000})`;
          setMockSignature(pseudoSignature);
          Alert.alert("Mock Signature Generated", "A mock signature has been placed in the input field.");
      } else {
          Alert.alert("Error", "No data available to sign for this transfer.");
      }
  };

  if (loading) {
    return <View style={styles.centered}><ActivityIndicator size="large" /><Text>Loading Transfer Details...</Text></View>;
  }

  if (error && !transferDetails) { // Show error prominently if initial load fails
    return <View style={styles.centered}><Text style={styles.errorText}>{error}</Text></View>;
  }

  if (!transferDetails) {
      return <View style={styles.centered}><Text>No transfer details found for ID: {transferId}</Text></View>;
  }

  const isPendingConfirmation = transferDetails.status === 'pending_confirmation';

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <Text style={styles.title}>Transfer Verification</Text>
      <Text style={styles.transferIdText}>Transfer ID: {transferDetails.id}</Text>

      <View style={styles.detailsCard}>
        <Text style={styles.cardTitle}>Transfer Summary</Text>
        <DetailRow label="Product ID:" value={transferDetails.productId} />
        <DetailRow label="From (Sender):" value={transferDetails.fromOwner} />
        <DetailRow label="To (Recipient):" value={transferDetails.toRecipient} />
        <DetailRow label="Quantity:" value={transferDetails.quantity?.toString()} />
        <DetailRow label="Initiated:" value={formatDate(transferDetails.initiatedDate)} />
        <DetailRow label="Status:" value={transferDetails.status?.replace(/_/g, ' ').toUpperCase()} styleValue={transferDetails.status === 'confirmed' ? styles.statusConfirmed : (transferDetails.status.includes('pending') ? styles.statusPending : styles.statusRejected)} />
        {transferDetails.notes && <DetailRow label="Notes:" value={transferDetails.notes} />}
        {transferDetails.confirmedDate && <DetailRow label="Confirmed On:" value={formatDate(transferDetails.confirmedDate)} />}
        {transferDetails.confirmationSignature && <DetailRow label="Conf. Signature:" value={transferDetails.confirmationSignature} isSignature={true} />}
      </View>

      {error && <Text style={styles.errorTextSmall}>{error}</Text>}

      {isPendingConfirmation && (
        <View style={styles.actionSection}>
          <Text style={styles.sectionTitle}>Confirm or Reject Transfer</Text>

          <View style={styles.formGroup}>
            <Text style={styles.label}>Mock Digital Signature / OTP:</Text>
            <View style={styles.signatureInputContainer}>
                <TextInput
                    style={styles.input}
                    value={mockSignature}
                    onChangeText={setMockSignature}
                    placeholder="Enter Signature or OTP"
                    editable={!actionLoading}
                />
                <TouchableOpacity onPress={handleGenerateMockSignature} style={styles.genSigButton} disabled={actionLoading}>
                    <Text style={styles.genSigButtonText}>Gen Mock Sig</Text>
                </TouchableOpacity>
            </View>
            <Text style={styles.infoText}>In a real app, this would be generated by a secure wallet or received via 2FA.</Text>
          </View>

          <View style={styles.formGroup}>
            <Text style={styles.label}>Enter Your 4-Digit PIN for Authentication:</Text>
            <TextInput
              style={styles.input}
              value={userPin}
              onChangeText={setUserPin}
              placeholder="****"
              keyboardType="number-pad"
              secureTextEntry
              maxLength={4}
              editable={!actionLoading}
            />
          </View>

          {actionLoading ? (
            <ActivityIndicator size="large" color="#007BFF" style={{marginVertical: 20}} />
          ) : (
            <View style={styles.buttonContainer}>
              <Button
                title="Reject Transfer"
                onPress={() => handleAction('reject')}
                style={[styles.actionButton, styles.rejectButton]}
                textStyle={styles.actionButtonText}
                disabled={actionLoading}
              />
              <Button
                title="Confirm Transfer"
                onPress={() => handleAction('confirm')}
                style={[styles.actionButton, styles.confirmButton]}
                textStyle={styles.actionButtonText}
                disabled={actionLoading}
              />
            </View>
          )}
        </View>
      )}

      {!isPendingConfirmation && transferDetails.status !== 'pending_confirmation' && (
        <Text style={styles.alreadyProcessedText}>
            This transfer has already been {transferDetails.status.replace(/_/g, ' ')}. No further actions can be taken.
        </Text>
      )}

    </ScrollView>
  );
};

const DetailRow = ({ label, value, styleValue, isSignature }) => (
  <View style={styles.detailRow}>
    <Text style={styles.detailLabel}>{label}</Text>
    <Text style={[styles.detailValue, styleValue, isSignature && styles.signatureText]} selectable={isSignature}>{value}</Text>
  </View>
);

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f4f7f9',
  },
  contentContainer: {
    paddingBottom: 30, // Ensure space for buttons at the bottom
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
    color: '#2c3e50',
    paddingVertical: 20,
    backgroundColor: 'white',
    borderBottomWidth: 1,
    borderBottomColor: '#e0e6ed',
  },
  transferIdText: {
    fontSize: 14,
    textAlign: 'center',
    color: 'grey',
    paddingBottom: 15,
    backgroundColor: 'white',
  },
  detailsCard: {
    backgroundColor: '#fff',
    marginHorizontal: 15,
    marginTop: 15,
    borderRadius: 8,
    padding: 20,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2, },
    shadowOpacity: 0.1,
    shadowRadius: 3.84,
    elevation: 3,
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#34495e',
    marginBottom: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#ecf0f1',
    paddingBottom: 10,
  },
  detailRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
    alignItems: 'flex-start',
  },
  detailLabel: {
    fontSize: 15,
    color: '#7f8c8d', // Asbestos
    fontWeight: '500',
    flex: 2, // Takes up more space
  },
  detailValue: {
    fontSize: 15,
    color: '#2c3e50', // Midnight Blue
    flex: 3, // Takes up more space
    textAlign: 'right',
  },
  signatureText: {
    fontFamily: Platform.OS === 'ios' ? 'Courier New' : 'monospace',
    fontSize: 13,
    color: '#3498db', // Peter River (Blue)
    flexWrap: 'wrap', // Allow long signatures to wrap
  },
  statusConfirmed: { color: '#27ae60' /* Emerald */, fontWeight: 'bold' },
  statusPending: { color: '#f39c12' /* Orange */, fontWeight: 'bold' },
  statusRejected: { color: '#c0392b' /* Pomegranate */, fontWeight: 'bold' },
  actionSection: {
    marginTop: 25,
    paddingHorizontal: 15,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#34495e',
    marginBottom: 15,
    textAlign: 'center',
  },
  formGroup: {
    marginBottom: 15,
  },
  label: {
    fontSize: 15,
    color: '#34495e',
    marginBottom: 8,
  },
  input: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#bdc3c7', // Silver
    borderRadius: 6,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 16,
    color: '#2c3e50',
    flex:1,
  },
  signatureInputContainer: {
      flexDirection: 'row',
      alignItems: 'center',
  },
  genSigButton: {
      marginLeft: 10,
      backgroundColor: '#3498db', // Peter River
      paddingHorizontal: 10,
      paddingVertical: 12,
      borderRadius: 6,
      height:45,
      justifyContent:'center',
  },
  genSigButtonText: {
      color: 'white',
      fontSize: 14,
      fontWeight: '500',
  },
  infoText: {
    fontSize: 12,
    color: '#7f8c8d',
    marginTop: 5,
    fontStyle:'italic',
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginTop: 20,
  },
  actionButton: {
    flex: 1, // Each button takes up equal space
    marginHorizontal: 8,
    paddingVertical: 12,
  },
  actionButtonText: {
    fontSize: 16,
    fontWeight: '500',
  },
  confirmButton: {
    backgroundColor: '#2ecc71', // Emerald
  },
  rejectButton: {
    backgroundColor: '#e74c3c', // Alizarin (Red)
  },
  errorText: {
    color: 'red',
    fontSize: 16,
    textAlign: 'center',
    padding:10,
  },
  errorTextSmall: {
    color: 'red',
    fontSize: 14,
    textAlign: 'center',
    marginHorizontal: 15,
    marginTop: 10,
  },
  alreadyProcessedText: {
      textAlign: 'center',
      fontSize: 16,
      color: '#7f8c8d',
      marginVertical: 30,
      paddingHorizontal: 20,
      fontStyle: 'italic',
  }
});

export default TransferVerificationScreen;
