package com.paymentprocessor.dto.validation;

import com.paymentprocessor.dto.PaymentDTO;
import com.paymentprocessor.entity.Payment;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PaymentValidator implements ConstraintValidator<ValidPayment, PaymentDTO> {
    @Override
    public boolean isValid(PaymentDTO value, ConstraintValidatorContext context) {
        //TODO look at new switch case features
        //TODO add error message
        switch (value.getType()) {
            case TYPE1:
                return value.getCurrency() == Payment.Currency.EUR && StringUtils.isNotBlank(value.getDetails());
            case TYPE2:
                return value.getCurrency() == Payment.Currency.USD;
            case TYPE3:
                return StringUtils.isNotBlank(value.getCreditorBankBic());
            default:
                return true;
        }
    }
}
