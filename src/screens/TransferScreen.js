import React, { useState } from 'react';
import { View, Text, TextInput, StyleSheet, Alert, ScrollView, ActivityIndicator } from 'react-native';
import Button from '../components/common/Button';
// import { Picker } from '@react-native-picker/picker'; // For selecting product/asset if not passed via route

// Mock API for transfer
const mockInitiateTransfer = async (transferDetails) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      console.log("Initiating transfer:", transferDetails);
      // Simulate some validation
      if (!transferDetails.productId || !transferDetails.recipientId) {
        reject({ message: "Product ID and Recipient ID are required." });
        return;
      }
      if (transferDetails.quantity <= 0) {
        reject({ message: "Quantity must be greater than zero." });
        return;
      }
      // Simulate successful transfer initiation
      resolve({
        success: true,
        message: `Transfer of ${transferDetails.quantity} unit(s) of ${transferDetails.productId} to ${transferDetails.recipientId} initiated. Awaiting confirmation.`,
        transferId: `T-${Date.now()}`
      });
    }, 1500);
  });
};


const TransferScreen = ({ route }) => {
  // const { productIdToTransfer, currentOwnerId } = route.params || {}; // Example: passed from another screen
  const productIdToTransfer = route?.params?.productIdToTransfer || 'DEFAULT_PROD_ID_123';
  const currentOwnerId = route?.params?.currentOwnerId || 'USER_A';


  const [productId, setProductId] = useState(productIdToTransfer);
  const [recipientId, setRecipientId] = useState('');
  const [quantity, setQuantity] = useState('1'); // Default to 1, ensure it's a string for TextInput
  const [transferNotes, setTransferNotes] = useState('');
  const [loading, setLoading] = useState(false);

  const handleInitiateTransfer = async () => {
    if (!productId.trim() || !recipientId.trim() || !quantity.trim()) {
      Alert.alert('Validation Error', 'Please fill in Product ID, Recipient ID, and Quantity.');
      return;
    }

    const numQuantity = parseInt(quantity, 10);
    if (isNaN(numQuantity) || numQuantity <= 0) {
      Alert.alert('Validation Error', 'Quantity must be a positive number.');
      return;
    }

    const transferDetails = {
      productId,
      currentOwnerId, // This would typically be the logged-in user or derived contextually
      recipientId,
      quantity: numQuantity,
      notes: transferNotes.trim(),
      initiatedBy: currentOwnerId, // Or a more specific user identifier
      timestamp: new Date().toISOString(),
      // In a real system, you might include:
      // - Digital signature of the sender
      // - Expected conditions for transfer (e.g., payment received)
    };

    setLoading(true);
    try {
      const result = await mockInitiateTransfer(transferDetails);
      Alert.alert('Transfer Initiated', result.message);
      // Clear form or navigate away
      setRecipientId('');
      setQuantity('1');
      setTransferNotes('');
      // Potentially navigate to a TransferStatusScreen or OwnershipHistoryScreen
      // navigation.navigate('TransferVerification', { transferId: result.transferId });
    } catch (error) {
      Alert.alert('Transfer Failed', error.message || 'An unexpected error occurred.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <Text style={styles.title}>Secure Product Transfer</Text>

      <View style={styles.formGroup}>
        <Text style={styles.label}>Product ID</Text>
        <TextInput
          style={styles.input}
          value={productId}
          onChangeText={setProductId}
          placeholder="Enter Product ID to transfer"
          // editable={!productIdToTransfer} // If pre-filled, make it non-editable or show as display text
        />
      </View>

      <View style={styles.formGroup}>
        <Text style={styles.label}>Current Owner (You)</Text>
        <Text style={styles.infoText}>{currentOwnerId}</Text>
      </View>

      <View style={styles.formGroup}>
        <Text style={styles.label}>Recipient ID / Wallet Address</Text>
        <TextInput
          style={styles.input}
          value={recipientId}
          onChangeText={setRecipientId}
          placeholder="Enter Recipient's unique ID or wallet address"
          autoCapitalize="none"
        />
      </View>

      <View style={styles.formGroup}>
        <Text style={styles.label}>Quantity to Transfer</Text>
        <TextInput
          style={styles.input}
          value={quantity}
          onChangeText={setQuantity}
          placeholder="e.g., 10"
          keyboardType="numeric"
        />
      </View>

      <View style={styles.formGroup}>
        <Text style={styles.label}>Transfer Notes (Optional)</Text>
        <TextInput
          style={[styles.input, styles.textArea]}
          value={transferNotes}
          onChangeText={setTransferNotes}
          placeholder="e.g., Payment received, transfer for quality check"
          multiline
          numberOfLines={3}
        />
      </View>

      {/* Secure transfer protocols might involve:
          - Displaying a summary of the transfer for confirmation
          - Requiring biometric auth or a PIN
          - Two-factor authentication if applicable
          - QR code scanning for recipient ID
      */}
      <Text style={styles.securityNotice}>
        Ensure all details are correct before initiating the transfer. Transfers may be irreversible once confirmed on the blockchain or ledger.
      </Text>

      {loading ? (
        <ActivityIndicator size="large" color="#007BFF" style={styles.loader} />
      ) : (
        <Button
          title="Initiate Transfer"
          onPress={handleInitiateTransfer}
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
  contentContainer: {
    padding: 20,
  },
  title: {
    fontSize: 24,
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
    height: 80,
    textAlignVertical: 'top',
  },
  infoText: {
    fontSize: 16,
    color: '#6c757d',
    paddingVertical: 10,
    paddingHorizontal: 5,
  },
  securityNotice: {
    fontSize: 13,
    color: '#dc3545', // Warning color
    textAlign: 'center',
    marginVertical: 20,
    paddingHorizontal: 10,
    fontStyle: 'italic',
  },
  loader: {
    marginTop: 20,
  },
  // Add styles for Picker if used
});

export default TransferScreen;
