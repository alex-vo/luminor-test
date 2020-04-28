package com.paymentprocessor.aspect;

import com.paymentprocessor.service.info.RequestInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class CountryLoggingAspect {

    private final RabbitTemplate rabbitTemplate;

    @Before("execution(@(@org.springframework.web.bind.annotation.RequestMapping *) * *(..))")
    public void logCountry() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request == null) {
            return;
        }

        rabbitTemplate.convertAndSend(
                //TODO queue names to constants
                "countryLoggingRoutingKey",
                new RequestInfo(request.getRemoteAddr(), String.format("%s %s", request.getMethod(), request.getRequestURL()))
        );
    }

    private HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            return request;
        }

        log.error("Not called in the context of an HTTP request");
        return null;
    }

}
