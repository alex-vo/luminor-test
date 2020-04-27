package com.paymentprocessor.dto.validation;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({Default.class, LogicalConstraints.class})
public interface PaymentValidationSequence {
}
