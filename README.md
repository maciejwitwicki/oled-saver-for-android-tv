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

## Operating hours override (emulator only)

Masking runs between 07:10 and 12:00 (Europe/Warsaw). To test at any time of day, add to
`local.properties`:

```properties
# window always contains the current time
oledsaver.operatingHours=now
# or an explicit window
oledsaver.operatingHours=14:00-18:30
```

The value is compiled into **debug** builds only, and applied at runtime only when the
build runs on an **emulator**. A debug build installed on the TV, and every release build,
keeps the 07:10-12:00 window. The effective window is logged at startup as
`ConfigProvider - operating hours: ...`.

## Debugging

View logs from connected device:
```bash
adb -s 192.168.0.3:5555 logcat "oled-saver:* *:S"
```