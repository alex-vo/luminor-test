package com.paymentprocessor.service;

import com.paymentprocessor.config.RabbitMQSettings;
import com.paymentprocessor.dto.PaymentDTO;
import com.paymentprocessor.dto.SinglePaymentDTO;
import com.paymentprocessor.dto.mapper.PaymentMapper;
import com.paymentprocessor.entity.Client;
import com.paymentprocessor.entity.Payment;
import com.paymentprocessor.entity.PaymentType;
import com.paymentprocessor.exception.BadRequestException;
import com.paymentprocessor.exception.NotFoundException;
import com.paymentprocessor.repository.ClientRepository;
import com.paymentprocessor.repository.PaymentRepository;
import com.paymentprocessor.repository.view.PaymentView;
import com.paymentprocessor.service.info.PaymentInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PaymentService.class)
public class PaymentServiceTest {

    @Mock
    PaymentMapper paymentMapper;
    @Mock
    PaymentRepository paymentRepository;
    @Mock
    ClientRepository clientRepository;
    @Mock
    RabbitTemplate rabbitTemplate;

    @InjectMocks
    PaymentService paymentService;

    @Test
    public void shouldFindPaymentIds() {
        Set<Long> ids = Set.of(23L, 45L);
        when(paymentRepository.findIdsByAmountBetween(BigDecimal.ZERO, BigDecimal.ONE)).thenReturn(ids);

        Set<Long> result = paymentService.findPaymentIds(BigDecimal.ZERO, BigDecimal.ONE);

        assertThat(result, is(ids));
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundExceptionIfPaymentNotFound() {
        when(paymentRepository.findById(10L)).thenReturn(Optional.empty());

        paymentService.findPayment(10L);
    }

    @Test
    public void shouldFindSinglePayment() {
        Payment payment = mock(Payment.class);
        when(payment.getId()).thenReturn(10L);
        when(payment.getCancellationFee()).thenReturn(BigDecimal.TEN);
        when(paymentRepository.findById(10L)).thenReturn(Optional.of(payment));

        SinglePaymentDTO result = paymentService.findPayment(10L);

        assertThat(result, allOf(
                hasProperty("id", is(10L)),
                hasProperty("cancellationFee", is(BigDecimal.TEN))
        ));
    }

    @Test
    public void shouldCreateType1PaymentAndIssueNotification() {
        PaymentDTO paymentDTO = new PaymentDTO();
        Payment payment = preparePayment(PaymentType.TYPE1, paymentDTO, "abc");

        paymentService.createPayment("abc", paymentDTO);

        verify(paymentRepository).save(payment);
        verify(rabbitTemplate).convertAndSend(RabbitMQSettings.NOTIFICATION_ROUTING_KEY, new PaymentInfo(payment.getId(), payment.getType()));
    }

    @Test
    public void shouldCreateType3PaymentAndNotIssueNotification() {
        PaymentDTO paymentDTO = new PaymentDTO();
        Payment payment = preparePayment(PaymentType.TYPE3, paymentDTO, "user2");

        paymentService.createPayment("user2", paymentDTO);

        verify(paymentRepository).save(payment);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(PaymentInfo.class));
    }

    private Payment preparePayment(PaymentType type, PaymentDTO paymentDTO, String username) {
        Client client = mock(Client.class);
        Payment payment = mock(Payment.class);
        when(payment.getType()).thenReturn(type);
        when(paymentMapper.toPayment(paymentDTO, client)).thenReturn(payment);
        when(clientRepository.getOne(username)).thenReturn(client);
        return payment;
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundExceptionIfClientNotFound() {
        when(paymentRepository.findByClientUsernameAndId("user3", 190L)).thenReturn(Optional.empty());

        paymentService.cancelPayment("user3", 190L);
    }

    @Test(expected = BadRequestException.class)
    public void shouldThrowBadRequestExceptionIfCancellationIsTooLate() {
        when(paymentRepository.findByClientUsernameAndId("user4", 191L))
                .thenReturn(Optional.of(preparePaymentView(LocalDateTime.now().minusDays(1), PaymentType.TYPE1)));

        paymentService.cancelPayment("user4", 191L);
    }

    @Test
    public void shouldPerformPaymentCancellationSuccessfully() {
        LocalDateTime now = LocalDateTime.of(2020, 4, 28, 5, 30, 0);
        LocalDateTime created = LocalDateTime.of(2020, 4, 28, 2, 40, 0);
        PowerMockito.mockStatic(LocalDateTime.class);
        PowerMockito.when(LocalDateTime.now()).thenReturn(now/*PowerMockito.mock(LocalDateTime.class)*/);

        when(paymentRepository.findByClientUsernameAndId("user5", 192L))
                .thenReturn(Optional.of(preparePaymentView(created, PaymentType.TYPE2)));

        paymentService.cancelPayment("user5", 192L);

        verify(paymentRepository).cancelPayment(192L, BigDecimal.valueOf(2).multiply(PaymentType.TYPE2.getCancellationFeeCoefficient()));
    }

    private PaymentView preparePaymentView(LocalDateTime created, PaymentType type) {
        return new PaymentView() {
            @Override
            public LocalDateTime getCreated() {
                return created;
            }

            @Override
            public PaymentType getType() {
                return type;
            }
        };
    }

    @Test
    public void shouldDecideThatNotificationShouldBeIssuedBasedOnPaymentType() {
        ReflectionTestUtils.invokeMethod(paymentService, "issuePaymentNotifications", preparePayment(1L, null, PaymentType.TYPE1));
        verify(rabbitTemplate).convertAndSend(RabbitMQSettings.NOTIFICATION_ROUTING_KEY, new PaymentInfo(1L, PaymentType.TYPE1));

        ReflectionTestUtils.invokeMethod(paymentService, "issuePaymentNotifications", preparePayment(2L, null, PaymentType.TYPE2));
        verify(rabbitTemplate).convertAndSend(RabbitMQSettings.NOTIFICATION_ROUTING_KEY, new PaymentInfo(2L, PaymentType.TYPE2));
    }

    @Test
    public void shouldDecideThatNotificationShouldNotBeIssuedBasedOnPaymentType() {
        ReflectionTestUtils.invokeMethod(paymentService, "issuePaymentNotifications", preparePayment(3L, null, PaymentType.TYPE3));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    private Payment preparePayment(Long id, LocalDateTime created, PaymentType type) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setCreated(created);
        payment.setType(type);
        return payment;
    }

}
