package com.assignment.congestion.tax.calculator.domain.core.model.vehicles;

public class Car implements Vehicle {

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.CAR;
    }
}
