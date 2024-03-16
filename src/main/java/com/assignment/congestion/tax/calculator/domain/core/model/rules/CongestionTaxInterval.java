package com.assignment.congestion.tax.calculator.domain.core.model.rules;

import com.assignment.congestion.tax.calculator.domain.core.model.calculation.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class CongestionTaxInterval {

    private LocalTime startTime;
    private LocalTime endTime;
    private Integer amount;

    public boolean contains(LocalTime time) {
        return new TimeSlot(startTime, endTime).contains(time);
    }
}
