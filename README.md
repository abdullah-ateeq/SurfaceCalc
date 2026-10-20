# SurfaceCalc 📐

**SurfaceCalc** is a modern, high-precision Android application built with **Kotlin** and **Jetpack Compose**. It provides an offline, privacy-focused utility for calculating the total surface area of 3D geometric shapes with exact mathematical precision, mixed unit conversions, step-by-step formula breakdowns, and calculation history.

---

## ✨ Features

- 🧊 **Supported 3D Shapes**:
  - **Cube**: $A = 6s^2$
  - **Rectangular Prism**: $A = 2(lw + lh + wh)$
  - **Cylinder**: $A = 2\pi r^2 + 2\pi rh$
  - **Sphere**: $A = 4\pi r^2$
  - **Cone**: $A = \pi r^2 + \pi rl$ *(using slant height $l$)*
  - **Square Pyramid**: $A = s^2 + 2sl$ *(using slant height $l$)*
  - **Triangular Prism**: $A = 2T + (a + b + c)L$ *(using Heron's formula for base area $T$)*
  - **Hemisphere**: $A = 3\pi r^2$ *(including base)*

- 📐 **High-Precision Mathematics**:
  - Built on `BigDecimal` arithmetic to eliminate floating-point precision errors.
  - Utilizes a 50-digit constant of $\pi$.
  - Customizable decimal precision (0 to 10 decimal places) and configurable rounding modes (`HALF_UP`, `HALF_EVEN`, `FLOOR`, `CEILING`, etc.).

- 🔄 **Flexible Unit Conversions**:
  - **Linear Length Units**: Millimeters (mm), Centimeters (cm), Meters (m), Kilometers (km), Inches (in), Feet (ft), Yards (yd).
  - **Area Output Units**: $\text{mm}^2$, $\text{cm}^2$, $\text{m}^2$, $\text{km}^2$, $\text{in}^2$, $\text{ft}^2$, $\text{yd}^2$.
  - Supports **mixed input units** per dimension with automatic normalization and squared conversion factors.

- 📊 **Step-by-Step Calculation Breakdown**:
  - Dynamic formula display and detailed, line-by-line breakdown of every calculation step.
  - Visual custom vector 3D shape diagrams.

- 📜 **History & Storage**:
  - Local history storage for past calculations with fast restore.
  - Search and filter past calculations.
  - Quick actions to copy results or complete breakdowns to clipboard, plus native Android system sharing.

- 🎨 **Modern Compose UI & Customization**:
  - Material 3 Design with dynamic colors (Android 12+).
  - System, Dark, and Light theme modes.
  - Customizable display settings (toggle diagrams, formulas, haptic feedback, and animations).

- 🔒 **100% Private & Offline**:
  - Zero internet permissions required (`android.permission.INTERNET` is not used).
  - No ads, no analytics, no third-party tracking. All calculations and data stay entirely on device.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/) 2.2+
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Unidirectional Data Flow with `StateFlow`
- **Navigation**: Navigation Compose
- **Target SDK**: 37 (Android 15+)
- **Minimum SDK**: 26 (Android 8.0 Oreo)
- **Build System**: Gradle 9+ with Version Catalog (`gradle/libs.versions.toml`)

---

## 🧪 Testing

The project includes unit tests for domain logic, unit conversions, formula calculations, parser/validators, and repositories.

To run the test suite:

```bash
./gradlew test
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** (Ladybug / Jellyfish / 2024.2+ recommended)
- **JDK 17** or higher
- Android SDK 37

### Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/SurfaceCalc.git
   cd SurfaceCalc
   ```

2. Open the project in Android Studio.

3. Build and run the app on an emulator or physical device:
   ```bash
   ./gradlew installDebug
   ```

---

## 📱 Screenshots & Previews

The app consists of four main screens:
1. **Home**: Shape selection, interactive numeric input fields with unit selectors, live calculation result, and detailed formula step breakdown.
2. **History**: Past calculations list with restore, filter, delete, and copy/share capabilities.
3. **Settings**: Precision preferences, rounding modes, UI customization, and theme selection.
4. **About**: App details, feature overview, and privacy assurances.

---

## 📄 License

```text
Copyright (c) 2025 SurfaceCalc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
