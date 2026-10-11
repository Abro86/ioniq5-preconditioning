package com.paytondev.launchpad;

final class VehicleClientFactory {
    static VehiclePort create() { return new OemVehicleClient(); }
}
