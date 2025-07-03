# 🔐 Keystore Setup Guide for Rooster App

This guide helps you configure release signing for the Rooster Poultry Management app.

## 📋 Prerequisites

- Java JDK installed (comes with Android Studio)
- Android Studio or command line access

## 🗝️ Step 1: Generate Release Keystore

Run this command in your project root directory:

```bash
keytool -genkey -v -keystore rooster-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias rooster-key
```

**Follow the prompts:**

- Enter keystore password (remember this!)
- Re-enter password
- Enter your details (name, organization, etc.)
- Enter key password (can be same as keystore password)

## ⚙️ Step 2: Configure Keystore Properties

1. Copy the template file:
   ```bash
   cp keystore.properties.template keystore.properties
   ```

2. Edit `keystore.properties` with your actual values:
   ```properties
   storeFile=rooster-release-key.jks
   storePassword=your_actual_keystore_password
   keyAlias=rooster-key
   keyPassword=your_actual_key_password
   ```

## 🔒 Step 3: Security Best Practices

- ✅ `keystore.properties` is already in `.gitignore`
- ✅ Keep your keystore file (`.jks`) secure and backed up
- ✅ Never commit keystore files or passwords to version control
- ✅ Use different keystores for different environments if needed

## 🚀 Step 4: Build Release APK

Once configured, you can build signed release APKs:

```bash
./gradlew assembleRelease
```

The signed APK will be in: `app/build/outputs/apk/release/`

## 🐛 Troubleshooting

### "No release keystore configured" message

This is just informational. If you see this:

1. Check if `keystore.properties` exists
2. Verify all properties are filled in
3. Ensure the keystore file path is correct

### Build fails with signing errors

1. Verify keystore file exists at specified path
2. Check that passwords are correct
3. Ensure key alias matches what you used when creating keystore

### Lost keystore or password

- **For development:** Generate a new keystore following Step 1
- **For production apps:** You'll need the original keystore to update published apps

## 📱 Production Deployment

For Google Play Store deployment:

1. Use the same keystore for all app updates
2. Keep multiple secure backups of your keystore
3. Consider using Play App Signing for additional security

## 🔧 Environment-Specific Signing

The build is configured to:

- **Debug builds:** Use debug signing (automatic)
- **Staging builds:** Use debug signing
- **Release builds:** Use your configured keystore (if available)

## 🆘 Need Help?

- Check Android
  documentation: [Sign your app](https://developer.android.com/studio/publish/app-signing)
- For project-specific issues, create a GitHub issue
- Ensure you're following the [Contributing Guidelines](CONTRIBUTING.md)

---

**✅ Once configured, your release builds will be properly signed for distribution!**