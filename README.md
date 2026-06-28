# 🕌 Prayer Time & Qibla Locator App

## 📖 Overview
An Android application that helps users with daily Islamic activities by providing:

- 🕒 Prayer Times  
- 🧭 Qibla Direction  
- 📿 Digital Tasbih Counter  
- ⚙️ Customizable Settings  

---

## ✨ Features
- ✅ Prayer time calculation using Adhan Library  
- ✅ Qibla direction using device sensors  
- ✅ Digital Tasbih counter with target tracking  
- ✅ 12-hour and 24-hour time formats  
- ✅ Hijri and Gregorian dates  
- ✅ User preferences saved using SharedPreferences  

---

## 🛠️ Technologies Used
- Java  
- Android Studio  
- SQLite  
- SharedPreferences  
- Material Design  
- Adhan Library  
- SensorManager API  

---

## 📂 Project Structure
```text
MainActivity.java
PrayerTimesFragment.java
QiblaFragment.java
TasbihFragment.java
SettingsFragment.java
PreferencesHelper.java
DatabaseHelper.java
```
---

## User Interface

The application uses a simple and user-friendly interface with a Bottom Navigation Bar for easy access to its four main features:

- 🕋 **Prayer Times Screen** – Displays daily prayer times, dates, and the next prayer countdown.
- 🧭 **Qibla Locator Screen** – Shows a compass that points toward the Kaaba.
- 📿 **Tasbih Screen** – Provides a digital Tasbih counter with progress tracking.
- ⚙️ **Settings Screen** – Allows users to customize preferences such as time format, notifications, and haptic feedback.

---

## 🚀 How to Run the Project
1. Clone the repository:
   ```bash
   git clone https://github.com/menahals/PrayerTime-Qibla-Locator.git
   ```
2. Open the project in Android Studio
3. Allow Gradle to sync
4. Run the app on Emulator OR Physical Android device
