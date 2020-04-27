package com.paymentprocessor.service.info;

import com.paymentprocessor.entity.PaymentType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PaymentInfo implements Serializable {
    Long id;
    PaymentType paymentType;
}
