package com.paymentprocessor.service;

import com.paymentprocessor.dto.PaymentDTO;
import com.paymentprocessor.dto.SinglePaymentDTO;
import com.paymentprocessor.dto.mapper.PaymentMapper;
import com.paymentprocessor.entity.Payment;
import com.paymentprocessor.entity.PaymentType;
import com.paymentprocessor.exception.BadRequestException;
import com.paymentprocessor.exception.NotFoundException;
import com.paymentprocessor.repository.ClientRepository;
import com.paymentprocessor.repository.PaymentRepository;
import com.paymentprocessor.service.info.PaymentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;
    private final RabbitTemplate rabbitTemplate;

    public Set<Long> findPaymentIds(BigDecimal amountFrom, BigDecimal amountTo) {
        return paymentRepository.findIdsByAmountBetween(amountFrom, amountTo);
    }

    public SinglePaymentDTO findPayment(Long id) {
        throw new UnsupportedOperationException();
    }

    public void createPayment(String clientUsername, PaymentDTO paymentDTO) {
        Payment payment = paymentMapper.toPayment(paymentDTO, clientRepository.getOne(clientUsername));
        paymentRepository.save(payment);
        issuePaymentNotifications(payment);
    }

    public void cancelPayment(String clientUsername, Long id) {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = paymentRepository.findByClientUsernameAndId(clientUsername, id)
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

    private void issuePaymentNotifications(Payment payment) {
        if (payment.getType() != PaymentType.TYPE1 && payment.getType() != PaymentType.TYPE2) {
            return;
        }

        rabbitTemplate.convertAndSend(new PaymentInfo(payment.getId(), payment.getType()));
    }

}
