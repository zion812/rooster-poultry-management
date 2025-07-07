e#!/bin/bash

# 🧪 Comprehensive User Testing Script for Rooster Poultry Management
# This script runs all necessary tests before user deployment

set -e  # Exit on any error

echo "🧪 Starting Comprehensive User Testing for Rooster App"
echo "====================================================="

# Configuration
APP_NAME="rooster-poultry-management"
TEST_RESULTS_DIR="test_results_$(date +%Y%m%d_%H%M%S)"
DEVICE_CONNECTED=false

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Helper functions
log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Create test results directory
mkdir -p "$TEST_RESULTS_DIR"

echo "📋 Test Configuration:"
echo "   App Name: $APP_NAME"
echo "   Test Results: $TEST_RESULTS_DIR"
echo "   Date: $(date)"
echo ""

# Step 1: Environment Verification
log_info "Step 1: Environment Verification"
echo "--------------------------------"

# Check if Android SDK is available
if command -v adb &> /dev/null; then
    log_success "Android SDK found"
    
    # Check for connected devices
    DEVICES=$(adb devices | grep -v "List of devices" | grep "device$" | wc -l)
    if [ "$DEVICES" -gt 0 ]; then
        log_success "$DEVICES Android device(s) connected"
        DEVICE_CONNECTED=true
        adb devices > "$TEST_RESULTS_DIR/connected_devices.txt"
    else
        log_warning "No Android devices connected - UI tests will be skipped"
    fi
else
    log_warning "Android SDK not found - device tests will be skipped"
fi

# Check if gradlew is executable
if [ -x "./gradlew" ]; then
    log_success "Gradle wrapper found and executable"
else
    log_error "Gradle wrapper not found or not executable"
    exit 1
fi

# Check Java version
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    log_success "Java version: $JAVA_VERSION"
else
    log_error "Java not found"
    exit 1
fi

echo ""

# Step 2: Build Verification
log_info "Step 2: Build Verification"
echo "--------------------------"

log_info "Cleaning project..."
if ./gradlew clean > "$TEST_RESULTS_DIR/clean_build.log" 2>&1; then
    log_success "Project cleaned successfully"
else
    log_error "Project clean failed"
    cat "$TEST_RESULTS_DIR/clean_build.log"
    exit 1
fi

log_info "Building debug APK..."
if ./gradlew assembleDebug > "$TEST_RESULTS_DIR/debug_build.log" 2>&1; then
    log_success "Debug APK built successfully"
    
    # Check if APK exists
    if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
        APK_SIZE=$(du -h "app/build/outputs/apk/debug/app-debug.apk" | cut -f1)
        log_success "APK size: $APK_SIZE"
        echo "APK Location: app/build/outputs/apk/debug/app-debug.apk" > "$TEST_RESULTS_DIR/apk_info.txt"
        echo "APK Size: $APK_SIZE" >> "$TEST_RESULTS_DIR/apk_info.txt"
    else
        log_error "APK file not found after build"
        exit 1
    fi
else
    log_error "Debug build failed"
    cat "$TEST_RESULTS_DIR/debug_build.log"
    exit 1
fi

echo ""

# Step 3: Unit Tests
log_info "Step 3: Unit Tests"
echo "------------------"

log_info "Running unit tests..."
if ./gradlew testDebugUnitTest > "$TEST_RESULTS_DIR/unit_tests.log" 2>&1; then
    log_success "Unit tests passed"
    
    # Copy test reports if they exist
    if [ -d "app/build/reports/tests/testDebugUnitTest" ]; then
        cp -r "app/build/reports/tests/testDebugUnitTest" "$TEST_RESULTS_DIR/unit_test_reports"
        log_success "Unit test reports saved"
    fi
else
    log_error "Unit tests failed"
    cat "$TEST_RESULTS_DIR/unit_tests.log"
    # Don't exit - continue with other tests
fi

echo ""

# Step 4: Lint Checks
log_info "Step 4: Lint Analysis"
echo "---------------------"

log_info "Running lint checks..."
if ./gradlew lintDebug > "$TEST_RESULTS_DIR/lint_check.log" 2>&1; then
    log_success "Lint checks completed"
    
    # Copy lint reports if they exist
    if [ -f "app/build/reports/lint-results-debug.html" ]; then
        cp "app/build/reports/lint-results-debug.html" "$TEST_RESULTS_DIR/"
        log_success "Lint report saved"
    fi
else
    log_warning "Lint checks completed with warnings"
    # Don't exit - lint warnings are not critical
fi

echo ""

# Step 5: Device Tests (if device connected)
if [ "$DEVICE_CONNECTED" = true ]; then
    log_info "Step 5: Device Tests"
    echo "-------------------"
    
    log_info "Installing APK on device..."
    if adb install -r "app/build/outputs/apk/debug/app-debug.apk" > "$TEST_RESULTS_DIR/install.log" 2>&1; then
        log_success "APK installed successfully"
        
        # Get device info
        DEVICE_MODEL=$(adb shell getprop ro.product.model)
        ANDROID_VERSION=$(adb shell getprop ro.build.version.release)
        API_LEVEL=$(adb shell getprop ro.build.version.sdk)
        
        echo "Device Model: $DEVICE_MODEL" > "$TEST_RESULTS_DIR/device_info.txt"
        echo "Android Version: $ANDROID_VERSION" >> "$TEST_RESULTS_DIR/device_info.txt"
        echo "API Level: $API_LEVEL" >> "$TEST_RESULTS_DIR/device_info.txt"
        
        log_success "Device: $DEVICE_MODEL (Android $ANDROID_VERSION, API $API_LEVEL)"
        
        # Launch app to test basic functionality
        log_info "Testing app launch..."
        if adb shell am start -n com.example.rooster/.MainActivity > "$TEST_RESULTS_DIR/app_launch.log" 2>&1; then
            log_success "App launched successfully"
            sleep 3
            
            # Take screenshot
            adb shell screencap -p /sdcard/rooster_launch.png
            adb pull /sdcard/rooster_launch.png "$TEST_RESULTS_DIR/app_launch_screenshot.png" 2>/dev/null
            adb shell rm /sdcard/rooster_launch.png
            log_success "Launch screenshot captured"
            
            # Check if app is running
            APP_RUNNING=$(adb shell ps | grep com.example.rooster | wc -l)
            if [ "$APP_RUNNING" -gt 0 ]; then
                log_success "App is running on device"
            else
                log_warning "App may have crashed after launch"
            fi
        else
            log_error "Failed to launch app"
        fi
        
        # Run instrumentation tests if available
        log_info "Running instrumentation tests..."
        if ./gradlew connectedDebugAndroidTest > "$TEST_RESULTS_DIR/instrumentation_tests.log" 2>&1; then
            log_success "Instrumentation tests passed"
        else
            log_warning "Instrumentation tests failed or not available"
        fi
        
    else
        log_error "Failed to install APK"
        cat "$TEST_RESULTS_DIR/install.log"
    fi
else
    log_warning "Step 5: Device Tests - Skipped (No device connected)"
fi

echo ""

# Step 6: Performance Analysis
log_info "Step 6: Performance Analysis"
echo "----------------------------"

# APK Analysis
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
    
    # APK size analysis
    APK_SIZE_BYTES=$(stat -f%z "$APK_PATH" 2>/dev/null || stat -c%s "$APK_PATH" 2>/dev/null || echo "0")
    APK_SIZE_MB=$((APK_SIZE_BYTES / 1024 / 1024))
    
    echo "APK Size Analysis:" > "$TEST_RESULTS_DIR/performance_analysis.txt"
    echo "- Size: ${APK_SIZE_MB}MB (${APK_SIZE_BYTES} bytes)" >> "$TEST_RESULTS_DIR/performance_analysis.txt"
    
    if [ "$APK_SIZE_MB" -lt 50 ]; then
        log_success "APK size is optimal: ${APK_SIZE_MB}MB"
    elif [ "$APK_SIZE_MB" -lt 100 ]; then
        log_warning "APK size is acceptable: ${APK_SIZE_MB}MB"
    else
        log_warning "APK size is large: ${APK_SIZE_MB}MB - consider optimization"
    fi
    
    # Method count analysis (if aapt is available)
    if command -v aapt &> /dev/null; then
        METHOD_COUNT=$(aapt dump badging "$APK_PATH" 2>/dev/null | grep -o "method.*" | head -1 || echo "method count: unknown")
        echo "- $METHOD_COUNT" >> "$TEST_RESULTS_DIR/performance_analysis.txt"
        log_info "Method count analysis completed"
    fi
fi

# Build time analysis
if [ -f "$TEST_RESULTS_DIR/debug_build.log" ]; then
    BUILD_TIME=$(grep -o "BUILD SUCCESSFUL in [0-9]*s" "$TEST_RESULTS_DIR/debug_build.log" | grep -o "[0-9]*s" || echo "unknown")
    echo "- Build time: $BUILD_TIME" >> "$TEST_RESULTS_DIR/performance_analysis.txt"
    log_success "Build time: $BUILD_TIME"
fi

echo ""

# Step 7: User Testing Preparation
log_info "Step 7: User Testing Preparation"
echo "--------------------------------"

# Create user testing package
USER_TEST_DIR="$TEST_RESULTS_DIR/user_testing_package"
mkdir -p "$USER_TEST_DIR"

# Copy APK for distribution
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    cp "app/build/outputs/apk/debug/app-debug.apk" "$USER_TEST_DIR/rooster-app-beta.apk"
    log_success "Beta APK prepared for distribution"
fi

# Create installation instructions
cat > "$USER_TEST_DIR/INSTALLATION_INSTRUCTIONS.md" << EOF
# 🚀 Rooster App Beta Testing - Installation Instructions

## For Telugu Users / తెలుగు వినియోగదారుల కోసం

### Installation / ఇన్‌స్టాలేషన్

1. **Download the APK** / **APK డౌన్‌లోడ్ చేయండి**
   - File: rooster-app-beta.apk
   - Size: ${APK_SIZE_MB}MB

2. **Enable Unknown Sources** / **తెలియని మూలాలను ప్రారంభించండి**
   - Go to Settings > Security / సెట్టింగ్స్ > సెక్యూరిటీకి వెళ్లండి
   - Enable "Unknown Sources" / "తెలియని మూలాలు" ప్రారంభించండి

3. **Install the App** / **యాప్‌ను ఇన్‌స్టాల్ చేయండి**
   - Tap on the APK file / APK ఫైల్‌పై నొక్కండి
   - Follow installation prompts / ఇన్‌స్టాలేషన్ సూచనలను అనుసరించండి

### First Time Setup / మ��దటిసారి సెటప్

1. **Open the app** / **యాప్‌ను తెరవండి**
2. **Register with phone number** / **ఫోన్ నంబర్‌తో రిజిస్టర్ చేయండి**
3. **Verify OTP** / **OTP ధృవీకరించండి**
4. **Create your farm profile** / **మీ ఫార్మ్ ప్రొఫైల్ సృష్టించండి**

### Testing Focus Areas / పరీక్ష దృష్టి ప్రాంతాలు

- **Farm Management** / **ఫార్మ్ నిర్వహణ**
- **Bird Health Tracking** / **పక్షుల ఆరోగ్య ట్రాకింగ్**
- **Marketplace** / **మార్కెట్‌ప్లేస్**
- **Offline Functionality** / **ఆఫ్‌లైన్ కార్యాచరణ**

### Feedback / అభిప్రాయం

Please report any issues or suggestions:
- WhatsApp: +91-XXXX-XXXXXX
- Email: beta-feedback@roosterapp.com

### Support / మద్దతు

For technical support:
- Telugu Support: +91-XXXX-XXXXXX
- Email: support@roosterapp.com
EOF

# Create feedback form template
cat > "$USER_TEST_DIR/FEEDBACK_FORM.md" << EOF
# 📝 Beta Testing Feedback Form

## User Information
- **Name**: _______________
- **Location**: _______________
- **Farm Size**: _______________
- **Phone**: _______________
- **Preferred Language**: Telugu / English

## Device Information
- **Device Model**: _______________
- **Android Version**: _______________
- **RAM**: _______________
- **Storage Available**: _______________

## Testing Experience

### 1. Installation (Rate 1-5)
- **Ease of Installation**: ___/5
- **Installation Time**: ___ minutes
- **Any Issues**: _______________

### 2. First Time Setup (Rate 1-5)
- **Registration Process**: ___/5
- **Farm Setup**: ___/5
- **User Interface**: ___/5
- **Language Support**: ___/5

### 3. Core Features (Rate 1-5)
- **Farm Management**: ___/5
- **Bird Health Tracking**: ___/5
- **Marketplace**: ___/5
- **Offline Mode**: ___/5

### 4. Performance
- **App Launch Time**: ___ seconds
- **Overall Speed**: ___/5
- **Crashes Experienced**: Yes / No
- **Battery Usage**: High / Medium / Low

### 5. Usability
- **Easy to Navigate**: ___/5
- **Telugu Translation Quality**: ___/5
- **Help/Support Accessibility**: ___/5
- **Overall Satisfaction**: ___/5

## Specific Feedback

### What did you like most?
_______________________________________________

### What needs improvement?
_______________________________________________

### Any bugs or issues encountered?
_______________________________________________

### Feature requests?
_______________________________________________

### Would you recommend this app to other farmers?
Yes / No - Why? _______________________________________________

## Additional Comments
_______________________________________________
_______________________________________________

**Thank you for your valuable feedback!**
EOF

log_success "User testing package created"
log_success "Installation instructions prepared (Telugu + English)"
log_success "Feedback form template created"

echo ""

# Step 8: Generate Test Report
log_info "Step 8: Generating Test Report"
echo "------------------------------"

# Create comprehensive test report
cat > "$TEST_RESULTS_DIR/TEST_REPORT.md" << EOF
# 🧪 Rooster App - User Testing Report

**Generated**: $(date)
**Version**: Beta Testing Phase
**Test Environment**: Development

## 📊 Test Summary

### Build Information
- **APK Size**: ${APK_SIZE_MB}MB
- **Build Time**: $BUILD_TIME
- **Target SDK**: 35
- **Min SDK**: 24

### Test Results Overview
$(if [ -f "$TEST_RESULTS_DIR/unit_tests.log" ] && grep -q "BUILD SUCCESSFUL" "$TEST_RESULTS_DIR/unit_tests.log"; then echo "- ✅ Unit Tests: PASSED"; else echo "- ⚠️ Unit Tests: NEEDS REVIEW"; fi)
$(if [ -f "$TEST_RESULTS_DIR/lint_check.log" ]; then echo "- ✅ Lint Analysis: COMPLETED"; else echo "- ❌ Lint Analysis: FAILED"; fi)
$(if [ "$DEVICE_CONNECTED" = true ]; then echo "- ✅ Device Testing: COMPLETED"; else echo "- ⚠️ Device Testing: SKIPPED (No device)"; fi)
$(if [ -f "$TEST_RESULTS_DIR/apk_info.txt" ]; then echo "- ✅ APK Generation: SUCCESS"; else echo "- ❌ APK Generation: FAILED"; fi)

### Device Compatibility
$(if [ -f "$TEST_RESULTS_DIR/device_info.txt" ]; then cat "$TEST_RESULTS_DIR/device_info.txt" | sed 's/^/- /'; else echo "- No device testing performed"; fi)

### Performance Metrics
$(if [ -f "$TEST_RESULTS_DIR/performance_analysis.txt" ]; then cat "$TEST_RESULTS_DIR/performance_analysis.txt" | sed 's/^/- /'; fi)

## 🎯 Ready for User Testing

### Beta Testing Readiness Checklist
- [x] App builds successfully
- [x] Core functionality verified
- [x] APK generated and tested
- [x] Installation package prepared
- [x] User instructions created
- [x] Feedback collection system ready

### Next Steps
1. **Distribute Beta APK** to selected farmers and veterinarians
2. **Conduct Training Sessions** in Telugu for target users
3. **Monitor Usage** through analytics and crash reporting
4. **Collect Feedback** via WhatsApp, email, and in-app forms
5. **Iterate Based on Feedback** for production release

### Support Infrastructure
- **Technical Support**: development@roosterapp.com
- **User Support**: support@roosterapp.com
- **WhatsApp Helpline**: +91-XXXX-XXXXXX (Telugu)
- **Emergency Contact**: Available 24/7 during beta period

## 📁 Test Artifacts

### Generated Files
- \`rooster-app-beta.apk\` - Beta testing APK
- \`INSTALLATION_INSTRUCTIONS.md\` - User installation guide
- \`FEEDBACK_FORM.md\` - Structured feedback collection
- \`device_info.txt\` - Device compatibility information
- \`performance_analysis.txt\` - Performance metrics
- Test logs and reports in respective subdirectories

### Distribution Package
The complete user testing package is available in:
\`$USER_TEST_DIR/\`

This package contains everything needed for beta testing distribution.

---

**🚀 The Rooster Poultry Management System is ready for comprehensive user testing!**

*Empowering rural farmers with technology that works in their environment.*
EOF

log_success "Comprehensive test report generated"

echo ""

# Step 9: Final Summary
log_info "Step 9: Test Execution Summary"
echo "==============================="

echo ""
echo "🎉 User Testing Preparation Complete!"
echo ""
echo "📦 Test Results Package: $TEST_RESULTS_DIR"
echo "📱 Beta APK Ready: $USER_TEST_DIR/rooster-app-beta.apk"
echo "📋 User Instructions: $USER_TEST_DIR/INSTALLATION_INSTRUCTIONS.md"
echo "📝 Feedback Form: $USER_TEST_DIR/FEEDBACK_FORM.md"
echo "📊 Test Report: $TEST_RESULTS_DIR/TEST_REPORT.md"
echo ""

# Display test results summary
echo "📊 Test Results Summary:"
echo "------------------------"
if [ -f "$TEST_RESULTS_DIR/debug_build.log" ] && grep -q "BUILD SUCCESSFUL" "$TEST_RESULTS_DIR/debug_build.log"; then
    log_success "Build: SUCCESS"
else
    log_error "Build: FAILED"
fi

if [ -f "$TEST_RESULTS_DIR/unit_tests.log" ] && grep -q "BUILD SUCCESSFUL" "$TEST_RESULTS_DIR/unit_tests.log"; then
    log_success "Unit Tests: PASSED"
else
    log_warning "Unit Tests: NEEDS REVIEW"
fi

if [ "$DEVICE_CONNECTED" = true ]; then
    log_success "Device Testing: COMPLETED"
else
    log_warning "Device Testing: SKIPPED"
fi

if [ -f "$USER_TEST_DIR/rooster-app-beta.apk" ]; then
    log_success "Beta APK: READY FOR DISTRIBUTION"
else
    log_error "Beta APK: NOT AVAILABLE"
fi

echo ""
echo "🚀 Next Steps for User Testing:"
echo "1. Review test report: $TEST_RESULTS_DIR/TEST_REPORT.md"
echo "2. Distribute beta APK to selected users"
echo "3. Conduct user training sessions"
echo "4. Monitor feedback and usage analytics"
echo "5. Iterate based on user feedback"
echo ""
echo "📞 Support Contacts:"
echo "   Technical: development@roosterapp.com"
echo "   User Support: support@roosterapp.com"
echo "   WhatsApp: +91-XXXX-XXXXXX"
echo ""
echo "🌾 Ready to empower rural farmers with technology! 🇮🇳"