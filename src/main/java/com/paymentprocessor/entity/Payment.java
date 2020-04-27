package com.paymentprocessor.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
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
    Type type;
    BigDecimal amount;
    Currency currency;
    String debtorIban;
    String creditorIban;
    String details;
    String creditorBankBic;
    @Enumerated(EnumType.STRING)
    PaymentStatus status;
    BigDecimal cancellationFee;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    Client client;
    LocalDateTime created;

    @Getter
    @AllArgsConstructor
    public enum Type {
        TYPE1(BigDecimal.valueOf(0.05)), TYPE2(BigDecimal.valueOf(0.1)), TYPE3(BigDecimal.valueOf(0.15));

        private BigDecimal cancellationFeeCoefficient;
    }

    public enum Currency {
        EUR, USD
    }

//    public enum Status {
//        NEW, CANCELLED
//    }

}
