# 🚀 Rooster Poultry Management - Setup Guide

## 📋 **Prerequisites**

- **Android Studio Narwhal** (2025.1.1) or newer
- **JDK 11** or higher
- **Git** for version control
- **Firebase Account** for backend services

## 🔧 **Project Setup**

### 1. **Clone & Build**

```bash
git clone <repository-url>
cd rooster-poultry-management
./gradlew build
```

### 2. **Firebase Configuration**

#### Create Firebase Project

1. Go to [Firebase Console](https://firebase.google.com/console)
2. Click "Create a project"
3. Project name: `rooster-poultry-management`
4. Enable Google Analytics (recommended)

#### Download Configuration

1. In Firebase Console, click "Add app" → Android
2. Package name: `com.example.rooster`
3. App nickname: `Rooster Poultry Management`
4. Download `google-services.json`

#### Configure Project

```bash
# Copy the downloaded file to app directory
cp ~/Downloads/google-services.json app/google-services.json

# Verify the file is properly placed
ls -la app/google-services.json
```

### 3. **Enable Firebase Services**

In Firebase Console, enable these services:

#### Authentication

- Go to **Authentication** → **Sign-in method**
- Enable **Email/Password**
- Enable **Phone** (for SMS verification)

#### Firestore Database

- Go to **Firestore Database**
- Click **Create database**
- Start in **test mode** (for development)
- Choose **default location**

#### Cloud Storage

- Go to **Storage**
- Click **Get started**
- Start in **test mode**

#### Cloud Functions (Optional)

- Go to **Functions**
- Set up billing (required for Functions)

## 🔑 **Keystore Setup (Optional)**

For Release builds, set up signing:

```bash
# Copy keystore template
cp keystore.properties.template keystore.properties

# Generate keystore (if needed)
keytool -genkey -v -keystore rooster-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias rooster-key

# Edit keystore.properties with your values
nano keystore.properties
```

## 🏗️ **Build Variants**

### Debug Build (Development)

```bash
./gradlew assembleDebug
```

- Uses debug signing
- Firebase emulator support
- Debug logging enabled

### Release Build (Production)

```bash
./gradlew assembleRelease
```

- Requires keystore configuration
- Optimized and obfuscated
- Production Firebase project

## 🧪 **Testing**

### Unit Tests

```bash
./gradlew test
```

### Integration Tests

```bash
./gradlew connectedAndroidTest
```

### Specific Module Tests

```bash
./gradlew :core:core-common:test
./gradlew :feature:feature-auctions:test
```

## 🌐 **API Configuration**

### Razorpay (Payment Gateway) - Development Mode

**Status**: 🔄 API Integration In Progress

Currently working without Razorpay API - the app uses realistic mock responses for development and
testing:

#### Current Mock Features:

- ✅ Realistic payment order creation (with delays)
- ✅ Payment verification simulation
- ✅ Error scenario testing
- ✅ Proper data structures ready for API integration

#### When Razorpay API is Ready:
1. Create [Razorpay Account](https://razorpay.com)
2. Get API keys from Dashboard
3. Add to `local.properties`:
```properties
razorpay.key.id=YOUR_KEY_ID
razorpay.key.secret=YOUR_KEY_SECRET
```

4. Update `RazorpayPaymentRepositoryImpl.kt` (marked with TODO comments)

#### Testing Payment Flow:

```bash
# Payment flows work end-to-end with mock data
# Valid payment IDs: "pay_*" 
# Valid order IDs: "order_*"
# Test both success and failure scenarios
```

### Backend API
Update `core/core-network/src/main/java/com/example/rooster/core/network/ApiConfig.kt`:
```kotlin
const val BASE_URL = "https://your-api-domain.com/api/"
```

## 📱 **Running the App**

### Development

1. Open in **Android Studio**
2. Wait for **Gradle sync** to complete
3. Select **app** configuration
4. Click **Run** (or press `Ctrl+Shift+F10`)

### Command Line

```bash
# Install on connected device
./gradlew installDebug

# Launch specific activity
adb shell am start -n com.example.rooster/.MainActivity
```

## 🔍 **Troubleshooting**

### Common Issues

#### 1. **Firebase Configuration Error**

```
Error: File google-services.json is missing
```

**Solution**: Follow Firebase Configuration steps above

#### 2. **Build Sync Issues**

```
Error: Unable to find Gradle tasks to build
```

**Solution**:

```bash
./gradlew --stop
rm -rf .gradle/
./gradlew build --refresh-dependencies
```

#### 3. **KSP Compilation Error**

```
Error: 'error.NonExistentClass' could not be resolved
```

**Solution**: Already fixed in latest codebase

#### 4. **Razorpay Namespace Conflict**

```
Warning: Namespace 'com.razorpay' is used in multiple modules
```

**Solution**: Under investigation, doesn't prevent compilation

### Performance Issues

#### Slow Build Times

```bash
# Enable build optimization
echo "org.gradle.parallel=true" >> ~/.gradle/gradle.properties
echo "org.gradle.caching=true" >> ~/.gradle/gradle.properties
echo "org.gradle.daemon=true" >> ~/.gradle/gradle.properties
```

#### Memory Issues

```bash
# Increase JVM memory
echo "org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m" >> ~/.gradle/gradle.properties
```

## 📊 **Development Workflow**

### Feature Development

1. Create feature branch: `git checkout -b feature/new-feature`
2. Implement in appropriate module under `feature/`
3. Add unit tests
4. Run full test suite: `./gradlew test`
5. Create pull request

### Module Structure

```
app/                    # Main application module
├── core/
│   ├── analytics/      # Analytics and tracking
│   ├── core-common/    # Shared utilities and models
│   ├── core-network/   # API clients and repositories
│   ├── navigation/     # Navigation logic
│   └── search/         # Search functionality
└── feature/
    ├── feature-auctions/     # Auction system
    ├── feature-community/    # Social features
    ├── feature-farm/         # Farm management
    └── feature-marketplace/  # Trading marketplace
```

## 🚀 **Deployment**

### Debug APK

```bash
./gradlew assembleDebug
# APK location: app/build/outputs/apk/debug/
```

### Release APK

```bash
./gradlew assembleRelease
# APK location: app/build/outputs/apk/release/
```

### Google Play Console

1. Build **Release** APK/AAB
2. Upload to **Play Console**
3. Configure **Store Listing**
4. Submit for **Review**

## 📞 **Support**

- **Build Issues**: Check `BUILD_ISSUES_REPORT.md`
- **Architecture Questions**: Review module documentation
- **Firebase Help**: [Firebase Documentation](https://firebase.google.com/docs)
- **Razorpay Integration**: [Razorpay Android SDK](https://razorpay.com/docs/android/)

---

**✅ Ready to build the future of poultry management in Krishna District! 🐓**
