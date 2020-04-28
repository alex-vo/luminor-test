package com.paymentprocessor.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class Payment {

    //TODO define constraints

    @Id
    @GeneratedValue
    Long id;
    @Column(nullable = false)
    PaymentType type;
    @Column(nullable = false)
    BigDecimal amount;
    @Column(nullable = false)
    PaymentCurrency currency;
    @Column(nullable = false)
    String debtorIban;
    @Column(nullable = false)
    String creditorIban;
    String details;
    String creditorBankBic;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    PaymentStatus status;
    BigDecimal cancellationFee;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    Client client;
    @Column(nullable = false)
    LocalDateTime created;
    Boolean externalServiceSuccessfullyNotified;

}
