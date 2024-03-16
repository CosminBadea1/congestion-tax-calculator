package com.assignment.congestion.tax.calculator.domain.core.model.calculation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SingleChargeTrail {

    private final SingleChargeWindow singleChargeWindow;
    private final List<LocalTime> tollTimes;

    public static SingleChargeTrail from(LocalTime windowStart, int windowLengthInMinutes) {
        LocalTime windowEnd = windowStart.plusMinutes(windowLengthInMinutes);

        return new SingleChargeTrail(
                new SingleChargeWindow(windowStart, windowEnd),
                new ArrayList<>(List.of(windowStart))
        );
    }

    public static SingleChargeTrail withExclusiveWindow(LocalTime windowStart) {
        return new SingleChargeTrail(
                ExclusiveWindow.create(),
                new ArrayList<>(List.of(windowStart))
        );
    }

    public void expandWith(LocalTime newTimestamp) {
        tollTimes.add(newTimestamp);
    }

    public boolean contains(LocalTime tollTime) {
        return singleChargeWindow.contains(tollTime);
    }

    public List<LocalTime> tollTimestamps() {
        return tollTimes.stream().toList();
    }
}
