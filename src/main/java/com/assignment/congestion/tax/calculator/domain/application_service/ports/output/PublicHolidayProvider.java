package com.assignment.congestion.tax.calculator.domain.application_service.ports.output;

import java.time.LocalDate;
import java.util.List;

public interface PublicHolidayProvider {

    List<LocalDate> getPublicHolidaysIn(int year);
}
