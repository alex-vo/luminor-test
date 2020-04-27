package com.paymentprocessor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener
@Slf4j
public class Receiver {

    @RabbitHandler
    public void handleMessage(String message) {
        log.info("Received <" + message + ">. Start processing");
        log.info("processing................");
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
//            log.error(String.format("failed to notify service about new %s payment %d", paymentType, paymentId), e);
            log.error("failed to notify service about new payment ", e);
        }
        log.info("Finished processing <" + message + ">");
    }


}
