import React, { useState, useEffect, useCallback } from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity, ActivityIndicator, Alert, Image, TextInput } from 'react-native';
import Button from '../components/common/Button';

// --- Mock API for Mentorship ---
const mockMentorsData = [
  { id: 'mentor1', name: 'Dr. Alice GreenThumb', expertise: ['Organic Farming', 'Soil Health', 'Crop Rotation'], experience: '15+ years', availability: 'Mon, Wed (Evenings)', bio: 'Passionate agronomist with extensive experience in sustainable and organic farming practices. Happy to guide newcomers!', rating: 4.8, menteesCount: 12, profilePicUrl: 'https://i.pravatar.cc/100?u=mentor1', location: 'Green Valley, CA' },
  { id: 'mentor2', name: 'Bob \'The Builder\' FarmFix', expertise: ['Farm Equipment Maintenance', 'DIY Irrigation', 'Workshop Skills'], experience: '20 years as a farm mechanic', availability: 'Weekends', bio: 'If it\'s broken, I can fix it. Or teach you how! Specializing in practical farm solutions.', rating: 4.5, menteesCount: 8, profilePicUrl: 'https://i.pravatar.cc/100?u=mentor2', location: 'Rural Route 5, TX' },
  { id: 'mentor3', name: 'Chloë MarketWise', expertise: ['Agri-Business', 'Marketing Local Produce', 'Farmers Market Strategy'], experience: '10 years in agricultural marketing', availability: 'Flexible (Online)', bio: 'Helping farmers turn their hard work into profitable businesses. Let\'s talk strategy!', rating: 4.9, menteesCount: 20, profilePicUrl: 'https://i.pravatar.cc/100?u=mentor3', location: 'Online / Metro Area' },
  { id: 'mentor4', name: 'David LivestockPro', expertise: ['Dairy Management', 'Animal Husbandry', 'Sustainable Grazing'], experience: '25 years in livestock', availability: 'Tue, Thu (Afternoons)', bio: 'Experienced livestock manager focused on animal welfare and productivity.', rating: 4.7, menteesCount: 15, profilePicUrl: 'https://i.pravatar.cc/100?u=mentor4', location: 'Pastureland, WY' },
];

// Simulate user's mentorship connections
let mockUserMentorships = {
    // 'mentor1': { status: 'active', startDate: '2023-09-01' }, // Example: User is a mentee of Alice
    // 'userX_menteeOf_mentor2': { status: 'pending_mentor_approval'} // Example: User requested Bob
};
// Current user ID (for demo purposes)
const currentUserId = 'user_Mentee_101';


const mockFetchMentors = async (searchTerm = '', expertiseFilter = 'all') => {
  return new Promise(resolve => {
    setTimeout(() => {
      let results = mockMentorsData;
      if (searchTerm) {
        const lowerSearch = searchTerm.toLowerCase();
        results = results.filter(m =>
            m.name.toLowerCase().includes(lowerSearch) ||
            m.bio.toLowerCase().includes(lowerSearch) ||
            m.expertise.some(e => e.toLowerCase().includes(lowerSearch))
        );
      }
      if (expertiseFilter !== 'all') {
        results = results.filter(m => m.expertise.includes(expertiseFilter));
      }
      resolve(results.map(m => ({...m, isConnected: !!mockUserMentorships[`${currentUserId}_menteeOf_${m.id}`]})));
    }, 800);
  });
};

const mockRequestMentorship = async (mentorId, userId, message) => {
    return new Promise(resolve => {
        setTimeout(() => {
            console.log(`User ${userId} requested mentorship from ${mentorId} with message: "${message}"`);
            // Simulate request logic
            if (mockUserMentorships[`${userId}_menteeOf_${mentorId}`]) {
                resolve({success: false, message: "You already have a pending or active mentorship with this mentor."});
                return;
            }
            mockUserMentorships[`${userId}_menteeOf_${mentorId}`] = { status: 'pending_mentor_approval', requestDate: new Date().toISOString(), message };
            resolve({success: true, message: "Mentorship request sent successfully!"});
        }, 1000);
    });
};

// Get unique expertise areas for filter
const allExpertiseAreas = ['all', ...new Set(mockMentorsData.flatMap(m => m.expertise))];
// --- End Mock API ---


const MentorshipScreen = ({ navigation }) => {
  const [mentors, setMentors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedExpertise, setSelectedExpertise] = useState('all');

  // For request modal (simplified with Alert.prompt for now)
  // const [isModalVisible, setIsModalVisible] = useState(false);
  // const [selectedMentorForRequest, setSelectedMentorForRequest] = useState(null);
  // const [requestMessage, setRequestMessage] = useState('');


  const loadMentors = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await mockFetchMentors(searchTerm, selectedExpertise);
      setMentors(data);
    } catch (e) {
      setError('Failed to load mentors.');
      console.error(e);
    } finally {
      setLoading(false);
    }
  }, [searchTerm, selectedExpertise]);

  useEffect(() => {
    loadMentors();
  }, [loadMentors]);

  const handleRequestMentorship = (mentor) => {
    Alert.prompt(
        `Request Mentorship with ${mentor.name}`,
        "Send a short message with your request (optional):",
        [
            { text: "Cancel", style: "cancel" },
            {
                text: "Send Request",
                onPress: async (message) => {
                    setLoading(true); // Indicate processing
                    try {
                        const result = await mockRequestMentorship(mentor.id, currentUserId, message || '');
                        if (result.success) {
                            Alert.alert("Success", result.message);
                            loadMentors(); // Refresh list to show pending status (if UI supports it)
                        } else {
                            Alert.alert("Request Failed", result.message || "Could not send request.");
                        }
                    } catch (reqError) {
                        Alert.alert("Error", reqError.message || "An error occurred.");
                    } finally {
                        setLoading(false);
                    }
                },
            },
        ],
        'plain-text', // Dialog type
        '', // Default value for message input
        'default' // Keyboard type
    );
  };


  const renderMentorCard = ({ item }) => (
    <View style={styles.mentorCard}>
      <Image source={{ uri: item.profilePicUrl }} style={styles.profilePic} />
      <View style={styles.mentorInfo}>
        <Text style={styles.mentorName}>{item.name}</Text>
        <Text style={styles.mentorLocation}>{item.location}</Text>
        <Text style={styles.expertiseTitle}>Expertise:</Text>
        <View style={styles.expertiseTags}>
            {item.expertise.map(exp => <Text key={exp} style={styles.tag}>{exp}</Text>)}
        </View>
        <Text style={styles.mentorBio} numberOfLines={3}>{item.bio}</Text>
        <View style={styles.mentorStats}>
            <Text style={styles.statText}>Rating: {item.rating}/5</Text>
            <Text style={styles.statText}>Mentees: {item.menteesCount}</Text>
        </View>
         {/* Simplified connection status display */}
        {mockUserMentorships[`${currentUserId}_menteeOf_${item.id}`] ? (
            <Text style={styles.statusText}>
                Status: {mockUserMentorships[`${currentUserId}_menteeOf_${item.id}`].status.replace('_', ' ')}
            </Text>
        ) : (
            <Button
                title="Request Mentorship"
                onPress={() => handleRequestMentorship(item)}
                style={styles.requestButton}
            />
        )}
      </View>
    </View>
  );

  const FilterControls = () => (
    <View style={styles.filterContainer}>
        <TextInput
            style={styles.searchInput}
            placeholder="Search mentors by name, expertise, bio..."
            value={searchTerm}
            onChangeText={setSearchTerm}
        />
        <Text style={styles.filterLabel}>Filter by Expertise:</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.expertiseFilterScroll}>
            {allExpertiseAreas.map(exp => (
                <TouchableOpacity
                    key={exp}
                    style={[styles.filterChip, selectedExpertise === exp && styles.filterChipActive]}
                    onPress={() => setSelectedExpertise(exp)}
                >
                    <Text style={[styles.filterChipText, selectedExpertise === exp && styles.filterChipTextActive]}>{exp}</Text>
                </TouchableOpacity>
            ))}
        </ScrollView>
    </View>
  );

  if (loading && mentors.length === 0) {
    return <View style={styles.centered}><ActivityIndicator size="large" /><Text>Finding Mentors...</Text></View>;
  }

  return (
    <View style={styles.container}>
      <Text style={styles.screenTitle}>Find a Mentor</Text>
      <Text style={styles.screenSubtitle}>Connect with experienced farmers and agricultural experts.</Text>

      <FilterControls />

      {loading && mentors.length > 0 && <ActivityIndicator style={{marginVertical:10}}/>}

      {error && (
        <View style={styles.centered}><Text style={styles.errorText}>{error}</Text><Button title="Retry" onPress={loadMentors}/></View>
      )}

      {!loading && mentors.length === 0 && !error && (
          <View style={styles.centered}><Text>No mentors found matching your criteria. Try adjusting filters.</Text></View>
      )}

      {!error && mentors.length > 0 && (
        <FlatList
          data={mentors}
          renderItem={renderMentorCard}
          keyExtractor={item => item.id}
          contentContainerStyle={styles.listContent}
        />
      )}
      {/* Button for users to apply to BECOME a mentor */}
      {/* <Button title="Become a Mentor" onPress={() => navigation.navigate('BecomeMentorScreen')} style={styles.becomeMentorButton} /> */}
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f4f6f8' },
  centered: { flex: 1, justifyContent: 'center', alignItems: 'center', padding: 20 },
  errorText: { color: 'red', fontSize: 16, textAlign: 'center', marginBottom:10, },
  screenTitle: { fontSize: 22, fontWeight: 'bold', color: '#2c3e50', textAlign: 'center', paddingTop: 20, },
  screenSubtitle: { fontSize: 15, color: '#7f8c8d', textAlign: 'center', marginBottom: 15, paddingBottom:15, borderBottomWidth:1, borderBottomColor:'#e0e6ed', marginHorizontal:10,},
  filterContainer: { paddingHorizontal: 15, paddingBottom: 10, backgroundColor: '#fff', marginBottom:5,},
  searchInput: { height: 45, backgroundColor: '#f0f2f5', borderRadius: 8, paddingHorizontal: 15, fontSize: 15, marginBottom: 10, borderWidth:1, borderColor:'#dfe4ea'},
  filterLabel: { fontSize: 14, fontWeight: '500', marginBottom: 8, color: '#495057' },
  expertiseFilterScroll: { paddingBottom: 5 },
  filterChip: { paddingVertical: 8, paddingHorizontal: 12, borderRadius: 20, backgroundColor: '#e9ecef', marginRight: 8, borderWidth:1, borderColor:'#ced4da' },
  filterChipActive: { backgroundColor: '#007bff', borderColor:'#0056b3'},
  filterChipText: { fontSize: 13, color: '#007bff' },
  filterChipTextActive: { color: 'white' },
  listContent: { paddingHorizontal: 15, paddingBottom: 20 },
  mentorCard: {
    backgroundColor: '#fff',
    borderRadius: 10,
    padding: 15,
    marginBottom: 15,
    flexDirection: 'row',
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 3,
  },
  profilePic: { width: 80, height: 80, borderRadius: 40, marginRight: 15, borderWidth:2, borderColor:'#007bff' },
  mentorInfo: { flex: 1 },
  mentorName: { fontSize: 18, fontWeight: 'bold', color: '#34495e', marginBottom:2, },
  mentorLocation: { fontSize: 12, color: '#7f8c8d', marginBottom: 5, fontStyle:'italic' },
  expertiseTitle: { fontSize: 13, fontWeight: '600', color: '#555', marginTop:4, marginBottom:3,},
  expertiseTags: { flexDirection: 'row', flexWrap: 'wrap', marginBottom: 6, },
  tag: { backgroundColor: '#007bff', color: 'white', paddingHorizontal: 8, paddingVertical: 3, borderRadius: 10, fontSize: 10, marginRight: 5, marginBottom: 5, fontWeight:'500', overflow:'hidden'},
  mentorBio: { fontSize: 13, color: '#606770', lineHeight: 18, marginBottom: 8, },
  mentorStats: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 10, borderTopWidth:1, borderTopColor:'#f0f2f5', paddingTop:8, },
  statText: { fontSize: 12, color: '#52616b' },
  requestButton: { backgroundColor: '#28a745', paddingVertical: 8, }, // Green for request
  statusText: { fontSize: 13, fontWeight: 'bold', color: '#ffae42', fontStyle:'italic', marginTop: 5, textAlign:'center', padding:5, backgroundColor:'#fff3e0', borderRadius:4 }, // Orange for pending/active
  // becomeMentorButton: { margin:15, backgroundColor:'#546e7a' } // Dark grey-blue
});

export default MentorshipScreen;
