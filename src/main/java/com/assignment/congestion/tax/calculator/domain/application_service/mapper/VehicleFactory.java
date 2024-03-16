package com.assignment.congestion.tax.calculator.domain.application_service.mapper;

import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.Bus;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.Car;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.DiplomatVehicle;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.EmergencyVehicle;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.ForeignVehicle;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.MilitaryVehicle;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.Motorcycle;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.Tractor;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.Vehicle;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.VehicleType;
import org.springframework.stereotype.Component;

@Component
public class VehicleFactory {

    public Vehicle create(String vehicleType) {
        return switch (VehicleType.from(vehicleType)) {
            case CAR -> new Car();
            case TRACTOR -> new Tractor();
            case EMERGENCY -> new EmergencyVehicle();
            case BUS -> new Bus();
            case DIPLOMAT -> new DiplomatVehicle();
            case MOTORCYCLE -> new Motorcycle();
            case MILITARY -> new MilitaryVehicle();
            case FOREIGN -> new ForeignVehicle();
        };
    }

}
