package com.example.services;

import com.example.data.ApplicationUserRepository;
import com.example.data.PaymentRepository;
import com.example.entities.ApplicationUser;
import com.example.entities.Gender;
import com.example.entities.Payment;
import com.example.security.ApplicationUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ApplicationUserServiceTest {

    @Mock
    ApplicationUserRepository applicationUserRepository;

    @Mock
    PaymentRepository paymentRepository;

    @InjectMocks
    ApplicationUserService service;

    @Test
    public void registrationNewUser_ReturnsNotEmptyOptional() {
        var applicationUser = new ApplicationUser(1L, "login1", "password1", 100000L);

        Mockito.doReturn(Optional.empty()).when(applicationUserRepository)
                .findByLogin(applicationUser.getLogin());
        Mockito.doReturn(applicationUser).when(applicationUserRepository).save(applicationUser);

        Optional<ApplicationUser> optionalNewUser = service.registrationNewUser(applicationUser);

        assertTrue(optionalNewUser.isPresent());
        assertEquals(applicationUser, optionalNewUser.get());

        Mockito.verify(applicationUserRepository).findByLogin(applicationUser.getLogin());
        Mockito.verify(applicationUserRepository).save(applicationUser);
        Mockito.verifyNoMoreInteractions(applicationUserRepository);
    }

    @Test
    public void registrationNewUser_ReturnsEmptyOptional() {
        var applicationUser = new ApplicationUser(1L, "login1", "password1", 100000L);

        Mockito.doReturn(Optional.of(applicationUser)).when(applicationUserRepository)
                .findByLogin(applicationUser.getLogin());

        Optional<ApplicationUser> optionalNewUser = service.registrationNewUser(applicationUser);

        assertFalse(optionalNewUser.isPresent());

        Mockito.verify(applicationUserRepository).findByLogin(any());
        Mockito.verifyNoMoreInteractions(applicationUserRepository);
    }

    @Test
    public void findByLogin_ReturnsValidApplicationUser() {
        var applicationUser = new ApplicationUser(1L, "login1", "password1", 100000L);

        Mockito.doReturn(Optional.of(applicationUser)).when(applicationUserRepository)
                .findByLogin(applicationUser.getLogin());

        Optional<ApplicationUser> optionalUser = service.findByLogin(applicationUser.getLogin());

        assertTrue(optionalUser.isPresent());
        assertEquals(applicationUser, optionalUser.get());

        Mockito.verify(applicationUserRepository).findByLogin(any());
        Mockito.verifyNoMoreInteractions(applicationUserRepository);
    }

    @Test
    public void makingPayment_FoundsEnough_ReturnsResponseString() {
        var applicationUser = new ApplicationUser(1L, "login1", "password1", 100000L);
        var payment = new Payment(1L, new Date(), "+79876543210", 1500, applicationUser);

        Mockito.doReturn(Optional.of(applicationUser)).when(applicationUserRepository)
                .findByLogin(applicationUser.getLogin());

        String responseString = service.makingPayment(applicationUser.getLogin(), payment);

        assertNotNull(responseString);
        assertEquals("Payment is success", responseString);

        Mockito.verify(applicationUserRepository).findByLogin(any());
        Mockito.verify(applicationUserRepository).save(any());
        Mockito.verifyNoMoreInteractions(applicationUserRepository);
    }

    @Test
    public void makingPayment_FoundsNotEnough_ReturnsResponseString() {
        var applicationUser = new ApplicationUser(1L, "login1", "password1", 100000L);
        var payment = new Payment(1L, new Date(), "+79876543210", 150000, applicationUser);

        Mockito.doReturn(Optional.of(applicationUser)).when(applicationUserRepository)
                .findByLogin(applicationUser.getLogin());

        String responseString = service.makingPayment(applicationUser.getLogin(), payment);

        assertNotNull(responseString);
        assertEquals("Not enough funds", responseString);

        Mockito.verify(applicationUserRepository).findByLogin(any());
        Mockito.verifyNoMoreInteractions(applicationUserRepository);
    }

    @Test
    public void history_ReturnsValidResponseEntity() {
        var applicationUser = new ApplicationUser(1L, "login", "password", 100000L);
        var applicationUserDetails = new ApplicationUserDetails(applicationUser);
        var pageRequest = PageRequest.of(0, 5, Sort.by("id"));
        var payments = List.of(
                new Payment(1L, new Date(), "+79876543210", 1500, applicationUser),
                new Payment(2L, new Date(), "+79876543210", 1500, applicationUser)
        );

        Mockito.doReturn(Optional.of(applicationUser)).when(applicationUserRepository)
                .findByLogin(applicationUserDetails.getUsername());
        Mockito.doReturn(payments).when(paymentRepository).findAllByUser(applicationUser, pageRequest);

        var returnsPayments = service.history(applicationUserDetails.getUsername(), pageRequest);

        assertNotNull(returnsPayments);
        assertEquals(payments, returnsPayments);

        Mockito.verify(applicationUserRepository).findByLogin(any());
        Mockito.verify(paymentRepository).findAllByUser(any(), any());
        Mockito.verifyNoMoreInteractions(applicationUserRepository);
        Mockito.verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    public void updateUserProfile_ReturnsValidResponseEntity() {
        var applicationUser = new ApplicationUser(1L, "login", "password", 100000L);
        var applicationUserDetails = new ApplicationUserDetails(applicationUser);
        applicationUser.setFio("Иванов Иван Иванович");
        applicationUser.setEmail("ivan@mail.ru");
        applicationUser.setGender(Gender.MALE);
        applicationUser.setBirthday(new Date());

        Mockito.doReturn(Optional.of(applicationUser)).when(applicationUserRepository)
                .findByLogin(applicationUserDetails.getUsername());
        Mockito.doReturn(applicationUser).when(applicationUserRepository).save(applicationUser);

        var updateUser = service.updateUserProfile(applicationUserDetails.getUsername(), applicationUser);

        assertNotNull(updateUser);
        assertEquals(updateUser, applicationUser);

        Mockito.verify(applicationUserRepository).findByLogin(any());
        Mockito.verify(applicationUserRepository).save(any());
        Mockito.verifyNoMoreInteractions(applicationUserRepository);
    }

}