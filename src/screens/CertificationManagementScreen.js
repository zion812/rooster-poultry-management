import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, FlatList, ActivityIndicator, TouchableOpacity, Alert, Modal, TextInput } from 'react-native';
import Button from '../components/common/Button';
import { formatDate } from '../utils/helpers';
// import DocumentPicker from 'react-native-document-picker'; // For file uploads

// Mock API functions
const mockFetchCertificates = (entityId, entityType = 'product') => { // entityType could be 'product', 'farm', 'farmer'
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve([
        { id: 'cert1', name: 'Organic Certification', issuer: 'EcoCert Group', issueDate: '2023-01-15', expiryDate: '2024-01-14', status: 'active', documentUrl: 'http://example.com/cert1.pdf', entityId: 'sampleProductId' },
        { id: 'cert2', name: 'Fair Trade Certificate', issuer: 'Fair Trade International', issueDate: '2022-06-01', expiryDate: '2023-05-31', status: 'expired', documentUrl: 'http://example.com/cert2.pdf', entityId: 'sampleProductId' },
        { id: 'cert3', name: 'GlobalG.A.P.', issuer: 'GLOBALG.A.P.', issueDate: '2023-03-10', expiryDate: '2024-03-09', status: 'active', documentUrl: 'http://example.com/cert3.pdf', entityId: 'sampleFarmId' },
      ].filter(cert => cert.entityId === entityId || (entityType === 'all'))); // Simple filter for example
    }, 1000);
  });
};

const mockAddCertificate = (certificateData) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      console.log('Adding certificate:', certificateData);
      resolve({ success: true, message: 'Certificate added successfully.', certificate: { ...certificateData, id: `cert${Date.now()}` } });
    }, 1500);
  });
};

const mockUpdateCertificate = (certificateId, updateData) => {
   return new Promise((resolve) => {
    setTimeout(() => {
      console.log('Updating certificate:', certificateId, updateData);
      resolve({ success: true, message: 'Certificate updated successfully.' });
    }, 1000);
  });
};

const mockDeleteCertificate = (certificateId) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      console.log('Deleting certificate:', certificateId);
      resolve({ success: true, message: 'Certificate deleted successfully.' });
    }, 1000);
  });
};


const CertificationManagementScreen = ({ route }) => {
  // const { entityId, entityType } = route.params; // e.g., productId, farmId
  const entityId = 'sampleProductId'; // Default for now
  const entityType = 'product'; // Default for now

  const [certificates, setCertificates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isModalVisible, setModalVisible] = useState(false);
  const [editingCertificate, setEditingCertificate] = useState(null); // null for new, object for editing

  // Form state for modal
  const [certName, setCertName] = useState('');
  const [certIssuer, setCertIssuer] = useState('');
  const [certIssueDate, setCertIssueDate] = useState(''); // Consider using a date picker
  const [certExpiryDate, setCertExpiryDate] = useState(''); // Consider using a date picker
  const [certDocumentUrl, setCertDocumentUrl] = useState(''); // Or manage file upload state

  const loadCertificates = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await mockFetchCertificates(entityId, entityType);
      setCertificates(data);
    } catch (e) {
      setError('Failed to load certificates.');
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCertificates();
  }, [entityId, entityType]);

  const openAddModal = () => {
    setEditingCertificate(null);
    setCertName('');
    setCertIssuer('');
    setCertIssueDate('');
    setCertExpiryDate('');
    setCertDocumentUrl('');
    setModalVisible(true);
  };

  const openEditModal = (certificate) => {
    setEditingCertificate(certificate);
    setCertName(certificate.name);
    setCertIssuer(certificate.issuer);
    setCertIssueDate(certificate.issueDate); // Format for input if necessary
    setCertExpiryDate(certificate.expiryDate); // Format for input if necessary
    setCertDocumentUrl(certificate.documentUrl || '');
    setModalVisible(true);
  };

  const handleSaveCertificate = async () => {
    if (!certName.trim() || !certIssuer.trim() || !certIssueDate.trim() || !certExpiryDate.trim()) {
      Alert.alert('Error', 'Please fill in all required fields (Name, Issuer, Issue Date, Expiry Date).');
      return;
    }

    const certificateData = {
      name: certName,
      issuer: certIssuer,
      issueDate: certIssueDate, // Ensure this is in a consistent format, e.g., YYYY-MM-DD
      expiryDate: certExpiryDate, // Ensure this is in a consistent format
      documentUrl: certDocumentUrl,
      entityId: entityId, // Associate with the current entity
      status: new Date(certExpiryDate) > new Date() ? 'active' : 'expired', // Auto-determine status
    };

    try {
      if (editingCertificate) {
        await mockUpdateCertificate(editingCertificate.id, certificateData);
        Alert.alert('Success', 'Certificate updated.');
      } else {
        await mockAddCertificate(certificateData);
        Alert.alert('Success', 'Certificate added.');
      }
      setModalVisible(false);
      loadCertificates(); // Refresh list
    } catch (e) {
      Alert.alert('Error', `Failed to save certificate: ${e.message}`);
    }
  };

  const handleDeleteCertificate = (certificateId) => {
    Alert.alert(
      "Confirm Delete",
      "Are you sure you want to delete this certificate?",
      [
        { text: "Cancel", style: "cancel" },
        { text: "Delete", style: "destructive", onPress: async () => {
            try {
              await mockDeleteCertificate(certificateId);
              Alert.alert('Success', 'Certificate deleted.');
              loadCertificates(); // Refresh list
            } catch (e) {
              Alert.alert('Error', `Failed to delete certificate: ${e.message}`);
            }
          }
        }
      ]
    );
  };

  // const handlePickDocument = async () => {
  //   try {
  //     const res = await DocumentPicker.pickSingle({
  //       type: [DocumentPicker.types.pdf, DocumentPicker.types.images],
  //     });
  //     console.log('Picked document:', res.uri, res.type, res.name, res.size);
  //     // In a real app, you would upload this file and get a URL
  //     setCertDocumentUrl(res.uri); // Placeholder, use actual URL after upload
  //     Alert.alert('Document Selected', `Selected: ${res.name}. Remember to upload it.`);
  //   } catch (err) {
  //     if (DocumentPicker.isCancel(err)) {
  //       // User cancelled the picker
  //     } else {
  //       Alert.alert('Error', 'Could not pick document.');
  //       console.error(err);
  //     }
  //   }
  // };


  const renderCertificateItem = ({ item }) => (
    <View style={[styles.certItem, item.status === 'expired' && styles.expiredCert]}>
      <View style={styles.certHeader}>
        <Text style={styles.certName}>{item.name}</Text>
        <Text style={[styles.certStatus, item.status === 'active' ? styles.statusActive : styles.statusExpired]}>
          {item.status.toUpperCase()}
        </Text>
      </View>
      <Text style={styles.certDetail}>Issuer: {item.issuer}</Text>
      <Text style={styles.certDetail}>Issued: {formatDate(item.issueDate)}</Text>
      <Text style={styles.certDetail}>Expires: {formatDate(item.expiryDate)}</Text>
      {item.documentUrl && (
        <TouchableOpacity onPress={() => Alert.alert("Open Document", `Would open: ${item.documentUrl}`)}>
          <Text style={styles.documentLink}>View Document</Text>
        </TouchableOpacity>
      )}
      <View style={styles.actionsContainer}>
        <Button title="Edit" onPress={() => openEditModal(item)} style={styles.actionButton} textStyle={styles.actionButtonText} />
        <Button title="Delete" onPress={() => handleDeleteCertificate(item.id)} style={[styles.actionButton, styles.deleteButton]} textStyle={styles.actionButtonText} />
      </View>
    </View>
  );

  if (loading) {
    return <View style={styles.centered}><ActivityIndicator size="large" /><Text>Loading Certificates...</Text></View>;
  }

  if (error) {
    return <View style={styles.centered}><Text style={styles.errorText}>{error}</Text><Button title="Retry" onPress={loadCertificates} /></View>;
  }

  return (
    <View style={styles.container}>
      <View style={styles.headerContainer}>
        <Text style={styles.title}>Certification Management</Text>
        <Text style={styles.subTitle}>For: {entityType} - {entityId}</Text>
      </View>
      <Button title="Add New Certificate" onPress={openAddModal} style={styles.addButton} />

      {certificates.length === 0 ? (
        <Text style={styles.noCertsText}>No certificates found for this entity.</Text>
      ) : (
        <FlatList
          data={certificates}
          renderItem={renderCertificateItem}
          keyExtractor={item => item.id}
          contentContainerStyle={styles.listContent}
        />
      )}

      <Modal
        animationType="slide"
        transparent={true}
        visible={isModalVisible}
        onRequestClose={() => setModalVisible(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <Text style={styles.modalTitle}>{editingCertificate ? 'Edit Certificate' : 'Add New Certificate'}</Text>
            <TextInput style={styles.input} placeholder="Certificate Name" value={certName} onChangeText={setCertName} />
            <TextInput style={styles.input} placeholder="Issuer" value={certIssuer} onChangeText={setCertIssuer} />
            <TextInput style={styles.input} placeholder="Issue Date (YYYY-MM-DD)" value={certIssueDate} onChangeText={setCertIssueDate} />
            <TextInput style={styles.input} placeholder="Expiry Date (YYYY-MM-DD)" value={certExpiryDate} onChangeText={setCertExpiryDate} />
            {/* <Button title="Select Document" onPress={handlePickDocument} /> */}
            <TextInput style={styles.input} placeholder="Document URL (or leave blank)" value={certDocumentUrl} onChangeText={setCertDocumentUrl} />

            <View style={styles.modalActions}>
              <Button title="Cancel" onPress={() => setModalVisible(false)} style={[styles.modalButton, styles.cancelButton]} />
              <Button title="Save" onPress={handleSaveCertificate} style={styles.modalButton} />
            </View>
          </View>
        </View>
      </Modal>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f0f4f7',
  },
  headerContainer: {
    padding: 16,
    backgroundColor: 'white',
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    textAlign: 'center',
    color: '#2c3e50',
  },
  subTitle: {
    fontSize: 14,
    textAlign: 'center',
    color: 'gray',
    marginTop: 4,
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  addButton: {
    margin: 16,
  },
  certItem: {
    backgroundColor: '#fff',
    padding: 15,
    marginHorizontal: 16,
    marginBottom: 12,
    borderRadius: 8,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2, },
    shadowOpacity: 0.1,
    shadowRadius: 3.84,
    elevation: 3,
  },
  expiredCert: {
    backgroundColor: '#ffebee', // Light red for expired
    borderColor: '#ef9a9a',
    borderLeftWidth: 4,
    borderLeftColor: '#d32f2f',
  },
  certHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  certName: {
    fontSize: 17,
    fontWeight: '600',
    color: '#34495e',
    flex: 1,
  },
  certStatus: {
    fontSize: 12,
    fontWeight: 'bold',
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 10,
    overflow: 'hidden', // for borderRadius on Text on Android
  },
  statusActive: {
    backgroundColor: '#e0f2f1', // Light teal
    color: '#00796b', // Dark teal
  },
  statusExpired: {
    backgroundColor: '#ffebee', // Light red
    color: '#c62828', // Dark red
  },
  certDetail: {
    fontSize: 14,
    color: '#7f8c8d',
    marginBottom: 3,
  },
  documentLink: {
    color: '#3498db',
    textDecorationLine: 'underline',
    marginTop: 5,
  },
  actionsContainer: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    marginTop: 10,
    paddingTop: 10,
    borderTopWidth: 1,
    borderColor: '#ecf0f1',
  },
  actionButton: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    marginLeft: 8,
    borderRadius: 4,
  },
  actionButtonText: {
    fontSize: 14,
  },
  deleteButton: {
    backgroundColor: '#e74c3c', // Red for delete
  },
  listContent: {
    paddingBottom: 16,
  },
  noCertsText: {
    textAlign: 'center',
    marginTop: 30,
    fontSize: 16,
    color: 'grey',
  },
  errorText: {
    color: 'red',
    fontSize: 16,
  },
  // Modal styles
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.5)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalContent: {
    backgroundColor: 'white',
    padding: 20,
    borderRadius: 10,
    width: '90%',
    maxHeight: '80%',
  },
  modalTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 15,
    textAlign: 'center',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ddd',
    padding: 10,
    borderRadius: 5,
    marginBottom: 10,
    fontSize: 16,
  },
  modalActions: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginTop: 20,
  },
  modalButton: {
    flex: 1, // Make buttons take equal space
    marginHorizontal: 5,
  },
  cancelButton: {
    backgroundColor: '#7f8c8d', // Grey for cancel
  }
});

export default CertificationManagementScreen;
