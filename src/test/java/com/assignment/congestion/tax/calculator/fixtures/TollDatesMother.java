package com.assignment.congestion.tax.calculator.fixtures;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

public class TollDatesMother {

    public static LocalDateTime aTaxableMonday() {
        return LocalDateTime.of(2013, Month.AUGUST, 12, 6, 31);
    }

    public static List<LocalDateTime> listOfMixedDates() {
        return List.of(
                LocalDateTime.of(2013, Month.JANUARY, 14, 21, 0),
                LocalDateTime.of(2013, Month.JANUARY, 15, 21, 0),
                LocalDateTime.of(2013, Month.FEBRUARY, 7, 6, 23, 27),
                LocalDateTime.of(2013, Month.FEBRUARY, 7, 15, 27),
                LocalDateTime.of(2013, Month.FEBRUARY, 8, 6, 27),
                LocalDateTime.of(2013, Month.FEBRUARY, 8, 6, 20, 27),
                LocalDateTime.of(2013, Month.FEBRUARY, 8, 14, 35),
                LocalDateTime.of(2013, Month.FEBRUARY, 8, 15, 29),
                LocalDateTime.of(2013, Month.FEBRUARY, 8, 15, 47),
                LocalDateTime.of(2013, Month.FEBRUARY, 8, 16, 1),
                LocalDateTime.of(2013, Month.FEBRUARY, 8, 16, 48),
                LocalDateTime.of(2013, Month.FEBRUARY, 8, 17, 49),
                LocalDateTime.of(2013, Month.FEBRUARY, 8, 18, 29),
                LocalDateTime.of(2013, Month.FEBRUARY, 8, 18, 35),
                LocalDateTime.of(2013, Month.MARCH, 26, 14, 25),
                LocalDateTime.of(2013, Month.MARCH, 28, 14, 7, 27)
        );
    }

    public static List<LocalDateTime> listOfMaxedChargesInOneDay(int singleChargeWindow) {
        LocalDateTime earliestTollTime = LocalDateTime.of(2013, Month.AUGUST, 12, 6, 0);

        return Stream.iterate(earliestTollTime,
                        tollTime -> tollTime.toLocalTime().isBefore(LocalTime.of(18, 30)),
                        tollTime -> tollTime.plusMinutes(singleChargeWindow + 1))
                .toList();
    }

    public static List<LocalDateTime> listOfWeekends() {
        return List.of(
                LocalDateTime.of(2013, Month.JANUARY, 12, 7, 0),
                LocalDateTime.of(2013, Month.JANUARY, 13, 7, 0),
                LocalDateTime.of(2013, Month.MARCH, 16, 7, 0),
                LocalDateTime.of(2013, Month.MARCH, 17, 7, 0),
                LocalDateTime.of(2013, Month.APRIL, 20, 7, 0),
                LocalDateTime.of(2013, Month.APRIL, 21, 7, 0),
                LocalDateTime.of(2013, Month.MAY, 25, 7, 0),
                LocalDateTime.of(2013, Month.MAY, 26, 7, 0),
                LocalDateTime.of(2013, Month.JUNE, 29, 7, 0),
                LocalDateTime.of(2013, Month.JUNE, 30, 7, 0),
                LocalDateTime.of(2013, Month.NOVEMBER, 16, 7, 0),
                LocalDateTime.of(2013, Month.NOVEMBER, 24, 7, 0),
                LocalDateTime.of(2013, Month.DECEMBER, 1, 7, 0),
                LocalDateTime.of(2013, Month.DECEMBER, 7, 7, 0),
                LocalDateTime.of(2013, Month.DECEMBER, 8, 7, 0)
        );
    }

    public static List<LocalDateTime> listOfHolidays() {
        return List.of(
                LocalDateTime.of(2013, Month.JANUARY, 1, 7, 0),
                LocalDateTime.of(2013, Month.JANUARY, 6, 7, 0),
                LocalDateTime.of(2013, Month.MARCH, 29, 7, 0),
                LocalDateTime.of(2013, Month.MARCH, 31, 7, 0),
                LocalDateTime.of(2013, Month.APRIL, 1, 7, 0),
                LocalDateTime.of(2013, Month.MAY, 1, 7, 0),
                LocalDateTime.of(2013, Month.MAY, 9, 7, 0),
                LocalDateTime.of(2013, Month.MAY, 19, 7, 0),
                LocalDateTime.of(2013, Month.MAY, 20, 7, 0),
                LocalDateTime.of(2013, Month.JUNE, 6, 7, 0),
                LocalDateTime.of(2013, Month.JUNE, 22, 7, 0),
                LocalDateTime.of(2013, Month.NOVEMBER, 1, 7, 0),
                LocalDateTime.of(2013, Month.DECEMBER, 25, 7, 0),
                LocalDateTime.of(2013, Month.DECEMBER, 26, 7, 0),
                LocalDateTime.of(2013, Month.DECEMBER, 31, 7, 0)
        );
    }

    public static List<LocalDateTime> listOfDaysBeforePublicHolidays() {
        return listOfHolidays().stream()
                .skip(1)
                .map(publicHoliday -> publicHoliday.minusDays(1))
                .toList();
    }

    public static List<LocalDateTime> listOfDaysInJuly() {
        return Stream.iterate(LocalDateTime.of(2013, Month.JULY, 1, 7, 0),
                        tollTime -> tollTime.toLocalDate().isBefore(LocalDate.of(2013, Month.AUGUST, 1)),
                        tollTime -> tollTime.plusDays(1))
                .toList();
    }

    public static List<LocalDateTime> listOfZeroChargeTollTimes() {
        LocalDateTime startOfZeroCongestionTax = aTaxableMonday().withHour(18).withMinute(30);
        LocalDateTime endOfZeroCongestionTax = aTaxableMonday().plusDays(1).withHour(6).withMinute(0);

        return Stream.iterate(startOfZeroCongestionTax,
                        tollTime -> tollTime.isBefore(endOfZeroCongestionTax),
                        tollTime -> tollTime.plusMinutes(1))
                .toList();
    }

}
