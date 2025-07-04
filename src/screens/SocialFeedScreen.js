import React, { useState, useEffect, useCallback } from 'react';
import { View, Text, StyleSheet, FlatList, ActivityIndicator, Image, TouchableOpacity, RefreshControl } from 'react-native';
import Button from '../components/common/Button'; // Assuming common Button
import { formatDate } from '../utils/helpers'; // Date formatting

// --- Mock API for Social Feed ---
let mockFeedItems = Array.from({ length: 50 }, (_, i) => ({
  id: `post${i + 1}`,
  user: {
    id: `user${(i % 5) + 1}`,
    name: ['Farmer Giles', 'AgriConnect Admin', 'Sunny Meadows Farm', 'CropTech Solutions', 'Local Harvester'][i % 5],
    avatarUrl: `https://i.pravatar.cc/50?u=user${(i % 5) + 1}` // Placeholder avatars
  },
  timestamp: new Date(Date.now() - Math.random() * 10000000000).toISOString(), // Random past dates
  content: [
    "Just harvested a bumper crop of tomatoes! 🍅 Who wants some fresh produce?",
    "Check out our latest blog post on sustainable farming practices. Link in bio! #sustainability #farming",
    "Exciting news! We're hosting a workshop on soil health next month. Sign up now!",
    "Anyone else experiencing issues with [Pest Name]? Looking for advice. #pests #cropprotection",
    "Beautiful sunrise over the fields this morning. Feeling grateful. 🙏 #farmLife #nature"
  ][i % 5],
  imageUrl: (i % 3 === 0) ? `https://picsum.photos/seed/${i}/400/300` : null, // Random images for some posts
  likes: Math.floor(Math.random() * 100),
  comments: Math.floor(Math.random() * 20),
  isLiked: Math.random() > 0.7, // Randomly pre-like some posts
}));

const ITEMS_PER_PAGE = 10;

const mockFetchFeedItems = async (page = 1) => {
  return new Promise(resolve => {
    setTimeout(() => {
      const start = (page - 1) * ITEMS_PER_PAGE;
      const end = start + ITEMS_PER_PAGE;
      const paginatedItems = mockFeedItems.slice(start, end);
      resolve({ items: paginatedItems, hasMore: end < mockFeedItems.length });
    }, 1000);
  });
};

const mockToggleLike = async (postId, currentLikedStatus) => {
    return new Promise(resolve => {
        setTimeout(() => {
            const postIndex = mockFeedItems.findIndex(item => item.id === postId);
            if (postIndex !== -1) {
                mockFeedItems[postIndex].isLiked = !currentLikedStatus;
                mockFeedItems[postIndex].likes += (!currentLikedStatus ? 1 : -1);
                resolve({ success: true, newLikesCount: mockFeedItems[postIndex].likes, newLikedStatus: mockFeedItems[postIndex].isLiked });
            } else {
                resolve({ success: false });
            }
        }, 300);
    });
};
// --- End Mock API ---


const SocialFeedScreen = ({ navigation }) => {
  const [feedItems, setFeedItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [loadingMore, setLoadingMore] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [error, setError] = useState(null);

  const loadFeed = async (pageNum = 1, isRefreshing = false) => {
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
      const { items: newItems, hasMore: newHasMore } = await mockFetchFeedItems(pageNum);
      setFeedItems(prevItems => (pageNum === 1 || isRefreshing ? newItems : [...prevItems, ...newItems]));
      setHasMore(newHasMore);
      setPage(pageNum);
    } catch (e) {
      setError('Failed to load feed. Please try again.');
      console.error(e);
    } finally {
      setLoading(false);
      setLoadingMore(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    loadFeed(1);
  }, []);

  const handleRefresh = useCallback(() => {
    loadFeed(1, true);
  }, []);

  const handleLoadMore = () => {
    if (!loadingMore && hasMore) {
      loadFeed(page + 1);
    }
  };

  const handleLikeToggle = async (postId, currentLikedStatus) => {
      // Optimistic update
      const oldFeedItems = [...feedItems];
      setFeedItems(items => items.map(item =>
          item.id === postId ? { ...item, isLiked: !item.isLiked, likes: item.likes + (!item.isLiked ? 1 : -1) } : item
      ));

      const result = await mockToggleLike(postId, currentLikedStatus);
      if (!result.success) {
          // Revert on failure
          setFeedItems(oldFeedItems);
          Alert.alert("Error", "Failed to update like status.");
      }
      // If API returns new counts, could update again, but optimistic is often good enough
  };

  const renderFooter = () => {
    if (!loadingMore) return null;
    return (
      <View style={styles.footerLoader}>
        <ActivityIndicator size="small" />
      </View>
    );
  };

  const renderEmptyComponent = () => {
    if (loading || refreshing) return null; // Don't show "no posts" while loading
    return (
      <View style={styles.centered}>
        {error ? <Text style={styles.errorText}>{error}</Text> : <Text>No posts yet. Be the first to share!</Text>}
        <Button title="Try Refresh" onPress={handleRefresh} style={{marginTop:10}}/>
      </View>
    );
  };


  const renderFeedItem = ({ item }) => (
    <View style={styles.postCard}>
      <View style={styles.postHeader}>
        <Image source={{ uri: item.user.avatarUrl }} style={styles.avatar} />
        <View style={styles.userInfo}>
          <Text style={styles.userName}>{item.user.name}</Text>
          <Text style={styles.timestamp}>{formatDate(item.timestamp)}</Text>
        </View>
      </View>
      <Text style={styles.postContent}>{item.content}</Text>
      {item.imageUrl && (
        <Image source={{ uri: item.imageUrl }} style={styles.postImage} resizeMode="cover" />
      )}
      <View style={styles.postActions}>
        <TouchableOpacity style={styles.actionButton} onPress={() => handleLikeToggle(item.id, item.isLiked)}>
          <Text style={[styles.actionText, item.isLiked && styles.likedText]}>
            {item.isLiked ? '❤️ Liked' : '🤍 Like'} ({item.likes})
          </Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.actionButton} onPress={() => navigation.navigate('DiscussionForum', { postId: item.id })}>
          {/* Assuming DiscussionForumScreen can handle postId to show comments */}
          <Text style={styles.actionText}>💬 Comment ({item.comments})</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.actionButton}>
          <Text style={styles.actionText}>🔗 Share</Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      {loading && page === 1 && !refreshing ? (
         <View style={styles.centered}><ActivityIndicator size="large" /><Text>Loading Feed...</Text></View>
      ) : (
        <FlatList
          data={feedItems}
          renderItem={renderFeedItem}
          keyExtractor={item => item.id}
          contentContainerStyle={styles.listContent}
          onEndReached={handleLoadMore}
          onEndReachedThreshold={0.5}
          ListFooterComponent={renderFooter}
          ListEmptyComponent={renderEmptyComponent}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={handleRefresh} colors={["#007AFF"]}/>
          }
        />
      )}
      <TouchableOpacity
        style={styles.newPostButton}
        onPress={() => navigation.navigate('PostCreation')}
      >
        <Text style={styles.newPostButtonText}>+</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#e9ebee', // Facebook-like light grey background
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
    textAlign:'center',
    marginBottom:10,
  },
  listContent: {
    paddingVertical: 8,
  },
  postCard: {
    backgroundColor: '#fff',
    borderRadius: 0, // More like FB, less rounded
    marginVertical: 4, // Tighter spacing
    // marginHorizontal: 8,
    padding: 12,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1, },
    shadowOpacity: 0.1, // Subtle shadow
    shadowRadius: 1.5,
    elevation: 2,
    borderTopWidth:1, // Add border for separation
    borderBottomWidth:1,
    borderColor: '#dddfe2',
  },
  postHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
  },
  avatar: {
    width: 40,
    height: 40,
    borderRadius: 20,
    marginRight: 10,
  },
  userInfo: {
    flex: 1,
  },
  userName: {
    fontSize: 15,
    fontWeight: 'bold',
    color: '#1c1e21', // FB Dark Grey
  },
  timestamp: {
    fontSize: 12,
    color: '#606770', // FB Medium Grey
  },
  postContent: {
    fontSize: 15,
    color: '#1c1e21',
    lineHeight: 22,
    marginBottom: 10,
  },
  postImage: {
    width: '100%',
    height: 250, // Adjust as needed
    borderRadius: 0, // No border radius for images within post
    marginVertical: 8,
  },
  postActions: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    paddingTop: 10,
    borderTopWidth: 1,
    borderColor: '#dadde1', // Lighter border for actions
  },
  actionButton: {
    padding: 8,
  },
  actionText: {
    fontSize: 14,
    color: '#606770', // FB Medium Grey for actions
    fontWeight: '500',
  },
  likedText: {
    color: '#2078f4', // FB Blue for liked
    fontWeight: 'bold',
  },
  footerLoader: {
    paddingVertical: 20,
  },
  newPostButton: {
    position: 'absolute',
    right: 20,
    bottom: 20,
    backgroundColor: '#1877f2', // FB Blue
    width: 56,
    height: 56,
    borderRadius: 28,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.3,
    shadowRadius: 3,
    elevation: 5,
  },
  newPostButtonText: {
    color: 'white',
    fontSize: 28,
    lineHeight: 30, // Adjust for vertical centering of '+'
  },
});

export default SocialFeedScreen;
