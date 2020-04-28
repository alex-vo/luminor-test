package com.paymentprocessor.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Check(constraints = "(type = 'TYPE1' and currency = 'EUR' and details is not null) " +
        "or (type = 'TYPE2' and currency = 'USD') " +
        "or (type = 'TYPE3' and creditor_bank_bic is not null)")
public class Payment {

    @Id
    @GeneratedValue
    Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    PaymentType type;
    @Column(nullable = false)
    BigDecimal amount;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
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
