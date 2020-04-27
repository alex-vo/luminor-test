package com.paymentprocessor.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class Client {

    @Id
    @GeneratedValue
    Long id;
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
//    List<Payment> payments;

}
