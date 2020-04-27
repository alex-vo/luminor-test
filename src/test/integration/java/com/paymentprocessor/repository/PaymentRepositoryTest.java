package com.paymentprocessor.repository;

import com.paymentprocessor.entity.Payment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PaymentRepositoryTest {

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    public void shouldFindPaymentIdsByAmountBounds() {
        paymentRepository.saveAll(List.of(
                preparePayment(BigDecimal.valueOf(1)),
                preparePayment(BigDecimal.valueOf(2)),
                preparePayment(BigDecimal.valueOf(3)),
                preparePayment(BigDecimal.valueOf(4)))
        );

        assertThat(paymentRepository.findIdsByAmountBetween(BigDecimal.valueOf(2), BigDecimal.valueOf(3)), hasSize(2));
        assertThat(paymentRepository.findIdsByAmountBetween(BigDecimal.valueOf(2), null), hasSize(3));
        assertThat(paymentRepository.findIdsByAmountBetween(null, BigDecimal.valueOf(1)), hasSize(1));
    }

    private Payment preparePayment(BigDecimal amount) {
        Payment payment = new Payment();
        payment.setAmount(amount);
        return payment;
    }

}
