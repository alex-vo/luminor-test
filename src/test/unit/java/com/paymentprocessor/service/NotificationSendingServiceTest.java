package com.paymentprocessor.service;

import com.paymentprocessor.entity.PaymentType;
import com.paymentprocessor.repository.PaymentRepository;
import com.paymentprocessor.service.info.PaymentInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationSendingServiceTest {

    @Mock
    PaymentRepository paymentRepository;
    @Mock
    RestTemplate externalServiceRestTemplate;

    @InjectMocks
    NotificationSendingService notificationSendingService;

    @Test
    public void shouldSetExternalServiceNotifiedStatusToFalseIfExternalServiceRepliedWithNon2xxStatusCode() {
        when(externalServiceRestTemplate.getForEntity(NotificationSendingService.TYPE1_NOTIFICATION_URL, Void.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        notificationSendingService.handleMessage(new PaymentInfo(23L, PaymentType.TYPE1));

        verify(paymentRepository).updateExternalServiceNotifiedStatus(23L, false);
    }

    @Test
    public void shouldSetExternalServiceNotifiedStatusToFalseIfExternalServiceNotAvailable() {
        when(externalServiceRestTemplate.getForEntity(NotificationSendingService.TYPE1_NOTIFICATION_URL, Void.class))
                .thenThrow(new RuntimeException());

        notificationSendingService.handleMessage(new PaymentInfo(25L, PaymentType.TYPE1));

        verify(paymentRepository).updateExternalServiceNotifiedStatus(25L, false);
    }

    @Test
    public void shouldSetExternalServiceNotifiedStatusToTrueIfExternalServiceRepliedWith2xxStatusCode() {
        when(externalServiceRestTemplate.getForEntity(NotificationSendingService.TYPE2_NOTIFICATION_URL, Void.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        notificationSendingService.handleMessage(new PaymentInfo(24L, PaymentType.TYPE2));

        verify(paymentRepository).updateExternalServiceNotifiedStatus(24L, true);
    }


}
