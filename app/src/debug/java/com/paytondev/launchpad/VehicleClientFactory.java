package com.paytondev.launchpad;

import android.os.Build;

/** Explicit emulator-only simulation, excluded from the release APK. */
final class VehicleClientFactory {
    static VehiclePort create() {
        if (!Build.HARDWARE.equals("ranchu") && !Build.HARDWARE.equals("goldfish"))
            return new OemVehicleClient();
        return new VehiclePort() {
            boolean requested;
            @Override public String unavailableReason() { return ""; }
            @Override public Reading read() { return new Reading(2, 0, 1, -1); }
            @Override public void beginManual() { requested = true; }
            @Override public void maintainManual() { }
            @Override public void stopAndRelease() { requested = false; }
            @Override public long maintenanceIntervalMs() { return 0; }
        };
    }
}
