package com.assignment.congestion.tax.calculator.domain.core.model.vehicles;

public class DiplomatVehicle implements Vehicle {
    @Override
    public VehicleType getVehicleType() {
        return VehicleType.DIPLOMAT;
    }
}
