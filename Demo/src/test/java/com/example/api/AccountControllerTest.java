package com.example.api;

import com.example.entities.ApplicationUser;
import com.example.entities.Balance;
import com.example.entities.Gender;
import com.example.entities.Payment;
import com.example.security.ApplicationUserDetails;
import com.example.services.ApplicationUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    ApplicationUserService service;

    @Mock
    PasswordEncoder encoder;

    @InjectMocks
    AccountController controller;

    @Test
    public void handleRegistrationNewUser_OptionalIsNotEmpty_ReturnsValidResponseEntity() {
        var applicationUser = new ApplicationUser(1L, "login", "password", 1000L);
        var optionalUser = Optional.of(applicationUser);

        Mockito.doReturn(optionalUser).when(service).registrationNewUser(applicationUser);
        Mockito.doReturn("password").when(encoder).encode(any());

        var responseEntity = controller.handleRegistrationNewUser(applicationUser);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(applicationUser, responseEntity.getBody());
    }

    @Test
    public void handleRegistrationNewUser_OptionalIsEmpty_ReturnsValidResponseEntity() {
        var applicationUser = new ApplicationUser(1L, "login", "password", 1000L);

        Mockito.doReturn(Optional.empty()).when(service).registrationNewUser(applicationUser);

        var responseEntity = controller.handleRegistrationNewUser(applicationUser);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void handleBalance_ReturnsValidResponseEntity() {
        var applicationUser = new ApplicationUser(1L, "login", "password", 1000L);
        var applicationUserDetails = new ApplicationUserDetails(applicationUser);

        Mockito.doReturn(Optional.of(applicationUser)).when(service).findByLogin(applicationUser.getLogin());

        var responseEntity = controller.handleBalance(applicationUserDetails);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(new Balance(applicationUser), responseEntity.getBody());
    }

    @Test
    public void handleMakingPayment_ReturnsValidResponseEntity() {
        var applicationUser = new ApplicationUser(1L, "login", "password", 100000L);
        var applicationUserDetails = new ApplicationUserDetails(applicationUser);
        var payment = new Payment(1L, new Date(), "+79876543210", 1500, applicationUser);

        Mockito.doReturn("Payment is success").when(service).makingPayment(applicationUserDetails.getUsername(), payment);

        var responseEntity = controller.handleMakingPayment(applicationUserDetails, payment);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void handleHistory_ReturnsValidResponseEntity() {
        var applicationUser = new ApplicationUser(1L, "login", "password", 100000L);
        var applicationUserDetails = new ApplicationUserDetails(applicationUser);
        var pageRequest = PageRequest.of(0, 5, Sort.by("id"));
        var payments = List.of(
                new Payment(1L, new Date(), "+79876543210", 1500, applicationUser),
                new Payment(2L, new Date(), "+79876543210", 1500, applicationUser)
        );

        Mockito.doReturn(payments).when(service).history(applicationUserDetails.getUsername(), pageRequest);

        var responseEntity = controller.handleHistory(0, 5, applicationUserDetails);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(payments, responseEntity.getBody());
    }

    @Test
    public void handleUpdateUserProfile_ReturnsValidResponseEntity() {
        var applicationUser = new ApplicationUser(1L, "login", "password", 100000L);
        var applicationUserDetails = new ApplicationUserDetails(applicationUser);
        applicationUser.setFio("Иванов Иван Иванович");
        applicationUser.setGender(Gender.MALE);

        Mockito.doReturn(applicationUser).when(service)
                .updateUserProfile(applicationUserDetails.getUsername(), applicationUser);

        var responseEntity = controller.handleUpdateUserProfile(applicationUserDetails, applicationUser);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(applicationUser, responseEntity.getBody());
    }

}
