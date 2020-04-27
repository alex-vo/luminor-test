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
    PaymentType type;
    BigDecimal amount;
    PaymentCurrency currency;
    String debtorIban;
    String creditorIban;
    String details;
    String creditorBankBic;
    @Enumerated(EnumType.STRING)
    PaymentStatus status;
    BigDecimal cancellationFee;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Client client; //todo change to lazy
    LocalDateTime created;

}
