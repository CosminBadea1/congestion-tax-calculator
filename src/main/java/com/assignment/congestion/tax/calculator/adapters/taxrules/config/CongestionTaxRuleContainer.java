package com.assignment.congestion.tax.calculator.adapters.taxrules.config;

import com.assignment.congestion.tax.calculator.domain.core.model.rules.CongestionTaxRules;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;
import java.util.Optional;

@Data
@Configuration
@ConfigurationProperties
@PropertySource(value = "classpath:/rules/congestion-tax-rules.yml", factory = MultipleYamlPropertySourceFactory.class)
public class CongestionTaxRuleContainer {

    private Map<String, CongestionTaxRules> congestionTaxRulesByCity;

    public Optional<CongestionTaxRules> forCity(String city) {
        return Optional.ofNullable(congestionTaxRulesByCity.get(city));
    }
}
