# ioniq5-preconditioning
an app that gives you a manual preconditioning button in your IONIQ 5.

## how to use it
once you get the app onto your IONIQ (instructions below), it's pretty simple to use.

to launch the app, hold the TUNE knob. other launch methods will be available soon. simply press the start preconditioning button, wait roughly 60sec (hardware limitation, nothing i can do about that unfortunately), and preconditioning will start!

## installation instructions

> [!WARNING]
> This process involves making **unauthorized modifications to the infotainment system of your car.** One wrong move can render it **entirely unusable, leaving you with a very costly repair bill**. Take care to **never delete or modify any files in the car.** If you are unsure about **anything in this document**, don't leave it up to chance! Feel free to DM me on [Twitter](https://x.com/paytondev) or [Reddit](https://reddit.com/u/paytondev) and I'll try to help.

> [!TIP]
> What you'll need: a laptop, a USB drive, a method to [reboot the headunit](https://www.facebook.com/groups/theioniqguy/posts/1355171539173388/), and a stable Wi-Fi connection (in my testing, an iPhone hotspot is too modern for the car to connect to. you may have success with older devices or Android phones)

right now, this process is fairly involved. eventually i'd like to make a single script that does this for you.

1. [Install NEMESIS](https://github.com/xwtk/hmg_nemesis/blob/main/03-Install-and-Uninstall.md). **(note: you do not need to build ota_package.tar yourself, you can find a downloadable file in the repo)**

2. **Enable ADB and Wi-Fi on the car.**

   Download **Enable ADB.zip** and **Enable Wireless Features.zip** from [NEMESIS’s packages folder](https://github.com/xwtk/hmg_nemesis/tree/main/Packages).

   Install them **one at a time**:
   - Extract **Enable ADB.zip** into the root of your FAT32/exFAT USB drive. `NEMESIS.data` should be directly on the drive, not inside another folder.
   - Insert the drive into the car’s USB data port and confirm installation.
   - Reboot the headunit (using the pinhole button in the climate control bar), and remove the USB drive.
   - Replace the installation files on the USB drive with the contents of **Enable Wireless Features.zip**, then repeat.

   These packages enable network ADB and wireless features.

4. **Open the hidden Wi-Fi settings.**

   Enabling wireless features may not add a Wi-Fi button to Hyundai’s normal settings. You can open Android’s existing Wi-Fi screen with a small USB script.

   Back up the previous `NEMESIS.data` from your USB drive to your computer. Create these two plain-text files at the root of the drive, using the exact filenames below.

   **`NEMESIS.data`:**
   ```ini
   [nemesis]
   version=1.0
   type=open
   ```

   **`NEMESIS.sh`:**
   ```sh
   #!/system/bin/sh
   export PATH=/sbin:/vendor/bin:/system/sbin:/system/bin:/system/xbin
   export LD_LIBRARY_PATH=/vendor/lib:/system/lib

   netcfg > /storage/usb0/NEMESIS-network.txt 2>&1
   am start -a android.settings.WIFI_SETTINGS
   ```

   Make sure your editor does not add `.txt` to either filename. Save the script with Unix/LF line endings.

   Insert the drive, then select **Run USB NEMESIS.sh** in NEMESIS Manager. Turn Wi-Fi on and connect the car to the same network as your computer.

   Run the script again after connecting. It saves the current network addresses to `NEMESIS-network.txt` on the USB drive.

5. **Install ADB on your computer.**

   Download [Android SDK Platform-Tools](https://developer.android.com/tools/releases/platform-tools) for your operating system and extract the ZIP. You do not need to install Android Studio.

   Open a terminal in the extracted `platform-tools` folder.

   On macOS/Linux:
   ```sh
   ./adb version
   ```

   On Windows PowerShell:
   ```powershell
   .\adb.exe version
   ```

   The remaining examples use `adb`. Substitute `./adb` on macOS/Linux or `.\adb.exe` in PowerShell unless you have already added Platform-Tools to your PATH.

6. **Find the car’s IP address and connect.**

   Check your router or hotspot’s connected-device list. Alternatively, read `NEMESIS-network.txt` from the USB drive and find the address of the connected Wi-Fi interface.

   Replace `CAR_IP` below with the car’s actual address:
   ```sh
   adb connect CAR_IP:5555
   adb devices -l
   ```

   The car should appear with the status **`device`**. If an authorization prompt appears on the car, approve it.

   Check that NEMESIS root access works:
   ```sh
   adb -s CAR_IP:5555 shell "nemesis root -c 'id'"
   ```

   You should see `uid=0(root)`.

   If the connection fails:
   - Confirm the car is still awake and connected to Wi-Fi.
   - Check whether its IP address changed.
   - Make sure your network allows devices to communicate with each other. Guest networks and some hotspots isolate clients.
   - If ADB reports `offline`, disconnect and reconnect:
     ```sh
     adb disconnect CAR_IP:5555
     adb connect CAR_IP:5555
     ```
   - Keep in mind `adb pair CAR_IP:5555` will NOT work due to Hyundai not supporting pairing codes.

7. **Install the TUNE-knob service.**

   Download the **TUNE-knob service bundle** and **`Launchpad.apk`** from this repository’s [GitHub releases page](../../releases).

   These are separate downloads:
   - The service listens for a TUNE hold and opens Launchpad.
   - `Launchpad.apk` contains the interface and preconditioning controls.

   Extract the service’s USB installation bundle onto your USB drive. Its installation layout should look like this:
   ```text
   USB drive/
   ├── NEMESIS.data
   └── packages/
       └── paytondev.launchpad.zip
   ```

   Use the installation descriptor supplied with the bundle instead of the `NEMESIS.data` file we used before. Do not extract the inner `paytondev.launchpad.zip`.

   Insert the drive and confirm installation. NEMESIS registers and starts the service. You can check it from your computer:
   ```sh
   adb -s CAR_IP:5555 shell "nemesis service status paytondev.launchpad/knobd"
   ```

   The current listener is configured for the inspected NEEV23 US head unit. Different hardware may require a different input-device profile; let me know if something doesn't work.

8. **Install `Launchpad.apk`.**

   Keep the car parked with the head unit awake. Run each command separately and stop if anything fails.

   The installation below adds our app at **`/system/app/Launchpad.apk`**. For an update, it replaces only that same file. Do not overwrite any Hyundai APK, NEMESIS file, or TUNE-service file.

   First, check root access and available space:
   ```sh
   adb -s CAR_IP:5555 shell "nemesis root -c 'id'"
   adb -s CAR_IP:5555 shell "df /system /data"
   adb -s CAR_IP:5555 shell "ls -l /system/app/Launchpad.apk"
   ```

   “No such file” for `Launchpad.apk` is expected on a first installation. If it already exists, save a backup and its metadata in a new folder on your computer before continuing:
   ```sh
   adb -s CAR_IP:5555 pull /system/app/Launchpad.apk Launchpad-before.apk
   adb -s CAR_IP:5555 shell "ls -l /system/app/Launchpad.apk" > Launchpad-before-metadata.txt
   ```

   Push the downloaded APK, substituting its actual local path:
   ```sh
   adb -s CAR_IP:5555 push "/path/to/Launchpad.apk" /data/local/tmp/Launchpad-install.upload
   ```

   Check the system mount:
   ```sh
   adb -s CAR_IP:5555 shell "cat /proc/mounts"
   ```

   The `/system` entry must be writable (`rw`) for installation. If it is read-only, use NEMESIS’s [documented remount command](https://github.com/xwtk/hmg_nemesis/blob/main/04-Packages.md):
   ```sh
   adb -s CAR_IP:5555 shell "nemesis remount rw"
   ```

   If that fails, stop. Do not try alternative mount flags or firmware changes.

   Stage the APK with root ownership and the correct permissions:
   ```sh
   adb -s CAR_IP:5555 shell "nemesis root -c 'cat /data/local/tmp/Launchpad-install.upload > /system/app/Launchpad-install.new && chown 0:0 /system/app/Launchpad-install.new && chmod 0644 /system/app/Launchpad-install.new'"
   adb -s CAR_IP:5555 pull /system/app/Launchpad-install.new Launchpad-staged.apk
   ```

   Now, move the staged file into its final location:
   ```sh
   adb -s CAR_IP:5555 shell "nemesis root -c 'mv /system/app/Launchpad-install.new /system/app/Launchpad.apk && sync'"
   adb -s CAR_IP:5555 shell "ls -l /system/app/Launchpad.apk"
   adb -s CAR_IP:5555 pull /system/app/Launchpad.apk Launchpad-installed.apk
   ```

   If you temporarily remounted `/system` writable, restore it:
   ```sh
   adb -s CAR_IP:5555 shell "nemesis remount ro"
   ```

   Do not use `adb install` for this installation route or disable package-verification settings to work around an error.

9. **Reboot and open Launchpad.**

   After verifying the installed APK, reboot:
   ```sh
   adb -s CAR_IP:5555 shell "nemesis root -c 'reboot'"
   ```

   Wait for the head unit to finish starting, then hold TUNE for roughly one second. Launchpad should open.

   If it does not, reconnect ADB and try opening the app directly:
   ```sh
   adb -s CAR_IP:5555 shell "am start -n com.paytondev.launchpad/.MainActivity"
   ```

   If direct launch works, check the TUNE service:
   ```sh
   adb -s CAR_IP:5555 shell "nemesis service status paytondev.launchpad/knobd"
   ```

   Once installed, you do not need to insert the USB drive every time you open Launchpad. Its **Advanced** screen includes Wi-Fi settings and connection information.

10. **Check preconditioning while parked.**

   Press **Start**. Launchpad will initially show that it is waiting for the BMS. Activation may take roughly **60–120 seconds**, and the BMS can refuse activation because of temperature, charge, or other operating conditions.

   **Waiting does not mean the battery heater is active.** Launchpad shows **Active** only when the car reports conditioning active.

   Press **Stop** to cancel, including while activation is pending. Closing Launchpad does not stop an ongoing request.

   V1 checks the head unit’s firmware and required Hyundai services. If preconditioning is unavailable or its status is unknown, do not bypass that check. NEMESIS compatibility alone does not establish preconditioning compatibility.

   V1 does not coordinate manual requests with Hyundai navigation, so test it without native navigation requesting battery conditioning.
