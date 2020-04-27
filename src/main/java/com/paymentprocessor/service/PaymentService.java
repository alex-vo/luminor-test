package com.paymentprocessor.service;

import com.paymentprocessor.dto.PaymentDTO;
import com.paymentprocessor.dto.SinglePaymentDTO;
import com.paymentprocessor.entity.Payment;
import com.paymentprocessor.exception.BadRequestException;
import com.paymentprocessor.exception.NotFoundException;
import com.paymentprocessor.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Set<Long> findPaymentIds(BigDecimal amountFrom, BigDecimal amountTo) {
        return paymentRepository.findIdsByAmountBetween(amountFrom, amountTo);
    }

    public SinglePaymentDTO findPayment(Long id) {
        throw new UnsupportedOperationException();
    }

    public void createPayment(PaymentDTO paymentDTO) {
        throw new UnsupportedOperationException();
        //TODO if saved valid TYPE1 or TYPE2 then enqueue notification task
    }

    public void cancelPayment(Long clientId, Long id) {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = paymentRepository.findByClientIdAndId(clientId, id)
                .orElseThrow(() -> new NotFoundException("payment not found"));
        if (now.with(LocalTime.MIN).isAfter(payment.getCreated())) {
            throw new BadRequestException("cancellation timeout exceeded");
        }

        BigDecimal cancellationFee = calculateCancellationFee(payment, now);

        paymentRepository.cancelPayment(id, cancellationFee);
    }

    private BigDecimal calculateCancellationFee(Payment payment, LocalDateTime relativeTo) {
        BigDecimal hours = BigDecimal.valueOf(Duration.between(payment.getCreated(), relativeTo).toHours());
        BigDecimal cancellationFeeCoefficient = payment.getType().getCancellationFeeCoefficient();
        return hours.multiply(cancellationFeeCoefficient);
    }

}
