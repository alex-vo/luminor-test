package com.paymentprocessor.dto.validation;

import com.paymentprocessor.dto.PaymentDTO;
import com.paymentprocessor.entity.PaymentCurrency;
import com.paymentprocessor.entity.PaymentType;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PaymentValidator implements ConstraintValidator<ValidPayment, PaymentDTO> {

    @Override
    public boolean isValid(PaymentDTO value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        switch (value.getType()) {
            case TYPE1:
                return isValidType1Payment(value, context);
            case TYPE2:
                return isValidType2Payment(value, context);
            case TYPE3:
                return isValidType3Payment(value, context);
            default:
                return true;
        }
    }

    private boolean isValidType1Payment(PaymentDTO value, ConstraintValidatorContext context) {
        if (value.getCurrency() != PaymentCurrency.EUR) {
            context.buildConstraintViolationWithTemplate(String.format("%s only possible in %s", PaymentType.TYPE1.name(),
                    PaymentCurrency.EUR.name()))
                    .addPropertyNode("currency")
                    .addConstraintViolation();
            return false;
        }

        if (StringUtils.isBlank(value.getDetails())) {
            context.buildConstraintViolationWithTemplate(String.format("Details cannot be blank for %s payments",
                    PaymentType.TYPE1.name()))
                    .addPropertyNode("details")
                    .addConstraintViolation();
            return false;
        }


        return true;
    }

    private boolean isValidType2Payment(PaymentDTO value, ConstraintValidatorContext context) {
        if (value.getCurrency() != PaymentCurrency.USD) {
            context.buildConstraintViolationWithTemplate(String.format("%s only possible in %s", PaymentType.TYPE2.name(),
                    PaymentCurrency.USD.name()))
                    .addPropertyNode("currency")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean isValidType3Payment(PaymentDTO value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value.getCreditorBankBic())) {
            context.buildConstraintViolationWithTemplate(String.format("Creditor bank BIC cannot be blank for %s payments",
                    PaymentType.TYPE3.name()))
                    .addPropertyNode("creditorBankBic")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
