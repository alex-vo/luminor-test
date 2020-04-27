package com.paymentprocessor.service;

import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PaymentServiceTest {

    @Test
    public void testTest() {
        System.out.println(Duration.between(LocalDateTime.now().with(LocalTime.MIN), LocalDateTime.now()).toHours());
    }

}
