package com.paymentprocessor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentprocessor.dto.PaymentDTO;
import com.paymentprocessor.repository.PaymentRepository;
import org.awaitility.Awaitility;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureRule;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {PaymentControllerTest.Initializer.class})
public class PaymentControllerTest {

    @ClassRule
    public static GenericContainer dockerComposeContainer = new GenericContainer("rabbitmq:3.8.3-management")
            .withExposedPorts(5672, 15672)
            .waitingFor(Wait.forListeningPort());

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.rabbitmq.port=" + dockerComposeContainer.getMappedPort(5672)
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Rule
    public final OutputCaptureRule outputCaptureRule = new OutputCaptureRule();

    @Autowired
    MockMvc mvc;
    @Autowired
    PaymentRepository paymentRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser("user1")
    public void shouldCreateType1PaymentAndFailToNotifyExternalService() throws Exception {
        int paymentCount = getPaymentCount();
        PaymentDTO paymentDTO = objectMapper.readValue(new File("src/test/resources/valid_type1_payment.json"), PaymentDTO.class);

        String rawResponse = performPost("/api/v1/payment", paymentDTO);
        Long paymentId = Long.parseLong(rawResponse);

        mvc.perform(MockMvcRequestBuilders
                .get("/api/v1/payment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(paymentCount + 1)))
                .andReturn().getResponse().getContentAsString();

        Awaitility.waitAtMost(Duration.ofSeconds(10L)).pollInterval(Duration.ofSeconds(1L)).until(() -> {
            assertThat(paymentRepository.findById(paymentId).orElseThrow(),
                    hasProperty("externalServiceSuccessfullyNotified", is(false)));
            return true;
        });

        outputCaptureRule.expect(allOf(
                containsString("Call [GET http://localhost/api/v1/payment, ip=127.0.0.1] was made from"),
                containsString("Call [POST http://localhost/api/v1/payment, ip=127.0.0.1] was made from")
        ));
    }

    @Test
    @WithMockUser("user1")
    public void shouldCreateType2PaymentAndNotifyExternalService() throws Exception {
        int paymentCount = getPaymentCount();
        PaymentDTO paymentDTO = objectMapper.readValue(new File("src/test/resources/valid_type2_payment.json"), PaymentDTO.class);

        String rawResponse = performPost("/api/v1/payment", paymentDTO);
        Long paymentId = Long.parseLong(rawResponse);

        mvc.perform(MockMvcRequestBuilders
                .get("/api/v1/payment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(paymentCount + 1)))
                .andReturn().getResponse().getContentAsString();

        Awaitility.waitAtMost(Duration.ofSeconds(10L)).pollInterval(Duration.ofSeconds(1L)).until(() -> {
            assertThat(paymentRepository.findById(paymentId).orElseThrow(),
                    hasProperty("externalServiceSuccessfullyNotified", is(true)));
            return true;
        });

        outputCaptureRule.expect(allOf(
                containsString("Call [GET http://localhost/api/v1/payment, ip=127.0.0.1] was made from"),
                containsString("Call [POST http://localhost/api/v1/payment, ip=127.0.0.1] was made from")
        ));
    }

    private String performPost(String url, PaymentDTO paymentDTO) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                .post(url)
                .content(objectMapper.writeValueAsString(paymentDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private int getPaymentCount() throws Exception {
        String rawResponse = mvc.perform(MockMvcRequestBuilders
                .get("/api/v1/payment"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(rawResponse, Long[].class).length;
    }

    @Test
    @WithMockUser("user1")
    public void shouldFailToCreatePayment() throws Exception {
        PaymentDTO paymentDTO = objectMapper.readValue(new File("src/test/resources/invalid_payment.json"), PaymentDTO.class);
        mvc.perform(MockMvcRequestBuilders
                .post("/api/v1/payment")
                .content(objectMapper.writeValueAsString(paymentDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

}
