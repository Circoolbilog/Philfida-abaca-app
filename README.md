# Abaca Disease Detection and Monitoring App

A mobile application developed for the Philippine Fiber Industry Development Authority (PhilFIDA) to assist in the detection and monitoring of abaca plant diseases using computer vision and machine learning.

## Features

- 📸 Capture and analyze abaca plant images for disease detection
- 🎯 Real-time disease identification using TensorFlow Lite models
- 📍 Location-based disease tracking and monitoring
- 📊 Comprehensive disease information and management recommendations
- 🔍 Image gallery for reviewing previous detections
- 🌐 Offline-first functionality with cloud sync capabilities

## Project Structure

```
app/
├── src/main/
│   ├── java/ph/gov/philfida/da/abaddapp/  # Main package
│   │   ├── activities/     # Activity classes
│   │   ├── adapters/       # RecyclerView adapters
│   │   ├── fragments/      # UI fragments
│   │   ├── models/         # Data models
│   │   └── utils/          # Utility classes
│   ├── res/                # Android resources
│   │   ├── layout/         # UI layouts
│   │   ├── values/         # Strings, colors, styles
│   │   └── ...
│   └── assets/             # ML models and assets
├── build.gradle            # Module build configuration
└── ...

gradle/                     # Gradle wrapper files
build.gradle               # Project build configuration
settings.gradle            # Project settings
```

## Getting Started

### Prerequisites

- Android Studio Flamingo (2022.2.1) or later
- Android SDK 33 (Android 13) or higher
- Android device with camera (or emulator with camera support)
- Minimum Android version: 5.0 (API level 21)
- Google Play Services (for Maps and Location)

### Required Permissions

- `CAMERA` - For capturing images of abaca plants
- `ACCESS_FINE_LOCATION` - For geotagging disease detections
- `READ_EXTERNAL_STORAGE` - For accessing saved images
- `WRITE_EXTERNAL_STORAGE` - For saving captured images and results

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-organization/Philfida-abaca-app.git
   cd Philfida-abaca-app
   ```

2. Open the project in Android Studio:
   - Select "Open an existing Android Studio project"
   - Navigate to the project directory and select it
   - Wait for Gradle sync to complete

3. Configure Firebase (if needed):
   - Add your `google-services.json` to the `app/` directory
   - Enable required Firebase services in the Firebase Console

4. Build and run:
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio or use:
     ```bash
     ./gradlew installDebug
     ```

## Usage Guide

### Capturing Images
1. Open the app and navigate to the camera screen
2. Position the camera to focus on the abaca plant's leaves or stems
3. Ensure good lighting conditions for better detection accuracy
4. Tap the capture button to take a photo
5. The app will process the image and display detection results
  989
### Viewing Results
- Detected diseases are shown with confidence scores
- Tap on a result to view detailed information
- View previously captured images in the gallery
- Filter and sort detections by date, location, or disease type

### Data Management
- All detections are stored locally on the device
- Export data for further analysis (CSV/Excel format)
- Sync data with the cloud when internet connection is available

### More Detailed Examples
1. Disease Detection:
- Open the Diagnose screen
- Adjust detection confidence threshold if needed (default 0.5)
- Hold camera steady for best results
- Multiple symptoms can be detected simultaneously

2. Assessment Review:
- Navigate to Assessment section
- View historical diagnoses grouped by capture session
- Filter by date or location
- Export data for further analysis

### Troubleshooting
Common issues:

1. Camera Preview Not Working
- Ensure camera permissions are granted
- Check if another app is using the camera
- Restart the app

2. Location Not Available
- Enable GPS on device
- Grant location permissions
- Move to area with better GPS signal

3. Detection Performance Issues
- Ensure good lighting conditions
- Hold camera steady
- Adjust detection threshold in settings
- Check available device memory

## Data Flow
The app processes image data through a pipeline that starts with camera capture and ends with disease detection and storage.

```
Camera Input -> Image Processing -> TFLite Model Detection -> Result Analysis -> 
Disease Classification -> Local Storage -> Optional Cloud Sync
```

Key interactions:
- Camera preview provides real-time feed to TensorFlow Lite detector
- Detector identifies symptoms and calculates confidence scores
- Results are matched against disease database
- Detected diseases and symptoms are stored locally with geolocation
- Data can be reviewed and analyzed in Assessment section
- Optional synchronization with Firebase backend
- Location data helps track disease spread patterns
### Firebase Resources
- Authentication: User management and login
- Realtime Database: User profiles and shared disease data
- Storage: Disease images and reference materials

### Local Storage
- SQLite Database: Disease information and detection history
- File Storage: Captured images and processed results
- Shared Preferences: User settings and preferences

## Development

### Technical Stack
- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Libraries**:
  - AndroidX Core KTX
  - CameraX
  - TensorFlow Lite
  - Room Database
  - Dagger Hilt for dependency injection
  - Coroutines & Flow
  - Google Maps SDK

### Building the Project

1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Build and run on a connected device or emulator

### TODO

### High Priority
- [ ] Implement batch image processing for multiple plant analysis
- [ ] Add disease severity assessment scoring
- [ ] Implement user authentication and profile management

### Medium Priority
- [ ] Develop treatment recommendation system
- [ ] Add multi-language support (Filipino, Cebuano)
- [ ] Implement data export to PDF reports

### Low Priority
- [ ] Implement dark mode theme
- [ ] Add voice-guided capture instructions
- [ ] Create tutorial/onboarding flow
- [ ] Implement advanced filtering and search options

### Technical Improvements
- [ ] Optimize TensorFlow Lite model size and performance
- [ ] Implement proper error handling and crash reporting
- [ ] Add unit and integration tests
- [ ] Improve camera preview performance
- [ ] Implement proper data migration strategies

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

We gratefully acknowledge the support and contributions of the following:

- **Philippine Fiber Industry Development Authority (DA-PhilFIDA)** – Implementing agency
- **Biotech Program Office (DA-BPO)** – Funding support
- **Bureau of Agricultural Research (DA-BAR)** – Funding support
- **TensorFlow Lite Team** – Machine learning framework
