package com.assignment.congestion.tax.calculator.domain.application_service.ports.input;

import com.assignment.congestion.tax.calculator.domain.application_service.dto.GetCongestionTaxResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface CongestionTaxApplicationService {

    GetCongestionTaxResponse calculateTax(String city, String vehicleType, List<LocalDateTime> tollDates);
}
