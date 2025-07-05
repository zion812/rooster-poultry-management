import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Text } from 'react-native'; // For tab icons placeholder

// Import Screens - Traceability
import ProductTraceabilityScreen from './src/screens/ProductTraceabilityScreen';
import QRCodeGeneratorScreen from './src/screens/QRCodeGeneratorScreen';
import VerificationWorkflowScreen from './src/screens/VerificationWorkflowScreen';
import CertificationManagementScreen from './src/screens/CertificationManagementScreen';
import BlockchainIntegrationScreen from './src/screens/BlockchainIntegrationScreen';

// Import Screens - Transfer & Verification
import TransferScreen from './src/screens/TransferScreen';
import VerificationRequestScreen from './src/screens/VerificationRequestScreen';
import OwnershipHistoryScreen from './src/screens/OwnershipHistoryScreen';
import TransferVerificationScreen from './src/screens/TransferVerificationScreen';
import ComplianceTrackingScreen from './src/screens/ComplianceTrackingScreen';

// Import Screens - Social Community
import SocialFeedScreen from './src/screens/SocialFeedScreen';
import CommunityGroupsScreen from './src/screens/CommunityGroupsScreen';
import PostCreationScreen from './src/screens/PostCreationScreen';
import DiscussionForumScreen from './src/screens/DiscussionForumScreen';
import KnowledgeSharingScreen from './src/screens/KnowledgeSharingScreen';

// Import Screens - Content & Engagement
import LiveStreamingScreen from './src/screens/LiveStreamingScreen';
import EventManagementScreen from './src/screens/EventManagementScreen';
import MentorshipScreen from './src/screens/MentorshipScreen';
import AchievementSystemScreen from './src/screens/AchievementSystemScreen';
import NewsAndUpdatesScreen from './src/screens/NewsAndUpdatesScreen';

const Stack = createStackNavigator();
const Tab = createBottomTabNavigator();

// --- Stack Navigators for each main feature area ---

const TraceabilityStack = () => (
  <Stack.Navigator>
    <Stack.Screen name="ProductTraceabilityList" component={ProductTraceabilityScreen} options={{ title: 'Product Traceability' }}/>
    <Stack.Screen name="QRCodeGenerator" component={QRCodeGeneratorScreen} options={{ title: 'QR Code Generator' }}/>
    <Stack.Screen name="VerificationWorkflow" component={VerificationWorkflowScreen} options={{ title: 'Verification Workflow' }}/>
    <Stack.Screen name="CertificationManagement" component={CertificationManagementScreen} options={{ title: 'Certifications' }}/>
    <Stack.Screen name="BlockchainIntegration" component={BlockchainIntegrationScreen} options={{ title: 'Blockchain Records' }}/>
    {/* Add other traceability related screens here if they are part of a sub-flow */}
  </Stack.Navigator>
);

const TransferStack = () => (
  <Stack.Navigator>
    <Stack.Screen name="TransferMain" component={TransferScreen} options={{ title: 'Product Transfer' }}/>
    <Stack.Screen name="VerificationRequest" component={VerificationRequestScreen} options={{ title: 'Request Verification' }}/>
    <Stack.Screen name="OwnershipHistory" component={OwnershipHistoryScreen} options={{ title: 'Ownership History' }}/>
    <Stack.Screen name="TransferVerification" component={TransferVerificationScreen} options={{ title: 'Verify Transfer' }}/>
    <Stack.Screen name="ComplianceTracking" component={ComplianceTrackingScreen} options={{ title: 'Compliance Tracking' }}/>
  </Stack.Navigator>
);

const SocialStack = () => (
  <Stack.Navigator>
    <Stack.Screen name="SocialFeed" component={SocialFeedScreen} options={{ title: 'Community Feed' }}/>
    <Stack.Screen name="CommunityGroups" component={CommunityGroupsScreen} options={{ title: 'Community Groups' }}/>
    {/* PostCreationScreen is often presented modally or as part of feed/group context */}
    {/* DiscussionForumScreen is typically navigated to from a post or group */}
    <Stack.Screen name="KnowledgeSharing" component={KnowledgeSharingScreen} options={{ title: 'Knowledge Hub' }}/>
     {/* Modal/Contextual Screens for Social - Not in Tab, but part of this stack for navigation ease */}
    <Stack.Screen name="PostCreation" component={PostCreationScreen} options={{ title: 'Create Post/Group' }} />
    <Stack.Screen name="DiscussionForum" component={DiscussionForumScreen} />
  </Stack.Navigator>
);

const EngagementStack = () => (
  <Stack.Navigator>
    <Stack.Screen name="NewsAndUpdates" component={NewsAndUpdatesScreen} options={{ title: 'News & Updates' }}/>
    <Stack.Screen name="LiveStreaming" component={LiveStreamingScreen} options={{ title: 'Live Streams' }}/>
    <Stack.Screen name="EventManagement" component={EventManagementScreen} options={{ title: 'Events & Workshops' }}/>
    <Stack.Screen name="Mentorship" component={MentorshipScreen} options={{ title: 'Mentorship Program' }}/>
    <Stack.Screen name="AchievementSystem" component={AchievementSystemScreen} options={{ title: 'Achievements' }}/>
  </Stack.Navigator>
);


// --- Main Bottom Tab Navigator ---
const MainTabNavigator = () => (
  <Tab.Navigator
    screenOptions={({ route }) => ({
        tabBarIcon: ({ focused, color, size }) => {
            let iconName = "●"; // Default placeholder icon
            if (route.name === 'Traceability') iconName = 'ιχ';
            else if (route.name === 'Transfer') iconName = '⇄';
            else if (route.name === 'Social') iconName = '👥';
            else if (route.name === 'Engage') iconName = '💡';
            return <Text style={{ fontSize: focused ? 24 : 20, color }}>{iconName}</Text>;
        },
        headerShown: false, // Hide header for tab screens, stack navigator will handle it
    })}
  >
    <Tab.Screen name="Traceability" component={TraceabilityStack} />
    <Tab.Screen name="Transfer" component={TransferStack} />
    <Tab.Screen name="Social" component={SocialStack} />
    <Tab.Screen name="Engage" component={EngagementStack} />
  </Tab.Navigator>
);


const App = () => {
  return (
    <NavigationContainer>
      {/*
        The MainTabNavigator contains stacks for each major feature area.
        Screens like PostCreation or DiscussionForum that might be accessed from multiple places
        can be included in one of the stacks (e.g., SocialStack) and navigated to directly,
        or they could be part of a root Stack.Navigator if they are meant to be presented modally over tabs.
        For simplicity here, they are included in the SocialStack.
      */}
      <MainTabNavigator />
    </NavigationContainer>
  );
};

export default App;
