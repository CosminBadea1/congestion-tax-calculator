package com.assignment.congestion.tax.calculator.domain.core.model.rules;

import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.VehicleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CongestionTaxRules {

    private Integer maxChargePerDay;
    private SingleChargeRule singleChargeRule;
    private List<VehicleType> taxExemptVehicles;
    private List<CongestionTaxInterval> congestionTaxIntervals;
    private List<LocalDate> publicHolidays;
}
