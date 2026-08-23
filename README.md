# Nearby Event Radar (Android)

**Nearby Event Radar** is an Android proximity networking application built with **Jetpack Compose**, **Bluetooth Low Energy (BLE)** scanning & broadcasting, and **Room Database** persistence for conferences, summits, and meetups.

## ✨ Core Features

- **Interactive Sonar Radar View**: Real-time rotating sweeping radar beam with range rings (5m, 15m, 30m), compass axes, and interactive pulsing blips for nearby attendees, speakers, investors, and venue beacons.
- **Proximity Calculations & Signal Estimator**: Live RSSI signal analysis and logarithmic path-loss distance estimation.
- **Smart Attendee Discovery**: Category color badges (Developers, Designers, Founders, Investors, Speakers, Organizers, Sponsors, Beacons), match affinity scores, and shared interest tags.
- **Contact & Badge Exchange**: Instant offline digital badge swap, in-event peer chat, and icebreaker suggestions.
- **Venue Zones & Event Schedule**: Live session schedule with speaker details, venue beacons (Main Stage, Coffee Lounge), and crowd density tracking.
- **My Digital Badge & Ghost Mode**: Customizable attendee pass with holographic QR matrix, BLE ID, and visibility modes (Broadcasting, In Conversation, Attending Talk, Ghost Mode).
- **Offline Persistence**: Room database caching for saved contacts, bookmarks, chat history, and personal profile.

## 🛠️ Architecture & Tech Stack

- **Framework**: Android SDK 35, Jetpack Compose, Material 3
- **Language**: Kotlin 2.0.21 (Coroutines & StateFlow)
- **Local Database**: AndroidX Room with KSP
- **Hardware Integration**: Android Bluetooth LE (`BluetoothLeScanner` / `BluetoothLeAdvertiser`) + Smart Proximity Simulation engine
- **Adaptive Launcher**: Modern Material You adaptive vector & raster icon
