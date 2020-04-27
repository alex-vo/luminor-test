package com.paymentprocessor.controller;

import com.paymentprocessor.dto.PaymentDTO;
import com.paymentprocessor.dto.SinglePaymentDTO;
import com.paymentprocessor.dto.validation.PaymentValidationSequence;
import com.paymentprocessor.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    //TODO log country using aspect -> which thread it is? maybe also queue?

    private final PaymentService paymentService;
    private final RabbitTemplate rabbitTemplate;

    @GetMapping
    public Set<Long> findPaymentIds(@RequestParam(required = false) BigDecimal amountFrom,
                                    @RequestParam(required = false) BigDecimal amountTo) {
        rabbitTemplate.convertAndSend("Hello from RabbitMQ!");
        return paymentService.findPaymentIds(amountFrom, amountTo);
    }

    @GetMapping
    @RequestMapping(":id")
    public SinglePaymentDTO findPayment(@PathVariable Long id) {
        return paymentService.findPayment(id);
    }

    @PostMapping
    public void createPayment(Principal principal,
                              @Validated(PaymentValidationSequence.class) @RequestBody PaymentDTO paymentDTO) {
        paymentService.createPayment(principal.getName(), paymentDTO);
    }

    @PostMapping
    @RequestMapping(":id/cancel")
    public void cancelPayment(Principal principal, @PathVariable Long id) {
        paymentService.cancelPayment(principal.getName(), id);
    }

}
