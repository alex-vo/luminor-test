package com.paymentprocessor.dto.mapper;

import com.paymentprocessor.dto.PaymentDTO;
import com.paymentprocessor.entity.Client;
import com.paymentprocessor.entity.Payment;
import com.paymentprocessor.entity.PaymentType;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cancellationFee", ignore = true)
    @Mapping(target = "status", constant = "NEW")
    @Mapping(target = "created", expression = "java(LocalDateTime.now())")
    Payment toPayment(PaymentDTO paymentDTO, Client client);

    @AfterMapping
    default void finalizePaymentMapping(@MappingTarget Payment payment) {
        if (payment.getType() == PaymentType.TYPE1 || payment.getType() == PaymentType.TYPE2) {
            payment.setCreditorBankBic(null);
        }

        if (payment.getType() == PaymentType.TYPE3) {
            payment.setDetails(null);
        }
    }

}
