package com.assignment.congestion.tax.calculator.domain.core.model.rules;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingleChargeRule {

    private boolean enabled;
    private Integer windowDurationInMinutes;
}
