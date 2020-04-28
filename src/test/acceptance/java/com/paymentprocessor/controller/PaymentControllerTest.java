package com.paymentprocessor.controller;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PaymentControllerTest {

    @ClassRule
    static DockerComposeContainer dockerComposeContainer = new DockerComposeContainer(new File("docker-compose.yml"));

    @Autowired
    MockMvc mvc;

    @Test
    public void shouldGetPayments() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/api/v1/payment"))
                .andExpect(status().isOk())
//                .andReturn().getResponse()
        ;
    }

}
