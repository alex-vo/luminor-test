package com.paymentprocessor.service;

import com.paymentprocessor.dto.PaymentDTO;
import com.paymentprocessor.dto.SinglePaymentDTO;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PaymentService {

    public Set<Long> getPayments() {
        throw new UnsupportedOperationException();
    }

    public SinglePaymentDTO getPayment(Long id) {
        throw new UnsupportedOperationException();
    }

    public void createPayment(PaymentDTO paymentDTO) {
        throw new UnsupportedOperationException();
        //TODO if saved valid TYPE1 or TYPE2 then enqueue notification task
    }

    public void cancelPayment(Long id) {
        throw new UnsupportedOperationException();
    }

}
