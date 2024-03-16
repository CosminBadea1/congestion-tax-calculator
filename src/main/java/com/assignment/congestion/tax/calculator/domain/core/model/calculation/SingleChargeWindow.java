package com.assignment.congestion.tax.calculator.domain.core.model.calculation;

import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@RequiredArgsConstructor
public class SingleChargeWindow {

    private final LocalTime start;
    private final LocalTime end;

    public boolean contains(LocalTime tollTime) {
        return !tollTime.isBefore(start) && !tollTime.isAfter(end);
    }

}
