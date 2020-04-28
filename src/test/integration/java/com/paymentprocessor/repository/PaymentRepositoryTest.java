package com.paymentprocessor.repository;

import com.paymentprocessor.entity.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PaymentRepositoryTest {

    @Autowired
    ClientRepository clientRepository;
    @Autowired
    PaymentRepository paymentRepository;

    @Test
    public void shouldFindPaymentIdsByAmountBounds() {
        preparePayment(BigDecimal.valueOf(1));
        preparePayment(BigDecimal.valueOf(2));
        preparePayment(BigDecimal.valueOf(3));
        preparePayment(BigDecimal.valueOf(4));

        assertThat(paymentRepository.findIdsByAmountBetween(BigDecimal.valueOf(2), BigDecimal.valueOf(3)), hasSize(2));
        assertThat(paymentRepository.findIdsByAmountBetween(BigDecimal.valueOf(2), null), hasSize(3));
        assertThat(paymentRepository.findIdsByAmountBetween(null, BigDecimal.valueOf(1)), hasSize(1));
        assertThat(paymentRepository.findIdsByAmountBetween(BigDecimal.valueOf(10), BigDecimal.valueOf(100)), is(empty()));
    }

    @Test
    public void shouldFindPaymentByClientUsernameAndId() {
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        Payment payment = preparePayment(BigDecimal.TEN, PaymentType.TYPE1, twoDaysAgo);

        assertThat(paymentRepository.findByClientUsernameAndId(payment.getClient().getUsername(), payment.getId()).orElseThrow(), allOf(
                hasProperty("type", is(PaymentType.TYPE1)),
                hasProperty("created", is(twoDaysAgo))
        ));
        assertThat(paymentRepository.findByClientUsernameAndId(payment.getClient().getUsername() + "a", payment.getId()), is(Optional.empty()));
    }

    private Payment preparePayment(BigDecimal amount) {
        return preparePayment(amount, PaymentType.TYPE1, LocalDateTime.now());
    }

    private Payment preparePayment(BigDecimal amount, PaymentType type, LocalDateTime created) {
        Client client = clientRepository.save(new Client(RandomStringUtils.random(5)));
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setType(type);
        payment.setCreated(created);
        payment.setClient(client);
        payment.setStatus(PaymentStatus.NEW);
        payment.setCreditorIban(RandomStringUtils.random(5));
        payment.setDebtorIban(RandomStringUtils.random(5));
        payment.setCurrency(PaymentCurrency.EUR);
        return paymentRepository.save(payment);
    }

    @Test
    public void shouldCancelPaymentById() {
        Payment payment = preparePayment(BigDecimal.valueOf(1));

        paymentRepository.cancelPayment(payment.getId(), BigDecimal.valueOf(50));

        assertThat(paymentRepository.findById(payment.getId()).orElseThrow(), allOf(
                hasProperty("status", is(PaymentStatus.CANCELLED))
        ));
    }

    @Test
    public void shouldSetExternalServiceSuccessfullyNotifiedFlag() {
        Payment payment = preparePayment(BigDecimal.valueOf(23));

        paymentRepository.updateExternalServiceNotifiedStatus(payment.getId(), false);

        assertThat(paymentRepository.findById(payment.getId()).orElseThrow(), allOf(
                hasProperty("externalServiceSuccessfullyNotified", is(false))
        ));
    }

}
