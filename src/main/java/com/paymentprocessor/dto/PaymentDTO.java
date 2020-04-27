package com.paymentprocessor.dto;

import com.paymentprocessor.dto.validation.LogicalConstraints;
import com.paymentprocessor.dto.validation.ValidPayment;
import com.paymentprocessor.entity.Payment;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.groups.Default;
import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ValidPayment(groups = LogicalConstraints.class)
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

}
