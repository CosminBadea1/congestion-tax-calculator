package com.assignment.congestion.tax.calculator.domain.application_service.ports.output;

import com.assignment.congestion.tax.calculator.domain.core.model.rules.CongestionTaxRules;

public interface CongestionTaxRulesProvider {

    CongestionTaxRules loadByCity(String city);
}
