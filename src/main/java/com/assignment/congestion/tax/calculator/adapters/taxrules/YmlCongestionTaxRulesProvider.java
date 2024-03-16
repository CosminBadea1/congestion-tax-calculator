package com.assignment.congestion.tax.calculator.adapters.taxrules;

import com.assignment.congestion.tax.calculator.adapters.taxrules.config.CongestionTaxRuleContainer;
import com.assignment.congestion.tax.calculator.domain.application_service.ports.output.CongestionTaxRulesProvider;
import com.assignment.congestion.tax.calculator.domain.core.model.rules.CongestionTaxRules;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class YmlCongestionTaxRulesProvider implements CongestionTaxRulesProvider {

    private static final String CITY_NOT_SUPPORTED_MSG = "%s is not a supported city. Currently, Gothenburg is the only supported city!";

    private final CongestionTaxRuleContainer congestionTaxRuleContainer;

    @Override
    public CongestionTaxRules loadByCity(String city) {
        return congestionTaxRuleContainer.forCity(city.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException(CITY_NOT_SUPPORTED_MSG.formatted(city)));
    }
}
