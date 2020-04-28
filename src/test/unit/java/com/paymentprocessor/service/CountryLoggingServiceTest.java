package com.paymentprocessor.service;

import com.paymentprocessor.service.info.RequestInfo;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LoggerFactory.class)
public class CountryLoggingServiceTest {

    static Logger log;

    @Mock
    RestTemplate externalServiceRestTemplate;
    @InjectMocks
    CountryLoggingService countryLoggingService;


    @BeforeClass
    public static void setup() {
        log = mock(Logger.class);
        PowerMockito.mockStatic(LoggerFactory.class);
        PowerMockito.when(LoggerFactory.getLogger(CountryLoggingService.class)).thenReturn(log);
    }

    @Test
    public void shouldNotLogIfFailsToResolveCountry() {
        RequestInfo requestInfo = new RequestInfo("1.1.1.1", "POST http://def.lv/qwe");
        RuntimeException e = new RuntimeException("fail");
        when(externalServiceRestTemplate.getForObject(CountryLoggingService.URL + "1.1.1.1", CountryLoggingService.CountryInfo.class))
                .thenThrow(e);

        countryLoggingService.handleMessage(requestInfo);

        verify(log, never()).info(anyString());
        verify(log).error(String.format("Failed to lookup country for request %s", requestInfo), e);
    }

    @Test
    public void shouldLogResolvedCountry() {
        RequestInfo requestInfo = new RequestInfo("2.2.2.2", "PUT https://zxc.org/poi");
        CountryLoggingService.CountryInfo countryInfo = new CountryLoggingService.CountryInfo();
        countryInfo.setCountryName("Latvia");
        when(externalServiceRestTemplate.getForObject(CountryLoggingService.URL + "2.2.2.2", CountryLoggingService.CountryInfo.class))
                .thenReturn(countryInfo);

        countryLoggingService.handleMessage(requestInfo);

        verify(log).info(String.format("Call %s was made from %s", requestInfo, "Latvia"));
    }

}
