package com.paymentprocessor.dto.mapper;

import com.paymentprocessor.PaymentDTOUtils;
import com.paymentprocessor.entity.Client;
import com.paymentprocessor.entity.Payment;
import com.paymentprocessor.entity.PaymentCurrency;
import com.paymentprocessor.entity.PaymentType;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class PaymentMapperTest {

    @Mock
    Client client;

    @Test
    public void shouldMapType1Payment() {
        PaymentMapper mapper = new PaymentMapperImpl();
        Payment payment = mapper.toPayment(PaymentDTOUtils.preparePaymentDTO(PaymentType.TYPE1,
                BigDecimal.valueOf(2), PaymentCurrency.EUR, "iban123", "iban456", "details_123", "bic_123"), client);

        assertThat(payment.getType(), is(PaymentType.TYPE1));
        assertThat(payment.getAmount(), is(BigDecimal.valueOf(2)));
        assertThat(payment.getCurrency(), is(PaymentCurrency.EUR));
        assertThat(payment.getDebtorIban(), is("iban123"));
        assertThat(payment.getCreditorIban(), is("iban456"));
        assertThat(payment.getDetails(), is("details_123"));
        assertThat(payment.getCreditorBankBic(), is(nullValue()));
    }

    @Test
    public void shouldMapType3Payment() {
        PaymentMapper mapper = new PaymentMapperImpl();
        Payment payment = mapper.toPayment(PaymentDTOUtils.preparePaymentDTO(PaymentType.TYPE3,
                BigDecimal.valueOf(2), PaymentCurrency.EUR, "iban123", "iban456", "details_123", "bic_123"), client);

        assertThat(payment.getType(), is(PaymentType.TYPE3));
        assertThat(payment.getAmount(), is(BigDecimal.valueOf(2)));
        assertThat(payment.getCurrency(), is(PaymentCurrency.EUR));
        assertThat(payment.getDebtorIban(), is("iban123"));
        assertThat(payment.getCreditorIban(), is("iban456"));
        assertThat(payment.getDetails(), is(nullValue()));
        assertThat(payment.getCreditorBankBic(), is("bic_123"));
    }

}
