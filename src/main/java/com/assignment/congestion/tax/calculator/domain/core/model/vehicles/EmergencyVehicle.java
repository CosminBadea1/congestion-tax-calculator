package com.assignment.congestion.tax.calculator.domain.core.model.vehicles;

public class EmergencyVehicle implements Vehicle {

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.EMERGENCY;
    }
}
