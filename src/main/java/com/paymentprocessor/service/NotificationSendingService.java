package com.paymentprocessor.service;

import com.paymentprocessor.repository.PaymentRepository;
import com.paymentprocessor.service.info.PaymentInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RabbitListener
@Slf4j
@RequiredArgsConstructor
public class NotificationSendingService {

    public static final String TYPE1_NOTIFICATION_URL = "https://www.google.com/lets_get_404";
    public static final String TYPE2_NOTIFICATION_URL = "https://www.google.com/";

    private final PaymentRepository paymentRepository;
    private final RestTemplate externalServiceRestTemplate;

    @RabbitHandler
    public void handleMessage(PaymentInfo paymentInfo) {
        switch (paymentInfo.getPaymentType()) {
            case TYPE1:
                notifyExternalService(paymentInfo, TYPE1_NOTIFICATION_URL);
                break;
            case TYPE2:
                notifyExternalService(paymentInfo, TYPE2_NOTIFICATION_URL);
                break;
            default:
                break;
        }
    }

    private void notifyExternalService(PaymentInfo paymentInfo, String url) {
        try {
            ResponseEntity<Void> responseEntity = externalServiceRestTemplate.getForEntity(url, Void.class);
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(String.format("Unexpected status %d received when notifying external service " +
                                "about successful %s payment %d", responseEntity.getStatusCodeValue(),
                        paymentInfo.getPaymentType(), paymentInfo.getId()));
            }

            paymentRepository.updateExternalServiceNotifiedStatus(paymentInfo.getId(), true);
            log.info(String.format("Notified external service about successful %s payment %d", paymentInfo.getPaymentType(),
                    paymentInfo.getId()));
        } catch (Throwable t) {
            log.error(String.format("Failed to notify external service about successful %s payment %d",
                    paymentInfo.getPaymentType(), paymentInfo.getId()), t);
            paymentRepository.updateExternalServiceNotifiedStatus(paymentInfo.getId(), false);
        }

    }


}
