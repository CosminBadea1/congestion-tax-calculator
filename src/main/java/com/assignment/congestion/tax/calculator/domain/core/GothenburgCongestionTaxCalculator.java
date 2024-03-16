package com.assignment.congestion.tax.calculator.domain.core;

import com.assignment.congestion.tax.calculator.domain.application_service.ports.output.PublicHolidayProvider;
import com.assignment.congestion.tax.calculator.domain.core.model.calculation.SingleChargeTrail;
import com.assignment.congestion.tax.calculator.domain.core.model.calculation.TimeSlot;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.Vehicle;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.VehicleType;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
public class GothenburgCongestionTaxCalculator {

    public static final Map<TimeSlot, Integer> CONGESTION_TAX_BY_TIME_SLOT = Map.ofEntries(
            Map.entry(new TimeSlot(LocalTime.of(6, 0), LocalTime.of(6, 29)), 8),
            Map.entry(new TimeSlot(LocalTime.of(6, 30), LocalTime.of(6, 59)), 13),
            Map.entry(new TimeSlot(LocalTime.of(7, 0), LocalTime.of(7, 59)), 18),
            Map.entry(new TimeSlot(LocalTime.of(8, 0), LocalTime.of(8, 29)), 13),
            Map.entry(new TimeSlot(LocalTime.of(8, 30), LocalTime.of(14, 59)), 8),
            Map.entry(new TimeSlot(LocalTime.of(15, 0), LocalTime.of(15, 29)), 13),
            Map.entry(new TimeSlot(LocalTime.of(15, 30), LocalTime.of(16, 59)), 18),
            Map.entry(new TimeSlot(LocalTime.of(17, 0), LocalTime.of(17, 59)), 13),
            Map.entry(new TimeSlot(LocalTime.of(18, 0), LocalTime.of(18, 29)), 8),
            Map.entry(new TimeSlot(LocalTime.of(18, 30), LocalTime.of(23, 59)), 0),
            Map.entry(new TimeSlot(LocalTime.of(0, 0), LocalTime.of(5, 59)), 0)
    );

    private static final Set<VehicleType> TAX_EXEMPT_VEHICLES = Set.of(
            VehicleType.EMERGENCY,
            VehicleType.BUS,
            VehicleType.DIPLOMAT,
            VehicleType.MOTORCYCLE,
            VehicleType.MILITARY,
            VehicleType.FOREIGN
    );

    private static final int MAXIMUM_CHARGE_PER_DAY = 60;
    private static final int SINGLE_CHARGE_WINDOW_IN_MINUTES = 60;

    private final PublicHolidayProvider publicHolidayProvider;

    public int calculateTax(Vehicle vehicle, List<LocalDateTime> tollDates) {
        if (isTaxExempt(vehicle) || isEmpty(tollDates)) {
            return 0;
        }

        Map<LocalDate, List<LocalTime>> tollTimestampsByDay = tollDates.stream()
                .collect(groupingBy(LocalDateTime::toLocalDate, mapping(LocalDateTime::toLocalTime, toList())));

        return tollTimestampsByDay.entrySet().stream()
                .mapToInt(dayTrail -> calculateDailyTax(dayTrail.getKey(), dayTrail.getValue()))
                .sum();
    }

    private int calculateDailyTax(LocalDate day, List<LocalTime> tollTimestamps) {
        if (isChargeFreeDay(day)) {
            return 0;
        }

        List<SingleChargeTrail> singleChargeTrails = groupBySingleChargeWindows(tollTimestamps);
        int totalCharge = singleChargeTrails.stream()
                .mapToInt(this::highestAmountInSingleChargeWindow)
                .sum();

        return Math.min(MAXIMUM_CHARGE_PER_DAY, totalCharge);
    }

    private boolean isChargeFreeDay(LocalDate day) {
        return Optional.of(day)
                .filter(not(this::isInJuly))
                .filter(not(this::isWeekend))
                .filter(not(this::isPublicHoliday))
                .filter(not(this::isTheDayBeforePublicHoliday))
                .isEmpty();
    }

    private List<SingleChargeTrail> groupBySingleChargeWindows(List<LocalTime> tollTimestamps) {
        List<SingleChargeTrail> singleChargeTrails = new ArrayList<>();
        singleChargeTrails.add(SingleChargeTrail.from(tollTimestamps.get(0), SINGLE_CHARGE_WINDOW_IN_MINUTES));

        for (LocalTime tollTime : tollTimestamps) {
            SingleChargeTrail latestSingleChargeTrail = singleChargeTrails.get(singleChargeTrails.size() - 1);
            if (latestSingleChargeTrail.contains(tollTime)) {
                latestSingleChargeTrail.expandWith(tollTime);
            } else {
                singleChargeTrails.add(SingleChargeTrail.from(tollTime, SINGLE_CHARGE_WINDOW_IN_MINUTES));
            }
        }

        return singleChargeTrails;
    }

    private int highestAmountInSingleChargeWindow(SingleChargeTrail singleChargeTrail) {
        return singleChargeTrail.tollTimestamps().stream()
                .mapToInt(this::getCongestionTaxAt)
                .max()
                .orElseThrow();
    }

    private int getCongestionTaxAt(LocalTime tollTime) {
        return CONGESTION_TAX_BY_TIME_SLOT.keySet().stream()
                .filter(timeslot -> timeslot.contains(tollTime))
                .map(CONGESTION_TAX_BY_TIME_SLOT::get)
                .findFirst()
                .orElseThrow();
    }

    private boolean isTaxExempt(Vehicle vehicle) {
        return TAX_EXEMPT_VEHICLES.contains(vehicle.getVehicleType());
    }

    private boolean isInJuly(LocalDate day) {
        return day.getMonth() == Month.JULY;
    }

    private boolean isWeekend(LocalDate day) {
        return day.getDayOfWeek() == DayOfWeek.SATURDAY ||
                day.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    private boolean isPublicHoliday(LocalDate day) {
        List<LocalDate> publicHolidays = publicHolidayProvider.getPublicHolidaysIn(day.getYear());

        return publicHolidays.stream()
                .anyMatch(publicHoliday -> publicHoliday.equals(day));
    }

    private boolean isTheDayBeforePublicHoliday(LocalDate day) {
        List<LocalDate> publicHolidays = publicHolidayProvider.getPublicHolidaysIn(day.getYear());

        return publicHolidays.stream()
                .map(publicHoliday -> publicHoliday.minusDays(1))
                .anyMatch(dayBeforePublicHoliday -> dayBeforePublicHoliday.equals(day));
    }

}
