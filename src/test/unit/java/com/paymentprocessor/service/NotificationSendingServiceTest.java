package com.paymentprocessor.service;

import com.paymentprocessor.entity.PaymentType;
import com.paymentprocessor.repository.PaymentRepository;
import com.paymentprocessor.service.info.PaymentInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    String type1NotificationUrl = "http://abc.com/notify";
    String type2NotificationUrl = "http://def.com/notify";

    NotificationSendingService notificationSendingService;

    @Before
    public void setup() {
        notificationSendingService = new NotificationSendingService(paymentRepository, externalServiceRestTemplate,
                type1NotificationUrl, type2NotificationUrl);
    }

    @Test
    public void shouldSetExternalServiceNotifiedStatusToFalseIfExternalServiceRepliedWithNon2xxStatusCode() {
        when(externalServiceRestTemplate.getForEntity(type1NotificationUrl, Void.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        notificationSendingService.handleMessage(new PaymentInfo(23L, PaymentType.TYPE1));

        verify(paymentRepository).updateExternalServiceNotifiedStatus(23L, false);
    }

    @Test
    public void shouldSetExternalServiceNotifiedStatusToFalseIfExternalServiceNotAvailable() {
        when(externalServiceRestTemplate.getForEntity(type1NotificationUrl, Void.class))
                .thenThrow(new RuntimeException());

        notificationSendingService.handleMessage(new PaymentInfo(25L, PaymentType.TYPE1));

        verify(paymentRepository).updateExternalServiceNotifiedStatus(25L, false);
    }

    @Test
    public void shouldSetExternalServiceNotifiedStatusToTrueIfExternalServiceRepliedWith2xxStatusCode() {
        when(externalServiceRestTemplate.getForEntity(type2NotificationUrl, Void.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        notificationSendingService.handleMessage(new PaymentInfo(24L, PaymentType.TYPE2));

        verify(paymentRepository).updateExternalServiceNotifiedStatus(24L, true);
    }


}
