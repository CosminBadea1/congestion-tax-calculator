package com.assignment.congestion.tax.calculator.adapters.calendar;

import com.assignment.congestion.tax.calculator.domain.application_service.ports.output.PublicHolidayProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultPublicHolidayProvider implements PublicHolidayProvider {

    private final CalendarClient calendarClient;

    @Override
    public List<LocalDate> getPublicHolidaysIn(int year) {
        return calendarClient.getPublicHolidaysBy("Sweden", year);
    }

}
