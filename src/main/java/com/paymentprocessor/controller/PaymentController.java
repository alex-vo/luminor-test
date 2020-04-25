package com.paymentprocessor.controller;

import com.paymentprocessor.dto.PaymentDTO;
import com.paymentprocessor.dto.SinglePaymentDTO;
import com.paymentprocessor.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("api/v1/payment")
@Validated
@RequiredArgsConstructor
public class PaymentController {
    //TODO log country using aspect -> which thread it is? maybe also queue?

    private final PaymentService paymentService;

    @GetMapping
    public Set<Long> getPayments() {
        //TODO investigate built in filtering mechanisms
        return paymentService.getPayments();
    }

    @GetMapping
    @RequestMapping(":id")
    public SinglePaymentDTO getPayment(@PathVariable Long id) {
        return paymentService.getPayment(id);
    }

    @PostMapping
    public void createPayment(@Valid PaymentDTO paymentDTO) {
        paymentService.createPayment(paymentDTO);
    }

    @PostMapping
    @RequestMapping("cancel/:id")
    public void cancelPayment(@PathVariable Long id) {
        paymentService.cancelPayment(id);
    }

}
