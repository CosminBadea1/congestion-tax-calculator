package com.assignment.congestion.tax.calculator.domain.core.model.vehicles;

public class Motorcycle implements Vehicle {
    @Override
    public VehicleType getVehicleType() {
        return VehicleType.MOTORCYCLE;
    }
}
