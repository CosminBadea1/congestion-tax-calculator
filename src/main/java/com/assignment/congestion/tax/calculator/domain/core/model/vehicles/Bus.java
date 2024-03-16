package com.assignment.congestion.tax.calculator.domain.core.model.vehicles;

public class Bus implements Vehicle {
    @Override
    public VehicleType getVehicleType() {
        return VehicleType.BUS;
    }
}
