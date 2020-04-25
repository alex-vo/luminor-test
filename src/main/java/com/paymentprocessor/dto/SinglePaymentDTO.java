package com.paymentprocessor.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SinglePaymentDTO {
    Long id;
    BigDecimal cancellationFee;
}
