import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, FlatList, ActivityIndicator, TouchableOpacity, Linking, TextInput } from 'react-native';
import Button from '../components/common/Button';
import { formatDate } from '../utils/helpers'; // Assuming date helper

// --- Mock API for Knowledge Sharing Content ---
const mockKnowledgeBase = [
  { id: 'article1', type: 'article', title: 'Understanding Soil Health: A Beginner\'s Guide', author: 'Dr. Agri Soul', date: '2023-10-15', summary: 'Learn the fundamentals of soil composition, nutrients, and how to improve soil health for better crops.', contentUrl: 'https://example.com/soil-health-guide', tags: ['soil', 'beginners', 'agronomy'], category: 'Soil Management', views: 1250, rating: 4.5 },
  { id: 'video1', type: 'video', title: 'Effective Pest Control Without Harmful Chemicals', expert: 'EcoFarmer Jane', date: '2023-11-01', summary: 'Watch this tutorial on implementing Integrated Pest Management (IPM) techniques on your farm.', videoUrl: 'https://youtube.com/watch?v=examplePestControl', duration: '15:30', tags: ['pest control', 'organic', 'ipm'], category: 'Crop Protection', views: 3400, rating: 4.8 },
  { id: 'guide1', type: 'guide', title: 'Step-by-Step Guide to Setting Up Drip Irrigation', author: 'WaterWise Tech', date: '2023-09-20', summary: 'A comprehensive PDF guide on designing and installing an efficient drip irrigation system.', documentUrl: 'https://example.com/drip-irrigation-setup.pdf', tags: ['irrigation', 'water management', 'diy'], category: 'Water Management', views: 850, rating: 4.2 },
  { id: 'webinar1', type: 'webinar', title: 'Webinar Replay: Maximizing Crop Yields in Dry Conditions', speaker: 'Prof. HarvestMax', date: '2023-10-05', summary: 'Insights from our recent webinar on drought-resistant crops and water conservation strategies.', recordingUrl: 'https://example.com/webinar-dry-conditions', tags: ['crop yield', 'drought', 'webinar'], category: 'Advanced Techniques', views: 560, rating: 4.6 },
  { id: 'faq1', type: 'faq', title: 'FAQ: Common Questions about Organic Certification', source: 'AgriCertify Org', date: '2023-08-01', summary: 'Answers to frequently asked questions regarding the process and benefits of organic certification.', contentUrl: 'https://example.com/organic-certification-faq', tags: ['organic', 'certification', 'faq'], category: 'Regulations & Standards', views: 1500, rating: 4.0 },
];

const mockFetchKnowledgeItems = async (searchTerm = '', categoryFilter = 'all', typeFilter = 'all') => {
  return new Promise(resolve => {
    setTimeout(() => {
      let filteredItems = mockKnowledgeBase;

      if (searchTerm) {
        const lowerSearchTerm = searchTerm.toLowerCase();
        filteredItems = filteredItems.filter(item =>
          item.title.toLowerCase().includes(lowerSearchTerm) ||
          item.summary.toLowerCase().includes(lowerSearchTerm) ||
          (item.tags && item.tags.some(tag => tag.toLowerCase().includes(lowerSearchTerm))) ||
          (item.author && item.author.toLowerCase().includes(lowerSearchTerm)) ||
          (item.expert && item.expert.toLowerCase().includes(lowerSearchTerm))
        );
      }

      if (categoryFilter !== 'all') {
        filteredItems = filteredItems.filter(item => item.category === categoryFilter);
      }
      if (typeFilter !== 'all') {
        filteredItems = filteredItems.filter(item => item.type === typeFilter);
      }

      resolve(filteredItems);
    }, 800);
  });
};
// --- End Mock API ---

// Get unique categories and types for filters
const uniqueCategories = ['all', ...new Set(mockKnowledgeBase.map(item => item.category))];
const uniqueTypes = ['all', ...new Set(mockKnowledgeBase.map(item => item.type))];


const KnowledgeSharingScreen = ({ navigation }) => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [selectedType, setSelectedType] = useState('all');

  const loadKnowledgeItems = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await mockFetchKnowledgeItems(searchTerm, selectedCategory, selectedType);
      setItems(data);
    } catch (e) {
      setError('Failed to load knowledge resources.');
      console.error(e);
    } finally {
      setLoading(false);
    }
  }, [searchTerm, selectedCategory, selectedType]);

  useEffect(() => {
    loadKnowledgeItems();
  }, [loadKnowledgeItems]); // Re-run when filters or search term change

  const handleOpenLink = async (url) => {
    const supported = await Linking.canOpenURL(url);
    if (supported) {
      await Linking.openURL(url);
    } else {
      Alert.alert(`Don't know how to open this URL: ${url}`);
    }
  };

  const renderItemCard = ({ item }) => (
    <View style={styles.card}>
      <Text style={styles.itemType}>{item.type.toUpperCase()} - {item.category}</Text>
      <Text style={styles.itemTitle}>{item.title}</Text>
      <Text style={styles.itemAuthorDate}>
        {item.author || item.expert || item.source} - {formatDate(item.date)}
      </Text>
      <Text style={styles.itemSummary}>{item.summary}</Text>
      <View style={styles.tagContainer}>
          {item.tags.map(tag => <Text key={tag} style={styles.tag}>#{tag}</Text>)}
      </View>
      <View style={styles.metaContainer}>
          <Text style={styles.metaText}>Views: {item.views}</Text>
          <Text style={styles.metaText}>Rating: {item.rating}/5</Text>
          {item.duration && <Text style={styles.metaText}>Duration: {item.duration}</Text>}
      </View>
      <Button
        title={item.type === 'video' ? "Watch Video" : (item.type === 'guide' ? "Open Guide (PDF)" : "Read More")}
        onPress={() => handleOpenLink(item.contentUrl || item.videoUrl || item.documentUrl || item.recordingUrl)}
        style={styles.actionButton}
      />
    </View>
  );

  const FilterControls = () => (
    <View style={styles.filterSection}>
        <TextInput
            style={styles.searchInput}
            placeholder="Search articles, videos, guides..."
            value={searchTerm}
            onChangeText={setSearchTerm} // Search triggers on text change (debouncing recommended for real app)
        />
        {/* Simple filter buttons for now, could be Pickers for more options */}
        <View style={styles.filterRow}>
            <Text style={styles.filterLabel}>Category:</Text>
            <ScrollView horizontal showsHorizontalScrollIndicator={false}>
                {uniqueCategories.map(cat => (
                    <TouchableOpacity key={cat} onPress={() => setSelectedCategory(cat)} style={[styles.filterButton, selectedCategory === cat && styles.filterButtonActive]}>
                        <Text style={[styles.filterButtonText, selectedCategory === cat && styles.filterButtonTextActive]}>{cat}</Text>
                    </TouchableOpacity>
                ))}
            </ScrollView>
        </View>
         <View style={styles.filterRow}>
            <Text style={styles.filterLabel}>Type:</Text>
            <ScrollView horizontal showsHorizontalScrollIndicator={false}>
                {uniqueTypes.map(type => (
                    <TouchableOpacity key={type} onPress={() => setSelectedType(type)} style={[styles.filterButton, selectedType === type && styles.filterButtonActive]}>
                        <Text style={[styles.filterButtonText, selectedType === type && styles.filterButtonTextActive]}>{type}</Text>
                    </TouchableOpacity>
                ))}
            </ScrollView>
        </View>
    </View>
  );


  if (loading && items.length === 0) { // Show full screen loader only on initial load
    return <View style={styles.centered}><ActivityIndicator size="large" /><Text>Loading Resources...</Text></View>;
  }

  return (
    <View style={styles.container}>
      <Text style={styles.screenTitle}>Knowledge Sharing Hub</Text>
      <Text style={styles.screenSubtitle}>Best Practices & Expert Advice</Text>

      <FilterControls />

      {loading && items.length > 0 && <ActivityIndicator style={{marginVertical:10}}/> /* Small loader during filter changes */}

      {error && (
        <View style={styles.centered}><Text style={styles.errorText}>{error}</Text><Button title="Retry" onPress={loadKnowledgeItems}/></View>
      )}

      {!loading && items.length === 0 && !error && (
           <View style={styles.centered}><Text>No resources found matching your criteria. Try adjusting filters.</Text></View>
      )}

      {!error && items.length > 0 && (
        <FlatList
          data={items}
          renderItem={renderItemCard}
          keyExtractor={item => item.id}
          contentContainerStyle={styles.listContent}
        />
      )}
      {/* Button to contribute content - navigates to a new screen or modal */}
      {/* <Button title="Share Your Knowledge" onPress={() => navigation.navigate('ContributeKnowledgeScreen')} style={styles.contributeButton} /> */}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f4f6f8', // Light background
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  errorText: { color: 'red', fontSize: 16, textAlign: 'center', marginBottom:10, },
  screenTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#2c3e50', // Dark blue/grey
    textAlign: 'center',
    paddingTop: 20,
  },
  screenSubtitle: {
      fontSize: 15,
      color: '#7f8c8d', // Grey
      textAlign: 'center',
      marginBottom: 15,
      paddingBottom:15,
      borderBottomWidth:1,
      borderBottomColor:'#e0e6ed',
  },
  filterSection: {
    paddingHorizontal: 15,
    paddingBottom: 10,
    backgroundColor: '#fff', // White background for filters
    marginBottom:5,
  },
  searchInput: {
    height: 45,
    backgroundColor: '#f0f2f5',
    borderRadius: 8,
    paddingHorizontal: 15,
    fontSize: 15,
    marginBottom: 10,
    borderWidth: 1,
    borderColor: '#dfe4ea',
  },
  filterRow: {
      flexDirection: 'row',
      alignItems: 'center',
      marginBottom: 8,
  },
  filterLabel: {
      fontSize: 14,
      fontWeight: '500',
      marginRight: 8,
      color: '#495057',
  },
  filterButton: {
      paddingVertical: 6,
      paddingHorizontal: 10,
      borderRadius: 15,
      backgroundColor: '#e9ecef',
      marginRight: 6,
      borderWidth:1,
      borderColor:'#ced4da',
  },
  filterButtonActive: {
      backgroundColor: '#007bff',
      borderColor:'#0056b3',
  },
  filterButtonText: {
      fontSize: 13,
      color: '#007bff',
  },
  filterButtonTextActive: {
      color: 'white',
  },
  listContent: {
    paddingHorizontal: 15,
    paddingBottom: 20, // Space for last item
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 8,
    padding: 15,
    marginBottom: 15,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2, },
    shadowOpacity: 0.1,
    shadowRadius: 2.5,
    elevation: 3,
  },
  itemType: {
    fontSize: 12,
    fontWeight: '600',
    color: '#6c757d', // Grey
    marginBottom: 4,
    textTransform: 'uppercase',
  },
  itemTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#34495e', // Darker blue/grey
    marginBottom: 6,
  },
  itemAuthorDate: {
    fontSize: 13,
    color: '#7f8c8d', // Medium grey
    marginBottom: 8,
    fontStyle: 'italic',
  },
  itemSummary: {
    fontSize: 14,
    color: '#555',
    lineHeight: 20,
    marginBottom: 10,
  },
  tagContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginBottom: 8,
  },
  tag: {
    backgroundColor: '#e0e6ed', // Light grey tag background
    color: '#52616b', // Darker grey text for tag
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 12,
    fontSize: 11,
    marginRight: 5,
    marginBottom: 5,
  },
  metaContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingTop: 8,
    borderTopWidth: 1,
    borderTopColor: '#f0f2f5',
    marginBottom: 10,
  },
  metaText: {
      fontSize: 12,
      color: '#6c757d',
  },
  actionButton: {
    backgroundColor: '#007bff', // Primary blue
    paddingVertical: 10,
  },
  // contributeButton: { // If you add a contribute button
  //   margin: 15,
  //   backgroundColor: '#28a745', // Green
  // }
});

export default KnowledgeSharingScreen;
