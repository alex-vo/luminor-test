package com.paymentprocessor.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class Payment {

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

    public enum Type {
        TYPE1, TYPE2, TYPE3;
    }

    public enum Currency {
        EUR, USD
    }

}
