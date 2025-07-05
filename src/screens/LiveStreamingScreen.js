import React, { useState, useEffect, useRef } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Alert, ScrollView, ActivityIndicator, Image } from 'react-native';
import Button from '../components/common/Button';
// For actual live streaming, you'd use libraries like:
// - react-native-nodemediaclient (for RTMP/RTSP streaming)
// - react-native-webrtc (for WebRTC based streaming)
// - or a service like Mux, Agora, Twilio Video.
// This mock will simulate the UI and state changes.

// --- Mock API for Live Streams ---
const mockAvailableStreams = [
  { id: 'stream1', title: 'Live Farm Tour: Sunny Meadows Dairy', host: 'Sunny Meadows Farm', status: 'live', viewers: 157, thumbnailUrl: 'https://picsum.photos/seed/dairyfarm/400/225', category: 'Farm Tour' },
  { id: 'stream2', title: 'Q&A: Pest Management Strategies', host: 'Dr. AgriExpert', status: 'upcoming', startTime: new Date(Date.now() + 3600000 * 2).toISOString(), viewers: 0, thumbnailUrl: 'https://picsum.photos/seed/pestexpert/400/225', category: 'Educational' },
  { id: 'stream3', title: 'Harvest Day at OrganiCo Orchards', host: 'OrganiCo Team', status: 'live', viewers: 92, thumbnailUrl: 'https://picsum.photos/seed/orchardharvest/400/225', category: 'Harvest' },
  { id: 'stream4', title: 'Workshop: Introduction to Permaculture', host: 'Permaculture Guild', status: 'upcoming', startTime: new Date(Date.now() + 86400000 * 1).toISOString(), viewers: 0, thumbnailUrl: 'https://picsum.photos/seed/permaculturews/400/225', category: 'Workshop' },
  { id: 'stream5', title: 'Archived: Soil Health Masterclass (Part 1)', host: 'SoilSense Institute', status: 'archived', duration: '45:12', viewers: 1200, thumbnailUrl: 'https://picsum.photos/seed/soilclass/400/225', category: 'Educational' },
];

const mockFetchStreams = async () => {
  return new Promise(resolve => setTimeout(() => resolve([...mockAvailableStreams]), 800));
};

// Simulate starting/stopping a stream (host perspective)
const mockStartStream = async (title) => {
    return new Promise(resolve => setTimeout(() => {
        console.log(`Host started stream: ${title}`);
        resolve({success: true, streamId: `stream${Date.now()}`, message: "Stream started successfully!"});
    }, 1500));
};
const mockStopStream = async (streamId) => {
    return new Promise(resolve => setTimeout(() => {
        console.log(`Host stopped stream: ${streamId}`);
        resolve({success: true, message: "Stream stopped."});
    }, 500));
};
// --- End Mock API ---


const LiveStreamingScreen = ({ navigation }) => {
  const [streams, setStreams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isHostMode, setIsHostMode] = useState(false); // Toggle for host UI
  const [isStreaming, setIsStreaming] = useState(false); // Host's current streaming status
  const [streamTitle, setStreamTitle] = useState(''); // For host to set title

  // Simulated player state (viewer perspective)
  const [currentViewingStream, setCurrentViewingStream] = useState(null);
  const [showChat, setShowChat] = useState(false);
  const [chatMessages, setChatMessages] = useState([]);
  const [newChatMessage, setNewChatMessage] = useState('');


  const loadStreams = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await mockFetchStreams();
      setStreams(data);
    } catch (e) {
      setError('Failed to load streams.');
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadStreams();
  }, []);

  // Simulate receiving chat messages for a viewed stream
  useEffect(() => {
    let chatInterval;
    if (currentViewingStream && showChat) {
        chatInterval = setInterval(() => {
            setChatMessages(prev => [...prev, {id: `msg${Date.now()}`, user: `User${Math.floor(Math.random()*100)}`, text: "This is a mock chat message!"}]);
        }, 5000); // New message every 5 seconds
    }
    return () => clearInterval(chatInterval);
  }, [currentViewingStream, showChat]);


  const handleSelectStream = (stream) => {
    if (stream.status === 'live' || stream.status === 'archived') {
      setCurrentViewingStream(stream);
      setShowChat(stream.status === 'live'); // Show chat only for live streams initially
      setChatMessages([]); // Clear previous chat
    } else if (stream.status === 'upcoming') {
      Alert.alert("Upcoming Stream", `${stream.title} is scheduled to start at ${new Date(stream.startTime).toLocaleTimeString()}.`);
    }
  };

  const handleSendChatMessage = () => {
      if (!newChatMessage.trim()) return;
      setChatMessages(prev => [...prev, {id: `msg_user_${Date.now()}`, user: 'You', text: newChatMessage.trim()}]);
      setNewChatMessage('');
      // In real app, send message to server/peers
  };

  // --- Host Mode Functions ---
  const handleToggleHostMode = () => setIsHostMode(!isHostMode);

  const handleStartStreaming = async () => {
    if (!streamTitle.trim()) {
        Alert.alert("Stream Title Required", "Please enter a title for your live stream.");
        return;
    }
    setLoading(true); // Use main loader for this action
    try {
        const result = await mockStartStream(streamTitle);
        if (result.success) {
            Alert.alert("Success", result.message);
            setIsStreaming(true);
            // In real app, you'd get stream keys, configure the media client, etc.
        } else { throw new Error("Failed to start stream."); }
    } catch (e) {
        Alert.alert("Error", e.message || "Could not start stream.");
    } finally {
        setLoading(false);
    }
  };

  const handleStopStreaming = async () => {
    setLoading(true);
    try {
        const result = await mockStopStream("current_stream_id_placeholder"); // Pass actual stream ID
        if (result.success) {
            Alert.alert("Stream Ended", result.message);
            setIsStreaming(false);
            setStreamTitle('');
        } else { throw new Error("Failed to stop stream."); }
    } catch (e) {
        Alert.alert("Error", e.message || "Could not stop stream.");
    } finally {
        setLoading(false);
    }
  };
  // --- End Host Mode Functions ---


  const renderStreamItem = ({ item }) => (
    <TouchableOpacity style={styles.streamCard} onPress={() => handleSelectStream(item)}>
      <Image source={{ uri: item.thumbnailUrl }} style={styles.thumbnail} />
      <View style={styles.streamInfo}>
        <Text style={styles.streamTitle}>{item.title}</Text>
        <Text style={styles.streamHost}>By: {item.host}</Text>
        <View style={styles.statusContainer}>
            <Text style={[styles.streamStatus, styles[`status_${item.status}`]]}>{item.status.toUpperCase()}</Text>
            {item.status === 'live' && <Text style={styles.viewers}>{item.viewers} watching</Text>}
            {item.status === 'upcoming' && <Text style={styles.startTime}>Starts: {new Date(item.startTime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</Text>}
            {item.status === 'archived' && <Text style={styles.duration}>Duration: {item.duration}</Text>}
        </View>
      </View>
    </TouchableOpacity>
  );

  const renderStreamPlayerView = () => (
    <View style={styles.playerOverlay}>
        <TouchableOpacity style={styles.closeButton} onPress={() => setCurrentViewingStream(null)}>
            <Text style={styles.closeButtonText}>✕ Close Player</Text>
        </TouchableOpacity>
        <View style={styles.videoPlaceholder}>
            <Text style={styles.videoPlaceholderText}>{currentViewingStream.title} - (Video Player Area)</Text>
            {currentViewingStream.status === 'live' && <Text style={styles.liveIndicatorPlayer}>● LIVE</Text>}
        </View>
        <View style={styles.streamDetailsPlayer}>
            <Text style={styles.playerTitle}>{currentViewingStream.title}</Text>
            <Text style={styles.playerHost}>Hosted by: {currentViewingStream.host}</Text>
        </View>

        {currentViewingStream.status === 'live' && (
            <TouchableOpacity style={styles.chatToggle} onPress={() => setShowChat(!showChat)}>
                <Text style={styles.chatToggleText}>{showChat ? "Hide Chat" : "Show Chat"}</Text>
            </TouchableOpacity>
        )}

        {showChat && currentViewingStream.status === 'live' && (
            <View style={styles.chatContainer}>
                <FlatList
                    data={chatMessages}
                    renderItem={({item}) => <Text style={styles.chatMessage}><Text style={styles.chatUser}>{item.user}:</Text> {item.text}</Text>}
                    keyExtractor={item => item.id}
                    style={styles.chatList}
                    inverted // For chat, new messages at bottom
                />
                <View style={styles.chatInputContainer}>
                    <TextInput
                        style={styles.chatInput}
                        value={newChatMessage}
                        onChangeText={setNewChatMessage}
                        placeholder="Say something..."
                        onSubmitEditing={handleSendChatMessage}
                    />
                    <TouchableOpacity style={styles.sendButton} onPress={handleSendChatMessage}>
                        <Text style={styles.sendButtonText}>Send</Text>
                    </TouchableOpacity>
                </View>
            </View>
        )}
    </View>
  );

  const renderHostView = () => (
      <View style={styles.hostContainer}>
          <Text style={styles.hostTitle}>Host Live Stream</Text>
          {!isStreaming ? (
              <>
                {/* <TextInput
                    style={styles.input}
                    placeholder="Enter Stream Title"
                    value={streamTitle}
                    onChangeText={setStreamTitle}
                />
                <View style={styles.cameraPreviewPlaceholder}><Text>Camera Preview Area</Text></View>
                <Button title={loading ? "Starting..." : "Go Live!"} onPress={handleStartStreaming} disabled={loading} /> */}
                <Text style={styles.hostInfo}>Host controls (camera preview, stream key input, quality settings) would appear here.</Text>
                 <Button title="Simulate Go Live (Not Implemented)" onPress={() => Alert.alert("Simulate", "Actual streaming not implemented in this mock.")} />

              </>
          ) : (
              <>
                <Text style={styles.streamingTitle}>You are LIVE: {streamTitle}</Text>
                <View style={styles.liveStatsPlaceholder}><Text>Live Stats: Viewers, Duration, etc.</Text></View>
                {/* <Button title={loading ? "Stopping..." : "Stop Stream"} onPress={handleStopStreaming} disabled={loading} style={{backgroundColor: 'red'}} /> */}
                <Button title="Simulate Stop Stream (Not Implemented)" onPress={() => Alert.alert("Simulate", "Actual streaming not implemented in this mock.")} style={{backgroundColor: 'red'}} />
              </>
          )}
          <Button title="Exit Host Mode" onPress={handleToggleHostMode} style={{marginTop: 20, backgroundColor: 'grey'}}/>
      </View>
  );


  if (currentViewingStream) {
      return renderStreamPlayerView();
  }

  if (isHostMode) {
      return renderHostView();
  }

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.screenTitle}>Live Streams & Farm Tours</Text>
        <TouchableOpacity onPress={handleToggleHostMode}>
            <Text style={styles.hostModeButtonText}>Host Mode</Text>
        </TouchableOpacity>
      </View>

      {loading && streams.length === 0 ? (
        <View style={styles.centered}><ActivityIndicator size="large" /><Text>Loading Streams...</Text></View>
      ) : error ? (
        <View style={styles.centered}><Text style={styles.errorText}>{error}</Text><Button title="Retry" onPress={loadStreams}/></View>
      ) : streams.length === 0 ? (
        <View style={styles.centered}><Text>No streams available right now.</Text></View>
      ) : (
        <FlatList
          data={streams}
          renderItem={renderStreamItem}
          keyExtractor={item => item.id}
          contentContainerStyle={styles.listContent}
          ListHeaderComponent={loading ? <ActivityIndicator style={{marginVertical:10}}/> : null}
        />
      )}
    </View>
  );
};

// Styles would be extensive, covering list items, player view, host view, chat, etc.
// For brevity, only essential structural styles are sketched.
const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f0f2f5' },
  centered: { flex: 1, justifyContent: 'center', alignItems: 'center', padding: 20 },
  errorText: { color: 'red', fontSize: 16, textAlign: 'center', marginBottom:10, },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingHorizontal: 15, paddingVertical:10, backgroundColor:'white', borderBottomWidth:1, borderBottomColor:'#ddd' },
  screenTitle: { fontSize: 20, fontWeight: 'bold', color: '#1c1e21' },
  hostModeButtonText: { fontSize: 14, color: '#007bff', fontWeight:'500' },
  listContent: { padding: 10 },
  streamCard: {
    backgroundColor: '#fff', borderRadius: 8, marginBottom: 15,
    shadowColor: "#000", shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.1, shadowRadius: 3, elevation: 3
  },
  thumbnail: { width: '100%', height: 180, borderTopLeftRadius: 8, borderTopRightRadius: 8 },
  streamInfo: { padding: 12 },
  streamTitle: { fontSize: 17, fontWeight: 'bold', color: '#050505', marginBottom: 4 },
  streamHost: { fontSize: 13, color: '#65676B', marginBottom: 8 },
  statusContainer: {flexDirection:'row', justifyContent:'space-between', alignItems:'center'},
  streamStatus: { fontSize: 12, fontWeight: 'bold', paddingVertical:2, paddingHorizontal:6, borderRadius:4, overflow:'hidden', color:'white' },
  status_live: { backgroundColor: 'red' },
  status_upcoming: { backgroundColor: 'orange' },
  status_archived: { backgroundColor: 'grey' },
  viewers: { fontSize: 12, color: '#65676B' },
  startTime: { fontSize: 12, color: '#65676B' },
  duration: { fontSize: 12, color: '#65676B' },

  // Player View
  playerOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.95)', justifyContent: 'flex-start' },
  closeButton: { position: 'absolute', top: 40, right: 15, zIndex: 10, padding:10, backgroundColor:'rgba(255,255,255,0.2)', borderRadius:5},
  closeButtonText: { color: 'white', fontSize: 16 },
  videoPlaceholder: { height: 250, backgroundColor: '#111', justifyContent: 'center', alignItems: 'center', marginTop: 70 },
  videoPlaceholderText: { color: '#555', fontSize: 18 },
  liveIndicatorPlayer: {position:'absolute', top:10, left:10, color:'red', fontWeight:'bold', backgroundColor:'rgba(0,0,0,0.5)', padding:5, borderRadius:3},
  streamDetailsPlayer: { padding: 15 },
  playerTitle: { fontSize: 20, fontWeight: 'bold', color: 'white', marginBottom: 5 },
  playerHost: { fontSize: 15, color: '#ccc', marginBottom: 15 },
  chatToggle: { padding:10, backgroundColor:'#333', marginHorizontal:15, borderRadius:5, alignItems:'center'},
  chatToggleText: {color:'white', fontWeight:'500'},
  chatContainer: { flex: 1, margin:15, backgroundColor:'#222', borderRadius:5, padding:10 },
  chatList: { flex: 1, marginBottom:10 },
  chatMessage: { color: 'white', marginBottom: 5, fontSize:13, lineHeight:18 },
  chatUser: {fontWeight:'bold'},
  chatInputContainer: { flexDirection: 'row', borderTopWidth:1, borderTopColor:'#444', paddingTop:10 },
  chatInput: { flex: 1, backgroundColor: '#333', color: 'white', borderRadius: 15, paddingHorizontal: 12, paddingVertical:8, marginRight:10, fontSize:14 },
  sendButton: { paddingHorizontal:15, justifyContent:'center', alignItems:'center', backgroundColor:'#007bff', borderRadius:15, height:38},
  sendButtonText: {color:'white', fontWeight:'bold'},

  // Host View
  hostContainer: { flex: 1, padding: 20, alignItems: 'center', backgroundColor:'white' },
  hostTitle: { fontSize: 22, fontWeight: 'bold', marginBottom: 20 },
  hostInfo: {textAlign:'center', color:'grey', marginVertical:30, fontSize:16, paddingHorizontal:20},
  input: { width: '100%', padding: 10, borderColor: 'gray', borderWidth: 1, borderRadius: 5, marginBottom: 15, fontSize:16 },
  cameraPreviewPlaceholder: { width: '100%', height: 200, backgroundColor: '#e0e0e0', justifyContent: 'center', alignItems: 'center', marginBottom: 15, borderRadius:5 },
  streamingTitle: { fontSize: 18, fontWeight: '500', color: 'green', marginBottom: 10 },
  liveStatsPlaceholder: { width: '100%', padding: 15, backgroundColor: '#f0f0f0', justifyContent: 'center', alignItems: 'center', marginBottom: 15, borderRadius:5 },
});

export default LiveStreamingScreen;
