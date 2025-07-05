import React, { useState, useEffect, useCallback } from 'react';
import { View, Text, StyleSheet, FlatList, ActivityIndicator, TouchableOpacity, Linking, Image, RefreshControl } from 'react-native';
import Button from '../components/common/Button';
import { formatDate } from '../utils/helpers'; // Date formatting

// --- Mock API for News & Updates ---
const mockNewsData = [
  { id: 'news1', title: 'New Government Subsidies Announced for Organic Farmers', date: '2023-11-10', source: 'AgriGov Portal', summary: 'The Ministry of Agriculture has unveiled a new package of subsidies aimed at supporting farmers transitioning to organic practices...', type: 'Government Update', category: 'Policy', imageUrl: 'https://picsum.photos/seed/govnews/400/200', link: 'https://example.com/gov-subsidies-organic' },
  { id: 'news2', title: 'Market Watch: Tomato Prices Expected to Rise Next Quarter', date: '2023-11-08', source: 'Farmonomics Today', summary: 'Analysts predict a 15% increase in tomato prices due to adverse weather conditions in key growing regions...', type: 'Market Report', category: 'Market Trends', imageUrl: 'https://picsum.photos/seed/tomatomarket/400/200', link: 'https://example.com/tomato-price-watch' },
  { id: 'news3', title: 'Innovation Spotlight: AI-Powered Crop Monitoring System Launched', date: '2023-11-05', source: 'TechAgri World', summary: 'AgriTech startup "CropMind" has launched its new AI system that promises early pest detection and yield optimization...', type: 'Technology', category: 'Innovation', imageUrl: 'https://picsum.photos/seed/cropai/400/200', link: 'https://example.com/cropmind-ai-launch' },
  { id: 'update1', title: 'Platform Maintenance Scheduled for Nov 15th, 2 AM - 4 AM UTC', date: '2023-11-03', source: 'AgriConnect Team', summary: 'Our platform will undergo scheduled maintenance. Services may be temporarily unavailable during this window.', type: 'Platform Update', category: 'Platform', imageUrl: null, link: null },
  { id: 'news4', title: 'Weather Advisory: Early Frost Expected in Northern Regions', date: '2023-11-12', source: 'National Weather Service', summary: 'Farmers in northern agricultural zones are advised to take precautions against an early frost expected late next week.', type: 'Weather Alert', category: 'Weather', imageUrl: 'https://picsum.photos/seed/frostalert/400/200', link: 'https://example.com/frost-advisory-north' },
  { id: 'news5', title: 'Success Story: Local Co-op Boosts Sales Through Direct-to-Consumer Model', date: '2023-10-28', source: 'Farmers Weekly', summary: 'The Greenfield Farmers Co-op shares how their new online platform has significantly increased their market reach and profitability.', type: 'Community Story', category: 'Success Stories', imageUrl: 'https://picsum.photos/seed/coopsuccess/400/200', link: 'https://example.com/greenfield-coop-story' },
];

const ITEMS_PER_PAGE_NEWS = 8;

const mockFetchNewsAndUpdates = async (page = 1, categoryFilter = 'all') => {
  return new Promise(resolve => {
    setTimeout(() => {
      let filteredData = mockNewsData;
      if (categoryFilter !== 'all') {
        filteredData = mockNewsData.filter(item => item.category === categoryFilter);
      }

      const sortedData = filteredData.sort((a, b) => new Date(b.date) - new Date(a.date)); // Sort by most recent
      const start = (page - 1) * ITEMS_PER_PAGE_NEWS;
      const end = start + ITEMS_PER_PAGE_NEWS;
      const paginatedItems = sortedData.slice(start, end);

      resolve({ items: paginatedItems, hasMore: end < sortedData.length });
    }, 700);
  });
};

// Get unique categories for filter
const newsCategories = ['all', ...new Set(mockNewsData.map(item => item.category))];
// --- End Mock API ---


const NewsAndUpdatesScreen = ({ navigation }) => {
  const [newsItems, setNewsItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [error, setError] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState('all');

  const loadNews = async (pageNum = 1, category = selectedCategory, isRefreshing = false) => {
    if ((loading || loadingMore) && !isRefreshing) return;

    if (isRefreshing) {
      setRefreshing(true);
    } else if (pageNum === 1) {
      setLoading(true);
    } else {
      setLoadingMore(true);
    }
    setError(null);

    try {
      const { items: newItems, hasMore: newHasMore } = await mockFetchNewsAndUpdates(pageNum, category);
      setNewsItems(prevItems => (pageNum === 1 || isRefreshing ? newItems : [...prevItems, ...newItems]));
      setHasMore(newHasMore);
      setPage(pageNum);
    } catch (e) {
      setError('Failed to load news and updates.');
      console.error(e);
    } finally {
      setLoading(false);
      setLoadingMore(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    loadNews(1, selectedCategory); // Load initial data or when category changes
  }, [selectedCategory]);


  const handleRefresh = useCallback(() => {
    loadNews(1, selectedCategory, true);
  }, [selectedCategory]);

  const handleLoadMore = () => {
    if (!loadingMore && hasMore) {
      loadNews(page + 1, selectedCategory);
    }
  };

  const handleOpenLink = async (url) => {
    if (!url) {
        Alert.alert("No Link", "This update does not have an external link.");
        return;
    }
    const supported = await Linking.canOpenURL(url);
    if (supported) {
      await Linking.openURL(url);
    } else {
      Alert.alert(`Don't know how to open this URL: ${url}`);
    }
  };


  const renderNewsItem = ({ item }) => (
    <TouchableOpacity style={styles.newsCard} onPress={() => handleOpenLink(item.link)}>
      {item.imageUrl && <Image source={{ uri: item.imageUrl }} style={styles.newsImage} />}
      <View style={styles.newsContent}>
        <Text style={styles.newsTitle}>{item.title}</Text>
        <View style={styles.metaRow}>
            <Text style={styles.newsSource}>{item.source} • </Text>
            <Text style={styles.newsDate}>{formatDate(item.date)}</Text>
        </View>
        <Text style={styles.newsSummary} numberOfLines={3}>{item.summary}</Text>
        <View style={styles.footerRow}>
            <Text style={styles.newsCategoryBadge}>{item.category}</Text>
            {item.link && <Text style={styles.readMoreLink}>Read More</Text>}
        </View>
      </View>
    </TouchableOpacity>
  );

  const CategoryFilter = () => (
    <View style={styles.filterContainer}>
        <ScrollView horizontal showsHorizontalScrollIndicator={false}>
            {newsCategories.map(cat => (
                <TouchableOpacity
                    key={cat}
                    style={[styles.filterChip, selectedCategory === cat && styles.filterChipActive]}
                    onPress={() => {
                        setSelectedCategory(cat);
                        // loadNews(1, cat); // This is now handled by useEffect on selectedCategory change
                    }}
                >
                    <Text style={[styles.filterChipText, selectedCategory === cat && styles.filterChipTextActive]}>{cat}</Text>
                </TouchableOpacity>
            ))}
        </ScrollView>
    </View>
  );

  const renderListFooter = () => {
    if (!loadingMore) return null;
    return <ActivityIndicator style={{ marginVertical: 20 }} size="small" />;
  };

  const renderEmptyList = () => {
      if (loading || refreshing) return null;
      return (
          <View style={styles.centered}>
              <Text>No news or updates found for "{selectedCategory}" category.</Text>
              {selectedCategory !== 'all' && <Button title="Show All Categories" onPress={() => setSelectedCategory('all')} style={{marginTop:10}}/>}
          </View>
      );
  };


  return (
    <View style={styles.container}>
      <Text style={styles.screenTitle}>Agricultural News & Market Updates</Text>
      <CategoryFilter />

      {loading && page === 1 && !refreshing ? (
         <View style={styles.centered}><ActivityIndicator size="large" /><Text>Loading News...</Text></View>
      ) : error ? (
        <View style={styles.centered}><Text style={styles.errorText}>{error}</Text><Button title="Retry" onPress={() => loadNews(1, selectedCategory)}/></View>
      ) : (
        <FlatList
          data={newsItems}
          renderItem={renderNewsItem}
          keyExtractor={item => item.id}
          contentContainerStyle={styles.listContent}
          onEndReached={handleLoadMore}
          onEndReachedThreshold={0.5}
          ListFooterComponent={renderListFooter}
          ListEmptyComponent={renderEmptyList}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={handleRefresh} colors={["#007AFF"]}/>
          }
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f0f2f5' },
  centered: { flex: 1, justifyContent: 'center', alignItems: 'center', padding: 20 },
  errorText: { color: 'red', fontSize: 16, textAlign: 'center', marginBottom:10, },
  screenTitle: { fontSize: 20, fontWeight: 'bold', color: '#1a2e44', textAlign: 'center', paddingVertical: 15, backgroundColor:'white', borderBottomWidth:1, borderBottomColor:'#d1d8e0' },
  filterContainer: { paddingVertical: 10, paddingHorizontal:10, backgroundColor:'#f8f9fa', borderBottomWidth:1, borderBottomColor:'#e3e9ed'},
  filterChip: { paddingVertical: 8, paddingHorizontal: 14, borderRadius: 18, backgroundColor: '#dde4eb', marginRight: 8, borderWidth:1, borderColor:'#c8d0d8'},
  filterChipActive: { backgroundColor: '#007bff', borderColor:'#0056b3' },
  filterChipText: { fontSize: 13, fontWeight:'500', color: '#007bff' },
  filterChipTextActive: { color: 'white' },
  listContent: { padding: 10, },
  newsCard: {
    backgroundColor: '#fff',
    borderRadius: 8,
    marginBottom: 15,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.15,
    shadowRadius: 2.22,
    elevation: 3,
    overflow:'hidden', // Ensure image corners are rounded if image is first element
  },
  newsImage: { width: '100%', height: 180, }, // Removed border radius from here
  newsContent: { padding: 15, },
  newsTitle: { fontSize: 17, fontWeight: 'bold', color: '#2c3e50', marginBottom: 6, },
  metaRow: { flexDirection:'row', alignItems:'center', marginBottom:8,},
  newsSource: { fontSize: 12, color: '#7f8c8d', fontWeight:'500', },
  newsDate: { fontSize: 12, color: '#95a5a6', },
  newsSummary: { fontSize: 14, color: '#34495e', lineHeight: 20, marginBottom: 10, },
  footerRow: {flexDirection:'row', justifyContent:'space-between', alignItems:'center', paddingTop:8, borderTopWidth:1, borderTopColor:'#ecf0f1'},
  newsCategoryBadge: {
      backgroundColor: '#3498db', color: 'white', fontSize: 11, fontWeight: 'bold',
      paddingHorizontal: 8, paddingVertical: 3, borderRadius: 10, overflow:'hidden', alignSelf:'flex-start'
  },
  readMoreLink: { fontSize: 13, color: '#2980b9', fontWeight:'500' },
});

export default NewsAndUpdatesScreen;
