package com.paymentprocessor.repository;

import com.paymentprocessor.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select p.id " +
            "from Payment p " +
            "where p.status <> com.paymentprocessor.entity.PaymentStatus.CANCELLED " +
            "  and (:pAmountFrom is null or p.amount >= :pAmountFrom) " +
            "  and (:pAmountTo is null or p.amount <= :pAmountTo)")
    Set<Long> findIdsByAmountBetween(@Param("pAmountFrom") BigDecimal amountFrom, @Param("pAmountTo") BigDecimal amountTo);

    @Modifying
    @Transactional
    @Query("update Payment p " +
            "set p.status = com.paymentprocessor.entity.PaymentStatus.CANCELLED, " +
            "  p.cancellationFee = :pCancellationFee " +
            "where p.id=:pId")
    int cancelPayment(@Param("pId") Long id, @Param("pCancellationFee") BigDecimal cancellationFee);

    Optional<Payment> findByClientUsernameAndId(String clientUsername, Long id);

}