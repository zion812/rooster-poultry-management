import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, FlatList, ActivityIndicator, TouchableOpacity, Alert } from 'react-native';
import Button from '../components/common/Button';
import { formatDate } from '../utils/helpers';
// import { Picker } from '@react-native-picker/picker'; // For filtering by type/status

// Mock API for compliance data
const mockFetchComplianceRequirements = async (entityId, entityType = 'product') => {
  // entityType could be 'product', 'farm', 'process', 'shipment'
  return new Promise((resolve) => {
    setTimeout(() => {
      const allRequirements = [
        // Product-specific
        { id: 'COMP001', entityId: 'PROD_XYZ_789', entityType: 'product', name: 'Pesticide Residue Limits (EU MRLs)', status: 'compliant', lastChecked: '2023-11-01', nextCheckDue: '2024-05-01', details: 'Complies with EU Regulation (EC) No 396/2005.', authority: 'EFSA', documents: [{ name: 'LabTest_Nov23.pdf', url: '#'}] },
        { id: 'COMP002', entityId: 'PROD_XYZ_789', entityType: 'product', name: 'Heavy Metals Contamination (FDA)', status: 'pending_check', lastChecked: '2023-08-15', nextCheckDue: '2023-11-20', details: 'Scheduled for testing by LabCorp.', authority: 'FDA', documents: [] },
        { id: 'COMP003', entityId: 'PROD_ABC_123', entityType: 'product', name: 'Labeling Accuracy (CPSC)', status: 'non_compliant', lastChecked: '2023-10-10', nextCheckDue: 'N/A - Corrective Action Required', details: 'Incorrect net weight displayed. Corrective action issued.', authority: 'CPSC', documents: [{name: 'NonCompliance_Report_Oct10.pdf', url:'#'}], alertLevel: 'high' },
        // Farm-specific
        { id: 'COMP004', entityId: 'FARM_A1', entityType: 'farm', name: 'Water Usage Permit', status: 'compliant', lastChecked: '2023-06-30', nextCheckDue: '2024-06-30', details: 'Permit #WU7890 valid.', authority: 'Local Water Board', documents: [] },
        { id: 'COMP005', entityId: 'FARM_A1', entityType: 'farm', name: 'Organic Farming Practices Audit (USDA)', status: 'compliant', lastChecked: '2023-09-01', nextCheckDue: '2024-09-01', details: 'Passed annual organic audit.', authority: 'USDA NOP', documents: [{name: 'OrganicCert_2023.pdf', url:'#'}]},
        // Process-specific (e.g., for a processing facility)
        { id: 'COMP006', entityId: 'PROCESS_UNIT_B2', entityType: 'process', name: 'HACCP Plan Implementation', status: 'requires_review', lastChecked: '2023-07-01', nextCheckDue: '2023-12-01', details: 'Annual review of HACCP plan due.', authority: 'Internal QA', documents: [], alertLevel: 'medium'},
      ];
      resolve(allRequirements.filter(req => req.entityId === entityId && req.entityType === entityType));
    }, 1200);
  });
};

// Mock function to update a compliance status (e.g., after a check)
const mockUpdateComplianceStatus = async (requirementId, newStatus, notes) => {
    return new Promise((resolve) => {
        setTimeout(() => {
            console.log(`Updating compliance for ${requirementId} to ${newStatus} with notes: ${notes}`);
            resolve({success: true, message: `Compliance status for ${requirementId} updated to ${newStatus}.`});
        }, 1000);
    });
};


const ComplianceTrackingScreen = ({ route }) => {
  // const { entityId, entityType } = route.params; // e.g., { entityId: 'PROD_XYZ_789', entityType: 'product' }
  const entityId = route?.params?.entityId || 'PROD_XYZ_789';
  const entityType = route?.params?.entityType || 'product';

  const [requirements, setRequirements] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Filters (example, could be expanded with Picker)
  const [filterStatus, setFilterStatus] = useState('all'); // 'all', 'compliant', 'non_compliant', 'pending_check'

  const loadComplianceData = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await mockFetchComplianceRequirements(entityId, entityType);
      setRequirements(data);
      if (data.length === 0) {
          setError(`No compliance requirements found for ${entityType} ID: ${entityId}.`);
      }
    } catch (e) {
      setError(e.message || 'Failed to load compliance data.');
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadComplianceData();
  }, [entityId, entityType]);

  const handleUpdateStatus = (reqId) => {
      // In a real app, this would open a modal or navigate to a form to update status, add notes, upload documents etc.
      Alert.prompt(
          "Update Compliance Status",
          `Enter new status for ${reqId} (e.g., compliant, non_compliant, pending_check) and optional notes:`,
          [
            { text: "Cancel", style: "cancel" },
            {
              text: "Submit",
              onPress: async (text) => { // text will be an array [status, notes] if multiple inputs in Alert.prompt (iOS only for multiple)
                if (text) { // For simplicity, assuming single input for status
                    const newStatus = text; // Or parse if combined
                    const notes = "Updated via quick action."; // Placeholder
                    Alert.alert("Updating...", `Setting ${reqId} to ${newStatus}`);
                    try {
                        await mockUpdateComplianceStatus(reqId, newStatus, notes);
                        Alert.alert("Success", "Status updated.");
                        loadComplianceData(); // Refresh data
                    } catch (updateError) {
                        Alert.alert("Error", `Failed to update: ${updateError.message}`);
                    }
                }
              },
            },
          ],
          'plain-text', // Dialog type
          '', // Default value
          'default' // Keyboard type
      );
  };

  const filteredRequirements = requirements.filter(req => {
    if (filterStatus === 'all') return true;
    return req.status === filterStatus;
  });


  const renderRequirementItem = ({ item }) => (
    <View style={[styles.itemCard, styles[`status_${item.status}`], item.alertLevel && styles[`alert_${item.alertLevel}`]]}>
      <Text style={styles.itemName}>{item.name}</Text>
      <View style={styles.itemRow}>
        <Text style={styles.itemLabel}>Status:</Text>
        <Text style={[styles.itemValue, styles[`statusText_${item.status}`]]}>{item.status.replace(/_/g, ' ').toUpperCase()}</Text>
      </View>
      <View style={styles.itemRow}>
        <Text style={styles.itemLabel}>Authority:</Text>
        <Text style={styles.itemValue}>{item.authority}</Text>
      </View>
      <View style={styles.itemRow}>
        <Text style={styles.itemLabel}>Last Checked:</Text>
        <Text style={styles.itemValue}>{formatDate(item.lastChecked)}</Text>
      </View>
      <View style={styles.itemRow}>
        <Text style={styles.itemLabel}>Next Check Due:</Text>
        <Text style={styles.itemValue}>{item.nextCheckDue.startsWith('N/A') ? item.nextCheckDue : formatDate(item.nextCheckDue)}</Text>
      </View>
      <Text style={styles.itemDetailsTitle}>Details:</Text>
      <Text style={styles.itemDetailsText}>{item.details}</Text>
      {item.documents && item.documents.length > 0 && (
        <View>
          <Text style={styles.itemDetailsTitle}>Documents:</Text>
          {item.documents.map((doc, index) => (
            <TouchableOpacity key={index} onPress={() => Alert.alert("Open Document", `Would open: ${doc.name} from ${doc.url}`)}>
              <Text style={styles.documentLink}>{doc.name}</Text>
            </TouchableOpacity>
          ))}
        </View>
      )}
      <Button title="Update Status" onPress={() => handleUpdateStatus(item.id)} style={styles.updateButton} textStyle={styles.updateButtonText} />
    </View>
  );

  // Basic filter buttons UI
  const renderFilterControls = () => (
    <View style={styles.filterContainer}>
        <Text style={styles.filterLabel}>Filter by Status:</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.filterButtonsScroll}>
            {['all', 'compliant', 'non_compliant', 'pending_check', 'requires_review'].map(statusKey => (
                 <TouchableOpacity
                    key={statusKey}
                    style={[styles.filterButton, filterStatus === statusKey && styles.filterButtonActive]}
                    onPress={() => setFilterStatus(statusKey)}>
                    <Text style={[styles.filterButtonText, filterStatus === statusKey && styles.filterButtonTextActive]}>
                        {statusKey.replace(/_/g, ' ').toUpperCase()}
                    </Text>
                 </TouchableOpacity>
            ))}
        </ScrollView>
    </View>
  );


  if (loading) {
    return <View style={styles.centered}><ActivityIndicator size="large" /><Text>Loading Compliance Data...</Text></View>;
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Compliance Tracking</Text>
      <Text style={styles.subTitle}>For {entityType}: {entityId}</Text>

      {renderFilterControls()}

      {error && !loading && requirements.length === 0 && ( // Show error if loading failed and no data
        <View style={styles.centered}><Text style={styles.errorText}>{error}</Text></View>
      )}

      {filteredRequirements.length === 0 && !loading && !error && (
          <View style={styles.centered}><Text>No requirements match the current filter.</Text></View>
      )}

      {filteredRequirements.length > 0 && (
        <FlatList
          data={filteredRequirements}
          renderItem={renderRequirementItem}
          keyExtractor={item => item.id}
          contentContainerStyle={styles.listContent}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#eef2f5', // Light grey-blue background
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
    color: '#2c3e50', // Dark blue
    paddingTop: 20,
    paddingBottom: 5,
    backgroundColor: 'white',
  },
  subTitle: {
      fontSize: 14,
      textAlign: 'center',
      color: 'grey',
      paddingBottom: 15,
      backgroundColor: 'white',
      borderBottomWidth:1,
      borderBottomColor:'#ddd'
  },
  filterContainer: {
    paddingVertical: 10,
    paddingHorizontal:15,
    backgroundColor: '#f8f9fa', // Off-white for filter bar
    borderBottomWidth: 1,
    borderBottomColor: '#dee2e6',
  },
  filterLabel: {
      fontSize: 14,
      fontWeight: '500',
      color: '#495057',
      marginBottom: 8,
  },
  filterButtonsScroll: {
      paddingRight: 15, // Ensure last button is not cut off
  },
  filterButton: {
    paddingVertical: 8,
    paddingHorizontal: 12,
    borderRadius: 15,
    backgroundColor: '#e9ecef', // Light grey button
    marginRight: 8,
    borderWidth:1,
    borderColor: '#ced4da'
  },
  filterButtonActive: {
    backgroundColor: '#007bff', // Primary blue for active
    borderColor: '#0056b3',
  },
  filterButtonText: {
    color: '#007bff',
    fontSize: 13,
    fontWeight: '500',
  },
  filterButtonTextActive: {
      color: 'white',
  },
  listContent: {
    padding: 15,
  },
  itemCard: {
    backgroundColor: '#fff',
    borderRadius: 8,
    padding: 15,
    marginBottom: 15,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2, },
    shadowOpacity: 0.08,
    shadowRadius: 4,
    elevation: 3,
    borderLeftWidth: 5,
    borderLeftColor: '#6c757d', // Default border color (grey)
  },
  // Status-specific border colors
  status_compliant: { borderLeftColor: '#28a745' /* Green */ },
  status_non_compliant: { borderLeftColor: '#dc3545' /* Red */ },
  status_pending_check: { borderLeftColor: '#ffc107' /* Yellow */ },
  status_requires_review: { borderLeftColor: '#17a2b8' /* Teal */ },
  // Alert level backgrounds (subtle)
  alert_high: { backgroundColor: '#fff3f3' /* Very light red */ },
  alert_medium: { backgroundColor: '#fff9e6' /* Very light yellow */},

  itemName: {
    fontSize: 17,
    fontWeight: '600',
    color: '#343a40', // Darker grey
    marginBottom: 10,
  },
  itemRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 5,
  },
  itemLabel: {
    fontSize: 14,
    color: '#6c757d', // Medium grey
    fontWeight:'500',
  },
  itemValue: {
    fontSize: 14,
    color: '#495057', // Dark grey
    textAlign: 'right',
  },
  // Status-specific text colors
  statusText_compliant: { color: '#28a745', fontWeight: 'bold' },
  statusText_non_compliant: { color: '#dc3545', fontWeight: 'bold' },
  statusText_pending_check: { color: '#b58900', fontWeight: 'bold' }, // Darker yellow for text
  statusText_requires_review: { color: '#17a2b8', fontWeight: 'bold' },

  itemDetailsTitle: {
      fontSize: 14,
      fontWeight: '600',
      color: '#495057',
      marginTop: 10,
      marginBottom: 3,
  },
  itemDetailsText: {
      fontSize: 13,
      color: '#6c757d',
      lineHeight: 18,
  },
  documentLink: {
    color: '#007bff', // Primary blue
    fontSize: 13,
    textDecorationLine: 'underline',
    marginTop:3,
  },
  updateButton: {
      marginTop: 15,
      backgroundColor: '#6c757d', // Grey button
      paddingVertical: 8,
  },
  updateButtonText: {
      fontSize: 14,
  },
  errorText: {
    color: 'red',
    fontSize: 16,
    textAlign: 'center',
  },
});

export default ComplianceTrackingScreen;
