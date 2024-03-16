package com.assignment.congestion.tax.calculator.fixtures;

import com.assignment.congestion.tax.calculator.domain.application_service.ports.output.PublicHolidayProvider;

import java.time.LocalDate;
import java.util.List;

import static com.assignment.congestion.tax.calculator.adapters.calendar.FakeCalendarClient.SWEDEN_HOLIDAYS_2013;

public class PublicHolidaysStub implements PublicHolidayProvider {

    @Override
    public List<LocalDate> getPublicHolidaysIn(int year) {
        return SWEDEN_HOLIDAYS_2013;
    }
}
