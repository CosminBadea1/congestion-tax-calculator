package com.assignment.congestion.tax.calculator.domain.core.model.vehicles;

public class Tractor implements Vehicle {

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.TRACTOR;
    }
}
