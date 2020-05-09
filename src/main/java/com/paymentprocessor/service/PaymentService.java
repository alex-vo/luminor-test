package com.paymentprocessor.service;

import com.paymentprocessor.config.RabbitMQSettings;
import com.paymentprocessor.dto.PaymentDTO;
import com.paymentprocessor.dto.SinglePaymentDTO;
import com.paymentprocessor.dto.mapper.PaymentMapper;
import com.paymentprocessor.dto.mapper.SinglePaymentDTOMapper;
import com.paymentprocessor.entity.Payment;
import com.paymentprocessor.entity.PaymentStatus;
import com.paymentprocessor.entity.PaymentType;
import com.paymentprocessor.exception.BadRequestException;
import com.paymentprocessor.exception.NotFoundException;
import com.paymentprocessor.repository.ClientRepository;
import com.paymentprocessor.repository.PaymentRepository;
import com.paymentprocessor.service.info.PaymentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SinglePaymentDTOMapper singlePaymentDTOMapper;

    public Set<Long> findPaymentIds(BigDecimal amountFrom, BigDecimal amountTo) {
        return paymentRepository.findIdsByAmountBetween(amountFrom, amountTo);
    }

    public SinglePaymentDTO findPayment(Long id) {
        return paymentRepository.findCancellationFeeById(id)
                .map(singlePaymentDTOMapper::toSinglePaymentDTO)
                .orElseThrow(() -> new NotFoundException("payment not found"));
    }

    public Long createPayment(String clientUsername, PaymentDTO paymentDTO) {
        Payment payment = paymentMapper.toPayment(paymentDTO, clientRepository.getOne(clientUsername));
        paymentRepository.save(payment);
        issuePaymentNotifications(payment);

        return payment.getId();
    }

    @Transactional
    public void cancelPayment(String clientUsername, Long id) {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = paymentRepository.findNotCancelledPayment(id, clientUsername)
                .orElseThrow(() -> new NotFoundException("payment not found"));
        if (now.with(LocalTime.MIN).isAfter(payment.getCreated())) {
            throw new BadRequestException("cancellation timeout exceeded");
        }

        BigDecimal cancellationFee = calculateCancellationFee(payment, now);
        payment.setCancellationFee(cancellationFee);
        payment.setStatus(PaymentStatus.CANCELLED);
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

        rabbitTemplate.convertAndSend(RabbitMQSettings.NOTIFICATION_ROUTING_KEY, new PaymentInfo(payment.getId(), payment.getType()));
    }

}
