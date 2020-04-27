package com.paymentprocessor.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum PaymentType {
    TYPE1(BigDecimal.valueOf(0.05)), TYPE2(BigDecimal.valueOf(0.1)), TYPE3(BigDecimal.valueOf(0.15));

    private BigDecimal cancellationFeeCoefficient;
}
