package com.assignment.congestion.tax.calculator.domain.core.model.vehicles;

public class MilitaryVehicle implements Vehicle {
    @Override
    public VehicleType getVehicleType() {
        return VehicleType.MILITARY;
    }
}
