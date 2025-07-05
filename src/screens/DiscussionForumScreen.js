import React, { useState, useEffect, useCallback } from 'react';
import { View, Text, StyleSheet, FlatList, TextInput, TouchableOpacity, ActivityIndicator, Alert, KeyboardAvoidingView, Platform } from 'react-native';
import Button from '../components/common/Button';
import { formatDate } from '../utils/helpers';

// --- Mock API for Discussion Forum (can be for a Post or a Group) ---
let mockForumThreads = {
  'post1': [ // Comments for SocialFeedScreen post with id 'post1'
    { id: 'comment1_post1', user: { name: 'AgriExpert', avatarUrl: 'https://i.pravatar.cc/40?u=expert' }, text: 'Great harvest! What variety of tomatoes are those?', timestamp: new Date(Date.now() - 3600000 * 5).toISOString(), replies: [
      { id: 'reply1_comment1', user: { name: 'Farmer Giles' }, text: 'Thanks! These are "Early Girl". Very productive this year.', timestamp: new Date(Date.now() - 3600000 * 4).toISOString() }
    ]},
    { id: 'comment2_post1', user: { name: 'NewbieFarmer', avatarUrl: 'https://i.pravatar.cc/40?u=newbie' }, text: 'Wow, they look delicious! Any tips for preventing blight?', timestamp: new Date(Date.now() - 3600000 * 2).toISOString(), replies: [] },
  ],
  'group1': [ // Discussion threads for Community Group with id 'group1' (Organic Tomato Growers)
    { id: 'thread1_group1', title: 'Best organic fertilizers for tomatoes?', user: { name: 'TomatoFanatic' }, timestamp: new Date(Date.now() - 86400000 * 3).toISOString(), lastReplyTimestamp: new Date(Date.now() - 3600000 * 10).toISOString(), replyCount: 5, initialPost: 'Looking for recommendations on the best organic fertilizers that have worked well for your tomato crops. Please share brands and application tips!' },
    { id: 'thread2_group1', title: 'Dealing with Hornworms Organically', user: { name: 'GreenThumbGrl' }, timestamp: new Date(Date.now() - 86400000 * 1).toISOString(), lastReplyTimestamp: new Date(Date.now() - 3600000 * 1).toISOString(), replyCount: 2, initialPost: 'Hornworms are attacking my plants! What are your go-to organic methods for controlling them?' },
  ],
  // Can add more threads for other posts or groups
};

const mockFetchForumData = async (contextId, contextType = 'post') => { // contextType: 'post' or 'group'
  return new Promise(resolve => {
    setTimeout(() => {
      resolve(mockForumThreads[contextId] || []);
    }, 700);
  });
};

const mockAddCommentOrThread = async (contextId, contextType, data) => {
  return new Promise(resolve => {
    setTimeout(() => {
      const newEntry = {
        id: `${contextType === 'post' ? 'comment' : 'thread'}${Date.now()}_${contextId}`,
        user: { name: 'CurrentUser', avatarUrl: 'https://i.pravatar.cc/40?u=currentUser' }, // Replace with actual user
        timestamp: new Date().toISOString(),
        ...data
      };
      if (contextType === 'post') { // Adding a comment to a post
          if (!mockForumThreads[contextId]) mockForumThreads[contextId] = [];
          mockForumThreads[contextId].push({...newEntry, replies: []}); // Comments can have replies
      } else { // Adding a new thread to a group
          if (!mockForumThreads[contextId]) mockForumThreads[contextId] = [];
          mockForumThreads[contextId].unshift({...newEntry, lastReplyTimestamp: newEntry.timestamp, replyCount: 0 }); // New threads go to top
      }
      console.log("Added to forum:", newEntry);
      resolve({ success: true, entry: newEntry });
    }, 1000);
  });
};

// --- End Mock API ---


const DiscussionForumScreen = ({ route, navigation }) => {
  const { postId, groupId, groupName } = route.params;
  const contextId = postId || groupId;
  const contextType = postId ? 'post' : 'group';
  const screenTitle = postId ? "Post Comments" : (groupName ? `${groupName} Forum` : "Group Discussion");

  const [threads, setThreads] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [newCommentText, setNewCommentText] = useState(''); // For post comments
  const [newThreadTitle, setNewThreadTitle] = useState(''); // For group new thread
  const [newThreadPost, setNewThreadPost] = useState('');   // For group new thread
  const [submitting, setSubmitting] = useState(false);

  const loadForumData = useCallback(async () => {
    if (!contextId) {
        setError("No discussion context (post or group ID) provided.");
        setLoading(false);
        return;
    }
    setLoading(true);
    setError(null);
    try {
      const data = await mockFetchForumData(contextId, contextType);
      setThreads(data);
    } catch (e) {
      setError('Failed to load discussion. Please try again.');
      console.error(e);
    } finally {
      setLoading(false);
    }
  }, [contextId, contextType]);

  useEffect(() => {
    navigation.setOptions({ title: screenTitle });
    loadForumData();
  }, [loadForumData, navigation, screenTitle]);

  const handleAddEntry = async () => {
    let dataPayload;
    if (contextType === 'post') { // Adding comment to a post
      if (!newCommentText.trim()) {
        Alert.alert("Empty Comment", "Please write something to comment.");
        return;
      }
      dataPayload = { text: newCommentText.trim() };
    } else { // Adding new thread to a group
      if (!newThreadTitle.trim() || !newThreadPost.trim()) {
        Alert.alert("Missing Info", "Thread title and initial post are required.");
        return;
      }
      dataPayload = { title: newThreadTitle.trim(), initialPost: newThreadPost.trim() };
    }

    setSubmitting(true);
    try {
      const result = await mockAddCommentOrThread(contextId, contextType, dataPayload);
      if (result.success) {
        // Add to local state for immediate reflection
        setThreads(prev => contextType === 'post' ? [...prev, result.entry] : [result.entry, ...prev]);
        setNewCommentText('');
        setNewThreadTitle('');
        setNewThreadPost('');
      } else {
        throw new Error("Failed to add entry.");
      }
    } catch (e) {
      Alert.alert('Error', e.message || 'Could not submit your entry.');
    } finally {
      setSubmitting(false);
    }
  };

  const renderCommentItem = ({ item }) => ( // For post comments
    <View style={styles.commentItem}>
      <View style={styles.commentHeader}>
        {/* <Image source={{ uri: item.user.avatarUrl }} style={styles.avatarSmall} /> */}
        <Text style={styles.commentUser}>{item.user.name}</Text>
        <Text style={styles.commentTimestamp}>{formatDate(item.timestamp)}</Text>
      </View>
      <Text style={styles.commentText}>{item.text}</Text>
      {/* Basic reply rendering - can be expanded */}
      {item.replies && item.replies.map(reply => (
          <View key={reply.id} style={styles.replyItem}>
              <Text style={styles.commentUser}>{reply.user.name}: <Text style={styles.commentText}>{reply.text}</Text></Text>
          </View>
      ))}
      {/* Add reply button here */}
    </View>
  );

  const renderThreadItem = ({ item }) => ( // For group forum threads
    <TouchableOpacity
        style={styles.threadItem}
        onPress={() => navigation.navigate('ThreadDetailScreen', { threadId: item.id, threadTitle: item.title, groupContextId: groupId })}
        // Assuming ThreadDetailScreen exists for viewing full thread and replies
    >
      <Text style={styles.threadTitle}>{item.title}</Text>
      <Text style={styles.threadMeta}>Started by: {item.user.name} on {formatDate(item.timestamp)}</Text>
      <Text style={styles.threadMeta}>{item.replyCount} replies | Last reply: {formatDate(item.lastReplyTimestamp)}</Text>
    </TouchableOpacity>
  );

  const renderInputArea = () => {
    if (contextType === 'post') { // Input for post comments
      return (
        <View style={styles.inputContainer}>
          <TextInput
            style={styles.textInput}
            placeholder="Write a comment..."
            value={newCommentText}
            onChangeText={setNewCommentText}
            multiline
            editable={!submitting}
          />
          <TouchableOpacity style={[styles.submitButton, submitting && styles.submitButtonDisabled]} onPress={handleAddEntry} disabled={submitting}>
            <Text style={styles.submitButtonText}>{submitting ? "..." : "Send"}</Text>
          </TouchableOpacity>
        </View>
      );
    } else { // Input for new group thread
      return (
        <View style={styles.newThreadContainer}>
            <Text style={styles.newThreadLabel}>Start a New Discussion Thread</Text>
            <TextInput
                style={styles.inputField}
                placeholder="Thread Title"
                value={newThreadTitle}
                onChangeText={setNewThreadTitle}
                editable={!submitting}
            />
            <TextInput
                style={[styles.inputField, styles.textArea]}
                placeholder="Your post content..."
                value={newThreadPost}
                onChangeText={setNewThreadPost}
                multiline
                textAlignVertical="top"
                editable={!submitting}
            />
            <Button title={submitting ? "Submitting..." : "Create Thread"} onPress={handleAddEntry} disabled={submitting} />
        </View>
      );
    }
  };


  if (loading) {
    return <View style={styles.centered}><ActivityIndicator size="large" /><Text>Loading Discussion...</Text></View>;
  }
  if (error) {
    return <View style={styles.centered}><Text style={styles.errorText}>{error}</Text><Button title="Retry" onPress={loadForumData}/></View>;
  }

  return (
    <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : "height"}
        style={styles.container}
        keyboardVerticalOffset={Platform.OS === "ios" ? 64 : 0} // Adjust as needed
    >
      <FlatList
        data={threads}
        renderItem={contextType === 'post' ? renderCommentItem : renderThreadItem}
        keyExtractor={item => item.id}
        ListEmptyComponent={<Text style={styles.emptyText}>No {contextType === 'post' ? 'comments' : 'discussions'} yet.</Text>}
        contentContainerStyle={styles.listContent}
        onRefresh={loadForumData} // Pull to refresh
        refreshing={loading}
      />
      {renderInputArea()}
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff', // White background for forum itself
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  errorText: { color: 'red', fontSize: 16, textAlign: 'center', marginBottom:10, },
  emptyText: { textAlign: 'center', marginTop: 30, color: 'grey', fontSize:16, },
  listContent: {
    padding: 10,
    flexGrow: 1, // Ensures list takes up space even when empty, for KeyboardAvoidingView
  },
  // Post Comment Styles
  commentItem: {
    backgroundColor: '#f0f2f5', // Light grey for comments
    padding: 10,
    borderRadius: 8,
    marginBottom: 10,
  },
  commentHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 5,
  },
  avatarSmall: { width: 25, height: 25, borderRadius: 12.5, marginRight: 8, },
  commentUser: { fontWeight: 'bold', color: '#050505', marginRight: 8, fontSize:14, },
  commentTimestamp: { fontSize: 11, color: '#65676B', },
  commentText: { fontSize: 14, color: '#050505', lineHeight: 19, },
  replyItem: {
      marginLeft: 20,
      marginTop: 5,
      paddingLeft: 8,
      borderLeftWidth: 2,
      borderLeftColor: '#ddd',
  },
  // Group Thread Styles
  threadItem: {
    backgroundColor: '#f9f9f9', // Slightly different grey for threads
    padding: 15,
    borderRadius: 5,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: '#e7e7e7',
  },
  threadTitle: { fontSize: 17, fontWeight: 'bold', color: '#1877f2', marginBottom: 5, },
  threadMeta: { fontSize: 12, color: '#606770', marginBottom: 3, },
  // Input Area Styles
  inputContainer: { // For post comments
    flexDirection: 'row',
    padding: 10,
    borderTopWidth: 1,
    borderColor: '#ccd0d5',
    backgroundColor: '#fff',
  },
  textInput: {
    flex: 1,
    minHeight: 40,
    maxHeight: 100, // Limit height of multiline input
    backgroundColor: '#f0f2f5',
    borderRadius: 20,
    paddingHorizontal: 15,
    paddingVertical: 10, // Adjust for multiline
    fontSize: 15,
    marginRight: 10,
  },
  submitButton: {
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 15,
    height: 40, // Match TextInput height
    borderRadius: 20,
    backgroundColor: '#1877f2',
  },
  submitButtonDisabled: { backgroundColor: '#a0c4ff', },
  submitButtonText: { color: '#fff', fontWeight: 'bold', fontSize:15, },
  // New Group Thread Input Styles
  newThreadContainer: {
    padding: 15,
    borderTopWidth: 1,
    borderColor: '#e0e0e0',
    backgroundColor: '#f9f9f9',
  },
  newThreadLabel: {
      fontSize: 16,
      fontWeight: 'bold',
      marginBottom: 10,
      color: '#333',
  },
  inputField: { // Shared by title and content for group thread
    backgroundColor: 'white',
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 5,
    padding: 10,
    fontSize: 15,
    marginBottom: 10,
  },
  textArea: { // Specific for group thread content
      minHeight: 80,
      textAlignVertical: 'top',
  },
});

export default DiscussionForumScreen;
