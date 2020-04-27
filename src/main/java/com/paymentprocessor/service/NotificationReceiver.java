package com.paymentprocessor.service;

import com.paymentprocessor.service.info.PaymentInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener
@Slf4j
public class NotificationReceiver {

    @RabbitHandler
    public void handleMessage(PaymentInfo paymentInfo) {
        System.out.println(paymentInfo);
        //todo notify external service
    }


}
