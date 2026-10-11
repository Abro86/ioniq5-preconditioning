# Building Launchpad and the TUNE-knob service

The Android project builds Launchpad 1.2.0 (version code 3), package
`com.paytondev.launchpad`, for Android 4.4/API 19. The TUNE daemon source is in
`app/src/main/java/com/paytondev/launchpad/knob/`; its NEMESIS scripts and input
profile are in `tune-service/`.

## Requirements

- JDK 17 or later supported by the bundled Gradle/Android plugin (JDK 25 is used
  for local verification).
- Android SDK platform 37 and build-tools 36.0.0.
- Python 3 for packaging.

Set `JAVA_HOME` and `ANDROID_HOME` for your machine, or configure the SDK path
in an untracked `local.properties` file. The Gradle wrapper downloads Gradle;
the first build also downloads Android build dependencies.

## Tests and development builds

```sh
./gradlew :app:testDebugUnitTest :app:lintRelease :app:assembleDebug :app:assembleRelease
```

Debug uses Android's local debug signing key. On a ranchu/goldfish emulator, the
debug vehicle client simulates a pending request; it never reports heating from
a timer. Release uses the OEM client and rejects incompatible firmware.

The unsigned release is at
`app/build/outputs/apk/release/app-release-unsigned.apk`. Do not install it as a
finished release. Building does not connect to the car.

## Signed, API 19-compatible release

Signing keys and passwords are deliberately not included. Obtain the original
private key from the maintainer to produce an update with the existing release
certificate. A new key will produce a different certificate and may prevent
updates; do not delete installed app data or change verifier settings to work
around that difference.

Set these environment variables privately, without committing them:

- `LAUNCHPAD_KEYSTORE`: absolute path to the private keystore.
- `LAUNCHPAD_STORE_PASSWORD`: keystore password.
- `LAUNCHPAD_KEY_PASSWORD`: key password (defaults to the store password).
- `LAUNCHPAD_KEY_ALIAS`: key alias (defaults to `launchpad`).
- `ANDROID_HOME`: Android SDK location.
- `JAVA_HOME`: JDK location, if needed.

Then run:

```sh
sh tools/build_apk.sh
```

The script runs tests/lint, builds, removes prior ZIP signature metadata, signs
with the existing v1 SHA1/RSA procedure, aligns the APK, and verifies the
signature for API 19. The final artifact is `dist/Launchpad.apk`.

## TUNE service USB bundle

```sh
python3 tools/build_tune_bundle.py
```

This builds the release APK as a **dex container** for the root `app_process`
listener. NEMESIS does not install that container through PackageManager, so it
does not require the app's release signing key. The same source compilation
contains `KnobDaemon`, `Evdev`, and `HoldDetector`; only `KnobDaemon` is invoked
by the service.

Outputs:

- `dist/tune-knob-usb.zip`: outer USB bundle; extract this onto the USB drive.
- `dist/paytondev.launchpad.zip`: inner NEMESIS service package.

The outer bundle contains `NEMESIS.data` and
`packages/paytondev.launchpad.zip`. Its service uses the package id
`paytondev.launchpad` and registry name `paytondev.launchpad/knobd`, opens
`com.paytondev.launchpad/.MainActivity`, and passes the existing `from_knob`
intent extra. It listens passively, without grabbing the input device or
injecting events. Its profile expects device name
`daudio_wp_daudioplus_neev23_us`, key code 17, and an 800 ms hold.

Publish `dist/Launchpad.apk` and `dist/tune-knob-usb.zip` as separate GitHub
release assets. Do not substitute the visible app for the installed daemon's
dex container when updating an existing car. These scripts generate local
artifacts only; vehicle installation remains a separate manual step.

See [COMPATIBILITY.md](COMPATIBILITY.md) for firmware and verification limits.
Icons retain the bundled [Phosphor license](app/src/main/assets/phosphor-LICENSE.txt).
