# OLED Saver for Android TV

## Development Setup

### Java Home Configuration

This project requires JDK 21. The Java home path is configured in `local.properties` (machine-specific, not committed to version control).

Update the `org.gradle.java.home` path in `local.properties` to match your local JDK installation:

**Mac example:**
```properties
org.gradle.java.home=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
```

**Windows example:**
```properties
org.gradle.java.home=D:/jdk/temurin-21
```

## Debugging

View logs from connected device:
```bash
adb -s 192.168.0.3:5555 logcat "oled-saver:* *:S"
```