import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, FlatList, ActivityIndicator, TouchableOpacity, Image, Alert, ScrollView } from 'react-native';
import Button from '../components/common/Button';

// --- Mock API for Community Groups ---
const mockAllGroupsData = [
  { id: 'group1', name: 'Organic Tomato Growers', description: 'Share tips and tricks for growing organic tomatoes.', memberCount: 125, type: 'public', coverImageUrl: 'https://picsum.photos/seed/tomato/300/150', tags: ['organic', 'tomatoes', 'horticulture'] },
  { id: 'group2', name: 'Sustainable Livestock Farming', description: 'Discussing ethical and sustainable livestock practices.', memberCount: 88, type: 'private', coverImageUrl: 'https://picsum.photos/seed/livestock/300/150', tags: ['livestock', 'sustainable', 'ethics'] },
  { id: 'group3', name: 'Local Farmers Market Connect (Region X)', description: 'Coordinating for the Region X farmers market.', memberCount: 45, type: 'closed', coverImageUrl: 'https://picsum.photos/seed/market/300/150', tags: ['local', 'market', 'region_x'] },
  { id: 'group4', name: 'Precision Agriculture Innovators', description: 'Exploring new technologies in precision ag.', memberCount: 210, type: 'public', coverImageUrl: 'https://picsum.photos/seed/precisionag/300/150', tags: ['technology', 'precision_ag', 'iot'] },
  { id: 'group5', name: 'Beginner Beekeepers Hub', description: 'A friendly space for new beekeepers to learn and share.', memberCount: 62, type: 'public', coverImageUrl: 'https://picsum.photos/seed/bees/300/150', tags: ['beekeeping', 'beginners', 'pollinators'] },
];

// Simulate user's joined groups (subset of all groups)
let mockUserJoinedGroupIds = ['group1', 'group4'];

const mockFetchAllGroups = async (searchTerm = '') => {
  return new Promise(resolve => {
    setTimeout(() => {
      const filtered = mockAllGroupsData.filter(group =>
        group.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        group.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (group.tags && group.tags.some(tag => tag.toLowerCase().includes(searchTerm.toLowerCase())))
      );
      resolve(filtered.map(group => ({ ...group, isJoined: mockUserJoinedGroupIds.includes(group.id) })));
    }, 800);
  });
};

const mockFetchUserGroups = async () => {
     return new Promise(resolve => {
        setTimeout(() => {
            const userGroups = mockAllGroupsData.filter(group => mockUserJoinedGroupIds.includes(group.id)).map(g => ({...g, isJoined: true}));
            resolve(userGroups);
        }, 500);
    });
};

const mockJoinLeaveGroupAPI = async (groupId, currentIsJoined) => {
    return new Promise(resolve => {
        setTimeout(() => {
            const groupIndexInAll = mockAllGroupsData.findIndex(g => g.id === groupId);
            if (groupIndexInAll === -1) {
                resolve({success: false, message: "Group not found"});
                return;
            }

            if (currentIsJoined) { // Leaving
                mockUserJoinedGroupIds = mockUserJoinedGroupIds.filter(id => id !== groupId);
                mockAllGroupsData[groupIndexInAll].memberCount--;
            } else { // Joining
                if (!mockUserJoinedGroupIds.includes(groupId)) {
                    mockUserJoinedGroupIds.push(groupId);
                    mockAllGroupsData[groupIndexInAll].memberCount++;
                }
            }
            // Update the isJoined status for this group in the main data source
            mockAllGroupsData[groupIndexInAll].isJoined = !currentIsJoined;
            resolve({ success: true, newIsJoined: !currentIsJoined, newMemberCount: mockAllGroupsData[groupIndexInAll].memberCount });
        }, 500);
    });
};

const mockCreateGroup = async (groupData) => { // Added definition
     return new Promise(resolve => {
        setTimeout(() => {
            const newGroup = {
                id: `group${Date.now()}`,
                ...groupData,
                memberCount: 1,
                coverImageUrl: groupData.coverImageUrl || `https://picsum.photos/seed/${Date.now()}/300/150`,
                isJoined: true,
            };
            mockAllGroupsData.unshift(newGroup);
            mockUserJoinedGroupIds.push(newGroup.id);
            console.log("Group created:", newGroup);
            resolve({ success: true, group: newGroup });
        }, 1000);
    });
};
// --- End Mock API ---


const CommunityGroupsScreen = ({ navigation }) => {
  const [myGroups, setMyGroups] = useState([]);
  const [discoverGroups, setDiscoverGroups] = useState([]);
  const [loadingMyGroups, setLoadingMyGroups] = useState(true);
  const [loadingDiscover, setLoadingDiscover] = useState(true);
  const [error, setError] = useState(null);

  const loadAllGroupData = async () => {
    setLoadingMyGroups(true);
    setLoadingDiscover(true);
    setError(null);
    try {
      // Fetch all groups first, then determine "My Groups" and "Discover" based on isJoined status
      const allGroupsData = await mockFetchAllGroups();

      const userJoined = allGroupsData.filter(g => g.isJoined);
      const toDiscover = allGroupsData.filter(g => !g.isJoined);

      setMyGroups(userJoined);
      setDiscoverGroups(toDiscover);

    } catch (e) {
      setError('Failed to load groups. Please try again.');
      console.error(e);
    } finally {
      setLoadingMyGroups(false);
      setLoadingDiscover(false);
    }
  };

  useEffect(() => {
    loadAllGroupData();
  }, []);

  useEffect(() => {
    const unsubscribe = navigation.addListener('focus', () => {
      loadAllGroupData(); // Refresh data when screen comes into focus
    });
    return unsubscribe;
  }, [navigation]);


  const handleJoinLeave = async (groupToUpdate, currentIsJoinedStatus) => {
    // Optimistically update UI first
    const oldMyGroups = [...myGroups];
    const oldDiscoverGroups = [...discoverGroups];

    if (currentIsJoinedStatus) { // Leaving
        setMyGroups(prev => prev.filter(g => g.id !== groupToUpdate.id));
        // Add to discover groups, ensuring its isJoined status is false
        const groupToAddBack = {...groupToUpdate, isJoined: false, memberCount: groupToUpdate.memberCount -1 };
         if (!discoverGroups.find(g => g.id === groupToUpdate.id)) { // Avoid duplicates if already there
            setDiscoverGroups(prev => [groupToAddBack, ...prev]);
        }
    } else { // Joining
        setDiscoverGroups(prev => prev.filter(g => g.id !== groupToUpdate.id));
        // Add to my groups, ensuring its isJoined status is true
        const groupToJoin = {...groupToUpdate, isJoined: true, memberCount: groupToUpdate.memberCount + 1 };
        if (!myGroups.find(g => g.id === groupToUpdate.id)) {
            setMyGroups(prev => [...prev, groupToJoin]);
        }
    }

    try {
      const result = await mockJoinLeaveGroupAPI(groupToUpdate.id, currentIsJoinedStatus);
      if (!result.success) {
        throw new Error(result.message || "Failed to update group membership.");
      }
      // API call successful, UI is already updated.
      // If API returns updated counts or status, you can fine-tune here, but for now, optimistic is good.
      // For instance, you might want to update the member count from the API response:
      // loadAllGroupData(); // Or more targeted update

    } catch (e) {
      Alert.alert("Error", e.message || "Could not update group membership.");
      // Revert UI changes on failure
      setMyGroups(oldMyGroups);
      setDiscoverGroups(oldDiscoverGroups);
    }
  };


  const renderGroupCard = (item) => ( // Removed isMyGroupSection as it's implicit now
    <TouchableOpacity
        style={styles.groupCard}
        onPress={() => navigation.navigate('DiscussionForum', { groupId: item.id, groupName: item.name })}
    >
      <Image source={{ uri: item.coverImageUrl }} style={styles.coverImage} />
      <View style={styles.groupInfo}>
        <Text style={styles.groupName}>{item.name}</Text>
        <Text style={styles.groupDescription} numberOfLines={2}>{item.description}</Text>
        <View style={styles.groupMeta}>
          <Text style={styles.memberCount}>{item.memberCount} members</Text>
          <Text style={styles.groupType}>{item.type.toUpperCase()}</Text>
        </View>
         <Button
            title={item.isJoined ? "Leave Group" : "Join Group"}
            onPress={(e) => { e.stopPropagation(); handleJoinLeave(item, item.isJoined); }}
            style={[styles.joinButton, item.isJoined ? styles.leaveButton : styles.joinButtonActive]}
            textStyle={[styles.joinButtonText, item.isJoined ? styles.leaveButtonText : styles.joinButtonActiveText]}
        />
      </View>
    </TouchableOpacity>
  );

  const ListSection = ({ title, data, loading }) => (
    <View style={styles.sectionContainer}>
      <Text style={styles.sectionTitle}>{title}</Text>
      {loading ? (
        <ActivityIndicator size="large" color="#007AFF" style={{marginTop: 20}}/>
      ) : data.length === 0 ? (
        <Text style={styles.emptyListText}>
            {title === "My Groups" ? "You haven't joined any groups yet." : "No new groups to discover right now."}
        </Text>
      ) : (
        <FlatList
          data={data}
          renderItem={({item}) => renderGroupCard(item)}
          keyExtractor={item => item.id}
          horizontal={title === "Discover New Groups"}
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={title === "Discover New Groups" ? styles.horizontalListContent : {paddingBottom:10}}
        />
      )}
    </View>
  );


  if (error && !loadingMyGroups && !loadingDiscover) {
    return <View style={styles.centered}><Text style={styles.errorText}>{error}</Text><Button title="Retry" onPress={loadAllGroupData}/></View>;
  }

  return (
    <ScrollView
        style={styles.container}
        // refreshControl={<RefreshControl refreshing={loadingMyGroups || loadingDiscover} onRefresh={loadAllGroupData} />} // Simple refresh
    >
      <ListSection title="My Groups" data={myGroups} loading={loadingMyGroups} />
      <ListSection title="Discover New Groups" data={discoverGroups} loading={loadingDiscover}/>

       <Button
        title="Create New Group"
        onPress={() => navigation.navigate('PostCreationScreen', { isCreatingGroup: true })} // Modified to reuse PostCreationScreen or navigate to a dedicated GroupCreationScreen
        style={styles.createGroupButton}
      />
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F0F2F5',
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  errorText: {
      color: 'red',
      fontSize: 16,
      textAlign: 'center',
      marginBottom: 10,
  },
  sectionContainer: {
    marginBottom: 10,
    backgroundColor:'white',
    paddingVertical:15,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#1c1e21',
    paddingHorizontal: 15,
    marginBottom: 10,
  },
  groupCard: {
    backgroundColor: '#fff',
    borderRadius: 8,
    marginHorizontal: 15,
    marginBottom: 15,
    width: 280,
    marginLeft: 15,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.20,
    shadowRadius: 1.41,
    elevation: 2,
    overflow: 'hidden',
  },
  coverImage: {
    width: '100%',
    height: 120,
  },
  groupInfo: {
    padding: 12,
  },
  groupName: {
    fontSize: 17,
    fontWeight: 'bold',
    color: '#050505',
  },
  groupDescription: {
    fontSize: 13,
    color: '#65676B',
    marginVertical: 5,
    minHeight: 35,
  },
  groupMeta: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 8,
    marginBottom:12,
  },
  memberCount: {
    fontSize: 12,
    color: '#65676B',
  },
  groupType: {
    fontSize: 11,
    color: '#fff',
    backgroundColor: '#606770',
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 4,
    overflow:'hidden',
    fontWeight:'bold',
  },
  joinButton: {
    paddingVertical: 8,
    borderRadius: 5,
    borderWidth: 1, // Add border for better distinction
  },
  joinButtonActive: {
    backgroundColor: '#1877F2',
    borderColor: '#1877F2',
  },
  joinButtonActiveText: { // Added for text color of active join button
    color: '#FFFFFF',
    fontSize: 14,
    fontWeight: '500',
  },
  leaveButton: {
    backgroundColor: '#E4E6EB',
    borderColor: '#CCD0D5',
  },
  leaveButtonText: { // Added for text color of leave button
    color: '#050505',
    fontSize: 14,
    fontWeight: '500',
  },
  joinButtonText: { // Default text styling, can be overridden by active/leave styles
    fontSize: 14,
    fontWeight: '500',
  },
  emptyListText: {
      textAlign: 'center',
      color: '#65676B',
      marginTop: 20,
      paddingHorizontal: 15,
  },
  horizontalListContent: {
      paddingRight: 15,
      paddingBottom: 10, // Add some padding at the bottom for horizontal list
  },
  createGroupButton: {
      marginHorizontal: 15,
      marginVertical: 20,
      backgroundColor: '#42B72A',
  }
});

export default CommunityGroupsScreen;
