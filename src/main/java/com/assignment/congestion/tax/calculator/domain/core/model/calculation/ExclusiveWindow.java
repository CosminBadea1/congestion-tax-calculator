package com.assignment.congestion.tax.calculator.domain.core.model.calculation;

import java.time.LocalTime;

public class ExclusiveWindow extends SingleChargeWindow {

    private ExclusiveWindow(LocalTime start, LocalTime end) {
        super(start, end);
    }

    public static ExclusiveWindow create() {
        return new ExclusiveWindow(null, null);
    }

    @Override
    public boolean contains(LocalTime tollTime) {
        return false;
    }

}
