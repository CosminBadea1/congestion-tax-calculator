package com.assignment.congestion.tax.calculator.domain.core.model.calculation;

import java.time.LocalTime;

public record TimeSlot(LocalTime startTime, LocalTime endTime) {

    public boolean contains(LocalTime tollTime) {
        return !tollTime.isBefore(startTime) && !tollTime.isAfter(endTime);
    }
}
