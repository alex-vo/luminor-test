package com.paymentprocessor.controller;

import com.paymentprocessor.dto.PaymentDTO;
import com.paymentprocessor.dto.SinglePaymentDTO;
import com.paymentprocessor.dto.validation.PaymentValidationSequence;
import com.paymentprocessor.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public Set<Long> findPaymentIds(@RequestParam(required = false) BigDecimal amountFrom,
                                    @RequestParam(required = false) BigDecimal amountTo) {
        return paymentService.findPaymentIds(amountFrom, amountTo);
    }

    @GetMapping
    @RequestMapping(":id")
    public SinglePaymentDTO findPayment(@PathVariable Long id) {
        return paymentService.findPayment(id);
    }

    @PostMapping
    public Long createPayment(Principal principal,
                              @Validated(PaymentValidationSequence.class) @RequestBody PaymentDTO paymentDTO) {
        return paymentService.createPayment(principal.getName(), paymentDTO);
    }

    @PostMapping
    @RequestMapping(":id/cancel")
    public void cancelPayment(Principal principal, @PathVariable Long id) {
        paymentService.cancelPayment(principal.getName(), id);
    }

}
