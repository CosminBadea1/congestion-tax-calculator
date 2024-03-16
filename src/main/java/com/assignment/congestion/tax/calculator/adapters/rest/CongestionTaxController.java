package com.assignment.congestion.tax.calculator.adapters.rest;

import com.assignment.congestion.tax.calculator.adapters.taxrules.config.CongestionTaxRuleContainer;
import com.assignment.congestion.tax.calculator.domain.application_service.dto.GetCongestionTaxResponse;
import com.assignment.congestion.tax.calculator.domain.application_service.ports.input.CongestionTaxApplicationService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/congestion-tax/")
public class CongestionTaxController {

    private final CongestionTaxApplicationService congestionTaxApplicationService;
    private final CongestionTaxRuleContainer congestionTaxRuleContainer;

    @GetMapping(value = "/{city}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetCongestionTaxResponse> calculateCongestionTax(
            @PathVariable("city") @NotBlank String city,
            @RequestParam @NotBlank String vehicleType,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) List<LocalDateTime> tollDates) {
        log.info("Calculating congestion tax in {}, for vehicle: {} and tollDates: {}", city, vehicleType, tollDates);

        GetCongestionTaxResponse response = congestionTaxApplicationService.calculateTax(city, vehicleType, tollDates);
        return ResponseEntity.ok(response);
    }
}
