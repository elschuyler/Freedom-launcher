# VibeForge Launcher 🚀

A powerful, customizable Android home screen launcher built entirely on a phone using Termux and Acode!

---

## ✨ Features

### 🏠 Home Screen
- Live clock and date display
- App grid with icons and labels
- Real-time app search bar
- Wallpaper background support
- Long press apps for quick options (Open / App Info / Uninstall)

### ⚙️ Settings
- **Themes** - Dark, Light, AMOLED, Ocean, Sunset
- **Grid Size** - 3 to 6 columns
- **Icon Size** - Small, Medium, Large
- **Clock & Date** - Show or hide
- **Gestures** - Swipe up and double tap actions

### 🔌 Plugins
- Weather Widget
- Battery Bar
- Step Counter
- Music Controls
- Quick Notes
- Calendar Widget
- System Monitor
- Gesture Shortcuts

### 💾 Backup & Restore
- Create timestamped backups
- Restore from any backup
- Delete old backups

---

## 📥 Download APK

Go to the **Actions** tab → Latest build → Download **VibeForge-APK**

---

## 🛠️ Building

This project is built automatically using GitHub Actions.

Every time you push code, GitHub builds the APK automatically.

To build manually:
```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📂 Project Structure

```
VibeForge/
├── build.gradle
├── settings.gradle
├── gradle/wrapper/
│   └── gradle-wrapper.properties
├── .github/workflows/
│   └── build.yml
└── app/
    ├── build.gradle
    └── src/main/
        ├── AndroidManifest.xml
        └── java/com/vibeforge/launcher/
            ├── MainActivity.java
            ├── SettingsActivity.java
            ├── PluginsActivity.java
            ├── PluginSettingsActivity.java
            └── BackupActivity.java
```

---

## 📱 Requirements

- Android 5.0 (Lollipop) or higher
- Minimum 1GB RAM

---

## 🗺️ Roadmap

- [ ] Widget support
- [ ] Icon packs
- [ ] Folder support
- [ ] Dock bar
- [ ] More plugins
- [ ] Theme creator
- [ ] Cloud backup

---

## 🧑‍💻 Built With

- Java (Android SDK)
- Built on Android phone using Termux + Acode
- Built using GitHub Actions

---

## 📄 License

MIT License - feel free to use and modify!
