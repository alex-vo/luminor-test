package com.paymentprocessor.controller;

import com.paymentprocessor.dto.PaymentDTO;
import com.paymentprocessor.dto.SinglePaymentDTO;
import com.paymentprocessor.dto.validation.PaymentValidationSequence;
import com.paymentprocessor.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Set;

@RestController
@RequestMapping("api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    //TODO log country using aspect -> which thread it is? maybe also queue?

    private final PaymentService paymentService;

    @GetMapping
    public Set<Long> findPaymentIds(@RequestParam BigDecimal amountFrom, @RequestParam BigDecimal amountTo) {
        return paymentService.findPaymentIds(amountFrom, amountTo);
    }

    @GetMapping
    @RequestMapping(":id")
    public SinglePaymentDTO findPayment(@PathVariable Long id) {
        return paymentService.findPayment(id);
    }

    @PostMapping
    public void createPayment(@Validated(PaymentValidationSequence.class) @RequestBody PaymentDTO paymentDTO) {
        paymentService.createPayment(paymentDTO);
    }

    @PostMapping
    @RequestMapping(":id/cancel")
    public void cancelPayment(@PathVariable Long id, Authentication authentication) {
        paymentService.cancelPayment((Long) authentication.getPrincipal(), id);
    }

}
