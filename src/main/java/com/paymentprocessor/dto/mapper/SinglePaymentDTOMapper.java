package com.paymentprocessor.dto.mapper;

import com.paymentprocessor.dto.SinglePaymentDTO;
import com.paymentprocessor.repository.view.PaymentView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SinglePaymentDTOMapper {

    SinglePaymentDTO toSinglePaymentDTO(PaymentView paymentView);

}
