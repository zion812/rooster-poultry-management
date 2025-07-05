import React, { useState, useEffect, useCallback } from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity, ActivityIndicator, Alert, ScrollView, Image } from 'react-native';
import Button from '../components/common/Button';
import { formatDate } from '../utils/helpers'; // Date formatting helper

// --- Mock API for Events ---
const mockUserRSVPStatus = { 'event1': 'going', 'event3': 'interested' }; // User's RSVP for events

const mockEventsData = [
  { id: 'event1', title: 'Annual Farmers Cooperative Meeting', date: new Date(Date.now() + 86400000 * 7).toISOString(), time: '10:00 AM - 01:00 PM', location: 'Community Hall, AgriTown', description: 'Join us for the annual meeting to discuss upcoming initiatives, budget approvals, and board elections. Lunch will be provided.', category: 'Meeting', type: 'in-person', rsvpCount: 75, capacity: 150, host: 'AgriTown Cooperative', coverImageUrl: 'https://picsum.photos/seed/meeting/400/200', isFree: true },
  { id: 'event2', title: 'Online Workshop: Advanced Soil Composting Techniques', date: new Date(Date.now() + 86400000 * 14).toISOString(), time: '02:00 PM - 04:00 PM', location: 'Online via Zoom', description: 'Learn advanced methods for creating nutrient-rich compost to boost your soil health. Link will be sent upon registration.', category: 'Workshop', type: 'online', rsvpCount: 120, capacity: 500, host: 'SoilSense Academy', coverImageUrl: 'https://picsum.photos/seed/compostws/400/200', price: 25.00, currency: 'USD' },
  { id: 'event3', title: 'Local Harvest Festival & Market Day', date: new Date(Date.now() + 86400000 * 30).toISOString(), time: '09:00 AM - 05:00 PM', location: 'Greenfield Park, Farmville', description: 'Celebrate the harvest season with local produce, crafts, live music, and family activities. Fun for all ages!', category: 'Festival', type: 'in-person', rsvpCount: 0, // Open event, rsvp might be for interest
    capacity: null, host: 'Farmville Community Council', coverImageUrl: 'https://picsum.photos/seed/harvestfest/400/200', isFree: true },
  { id: 'event4', title: 'FarmTech Conference 2024', date: new Date(Date.now() + 86400000 * 90).toISOString(), time: 'Full Day', location: 'Expo Center, Metro City', description: 'The premier conference for agricultural technology innovations, networking with industry leaders, and discovering new solutions.', category: 'Conference', type: 'hybrid', rsvpCount: 350, capacity: 1000, host: 'FutureAgri Group', coverImageUrl: 'https://picsum.photos/seed/farmtechconf/400/200', price: 199.00, currency: 'USD' },
];

const mockFetchEvents = async (filter = 'all') => { // filter: 'all', 'upcoming', 'past', 'my_rsvps'
  return new Promise(resolve => {
    setTimeout(() => {
      let results = mockEventsData.map(event => ({
          ...event,
          userRsvp: mockUserRSVPStatus[event.id] || null
      }));
      if (filter === 'upcoming') {
        results = results.filter(event => new Date(event.date) >= new Date());
      } else if (filter === 'past') {
        results = results.filter(event => new Date(event.date) < new Date());
      } else if (filter === 'my_rsvps') {
          results = results.filter(event => !!mockUserRSVPStatus[event.id]);
      }
      resolve(results.sort((a,b) => new Date(a.date) - new Date(b.date))); // Sort by date
    }, 1000);
  });
};

const mockRsvpToEvent = async (eventId, rsvpStatus) => { // rsvpStatus: 'going', 'interested', 'not_going' or null to remove
  return new Promise(resolve => {
    setTimeout(() => {
      if (rsvpStatus) {
        mockUserRSVPStatus[eventId] = rsvpStatus;
      } else {
        delete mockUserRSVPStatus[eventId];
      }
      // Simulate updating RSVP count (simplified)
      const eventIndex = mockEventsData.findIndex(e => e.id === eventId);
      if (eventIndex !== -1 && rsvpStatus === 'going' && (!mockUserRSVPStatus[eventId] || mockUserRSVPStatus[eventId] !== 'going')) {
          // mockEventsData[eventIndex].rsvpCount++; // This would be more complex in reality
      } else if (eventIndex !== -1 && rsvpStatus !== 'going' && mockUserRSVPStatus[eventId] === 'going') {
          // mockEventsData[eventIndex].rsvpCount--;
      }
      console.log(`RSVP for ${eventId} updated to ${rsvpStatus}. New status:`, mockUserRSVPStatus);
      resolve({ success: true, newRsvpStatus: rsvpStatus });
    }, 500);
  });
};
// --- End Mock API ---


const EventManagementScreen = ({ navigation }) => {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeFilter, setActiveFilter] = useState('upcoming'); // Default filter

  const loadEvents = useCallback(async (filter) => {
    setLoading(true);
    setError(null);
    try {
      const data = await mockFetchEvents(filter);
      setEvents(data);
    } catch (e) {
      setError('Failed to load events.');
      console.error(e);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadEvents(activeFilter);
  }, [loadEvents, activeFilter]);

  const handleRsvp = async (eventId, currentRsvp, newRsvp) => {
    // Optimistic update
    const oldEvents = [...events];
    setEvents(prevEvents => prevEvents.map(event =>
        event.id === eventId ? { ...event, userRsvp: newRsvp } : event
    ));

    try {
      const result = await mockRsvpToEvent(eventId, newRsvp);
      if (!result.success) {
        throw new Error("Failed to update RSVP.");
      }
      // If API returns updated counts, you could update here.
      // For now, optimistic is fine.
    } catch (e) {
      Alert.alert("Error", e.message || "Could not update RSVP status.");
      setEvents(oldEvents); // Revert on failure
    }
  };

  const renderEventCard = ({ item }) => (
    <TouchableOpacity
        style={styles.eventCard}
        onPress={() => navigation.navigate('EventDetailScreen', { eventId: item.id, eventTitle: item.title })}
        // Assuming EventDetailScreen exists
    >
      <Image source={{ uri: item.coverImageUrl }} style={styles.eventImage} />
      <View style={styles.eventInfo}>
        <Text style={styles.eventTitle}>{item.title}</Text>
        <Text style={styles.eventDate}>{formatDate(item.date)} - {item.time}</Text>
        <Text style={styles.eventLocation}>{item.location} ({item.type})</Text>
        <Text style={styles.eventDescription} numberOfLines={2}>{item.description}</Text>
        <View style={styles.eventMeta}>
            <Text style={styles.metaText}>{item.category}</Text>
            <Text style={styles.metaText}>{item.isFree ? 'Free Event' : `$${item.price} ${item.currency}`}</Text>
        </View>
         <View style={styles.rsvpContainer}>
            <Text style={styles.rsvpText}>RSVPs: {item.rsvpCount}{item.capacity ? ` / ${item.capacity}` : ''}</Text>
            {/* Basic RSVP buttons - could be a dropdown or more complex component */}
            <View style={styles.rsvpButtons}>
                {['going', 'interested', null].map((status) => ( // null for 'Remove RSVP' or 'Not Going'
                    <TouchableOpacity
                        key={status || 'remove'}
                        style={[styles.rsvpButton, item.userRsvp === status && styles.rsvpButtonActive, status === null && item.userRsvp && styles.removeRsvpButton]}
                        onPress={() => handleRsvp(item.id, item.userRsvp, status)}
                    >
                        <Text style={[styles.rsvpButtonText, item.userRsvp === status && styles.rsvpButtonTextActive]}>
                            {status === 'going' ? 'Going' : (status === 'interested' ? 'Interested' : (item.userRsvp ? 'Clear' : 'RSVP'))}
                        </Text>
                    </TouchableOpacity>
                ))}
            </View>
         </View>
      </View>
    </TouchableOpacity>
  );

  const FilterTabs = () => (
      <View style={styles.filterTabsContainer}>
          {['upcoming', 'past', 'my_rsvps', 'all'].map(filterKey => (
              <TouchableOpacity
                key={filterKey}
                style={[styles.filterTab, activeFilter === filterKey && styles.filterTabActive]}
                onPress={() => setActiveFilter(filterKey)}
              >
                  <Text style={[styles.filterTabText, activeFilter === filterKey && styles.filterTabTextActive]}>
                      {filterKey.replace('_',' ').toUpperCase()}
                  </Text>
              </TouchableOpacity>
          ))}
      </View>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.screenTitle}>Agricultural Events & Workshops</Text>
      <FilterTabs />
      {loading ? (
        <View style={styles.centered}><ActivityIndicator size="large" /><Text>Loading Events...</Text></View>
      ) : error ? (
        <View style={styles.centered}><Text style={styles.errorText}>{error}</Text><Button title="Retry" onPress={() => loadEvents(activeFilter)}/></View>
      ) : events.length === 0 ? (
        <View style={styles.centered}><Text>No events found for "{activeFilter.replace('_',' ')}" filter.</Text></View>
      ) : (
        <FlatList
          data={events}
          renderItem={renderEventCard}
          keyExtractor={item => item.id}
          contentContainerStyle={styles.listContent}
        />
      )}
      {/* <Button title="Create New Event" onPress={() => navigation.navigate('CreateEventScreen')} style={styles.createEventButton} /> */}
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f8f9fa' },
  centered: { flex: 1, justifyContent: 'center', alignItems: 'center', padding: 20 },
  errorText: { color: 'red', fontSize: 16, textAlign: 'center', marginBottom:10, },
  screenTitle: { fontSize: 20, fontWeight: 'bold', color: '#212529', textAlign: 'center', paddingVertical: 15, backgroundColor:'white', borderBottomWidth:1, borderBottomColor:'#dee2e6' },
  filterTabsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    backgroundColor: '#e9ecef',
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#ced4da',
  },
  filterTab: {
    paddingHorizontal: 10,
    paddingVertical: 8,
    borderRadius: 15,
  },
  filterTabActive: {
    backgroundColor: '#007bff',
  },
  filterTabText: {
    fontSize: 13,
    fontWeight: '500',
    color: '#007bff',
  },
  filterTabTextActive: {
    color: 'white',
  },
  listContent: { padding: 10 },
  eventCard: {
    backgroundColor: '#fff',
    borderRadius: 8,
    marginBottom: 15,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 2.62,
    elevation: 4,
    overflow: 'hidden', // Clip image
  },
  eventImage: { width: '100%', height: 180 },
  eventInfo: { padding: 15 },
  eventTitle: { fontSize: 18, fontWeight: 'bold', color: '#343a40', marginBottom: 5 },
  eventDate: { fontSize: 14, color: '#495057', marginBottom: 3 },
  eventLocation: { fontSize: 14, color: '#6c757d', marginBottom: 8, fontStyle:'italic' },
  eventDescription: { fontSize: 14, color: '#212529', lineHeight: 20, marginBottom:10 },
  eventMeta: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 12, borderTopWidth:1, borderTopColor:'#f1f3f5', paddingTop:8 },
  metaText: { fontSize: 13, color: '#007bff', fontWeight:'500' },
  rsvpContainer: { borderTopWidth:1, borderTopColor:'#f1f3f5', paddingTop:10 },
  rsvpText: { fontSize: 13, color: '#6c757d', marginBottom: 8, fontWeight:'500' },
  rsvpButtons: { flexDirection: 'row', justifyContent: 'flex-start' },
  rsvpButton: {
    paddingVertical: 6, paddingHorizontal: 10, borderRadius: 5, borderWidth:1, borderColor: '#007bff', marginRight: 8,
  },
  rsvpButtonActive: { backgroundColor: '#007bff' },
  removeRsvpButton: { borderColor: '#6c757d' },
  rsvpButtonText: { fontSize: 12, color: '#007bff', fontWeight:'bold' },
  rsvpButtonTextActive: { color: 'white' },
  // createEventButton: { margin: 15, backgroundColor: '#28a745' },
});

export default EventManagementScreen;
