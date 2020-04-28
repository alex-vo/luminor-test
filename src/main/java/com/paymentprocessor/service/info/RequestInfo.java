package com.paymentprocessor.service.info;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RequestInfo implements Serializable {
    String ip;
    String requestPreview;

    @Override
    public String toString() {
        return "[" + requestPreview + ", ip='" + ip + ']';
    }
}
