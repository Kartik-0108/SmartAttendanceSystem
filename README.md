# Smart Attendance System

AI-powered Android Attendance System using Face Recognition, Anti-Spoofing Detection, and Real-Time ML Processing.

---

# Features

## Student Registration
- Register students with:
    - Name
    - Roll Number
    - Department/Class
- Capture multiple face images
- Store face embeddings

## Smart Attendance
- Real-time face detection
- Automatic attendance marking
- Date & time logging
- Live camera processing

## Anti-Spoofing Security
- Blink detection
- Head movement verification
- Photo attack prevention
- Video replay attack prevention
- Liveness verification

## Reports & Analytics
- Daily attendance report
- Weekly attendance report
- Monthly attendance percentage
- CSV export
- PDF export
- Attendance analytics dashboard

## Admin Dashboard
- Search students
- View attendance records
- Export reports
- Attendance statistics

---

# Tech Stack

## Android Development
- Kotlin
- Jetpack Compose
- CameraX
- MVVM Architecture
- Material 3

## Machine Learning
- TensorFlow Lite
- MediaPipe Face Detection
- MediaPipe Face Mesh
- MobileFaceNet
- On-device ML Inference

## Backend
- FastAPI
- PostgreSQL
- REST API

---

# Current Progress

## Completed

- [x] Phase 1 — Project Setup
- [x] Phase 2 — Android UI System
- [x] Phase 3 — CameraX Integration
- [x] Phase 4 — Real-Time Face Detection

## Upcoming

- [ ] Phase 5 — Face Recognition
- [ ] Phase 6 — Anti-Spoofing Detection
- [ ] Phase 7 — Backend Integration
- [ ] Phase 8 — Attendance Logic
- [ ] Phase 9 — Reports System
- [ ] Phase 10 — Deployment & Optimization

---

# Current Working Features

- Real-time camera preview
- Front camera support
- Live frame analysis
- MediaPipe face detection
- Real-time face count detection
- ML processing pipeline

---

# Project Architecture

```text
CameraX
   ↓
Frame Analyzer
   ↓
MediaPipe Face Detection
   ↓
Face Recognition
   ↓
Anti-Spoofing Verification
   ↓
Attendance System
```

---

# Folder Structure

```text
app/
 ├── data/
 │   ├── local/
 │   ├── remote/
 │   └── repository/
 │
 ├── domain/
 │   ├── model/
 │   ├── repository/
 │   └── usecase/
 │
 ├── presentation/
 │   ├── screens/
 │   ├── navigation/
 │   ├── components/
 │   ├── viewmodel/
 │   └── theme/
 │
 ├── ml/
 │   ├── facedetection/
 │   ├── facerecognition/
 │   └── antispoof/
 │
 └── utils/
```

---

# Future Improvements

- Cloud synchronization
- Multi-face attendance
- Face recognition optimization
- Offline attendance mode
- Admin web panel
- AI attendance analytics

---

# Author

## Kartik Devadiga

Android Developer | AI Enthusiast | Full Stack Learner

---

# License

This project is developed for educational, research, and portfolio purposes.