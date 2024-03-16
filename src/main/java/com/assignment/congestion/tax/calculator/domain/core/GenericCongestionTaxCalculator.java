package com.assignment.congestion.tax.calculator.domain.core;

import com.assignment.congestion.tax.calculator.domain.core.model.calculation.SingleChargeTrail;
import com.assignment.congestion.tax.calculator.domain.core.model.rules.CongestionTaxInterval;
import com.assignment.congestion.tax.calculator.domain.core.model.rules.CongestionTaxRules;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.Vehicle;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.VehicleType;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class GenericCongestionTaxCalculator {

    public int calculateTax(Vehicle vehicle, List<LocalDateTime> tollDates, CongestionTaxRules rules) {
        if (isTaxExempt(vehicle, rules.getTaxExemptVehicles()) || isEmpty(tollDates)) {
            return 0;
        }

        Map<LocalDate, List<LocalTime>> tollTimestampsByDay = tollDates.stream()
                .collect(groupingBy(LocalDateTime::toLocalDate, mapping(LocalDateTime::toLocalTime, toList())));

        return tollTimestampsByDay.entrySet().stream()
                .mapToInt(dayTrail -> calculateDailyTax(dayTrail.getKey(), dayTrail.getValue(), rules))
                .sum();
    }

    private int calculateDailyTax(LocalDate day, List<LocalTime> tollTimestamps, CongestionTaxRules rules) {
        if (isChargeFreeDay(day, rules.getPublicHolidays())) {
            return 0;
        }

        List<SingleChargeTrail> singleChargeTrails = groupBySingleChargeWindows(tollTimestamps, rules);
        int totalCharge = singleChargeTrails.stream()
                .mapToInt(singleChargeTrail -> highestAmountInSingleChargeWindow(singleChargeTrail, rules.getCongestionTaxIntervals()))
                .sum();

        return Math.min(rules.getMaxChargePerDay(), totalCharge);
    }

    private boolean isChargeFreeDay(LocalDate day, List<LocalDate> publicHolidays) {
        return Optional.of(day)
                .filter(not(this::isInJuly))
                .filter(not(this::isWeekend))
                .filter(tollDay -> !isPublicHoliday(tollDay, publicHolidays))
                .filter(tollDay -> !isTheDayBeforePublicHoliday(tollDay, publicHolidays))
                .isEmpty();
    }

    private List<SingleChargeTrail> groupBySingleChargeWindows(List<LocalTime> tollTimestamps, CongestionTaxRules rules) {
        if (rules.getSingleChargeRule().isEnabled()) {
            return groupTollDatesInSingleChargeWindows(tollTimestamps, rules.getSingleChargeRule().getWindowDurationInMinutes());
        } else {
            return tollTimestamps.stream()
                    .map(SingleChargeTrail::withExclusiveWindow)
                    .toList();
        }
    }

    private List<SingleChargeTrail> groupTollDatesInSingleChargeWindows(List<LocalTime> tollTimestamps, int singleChargeWindow) {
        List<SingleChargeTrail> singleChargeTrails = new ArrayList<>();
        singleChargeTrails.add(SingleChargeTrail.from(tollTimestamps.get(0), singleChargeWindow));

        for (LocalTime tollTime : tollTimestamps) {
            SingleChargeTrail latestSingleChargeTrail = singleChargeTrails.get(singleChargeTrails.size() - 1);
            if (latestSingleChargeTrail.contains(tollTime)) {
                latestSingleChargeTrail.expandWith(tollTime);
            } else {
                singleChargeTrails.add(SingleChargeTrail.from(tollTime, singleChargeWindow));
            }
        }

        return singleChargeTrails;
    }

    private int highestAmountInSingleChargeWindow(SingleChargeTrail singleChargeTrail, List<CongestionTaxInterval> congestionTaxIntervals) {
        return singleChargeTrail.tollTimestamps().stream()
                .mapToInt(tollTime -> getCongestionTaxAt(tollTime, congestionTaxIntervals))
                .max()
                .orElseThrow();
    }

    private int getCongestionTaxAt(LocalTime tollTime, List<CongestionTaxInterval> congestionTaxIntervals) {
        return congestionTaxIntervals.stream()
                .filter(taxInterval -> taxInterval.contains(tollTime))
                .map(CongestionTaxInterval::getAmount)
                .findFirst()
                .orElseThrow();
    }

    private boolean isTaxExempt(Vehicle vehicle, List<VehicleType> taxExemptVehicles) {
        return taxExemptVehicles.contains(vehicle.getVehicleType());
    }

    private boolean isInJuly(LocalDate day) {
        return day.getMonth() == Month.JULY;
    }

    private boolean isWeekend(LocalDate day) {
        return day.getDayOfWeek() == DayOfWeek.SATURDAY ||
                day.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    private boolean isPublicHoliday(LocalDate day, List<LocalDate> publicHolidays) {
        return publicHolidays.stream()
                .anyMatch(publicHoliday -> publicHoliday.equals(day));
    }

    private boolean isTheDayBeforePublicHoliday(LocalDate day, List<LocalDate> publicHolidays) {
        return publicHolidays.stream()
                .map(publicHoliday -> publicHoliday.minusDays(1))
                .anyMatch(dayBeforePublicHoliday -> dayBeforePublicHoliday.equals(day));
    }
}
