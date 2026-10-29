# OLED Saver for Android TV

## Modules

| Module | Kind | Needs |
|---|---|---|
| `:core` | pure Kotlin/JVM: operating hours, launch decision, alarm timing, snooze delays | JDK 17+, Maven Central, Gradle Plugin Portal |
| `:app` | Android TV app: activities, fragments, DataStore, AlarmManager, animations | JDK 21, Android SDK, Google Maven (`dl.google.com`) |

`:app` is only included in the build when an SDK location is known (`sdk.dir` in
`local.properties`, `ANDROID_HOME` or `ANDROID_SDK_ROOT`). Without one, Gradle configures
`:core` alone, so in an environment without Android tooling you can still run:

```bash
gradle :core:test          # unit tests for the logic
gradle :core:build         # compile + test + jar
```

Force the Android module on or off with `-PincludeAndroidApp=true|false`.

Rule of thumb: anything that does not import `android.*` or AndroidX belongs in `:core`.
`:app` wraps it in thin adapters (e.g. `AndroidOperatingHours` reads `BuildConfig`/`Build`
and hands a plain `OperatingHours` to `ConfigProvider`).

## Sandbox builds (no Google Maven, no SDK download)

Some environments can reach Maven Central but not `dl.google.com` or the Android SDK site.
For those, prepare a self-contained copy of the project on a machine with internet access:

```bash
sandbox/prepare-offline.sh              # defaults: --target-os linux --target-arch x86_64
```

It creates `offline/` (gitignored, roughly 400 MB): `m2/` with every Maven artifact the build
needs (plugins, Kotlin compiler, AndroidX, Android Gradle Plugin, plus `aapt2`/`protoc` for the
target OS), `sdk/` with the Android platform and build-tools for the target OS, and a
`MANIFEST`. It ends with a self-check that builds from the bundle alone. Everything is staged
in a temp dir and moved into place; a previous bundle is moved aside and removed only after
the self-check passes.

Then zip or copy the whole project directory, including `offline/`, into the sandbox and run
plain `gradle build` there (Gradle 8.13+, JDK 17 for `:core`, JDK 21 for `:app`).
`settings.gradle` switches to the bundle automatically when it is present and the remote
repositories are unreachable, and writes `sdk.dir` for the bundled SDK into
`local.properties`. Force the mode with `-Poffline=true|false` or `OLED_OFFLINE=1|0`.
Instrumented tests still need a device or emulator.

Re-run the script after changing dependency versions; `offline/MANIFEST` records the hash of
the build files it was made from.

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