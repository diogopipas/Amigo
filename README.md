# Amigo

A simple and powerful Android app that tracks calories and macronutrients by taking pictures of meals, using Google's Gemini API for image analysis.

## Features

- 📸 **Photo-Based Tracking**: Take or upload photos of your meals
- 🤖 **AI-Powered Analysis**: Uses Google Gemini AI to estimate nutritional information
- 📊 **Daily Summary**: View your daily intake of calories, protein, carbs, and fat
- 📱 **Meal History**: Browse through all your past meals
- 💬 **Share Progress**: Share your daily summary with friends via any messaging app
- 🌓 **Dark Mode**: Light and dark theme support

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI**: Jetpack Compose
- **Database**: Room (local storage)
- **API**: Google Gemini AI SDK
- **Image Loading**: Coil
- **Navigation**: Jetpack Navigation Compose

## Setup Instructions

### Prerequisites

1. Android Studio (Hedgehog | 2023.1.1 or newer)
2. Android SDK with minimum API level 26 (Android 8.0)
3. A Gemini API key from [Google AI Studio](https://aistudio.google.com/app/apikey)

### Installation

1. **Clone or download this repository**

2. **Get your Gemini API Key**
   - Go to https://aistudio.google.com/app/apikey
   - Sign in with your Google account
   - Create a new API key
   - Copy the API key

3. **Configure the API Key**
   
   Create a `local.properties` file in the project root (if it doesn't exist) and add:
   ```properties
   sdk.dir=/path/to/your/android/sdk
   GEMINI_API_KEY=your_gemini_api_key_here
   ```
   
   Replace `your_gemini_api_key_here` with your actual API key.
   
   **Note**: The `local.properties` file is git-ignored and won't be committed to version control.

4. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the project directory and select it
   - Wait for Gradle sync to complete

5. **Build and Run**
   - Connect an Android device or start an emulator (API 26+)
   - Click "Run" or press Shift+F10
   - The app will install and launch on your device

## Project Structure

```
com.amigo/
├── data/
│   ├── local/          # Room database, DAOs, entities
│   ├── remote/         # Gemini API service
│   └── repository/     # Data layer abstraction
├── ui/
│   ├── screens/        # Compose screens (Main, History, Details, Settings)
│   ├── components/     # Reusable UI components
│   └── theme/          # Material 3 theme configuration
├── viewmodel/          # ViewModels for each screen
├── model/              # Data classes
├── utils/              # Helper functions
└── navigation/            # Navigation configuration
```

## Usage

1. **Add Your First Meal**
   - Tap the floating action button (+)
   - Choose to take a photo or select from gallery
   - Wait for AI analysis (usually takes a few seconds)
   - Review the nutritional estimate and tap "Save Meal"

2. **View Daily Summary**
   - The main screen shows your daily totals
   - Track calories and macros with progress bars

3. **Browse History**
   - Navigate to the History tab
   - Tap any meal to see details
   - Swipe or use delete button to remove meals

4. **Share Your Progress**
   - View a meal's details
   - Tap the share button
   - Choose how to share (WhatsApp, Messages, etc.)

5. **Settings**
   - Configure your Gemini API key
   - Toggle between light, dark, or system theme

## API Key Configuration

The app supports API key configuration in two ways:

1. **Via local.properties** (for development): Add `GEMINI_API_KEY=your_key` to `local.properties`
2. **Via Settings Screen** (within the app): Go to Settings and enter your API key

## Permissions

The app requires the following permissions:
- **Camera**: To take photos of meals
- **Storage/Media**: To access images from gallery
- **Internet**: To communicate with Gemini API

## Building from Source

1. Make sure you have the latest Android SDK tools installed
2. Open the project in Android Studio
3. Sync Gradle dependencies
4. Build the APK: `Build > Build Bundle(s) / APK(s) > Build APK(s)`
5. Install the APK on your device or run directly from Android Studio

## Troubleshooting

### "API key not found" Error
- Make sure you've added `GEMINI_API_KEY` to `local.properties` or entered it in Settings
- Verify the API key is correct and has proper permissions

### Camera Not Working
- Check that camera permission is granted in device settings
- Make sure your device has a camera

### Images Not Loading
- Verify storage/media permissions are granted
- Check internet connection for image analysis

## License

This project is open source and available for personal use.

## Contributing

Contributions, issues, and feature requests are welcome!

## Acknowledgments

- Powered by Google's Gemini AI
- Built with Jetpack Compose and Material Design 3

---

**Keep it up, Amigo! 💙**
