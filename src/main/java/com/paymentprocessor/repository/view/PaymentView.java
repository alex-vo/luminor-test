package com.paymentprocessor.repository.view;

import com.paymentprocessor.entity.PaymentType;

import java.time.LocalDateTime;

public interface PaymentView {

    LocalDateTime getCreated();

    PaymentType getType();

}
