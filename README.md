![App banner](assets/Card_UnitWise.png)

<p align="center">
  <a href="https://kotlinlang.org/">
    <img src="https://img.shields.io/badge/Kotlin-102214.svg?style=for-the-badge&logo=kotlin&logoColor=white" alt="KOTLIN" />
  </a>
  <a href="https://developer.android.com/compose">
    <img src="https://img.shields.io/badge/Jetpack%20Compose-102214.svg?style=for-the-badge&logo=jetpack-compose&logoColor=white" alt="JETPACK COMPOSE"/>
  </a>
  <a href="https://developer.android.com/studio?hl=es-419">
    <img src="https://img.shields.io/badge/Android%20Studio-102214.svg?style=for-the-badge&logo=android-studio&logoColor=white" alt="ANDROID STUDIO" />
  </a>
  <img src="https://img.shields.io/badge/Google%20Play-102214.svg?style=for-the-badge&logo=google-play&logoColor=white" alt="GOOGLE PLAY" />
</p>

<p align="center">
  <a href="https://www.figma.com/design/f4MiIjPaYwh5UOZeIPF82c/UnitWise?node-id=2-11&t=Is5ZVGDDyFk6Bzhz-1">
    <img alt="Figma" src="https://img.shields.io/badge/Figma-102214.svg?style=for-the-badge&logo=figma&logoColor=white"/>
  </a>
  <a href="https://github.com/BR444N/UnitWise">
    <img alt="GitHub" src="https://img.shields.io/badge/GitHub-102214.svg?style=for-the-badge&logo=github&logoColor=white"/>
  </a>
</p>

<p align="center">
  <img alt="API" src="https://img.shields.io/badge/Android_API-36-102214.svg?style=for-the-badge&logo=android&logoColor=white"/>
  <img alt="Version" src="https://img.shields.io/badge/Version-v1.0-102214.svg?style=for-the-badge&logo=git&logoColor=white"/>
  <a href="https://github.com/BR444N/UnitWise/blob/main/LICENSE">
    <img alt="License" src="https://img.shields.io/badge/License-MIT-102214.svg?style=for-the-badge&logo=open-source-initiative&logoColor=white"/>
  </a>
</p>

# 🛒 UnitWise — Smart Grocery Comparison App

**UnitWise** is an Android application designed to help users **compare supermarket products and save money** by identifying the best value based on unit pricing.

Instead of guessing which product is cheaper, UnitWise calculates and compares prices automatically, allowing users to make faster and smarter shopping decisions.

The app focuses on simplicity, clarity, and real-world usefulness during everyday grocery supermarket shopping.

---

## Table of Content

- [# 🛒 UnitWise — Smart Grocery Comparison App](#-unitwise--smart-grocery-comparison-app)
- [# ✨ Features](#-features)
- [# 🔜 Coming Soon](#-coming-soon)
- [# 🧠 Problem It Solves](#-problem-it-solves)
- [# 🛠️ Tech Stack](#-tech-stack)
- [# 📱 Platform](#-platform)
- [# 🚀 Project Status](#-project-status)
- [# 🔒 Privacy](#-privacy)
- [# 🤝 Contribution](#-contribution)
- [# 👨‍💻 Author](#-author)

---

# ✨ Features

- **📊 Product Comparison:**
Compare multiple grocery products by price and quantity.
Automatic unit price calculation.

- **📷 Camera Product Scanning:**
Scan product names using the device camera.
Quickly add products without manual typing.

- **🧾 Comparison History:**
Save previous comparisons.
Revisit and reuse results anytime.

- **🎨 Modern UI:**
Built with Jetpack Compose.
Minimal and accessibility-focused design system.

---

# 🔜 Coming Soon

- **🔗 Share Comparisons:**
  Share results with friends or family.
  Generate shareable comparison summaries.

---

# 🧠 Problem It Solves

Many supermarket products use different sizes and prices, making it difficult to know which option is actually cheaper.

UnitWise solves this by:

- Standardizing price comparisons.
- Showing the real cost per unit.
- Reducing decision time while shopping.

---

# 🖼️ Visual Evolution: AI Inspiration vs. Human-Centered Design

This table showcases the iterative process of taking raw AI concepts and refining them into a production-ready Android interface.

|                          Stitch AI Exploration (Inspiration)                           |                      Human-Centered Refinement (Final Design)                      | Key Iteration Detail                                                                                                                                                                                                                                                                                                                                                                                               |
|:--------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| <img src="assets/UI_UnitWise.png" width="180" alt="Stitch AI Concept for Home Screen"> |  <img src="assets/App_UnitWise.png" width="180" alt="UnitWise Final Home Screen">  | **Simplified UX:** The AI-generated "Smart Shopper" layout erroneously included features like 'Lists' and 'Trends,' cluttering the interface. My final design strips away this unnecessary noise, simplifying the navigation from four sections to just Home and History. This refinement refocuses UnitWise on its primary mission: providing the fastest, most intuitive tool for on-the-spot price comparisons. |
| <img src="assets/UI_Scann.png" width="180" alt="Stitch AI Concept for Scan Products">  | <img src="assets/UI_Scann.png" width="180" alt="UnitWise Final Concept Solution "> | **Focused Precision for ML Kit:** The core iteration here is on the focused text acquisition workflow. In the AI-generated concept (left), the entire screen appears to be active for ML Kit processing, which could lead to detecting noise from surrounding product labels. My final design introduces a constrained green framing grid. ML Kit is only programmed to analyze text within this precise frame.                 |
|             <img src="assets/UI_Results.png" width="180" alt="Stitch AI ">             |      <img src="assets/App_Comparison.png" width="180" alt="UnitWise Final 3">      | **Native Feel:** While the AI envisioned a full-fledged e-commerce platform with product images, brands, and "Add to Cart" functions, UnitWise is designed as a standalone utility for pure price-per-unit mathematics. My functional app does not access external product databases and, therefore, does not display images.                                                                                                                                                                                                                                                                          |

---

# 🛠️ Tech Stack

## 📱 Core & UI
* **[Kotlin](https://kotlinlang.org/):** Main language utilizing Coroutines and Flow for asynchronous programming.
* **[Jetpack Compose](https://developer.android.com/compose):** Modern declarative UI with Material 3 and Material Icons Extended.
* **[Lottie Compose](https://github.com/airbnb/lottie-android):** Interactive vector animations for enhanced UX.
* **[Splashscreen API](https://developer.android.com/develop/ui/views/launch/splash-screen):** Native implementation for the application's launch screen.

## 🏛️ Architecture & Data
* **[MVVM (Model-View-ViewModel)](https://www.geeksforgeeks.org/android/mvvm-model-view-viewmodel-architecture-pattern-in-android/):** Pattern used for separation of concerns between business logic and UI.
* **[Room Persistence](https://developer.android.com/training/data-storage/room):** Local database with KSP (Kotlin Symbol Processing) support.
* **[Navigation Compose](https://developer.android.com/develop/ui/compose/navigation):** Robust route management and screen-to-screen navigation.
* **[Lifecycle & ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel):** Efficient UI state management and lifecycle handling.

## 🔍 AI & Hardware Integration
* **[ML Kit Text Recognition](https://developers.google.com/ml-kit/vision/text-recognition):** On-device image processing for automatic price and product detection.
* **[CameraX](https://developer.android.com/training/camerax):** Robust camera integration for scanning price tags or grocery receipts.

## 🧪 Testing & Quality Assurance (QA)
* **Unit Testing:** [JUnit](https://junit.org/), [MockK](https://mockk.io/) for mocking, [Turbine](https://github.com/cashapp/turbine) for Flow testing, and [Google Truth](https://truth.dev/) for fluent assertions.
* **UI & Instrumentation Testing:** [Espresso](https://developer.android.com/training/testing/espresso) and Compose UI Test to ensure interface integrity.
* **KSP (Kotlin Symbol Processing):** High-performance code generation for Room database.

---

# 📱 Platform

* Android Native Application
* Minimum SDK: **24+**
* Target SDK: **Latest Android version (36)**

---

# 🚀 Project Status

Current stage:

* ✅ MVP completed
* ✅ Closed testing on Google Play
* 🔄 Continuous improvements and UX refinements

---

# Design Process

The UI design combines:

* AI-assisted exploration using Stitch AI
* Manual UX/UI refinement and decision-making
* Iterative usability validation

AI designs were used as inspiration, while final interfaces were adapted to better match real user workflows.

---

# 🔒 Privacy

- UnitWise does not require unnecessary personal data.
- All comparisons are stored locally.

---

# 🤝 Contribution

This project is currently maintained as a personal production app.
**Suggestions and feedback are welcome through issues.**

---

# 👨‍💻 Author
**[BR444N](https://github.com/BR444N)**

Developed as a native Android project focused on learning, product design, and real-world usability.

📧 **Contact:** [josebrandonmartinezrios@duck.com](mailto:josebrandonmartinezrios@duck.com)