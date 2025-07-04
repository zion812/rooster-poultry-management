import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, FlatList, ActivityIndicator, Image, TouchableOpacity, ScrollView } from 'react-native';
import Button from '../components/common/Button'; // Assuming common Button
import { formatDate } from '../utils/helpers'; // Date formatting

// --- Mock API for Achievements ---
const allPossibleAchievements = {
  'joined_community': { name: 'Community Pioneer', description: 'Successfully joined the AgriConnect platform.', icon: '🤝', points: 10, category: 'Community' },
  'first_post': { name: 'Voice of the Farm', description: 'Made your first post in the community feed.', icon: '📢', points: 20, category: 'Engagement' },
  'joined_5_groups': { name: 'Group Guru', description: 'Joined 5 different community groups.', icon: '👨‍👩‍👧‍👦', points: 50, category: 'Community' },
  'product_trace_10': { name: 'Traceability Titan', description: 'Successfully traced 10 products using the system.', icon: 'ιχ', points: 100, category: 'Traceability' },
  'mentor_connect': { name: 'Guidance Seeker', description: 'Connected with a mentor.', icon: '🧑‍🏫', points: 75, category: 'Mentorship' },
  'event_attend_3': { name: 'Event Enthusiast', description: 'Attended 3 community events or workshops.', icon: '🎉', points: 60, category: 'Events' },
  'knowledge_share_5': { name: 'Knowledge Sharer', description: 'Viewed 5 articles/videos from the knowledge hub.', icon: '📚', points: 40, category: 'Learning' },
  'compliance_champ': { name: 'Compliance Champion', description: 'Maintained 100% compliance on a tracked product for 30 days.', icon: '🛡️', points: 150, category: 'Compliance', isRare: true },
  'sustainability_badge': { name: 'Eco Warrior', description: 'Completed a sustainability certification via the platform.', icon: '🌿', points: 200, category: 'Certification', isRare: true },
  'top_contributor_month': { name: 'Top Contributor', description: 'Awarded for being a top community contributor this month.', icon: '🌟', points: 0, category: 'Recognition', isSpecial: true }, // Points might be 0 if it's purely recognition
};

// Simulate user's earned achievements
let mockUserEarnedAchievements = {
  'joined_community': { earnedDate: '2023-01-15T00:00:00Z' },
  'first_post': { earnedDate: '2023-01-20T00:00:00Z' },
  'knowledge_share_5': { earnedDate: '2023-02-10T00:00:00Z' },
  'mentor_connect': { earnedDate: '2023-03-01T00:00:00Z', mentorName: 'Dr. Alice GreenThumb'}, // Extra data for specific achievements
};

const mockFetchUserAchievements = async (userId) => {
  return new Promise(resolve => {
    setTimeout(() => {
      const earnedList = Object.keys(mockUserEarnedAchievements).map(key => ({
        id: key,
        ...allPossibleAchievements[key],
        ...mockUserEarnedAchievements[key],
      }));
      const totalPoints = earnedList.reduce((sum, ach) => sum + (ach.points || 0), 0);
      resolve({ achievements: earnedList.sort((a,b) => new Date(b.earnedDate) - new Date(a.earnedDate)), totalPoints });
    }, 800);
  });
};

const mockFetchAllPossibleAchievements = async () => {
     return new Promise(resolve => {
        setTimeout(() => {
            resolve(Object.keys(allPossibleAchievements).map(key => ({id: key, ...allPossibleAchievements[key]})));
        }, 500);
    });
};
// --- End Mock API ---

const AchievementSystemScreen = ({ navigation }) => {
  const [userAchievements, setUserAchievements] = useState([]);
  const [allAchievements, setAllAchievements] = useState([]);
  const [totalPoints, setTotalPoints] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedTab, setSelectedTab] = useState('earned'); // 'earned' or 'all'

  const loadAchievements = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [userAchData, allAchData] = await Promise.all([
        mockFetchUserAchievements('currentUser'), // Replace with actual user ID
        mockFetchAllPossibleAchievements()
      ]);
      setUserAchievements(userAchData.achievements);
      setTotalPoints(userAchData.totalPoints);
      setAllAchievements(allAchData);
    } catch (e) {
      setError('Failed to load achievements.');
      console.error(e);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadAchievements();
  }, [loadAchievements]);

  const renderAchievementItem = ({ item }) => {
    const isEarned = userAchievements.some(ua => ua.id === item.id);
    const earnedData = isEarned ? userAchievements.find(ua => ua.id === item.id) : null;

    return (
      <View style={[styles.achievementCard, isEarned ? styles.earnedCard : styles.lockedCard, item.isRare && styles.rareCard, item.isSpecial && styles.specialCard]}>
        <View style={styles.iconContainer}>
            {/* For real icons, use <Image source={...} /> or an icon library */}
            <Text style={styles.iconText}>{item.icon || '🏆'}</Text>
        </View>
        <View style={styles.detailsContainer}>
            <Text style={styles.achievementName}>{item.name}</Text>
            <Text style={styles.achievementDescription}>{item.description}</Text>
            <Text style={styles.achievementCategory}>Category: {item.category}</Text>
            {item.points > 0 && <Text style={styles.achievementPoints}>Points: {item.points}</Text>}
            {isEarned && earnedData.earnedDate && (
                <Text style={styles.earnedDate}>Earned: {formatDate(earnedData.earnedDate)}</Text>
            )}
            {isEarned && earnedData.mentorName && ( // Example of specific data
                <Text style={styles.earnedDetail}>Mentor: {earnedData.mentorName}</Text>
            )}
            {!isEarned && selectedTab === 'all' && <Text style={styles.statusLocked}>Locked</Text>}
        </View>
        {(item.isRare || item.isSpecial) && (
            <View style={item.isSpecial ? styles.specialBanner : styles.rareBanner}>
                <Text style={styles.bannerText}>{item.isSpecial ? 'SPECIAL' : 'RARE'}</Text>
            </View>
        )}
      </View>
    );
  };

  const TabSelector = () => (
      <View style={styles.tabContainer}>
          <TouchableOpacity
            style={[styles.tabButton, selectedTab === 'earned' && styles.tabButtonActive]}
            onPress={() => setSelectedTab('earned')}>
              <Text style={[styles.tabText, selectedTab === 'earned' && styles.tabTextActive]}>My Achievements</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.tabButton, selectedTab === 'all' && styles.tabButtonActive]}
            onPress={() => setSelectedTab('all')}>
              <Text style={[styles.tabText, selectedTab === 'all' && styles.tabTextActive]}>All Achievements</Text>
          </TouchableOpacity>
      </View>
  );

  const dataToDisplay = selectedTab === 'earned' ? userAchievements : allAchievements;

  if (loading) {
    return <View style={styles.centered}><ActivityIndicator size="large" /><Text>Loading Achievements...</Text></View>;
  }
  if (error) {
    return <View style={styles.centered}><Text style={styles.errorText}>{error}</Text><Button title="Retry" onPress={loadAchievements}/></View>;
  }

  return (
    <ScrollView style={styles.container}>
      <View style={styles.headerContainer}>
        <Text style={styles.screenTitle}>Farmer Recognition & Badges</Text>
        <View style={styles.pointsContainer}>
            <Text style={styles.pointsText}>Total Points: {totalPoints} ✨</Text>
        </View>
      </View>
      <TabSelector />

      {dataToDisplay.length === 0 ? (
          <View style={styles.centeredMessage}>
            <Text>{selectedTab === 'earned' ? "You haven't earned any achievements yet. Keep engaging!" : "No achievements defined yet."}</Text>
          </View>
      ) : (
        <FlatList
          data={dataToDisplay}
          renderItem={renderAchievementItem}
          keyExtractor={item => item.id}
          contentContainerStyle={styles.listContent}
          scrollEnabled={false} // Because parent is ScrollView
        />
      )}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f9fafb' }, // Lightest grey
  centered: { flex: 1, justifyContent: 'center', alignItems: 'center', padding: 20 },
  centeredMessage: { alignItems:'center', paddingVertical:50,},
  errorText: { color: 'red', fontSize: 16, textAlign: 'center', marginBottom:10, },
  headerContainer: { padding: 20, alignItems:'center', backgroundColor:'white', borderBottomWidth:1, borderBottomColor:'#e5e7eb'},
  screenTitle: { fontSize: 22, fontWeight: 'bold', color: '#1f2937', marginBottom: 5, textAlign:'center' },
  pointsContainer: { backgroundColor: '#e0f2fe', paddingVertical: 8, paddingHorizontal:15, borderRadius: 20, alignSelf:'center' },
  pointsText: { fontSize: 16, fontWeight: '600', color: '#0c4a6e' }, // Sky blue dark
  tabContainer: { flexDirection: 'row', justifyContent: 'space-around', backgroundColor: '#e5e7eb', paddingVertical:0, },
  tabButton: { flex:1, paddingVertical: 12, alignItems:'center', borderBottomWidth:3, borderBottomColor:'transparent'},
  tabButtonActive: { borderBottomColor:'#3b82f6' }, // Blue active tab
  tabText: { fontSize: 15, fontWeight: '500', color: '#4b5563' },
  tabTextActive: { color: '#3b82f6' },
  listContent: { padding: 15 },
  achievementCard: {
    backgroundColor: '#ffffff',
    borderRadius: 12,
    padding: 15,
    marginBottom: 15,
    flexDirection: 'row',
    alignItems: 'center',
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 2,
    borderWidth: 1,
    borderColor: '#e5e7eb', // Light border
  },
  earnedCard: { borderColor: '#10b981', backgroundColor:'#f0fdf4' }, // Green border and very light green bg for earned
  lockedCard: { opacity: 0.7, backgroundColor:'#f3f4f6' },
  rareCard: { borderColor: '#8b5cf6', borderWidth:2 }, // Purple border for rare
  specialCard: { borderColor: '#f59e0b', borderWidth:2, backgroundColor:'#fffbeb'}, // Amber border for special
  iconContainer: {
    width: 60, height: 60, borderRadius: 30,
    backgroundColor: '#e0e7ff', // Indigo light
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 15,
  },
  iconText: { fontSize: 28 }, // Emoji size
  detailsContainer: { flex: 1 },
  achievementName: { fontSize: 17, fontWeight: 'bold', color: '#111827', marginBottom: 3 },
  achievementDescription: { fontSize: 13, color: '#4b5563', marginBottom: 4, lineHeight:18, },
  achievementCategory: { fontSize: 11, color: '#6b7280', fontStyle:'italic', marginBottom:2, },
  achievementPoints: { fontSize: 12, fontWeight: '600', color: '#059669', marginBottom: 3 }, // Dark Green for points
  earnedDate: { fontSize: 11, color: '#047857', fontWeight:'500' },
  earnedDetail: { fontSize: 11, color: '#065f46', fontStyle:'italic' },
  statusLocked: { fontSize: 12, fontWeight:'bold', color:'#9ca3af', marginTop:4 },
  bannerText: { color: 'white', fontSize: 10, fontWeight: 'bold' },
  rareBanner: { position: 'absolute', top: 8, right: -15, backgroundColor: '#8b5cf6', paddingVertical: 3, paddingHorizontal: 20, transform: [{ rotate: '30deg' }], elevation:5 },
  specialBanner: { position: 'absolute', top: 8, right: -18, backgroundColor: '#f59e0b', paddingVertical: 3, paddingHorizontal: 20, transform: [{ rotate: '30deg' }], elevation:5 },
});

export default AchievementSystemScreen;
