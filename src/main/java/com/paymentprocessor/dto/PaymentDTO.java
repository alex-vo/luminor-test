package com.paymentprocessor.dto;

import com.paymentprocessor.dto.validation.ValidPayment;
import com.paymentprocessor.entity.Payment;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ValidPayment
public class PaymentDTO {

    @NotNull(message = "type cannot be empty")
    Payment.Type type;
    @NotNull(message = "amount cannot be empty")
    @Positive(message = "amount must be positive")
    BigDecimal amount;
    @NotNull(message = "currency cannot be empty")
    Payment.Currency currency;
    @NotBlank(message = "debtor iban cannot be blank")
    String debtorIban;
    @NotBlank(message = "creditor iban cannot be blank")
    String creditorIban;
    String details;
    String creditorBankBic;

    /*
    * Client should be able to create payment of one of 3 types - TYPE1, TYPE2, TYPE3. Fields 'amount' (positive decimal),
    * 'currency' (EUR or USD), 'debtor_iban' and 'creditor_iban' (texts) are mandatory for all types.
Additional type-specific requirements:
TYPE1 is only applicable for EUR payments, has additional field 'details' (text) which is mandatory;
TYPE2 is only applicable for USD payments, has additional field ‘details’ (text) which is optional.
TYPE3 is applicable for payments in both EUR and USD currency, has additional field for creditor bank BIC code (text)
 which is mandatory.
    * */

}
