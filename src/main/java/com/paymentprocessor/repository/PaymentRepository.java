package com.paymentprocessor.repository;

import com.paymentprocessor.entity.Payment;
import com.paymentprocessor.repository.view.PaymentView;
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

    Optional<PaymentView> findCancellationFeeById(Long id);

    @Query("select p " +
            "from Payment p " +
            "where p.id = :pId and p.client.username = :pClientUsername " +
            "  and p.status <> com.paymentprocessor.entity.PaymentStatus.CANCELLED")
    Optional<Payment> findNotCancelledPayment(@Param("pId") Long id, @Param("pClientUsername") String clientUsername);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Payment p " +
            "set p.externalServiceSuccessfullyNotified = :pExternalServiceSuccessfullyNotified " +
            "where p.id=:pId")
    int updateExternalServiceNotifiedStatus(@Param("pId") Long id,
                                            @Param("pExternalServiceSuccessfullyNotified") Boolean externalServiceNotified);

}