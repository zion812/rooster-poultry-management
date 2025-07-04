import React, { useState } from 'react';
import { View, Text, TextInput, StyleSheet, Alert } from 'react-native';
import QRCode from 'react-native-qrcode-svg';
import Button from '../components/common/Button';

const QRCodeGeneratorScreen = () => {
  const [productId, setProductId] = useState('');
  const [generatedQRData, setGeneratedQRData] = useState(null);

  const handleGenerateQR = () => {
    if (!productId.trim()) {
      Alert.alert('Error', 'Please enter a Product ID.');
      return;
    }
    // In a real app, you might want to generate a more complex data structure
    // or a URL that points to the product's details.
    const qrData = JSON.stringify({
      productId: productId.trim(),
      timestamp: new Date().toISOString(),
    });
    setGeneratedQRData(qrData);
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>QR Code Generator</Text>

      <TextInput
        style={styles.input}
        placeholder="Enter Product ID"
        value={productId}
        onChangeText={setProductId}
        autoCapitalize="none"
      />

      <Button title="Generate QR Code" onPress={handleGenerateQR} />

      {generatedQRData && (
        <View style={styles.qrContainer}>
          <Text style={styles.qrLabel}>Generated QR Code for: {productId}</Text>
          <QRCode
            value={generatedQRData}
            size={200}
            backgroundColor="white"
            color="black"
          />
          <Text style={styles.qrDataText}>Data: {generatedQRData}</Text>
          {/* Add options to save or share the QR code */}
        </View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    alignItems: 'center',
    backgroundColor: '#f5f5f5',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  input: {
    width: '100%',
    height: 40,
    borderColor: 'gray',
    borderWidth: 1,
    borderRadius: 5,
    paddingHorizontal: 10,
    marginBottom: 20,
    backgroundColor: 'white',
  },
  qrContainer: {
    marginTop: 30,
    alignItems: 'center',
    padding: 20,
    backgroundColor: 'white',
    borderRadius: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
    elevation: 5,
  },
  qrLabel: {
    fontSize: 16,
    marginBottom: 10,
  },
  qrDataText: {
    marginTop: 10,
    fontSize: 12,
    color: 'grey',
    textAlign: 'center',
  }
});

export default QRCodeGeneratorScreen;
