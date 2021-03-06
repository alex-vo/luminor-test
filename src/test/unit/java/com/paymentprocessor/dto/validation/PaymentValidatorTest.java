package com.paymentprocessor.dto.validation;

import com.paymentprocessor.PaymentDTOUtils;
import com.paymentprocessor.dto.PaymentDTO;
import com.paymentprocessor.entity.PaymentCurrency;
import com.paymentprocessor.entity.PaymentType;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PaymentValidatorTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldPreventPaymentWithEmptyRequiredFields() {
        Set<ConstraintViolation<PaymentDTO>> constraintViolations = validator.validate(new PaymentDTO(), PaymentValidationSequence.class);

        assertThat(constraintViolations, containsInAnyOrder(
                allOf(
                        hasProperty("messageTemplate", equalTo("debtor iban cannot be blank")),
                        constraintViolatedOnProperty("debtorIban")
                ),
                allOf(
                        hasProperty("messageTemplate", equalTo("type cannot be empty")),
                        constraintViolatedOnProperty("type")
                ),
                allOf(
                        hasProperty("messageTemplate", equalTo("amount cannot be empty")),
                        constraintViolatedOnProperty("amount")
                ),
                allOf(
                        hasProperty("messageTemplate", equalTo("creditor iban cannot be blank")),
                        constraintViolatedOnProperty("creditorIban")
                ),
                allOf(
                        hasProperty("messageTemplate", equalTo("currency cannot be empty")),
                        constraintViolatedOnProperty("currency")
                )
        ));
    }

    @Test
    public void shouldPreventPaymentWithNonPositiveAmount() {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setAmount(BigDecimal.ZERO);
        Set<ConstraintViolation<PaymentDTO>> constraintViolations = validator.validate(paymentDTO, PaymentValidationSequence.class);

        assertThat(constraintViolations, hasItem(
                allOf(
                        hasProperty("messageTemplate", equalTo("amount must be positive")),
                        constraintViolatedOnProperty("amount")
                )
        ));
    }

    @Test
    public void shouldPreventType1PaymentNotInEuro() {
        Set<ConstraintViolation<PaymentDTO>> constraintViolations = validator.validate(
                PaymentDTOUtils.preparePaymentDTO(PaymentType.TYPE1, PaymentCurrency.USD, null, null),
                PaymentValidationSequence.class
        );

        assertThat(constraintViolations, hasItem(
                allOf(
                        hasProperty("messageTemplate", equalTo(String.format("%s only possible in %s", PaymentType.TYPE1.name(), PaymentCurrency.EUR.name()))),
                        constraintViolatedOnProperty("currency")
                )
        ));
    }

    @Test
    public void shouldPreventType1PaymentInEuroWithoutDetails() {
        Set<ConstraintViolation<PaymentDTO>> constraintViolations = validator.validate(
                PaymentDTOUtils.preparePaymentDTO(PaymentType.TYPE1, PaymentCurrency.EUR, null, null),
                PaymentValidationSequence.class
        );

        assertThat(constraintViolations, hasItem(
                allOf(
                        hasProperty("messageTemplate", equalTo(String.format("Details cannot be blank for %s payments", PaymentType.TYPE1.name()))),
                        constraintViolatedOnProperty("details")
                )
        ));
    }

    @Test
    public void shouldAllowValidType1Payment() {
        Set<ConstraintViolation<PaymentDTO>> constraintViolations = validator.validate(
                PaymentDTOUtils.preparePaymentDTO(PaymentType.TYPE1, PaymentCurrency.EUR, "123", null),
                PaymentValidationSequence.class
        );

        assertThat(constraintViolations, is(empty()));
    }

    @Test
    public void shouldPreventType2PaymentNotInDollars() {
        Set<ConstraintViolation<PaymentDTO>> constraintViolations = validator.validate(
                PaymentDTOUtils.preparePaymentDTO(PaymentType.TYPE2, PaymentCurrency.EUR, null, null),
                PaymentValidationSequence.class
        );

        assertThat(constraintViolations, hasItem(
                allOf(
                        hasProperty("messageTemplate", equalTo(String.format("%s only possible in %s", PaymentType.TYPE2.name(), PaymentCurrency.USD.name()))),
                        constraintViolatedOnProperty("currency")
                )
        ));
    }

    @Test
    public void shouldAllowValidType2Payment() {
        Set<ConstraintViolation<PaymentDTO>> constraintViolations = validator.validate(
                PaymentDTOUtils.preparePaymentDTO(PaymentType.TYPE2, PaymentCurrency.USD, null, null),
                PaymentValidationSequence.class
        );

        assertThat(constraintViolations, is(empty()));
    }

    @Test
    public void shouldPreventType3PaymentWithoutCreditorBankBIC() {
        Set<ConstraintViolation<PaymentDTO>> constraintViolations = validator.validate(
                PaymentDTOUtils.preparePaymentDTO(PaymentType.TYPE3, PaymentCurrency.EUR, null, null),
                PaymentValidationSequence.class
        );

        assertThat(constraintViolations, hasItem(
                allOf(
                        hasProperty("messageTemplate", equalTo(String.format("Creditor bank BIC cannot be blank for %s payments",
                                PaymentType.TYPE3.name()))),
                        constraintViolatedOnProperty("creditorBankBic")
                )
        ));
    }

    @Test
    public void shouldAllowValidType3Payment() {
        Set<ConstraintViolation<PaymentDTO>> constraintViolations = validator.validate(
                PaymentDTOUtils.preparePaymentDTO(PaymentType.TYPE3, PaymentCurrency.USD, null, "123"),
                PaymentValidationSequence.class
        );

        assertThat(constraintViolations, is(empty()));
    }

    private Matcher<Object> constraintViolatedOnProperty(String property) {
        return hasProperty("propertyPath", hasProperty("leafNode", hasProperty("name", equalTo(property))));
    }


}
