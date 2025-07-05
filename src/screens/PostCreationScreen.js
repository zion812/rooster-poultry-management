import React, { useState } from 'react';
import { View, Text, TextInput, StyleSheet, Alert, ScrollView, Image, TouchableOpacity, ActivityIndicator } from 'react-native';
import Button from '../components/common/Button';
// For image picker: import ImagePicker from 'react-native-image-crop-picker';
// For location: import Geolocation from '@react-native-community/geolocation'; (or expo-location)

// Mock API for creating a post
const mockCreatePost = async (postData) => {
  return new Promise(resolve => {
    setTimeout(() => {
      console.log("Creating post:", postData);
      // Simulate adding to the feed (in SocialFeedScreen, this would be a new item in mockFeedItems)
      const newPost = {
        id: `post${Date.now()}`,
        user: { id: 'currentUser', name: 'Current User Name', avatarUrl: 'https://i.pravatar.cc/50?u=currentUser' }, // Replace with actual user
        timestamp: new Date().toISOString(),
        content: postData.text,
        imageUrl: postData.imageUri, // This would be an uploaded URL
        likes: 0,
        comments: 0,
        isLiked: false,
        location: postData.location,
        tags: postData.tags,
      };
      // In a real app, you'd likely call a function to add this to your global state / refetch feed.
      // For SocialFeedScreen mock, manually add to its `mockFeedItems` for immediate reflection if desired.
      // Example: SocialFeedScreen.mockFeedItems.unshift(newPost);
      resolve({ success: true, post: newPost });
    }, 1500);
  });
};


const PostCreationScreen = ({ navigation, route }) => {
  const { isCreatingGroup } = route.params || {}; // Check if this screen is for group creation

  const [postText, setPostText] = useState('');
  const [imageUri, setImageUri] = useState(null); // For selected image
  const [location, setLocation] = useState(null); // { latitude, longitude, name }
  const [tags, setTags] = useState(''); // Comma-separated string of tags
  const [loading, setLoading] = useState(false);

  // --- States for Group Creation (if isCreatingGroup is true) ---
  const [groupName, setGroupName] = useState('');
  const [groupDescription, setGroupDescription] = useState('');
  const [groupType, setGroupType] = useState('public'); // 'public', 'private', 'closed'
  const [groupCoverImageUri, setGroupCoverImageUri] = useState(null);

  const pageTitle = isCreatingGroup ? "Create New Group" : "Create New Post";
  const submitButtonTitle = isCreatingGroup ? "Create Group" : "Post";


  const handlePickImage = async (forCover = false) => {
    Alert.alert("Image Picker", "Image picker functionality would be implemented here. (e.g., using react-native-image-picker)");
    // Example with a placeholder image:
    const placeholder = 'https://picsum.photos/400/300';
    if (forCover) {
        setGroupCoverImageUri(placeholder);
    } else {
        setImageUri(placeholder);
    }
    // try {
    //   const image = await ImagePicker.openPicker({
    //     width: 800, // Max width
    //     height: 600, // Max height
    //     cropping: true,
    //   });
    //   console.log(image);
    //   if (forCover) setGroupCoverImageUri(image.path);
    //   else setImageUri(image.path);
    // } catch (error) {
    //   if (error.code !== 'E_PICKER_CANCELLED') {
    //     Alert.alert('Error', 'Could not select image.');
    //   }
    // }
  };

  const handleAttachLocation = () => {
      Alert.alert("Location", "Location tagging functionality would be implemented here.");
      // Geolocation.getCurrentPosition(
      //     position => {
      //         const { latitude, longitude } = position.coords;
      //         // Here you might use a reverse geocoding service to get a place name
      //         setLocation({ latitude, longitude, name: `Coords: ${latitude.toFixed(3)}, ${longitude.toFixed(3)}` });
      //         Alert.alert("Location Attached", `Lat: ${latitude}, Long: ${longitude}`);
      //     },
      //     error => Alert.alert("Location Error", error.message),
      //     { enableHighAccuracy: true, timeout: 20000, maximumAge: 1000 }
      // );
      setLocation({name: "Mock Farm Location, USA"}); // Placeholder
  };

  const handleSubmit = async () => {
    if (isCreatingGroup) {
        if (!groupName.trim() || !groupDescription.trim()) {
            Alert.alert('Error', 'Group Name and Description are required.');
            return;
        }
        const groupData = {
            name: groupName,
            description: groupDescription,
            type: groupType,
            coverImageUrl: groupCoverImageUri,
            tags: tags.split(',').map(t => t.trim()).filter(t => t),
        };
        setLoading(true);
        try {
            // Assume mockCreateGroup is adapted or a new mockCreateCommunityGroup exists
            // For now, using a placeholder for group creation success:
            // const result = await CommunityGroupsScreen.mockCreateGroup(groupData); // If imported
            console.log("Submitting group data:", groupData);
            await new Promise(res => setTimeout(res, 1000)); // Simulate API call
            Alert.alert('Group Created', `${groupName} has been created successfully!`);
            navigation.goBack(); // Or navigate to the new group's page
        } catch (error) {
            Alert.alert('Error', error.message || 'Could not create group.');
        } finally {
            setLoading(false);
        }

    } else { // Creating a Post
        if (!postText.trim() && !imageUri) {
          Alert.alert('Empty Post', 'Please write something or add an image to post.');
          return;
        }
        const postData = {
          text: postText.trim(),
          imageUri: imageUri, // In real app, this would be uploaded first, then URL stored
          location: location,
          tags: tags.split(',').map(t => t.trim()).filter(t => t), // Split string into array
        };
        setLoading(true);
        try {
          const result = await mockCreatePost(postData);
          if (result.success) {
            Alert.alert('Posted!', 'Your content has been shared.');
            navigation.goBack(); // Or navigate to the feed
          } else {
            throw new Error('Failed to create post.');
          }
        } catch (error) {
          Alert.alert('Error', error.message || 'Could not create post.');
        } finally {
          setLoading(false);
        }
    }
  };

  const renderPostCreationFields = () => (
    <>
        <TextInput
            style={styles.textInput}
            placeholder="What's on your mind, farmer?"
            multiline
            value={postText}
            onChangeText={setPostText}
            textAlignVertical="top"
        />
        {imageUri && <Image source={{ uri: imageUri }} style={styles.previewImage} />}

        <View style={styles.toolbar}>
            <TouchableOpacity style={styles.toolButton} onPress={() => handlePickImage(false)}>
            <Text style={styles.toolButtonText}>📷 Add Photo</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.toolButton} onPress={handleAttachLocation}>
            <Text style={styles.toolButtonText}>📍 Tag Location</Text>
            </TouchableOpacity>
        </View>
        {location && <Text style={styles.locationText}>Location: {location.name}</Text>}
    </>
  );

  const renderGroupCreationFields = () => (
    <>
        <TextInput
            style={styles.inputField}
            placeholder="Group Name (e.g., Organic Tomato Growers)"
            value={groupName}
            onChangeText={setGroupName}
        />
        <TextInput
            style={[styles.inputField, styles.descriptionInput]}
            placeholder="Group Description (What is this group about?)"
            multiline
            value={groupDescription}
            onChangeText={setGroupDescription}
            textAlignVertical="top"
        />
        <Text style={styles.label}>Group Type:</Text>
        <View style={styles.groupTypeSelector}>
            {['public', 'private', 'closed'].map(type => (
                <TouchableOpacity
                    key={type}
                    style={[styles.typeButton, groupType === type && styles.typeButtonActive]}
                    onPress={() => setGroupType(type)}
                >
                    <Text style={[styles.typeButtonText, groupType === type && styles.typeButtonTextActive]}>{type.toUpperCase()}</Text>
                </TouchableOpacity>
            ))}
        </View>
        {groupCoverImageUri && <Image source={{ uri: groupCoverImageUri }} style={styles.previewImage} />}
         <TouchableOpacity style={[styles.toolButton, {alignSelf:'flex-start', marginVertical:10}]} onPress={() => handlePickImage(true)}>
            <Text style={styles.toolButtonText}>🖼️ Add Cover Photo</Text>
        </TouchableOpacity>
    </>
  );


  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <Text style={styles.title}>{pageTitle}</Text>

      {isCreatingGroup ? renderGroupCreationFields() : renderPostCreationFields()}

      <TextInput
        style={styles.inputField}
        placeholder={isCreatingGroup ? "Group Tags (comma-separated, e.g., organic, tomatoes)" : "Add Tags (e.g., #harvest, #sustainability)"}
        value={tags}
        onChangeText={setTags}
        autoCapitalize="none"
      />

      {loading ? (
        <ActivityIndicator size="large" color="#007BFF" style={{marginTop: 20}} />
      ) : (
        <Button title={submitButtonTitle} onPress={handleSubmit} style={styles.postButton} />
      )}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  contentContainer: {
    padding: 15,
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#1c1e21',
    textAlign: 'center',
    marginBottom: 20,
  },
  textInput: { // For post content
    minHeight: 120,
    backgroundColor: '#f0f2f5', // Light grey input background
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
    color: '#050505',
    marginBottom: 15,
    borderWidth: 1,
    borderColor: '#ccd0d5',
  },
  inputField: { // For tags, group name, group description
    height: 50,
    backgroundColor: '#f0f2f5',
    borderRadius: 8,
    paddingHorizontal: 12,
    fontSize: 16,
    color: '#050505',
    marginBottom: 15,
    borderWidth: 1,
    borderColor: '#ccd0d5',
  },
  descriptionInput: { // Specific for group description
      minHeight: 100,
      textAlignVertical: 'top',
      paddingTop: 12,
  },
  previewImage: {
    width: '100%',
    height: 200,
    borderRadius: 8,
    marginBottom: 15,
    resizeMode: 'cover',
    alignSelf: 'center',
  },
  toolbar: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    paddingVertical: 10,
    borderTopWidth: 1,
    borderBottomWidth: 1,
    borderColor: '#e4e6eb',
    marginBottom: 15,
  },
  toolButton: {
    padding: 8,
    flexDirection: 'row',
    alignItems: 'center',
  },
  toolButtonText: {
    fontSize: 15,
    color: '#1877f2', // FB Blue
    fontWeight: '500',
    marginLeft: 5, // If using icons
  },
  locationText: {
    fontSize: 14,
    color: '#606770',
    marginBottom: 15,
    fontStyle: 'italic',
  },
  postButton: {
    marginTop: 10,
    backgroundColor: '#1877f2', // FB Blue
  },
  // Group Creation Specific Styles
  label: {
      fontSize: 16,
      color: '#1c1e21',
      marginBottom: 8,
      fontWeight: '500',
  },
  groupTypeSelector: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      marginBottom: 15,
  },
  typeButton: {
      flex: 1,
      paddingVertical: 10,
      marginHorizontal: 4,
      borderRadius: 6,
      borderWidth: 1,
      borderColor: '#ccd0d5',
      alignItems: 'center',
  },
  typeButtonActive: {
      backgroundColor: '#1877f2',
      borderColor: '#1877f2',
  },
  typeButtonText: {
      color: '#050505',
      fontWeight: '500',
  },
  typeButtonTextActive: {
      color: '#fff',
  }
});

export default PostCreationScreen;
