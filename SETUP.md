# Quick Setup Guide

## Before First Run

1. **Create `local.properties`** in the project root:
   ```properties
   sdk.dir=/Users/yourusername/Library/Android/sdk
   GEMINI_API_KEY=your_actual_api_key_here
   ```
   
   To find your SDK path:
   - Android Studio: File > Settings > Appearance & Behavior > System Settings > Android SDK
   - Look for "Android SDK Location"

2. **Get Gemini API Key**:
   - Visit: https://aistudio.google.com/app/apikey
   - Sign in with Google
   - Click "Create API Key"
   - Copy and paste it into `local.properties`

3. **Open in Android Studio**:
   - File > Open > Select the `Amigo` folder
   - Wait for Gradle sync (may take a few minutes on first open)

4. **Run the App**:
   - Connect device or start emulator
   - Click Run (green play button) or press Shift+F10

## First Launch

1. Open the app
2. Grant camera and storage permissions when prompted
3. Tap the + button
4. Take or select a photo of food
5. Wait for analysis (may take 5-10 seconds)
6. Review and save!

## Troubleshooting

**"API key not found" error:**
- Double-check `local.properties` has `GEMINI_API_KEY=...`
- Make sure there are no extra spaces
- Restart Android Studio after adding the key

**Build fails:**
- In Android Studio: File > Invalidate Caches > Invalidate and Restart
- Clean project: Build > Clean Project
- Rebuild: Build > Rebuild Project

**Camera not working:**
- Check app permissions in device Settings > Apps > Amigo > Permissions
- Make sure camera permission is enabled

## Project Structure Quick Reference

- **Main Screen**: Home dashboard with daily summary
- **History Screen**: List of all past meals
- **Details Screen**: Full meal information with share option
- **Settings Screen**: API key configuration and theme

---

Happy tracking! 🍎📊

