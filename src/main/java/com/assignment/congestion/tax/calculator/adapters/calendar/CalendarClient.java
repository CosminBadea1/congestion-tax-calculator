package com.assignment.congestion.tax.calculator.adapters.calendar;

import java.time.LocalDate;
import java.util.List;

/*
    This would be the integration point with some external calendar service
    Interface can be annotated with @FeignClient and transformed in a REST client
    Since the result for a specific input is static it would make sense for this call to be cached
 */
public interface CalendarClient {

    List<LocalDate> getPublicHolidaysBy(String country, int year);

}
