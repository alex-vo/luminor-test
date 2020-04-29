package com.paymentprocessor.aspect;

import com.paymentprocessor.config.RabbitMQSettings;
import com.paymentprocessor.service.info.RequestInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RequestContextHolder.class, ServletRequestAttributes.class})
public class CountryLoggingAspectTest {

    @Mock
    RabbitTemplate rabbitTemplate;
    @InjectMocks
    CountryLoggingAspect countryLoggingAspect;

    @Test
    public void shouldNotCallMQIfCalledOutsideOfHttpContext() {
        countryLoggingAspect.logCountry();

        verify(rabbitTemplate, never()).convertAndSend(any(String.class), any(Object.class));
    }

    @Test
    public void shouldCallMQWithCorrectQueueNameAndRequestInfo() {
        ServletRequestAttributes requestAttributes = PowerMockito.mock(ServletRequestAttributes.class);
        HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
        PowerMockito.when(request.getRemoteAddr()).thenReturn("8.8.8.8");
        PowerMockito.when(request.getMethod()).thenReturn("GET");
        PowerMockito.when(request.getRequestURL()).thenReturn(new StringBuffer("http://abc.com"));
        PowerMockito.when(requestAttributes.getRequest()).thenReturn(request);
        PowerMockito.mockStatic(RequestContextHolder.class);
        PowerMockito.when(RequestContextHolder.getRequestAttributes()).thenReturn(requestAttributes);

        countryLoggingAspect.logCountry();

        verify(rabbitTemplate, times(1)).convertAndSend(eq(RabbitMQSettings.COUNTRY_LOGGING_ROUTING_KEY), eq(new RequestInfo("8.8.8.8", "GET http://abc.com")));
    }

}
