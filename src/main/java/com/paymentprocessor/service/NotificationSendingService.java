package com.paymentprocessor.service;

import com.paymentprocessor.repository.PaymentRepository;
import com.paymentprocessor.service.info.PaymentInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RabbitListener
@Slf4j
public class NotificationSendingService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate externalServiceRestTemplate;
    private final String type1NotificationUrl;
    private final String type2NotificationUrl;

    public NotificationSendingService(PaymentRepository paymentRepository, RestTemplate externalServiceRestTemplate,
                                      @Value("${application.external.type1_payment_notification_url}") String type1NotificationUrl,
                                      @Value("${application.external.type2_payment_notification_url}") String type2NotificationUrl) {
        this.paymentRepository = paymentRepository;
        this.externalServiceRestTemplate = externalServiceRestTemplate;
        this.type1NotificationUrl = type1NotificationUrl;
        this.type2NotificationUrl = type2NotificationUrl;
    }

    @RabbitHandler
    public void handleMessage(PaymentInfo paymentInfo) {
        switch (paymentInfo.getPaymentType()) {
            case TYPE1:
                notifyExternalService(paymentInfo, type1NotificationUrl);
                break;
            case TYPE2:
                notifyExternalService(paymentInfo, type2NotificationUrl);
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
            log.info(String.format("Notified external service '%s' about successful %s payment %d", url,
                    paymentInfo.getPaymentType(), paymentInfo.getId()));
        } catch (Throwable t) {
            log.error(String.format("Failed to notify external service about successful %s payment %d",
                    paymentInfo.getPaymentType(), paymentInfo.getId()), t);
            paymentRepository.updateExternalServiceNotifiedStatus(paymentInfo.getId(), false);
        }

    }


}
