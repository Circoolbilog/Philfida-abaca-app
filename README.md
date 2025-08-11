# Abaca Disease Detection and Monitoring App

A mobile application developed for the Philippine Fiber Industry Development Authority (PhilFIDA) to assist in the detection and monitoring of abaca plant diseases using computer vision and machine learning.

## Features

- 📸 Capture and analyze abaca plant images for disease detection
- 🎯 Real-time disease identification using TensorFlow Lite models
- 📍 Location-based disease tracking and monitoring
- 📊 Comprehensive disease information and management recommendations
- 🔍 Image gallery for reviewing previous detections with delete functionality
- 🌐 Offline-first functionality with cloud sync capabilities
- 📦 **NEW**: Bounding box visualization to show exact detection locations
- 🏆 **NEW**: Multi-disease detection with confidence-based ranking
- ⭐ **NEW**: Visual indicators for highest confidence detections
- 🎨 **NEW**: Modern Material Design 3 interface

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
   git clone https://github.com/Circoolbilog/Philfida-abaca-app
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
- **Multi-Disease Display**: All detected diseases shown sorted by confidence with star (⭐) indicators for highest confidence
- **Bounding Box Visualization**: Click "Show Boxes" button to see exact detection locations on images
- **Enhanced Information**: Comprehensive detection metadata including symptoms, confidence scores, and location
- **Gallery Management**: View, analyze, and delete previously captured images
- **Real-time Confidence**: Actual ML model confidence scores displayed instead of default values

### Data Management
- All detections are stored locally on the device
- Export data for further analysis (CSV/Excel format)
- Sync data with the cloud when internet connection is available

### More Detailed Examples
1. **Enhanced Disease Detection**:
   - Open the camera/diagnose screen
   - Capture image with proper lighting and steady hands
   - View detection results with diseases ranked by confidence
   - Highest confidence disease highlighted with star (⭐) indicator
   - Multiple diseases and symptoms detected simultaneously

2. **Visual Analysis**:
   - Navigate to Gallery and select any captured image
   - Click "Show Boxes" to visualize detection bounding boxes
   - Red rectangles show exact locations where diseases were detected
   - Toggle between original image and annotated version

3. **Assessment Review**:
   - Navigate to Assessment section
   - View historical diagnoses with enhanced metadata
   - See confidence scores, location data, and detection timestamps
   - Delete unwanted captures with integrated database cleanup

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
The app processes image data through an enhanced pipeline with comprehensive metadata storage.

```
Camera Input -> Image Processing -> TFLite Model Detection -> Multi-Disease Analysis -> 
Confidence Ranking -> Bounding Box Storage -> Database Integration -> Visual Display
```

Key interactions:
- **Enhanced Detection**: TensorFlow Lite model identifies multiple diseases with real confidence scores
- **Comprehensive Storage**: Bounding boxes, confidence scores, symptoms, and location data stored in SQLite database
- **Visual Analysis**: Bounding box coordinates enable precise detection visualization
- **Multi-Disease Ranking**: All detected diseases sorted by confidence with visual indicators
- **Metadata Integration**: Complete detection metadata available for analysis and export
- **Real-time Processing**: Improved error handling and performance optimization
### Firebase Resources
- Authentication: User management and login
- Realtime Database: User profiles and shared disease data
- Storage: Disease images and reference materials

### Local Storage
- **Enhanced SQLite Database**: 
  - Disease information and comprehensive detection history
  - Bounding box coordinates and confidence scores
  - Location metadata and timestamps
  - Symptom-to-disease mapping with real confidence values
- **File Storage**: Captured images with integrated database references
- **Shared Preferences**: User settings and detection thresholds

## Development

### Technical Stack
- **Language**: Kotlin (with Java interop)
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI Framework**: Material Design 3 components
- **Libraries**:
  - AndroidX Core KTX
  - CameraX for camera operations
  - TensorFlow Lite for ML inference
  - SQLite for local database
  - Gson for JSON parsing
  - Google Play Services (Location, Maps)
  - Material Design Components
  - Coroutines for async operations

### Building the Project

1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Build and run on a connected device or emulator

### Recent Updates (v1.4.0)

### ✅ Completed Features
- [x] **Bounding Box Visualization**: Users can now see exact detection locations
- [x] **Multi-Disease Detection**: All diseases displayed with confidence ranking
- [x] **Material Design 3**: Modern UI with improved user experience
- [x] **Enhanced Database**: Comprehensive metadata storage and retrieval
- [x] **Gallery Management**: Delete functionality with database integration
- [x] **Real Confidence Scores**: Actual ML model confidence values displayed
- [x] **Visual Indicators**: Star (⭐) highlighting for highest confidence detections
- [x] **Improved Error Handling**: Better stability and user feedback

### TODO - Future Enhancements

### High Priority
- [ ] Implement batch image processing for multiple plant analysis
- [ ] Add disease severity assessment scoring
- [ ] Implement user authentication and profile management
- [ ] Add treatment recommendation system based on detected diseases

### Medium Priority
- [ ] Add multi-language support (Filipino, Cebuano)
- [ ] Implement data export to PDF reports with bounding box annotations
- [ ] Create comprehensive analytics dashboard
- [ ] Add offline map integration for field work

### Low Priority
- [ ] Implement dark mode theme
- [ ] Add voice-guided capture instructions
- [ ] Create tutorial/onboarding flow
- [ ] Implement advanced filtering and search options
- [ ] Add social sharing features for detection results

### Technical Improvements
- [ ] Optimize TensorFlow Lite model size and performance
- [ ] Add unit and integration tests
- [ ] Implement proper data migration strategies
- [ ] Add crash reporting and analytics
- [ ] Optimize memory usage for large image processing

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Version History

### v1.4.0 (Latest) - "Enhanced Detection & Visual Analysis"
- Bounding box visualization for precise detection locations
- Multi-disease detection with confidence-based ranking
- Material Design 3 interface improvements
- Enhanced database with comprehensive metadata storage
- Gallery management with delete functionality
- Real confidence scores from ML model
- Visual indicators for highest confidence detections

### v1.3.0 - Previous stable release
- Basic disease detection and classification
- Image capture and gallery functionality
- Location-based tracking
- Firebase integration

## Acknowledgments

We gratefully acknowledge the support and contributions of the following:

- **Philippine Fiber Industry Development Authority (DA-PhilFIDA)** – Implementing agency
- **Biotech Program Office (DA-BPO)** – Funding support
- **Bureau of Agricultural Research (DA-BAR)** – Funding support
- **TensorFlow Lite Team** – Machine learning framework
- **Material Design Team** – UI/UX framework
- **Android CameraX Team** – Camera functionality
