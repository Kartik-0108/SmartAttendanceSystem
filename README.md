# FaceTrack 🚀

**FaceTrack** is a production-grade, AI-powered attendance management system built for Android. It leverages on-device Machine Learning for real-time face recognition and liveness detection, ensuring high security and privacy with a modern, offline-first architecture.

---

## ✨ Key Features

### 🔐 Secure Staff Access
- **Firebase Authentication**: Secure login and registration for authorized staff.
- **Google Sign-In**: (Optional/Scalable) Seamless entry for organizational accounts.

### 👤 Student Management & Registration
- **Face Fingerprinting**: Converts student faces into 128-dimension mathematical embeddings (vectors) using **MobileFaceNet**.
- **On-Device Storage**: Biometric data remains on the device, ensuring user privacy.
- **Profile Capture**: Saves a master cropped face photo for visual verification.

### 📸 Smart Attendance (Real-Time)
- **Face Recognition**: Real-time vector matching using **Cosine Similarity** (Threshold > 0.8).
- **Liveness Verification (Anti-Spoofing)**: Uses **MediaPipe** to track Eye Aspect Ratio (EAR) and detect blinks, preventing photo/video spoofing attacks.
- **Attendance Cooldown**: Smart logic to prevent multiple entries for the same person within a session.

### 📊 Reporting & Analytics
- **Visual History**: Every attendance record includes the live face captured during scanning.
- **PDF Export**: Generate professional attendance reports directly from the app.
- **Cloud Sync**: Offline-first architecture using **Room + Firestore**. Data syncs automatically when the internet is available.

---

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **AI/ML Engine**: 
    - **MediaPipe**: Face Detection & Landmark Tracking
    - **TensorFlow Lite**: MobileFaceNet Inference
- **Database**: 
    - **Room**: Local Source of Truth
    - **Firebase Firestore**: Cloud Synchronization
- **Camera**: CameraX (Analysis API)
- **Image Loading**: Coil (Coroutines Image Loader)

---

## 🏗 Architecture Overview

```text
Camera Stream (CameraX)
       ↓
    Frame Analyzer (Atomic Lock)
       ↓
    MediaPipe Landmark Mesh ──→ [Liveness Check (Blink EAR)]
       ↓
    TFLite Embedding Generator (MobileFaceNet)
       ↓
    Cosine Similarity Matcher (Room Database Search)
       ↓
    Success Handler (Save Record + Push to Firestore)
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or newer
- Min SDK 26 (Android 8.0)
- Firebase Project setup

### Installation
1. Clone the repository.
2. Place your `google-services.json` in the `app/` folder.
3. Sync the project with Gradle.
4. Run on a physical Android device (for camera and ML performance).

---

## 📈 Project Status

- [x] Phase 1 — Project Rebranding & UI Overhaul
- [x] Phase 2 — TFLite Recognition Pipeline
- [x] Phase 3 — Anti-Spoofing (Blink Detection)
- [x] Phase 4 — PDF Export Utility
- [x] Phase 5 — Firebase Auth & Firestore Sync
- [ ] Phase 6 — Performance Optimization (GPU Delegate)
- [ ] Phase 7 — Advanced Analytics Dashboard

---

## ✍️ Author

**Kartik Devadiga**  
*Android Developer | AI & ML Enthusiast*

---

## 📜 License

This project is licensed under the Apache 2.0 License - see the LICENSE file for details.
