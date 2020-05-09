package com.paymentprocessor.repository.view;

import java.math.BigDecimal;

public interface PaymentView {

    Long getId();

    BigDecimal getCancellationFee();

}
