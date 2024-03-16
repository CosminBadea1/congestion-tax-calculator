package com.assignment.congestion.tax.calculator.adapters.calendar;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Component
public class FakeCalendarClient implements CalendarClient {

    public static final List<LocalDate> SWEDEN_HOLIDAYS_2013 = List.of(
            LocalDate.of(2013, Month.JANUARY, 1),
            LocalDate.of(2013, Month.JANUARY, 6),
            LocalDate.of(2013, Month.MARCH, 29),
            LocalDate.of(2013, Month.MARCH, 31),
            LocalDate.of(2013, Month.APRIL, 1),
            LocalDate.of(2013, Month.MAY, 1),
            LocalDate.of(2013, Month.MAY, 9),
            LocalDate.of(2013, Month.MAY, 19),
            LocalDate.of(2013, Month.MAY, 20),
            LocalDate.of(2013, Month.JUNE, 6),
            LocalDate.of(2013, Month.JUNE, 22),
            LocalDate.of(2013, Month.NOVEMBER, 1),
            LocalDate.of(2013, Month.DECEMBER, 25),
            LocalDate.of(2013, Month.DECEMBER, 26),
            LocalDate.of(2013, Month.DECEMBER, 31)
    );

    @Override
    public List<LocalDate> getPublicHolidaysBy(String country, int year) {
        return SWEDEN_HOLIDAYS_2013;
    }

}
