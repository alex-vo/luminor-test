package com.paymentprocessor;

import com.paymentprocessor.dto.PaymentDTO;
import com.paymentprocessor.entity.PaymentCurrency;
import com.paymentprocessor.entity.PaymentType;

import java.math.BigDecimal;

public class PaymentDTOUtils {

    public static PaymentDTO preparePaymentDTO(PaymentType type, BigDecimal amount, PaymentCurrency currency,
                                               String debtorIban, String creditorIban, String details,
                                               String creditorBankBic) {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setType(type);
        paymentDTO.setCurrency(currency);
        paymentDTO.setDetails(details);
        paymentDTO.setCreditorBankBic(creditorBankBic);
        paymentDTO.setAmount(amount);
        paymentDTO.setDebtorIban(debtorIban);
        paymentDTO.setCreditorIban(creditorIban);
        return paymentDTO;
    }

    public static PaymentDTO preparePaymentDTO(PaymentType type, PaymentCurrency currency, String details,
                                               String creditorBankBic) {
        return preparePaymentDTO(type, BigDecimal.ONE, currency, "123", "123", details, creditorBankBic);
    }

}
