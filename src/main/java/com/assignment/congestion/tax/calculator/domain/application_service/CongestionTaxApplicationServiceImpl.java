package com.assignment.congestion.tax.calculator.domain.application_service;

import com.assignment.congestion.tax.calculator.domain.application_service.dto.GetCongestionTaxResponse;
import com.assignment.congestion.tax.calculator.domain.application_service.mapper.VehicleFactory;
import com.assignment.congestion.tax.calculator.domain.application_service.ports.input.CongestionTaxApplicationService;
import com.assignment.congestion.tax.calculator.domain.application_service.ports.output.CongestionTaxRulesProvider;
import com.assignment.congestion.tax.calculator.domain.application_service.ports.output.PublicHolidayProvider;
import com.assignment.congestion.tax.calculator.domain.core.GenericCongestionTaxCalculator;
import com.assignment.congestion.tax.calculator.domain.core.model.rules.CongestionTaxRules;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.partitioningBy;

@Service
@RequiredArgsConstructor
public class CongestionTaxApplicationServiceImpl implements CongestionTaxApplicationService {

    private static final int SUPPORTED_YEAR = 2013;
    private static final String TOLL_DATE_YEAR_NOT_SUPPORTED_MSG = "Currently we only support calculations for toll dates in the year: %d";

    private final VehicleFactory vehicleFactory;
    private final PublicHolidayProvider publicHolidayProvider;
    private final CongestionTaxRulesProvider congestionTaxRulesProvider;
    private final GenericCongestionTaxCalculator congestionTaxCalculator;

    public GetCongestionTaxResponse calculateTax(String city, String vehicleType, List<LocalDateTime> tollDates) {
        Vehicle vehicle = vehicleFactory.create(vehicleType);
        CongestionTaxRules congestionTaxRules = congestionTaxRulesProvider.loadByCity(city);
        congestionTaxRules.setPublicHolidays(publicHolidayProvider.getPublicHolidaysIn(SUPPORTED_YEAR));

        int taxAmount = congestionTaxCalculator.calculateTax(vehicle, validateTollDates(tollDates), congestionTaxRules);
        return new GetCongestionTaxResponse(taxAmount, "SEK");
    }

    private List<LocalDateTime> validateTollDates(List<LocalDateTime> tollDates) {
        Map<Boolean, List<LocalDateTime>> tollDatesInSupportedYear = tollDates.stream()
                .collect(partitioningBy(tollDate -> SUPPORTED_YEAR == tollDate.getYear()));

        if (!tollDatesInSupportedYear.get(FALSE).isEmpty()) {
            throw new IllegalArgumentException(TOLL_DATE_YEAR_NOT_SUPPORTED_MSG.formatted(SUPPORTED_YEAR));
        }

        return tollDatesInSupportedYear.get(TRUE).stream()
                .sorted()
                .toList();
    }

}
