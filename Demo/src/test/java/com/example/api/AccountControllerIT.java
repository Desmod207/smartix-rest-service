package com.example.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Sql("/sql/account_controller/test_data.sql")
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Transactional
public class AccountControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void handleRegistrationNewUser_OptionalIsNotEmpty_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "login":"login2",
                            "password":"password2"
                        }
                        """);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                    {
                                        "login":"login2",
                                        "balance":100000
                                    }
                                """),
                        jsonPath("$.id").exists(),
                        jsonPath("$.password").exists()
                );
    }

    @Test
    public void handleRegistrationNewUser_OptionalIsEmpty_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "login":"login1",
                            "password":"1"
                        }
                        """);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION)
                );
    }

    @Test
    public void handleBalance_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.get("/api/balance")
                .with(httpBasic("login1", "password1"));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "login":"login1",
                                    "balance":1000.0
                                }
                                """)
                );
    }

    @Test
    public void handleMakingPayment_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/payment")
                .with(httpBasic("login1", "password1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "phoneNumber": "+79876543210",
                            "amount": 1500
                        }
                        """);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().string("Payment is success")
                );
    }

    @Test
    public void handleHistory_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.get("/api/history")
                .with(httpBasic("login1", "password1"));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {
                                        "date": "2024-03-17T08:43:35.026+00:00",
                                        "phoneNumber": "+79876543210",
                                        "amount": 15000
                                    }
                                ]
                                """),
                        jsonPath("$.[0].id").exists()
                );
    }

    @Test
    public void handleUpdateUserProfile_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/edit")
                .with(httpBasic("login1", "password1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "login":"1",
                            "fio":"Иванов Иван Иванович",
                            "gender":"MALE",
                            "email":"ivan@mail.ru"
                        }
                        """);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "login": "login1",
                                    "password": "$2a$10$k0OmFCyYmCE3fPa9klkIm.AkWE0r6mzR9IKHRPG90IjAPmZmjuwfu",
                                    "balance": 100000,
                                    "fio": "Иванов Иван Иванович",
                                    "email": "ivan@mail.ru",
                                    "gender": "MALE",
                                    "birthday": null
                                }
                                """),
                        jsonPath("$.id").exists()
                );
    }

}
