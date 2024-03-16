package com.example.services;

import com.example.data.ApplicationUserRepository;
import com.example.data.PaymentRepository;
import com.example.entities.ApplicationUser;
import com.example.entities.Payment;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationUserService {

    private static final long START_BALANCE = 100000L;

    private final ApplicationUserRepository applicationUserRepository;

    private final PaymentRepository paymentRepository;


    public ApplicationUserService(ApplicationUserRepository applicationUserRepository, PaymentRepository paymentRepository) {
        this.applicationUserRepository = applicationUserRepository;
        this.paymentRepository = paymentRepository;
    }

    public Optional<ApplicationUser> registrationNewUser(ApplicationUser applicationUser) {
        Optional<ApplicationUser> optionalUser = applicationUserRepository.findByLogin(applicationUser.getLogin());
        if (optionalUser.isPresent()) {
            return Optional.empty();
        } else {
            applicationUser.setBalance(START_BALANCE);
            return Optional.of(applicationUserRepository.save(applicationUser));
        }
    }

    public Optional<ApplicationUser> findByLogin(String login) {
        return applicationUserRepository.findByLogin(login);
    }

    public String makingPayment(String login, Payment payment) {
        ApplicationUser applicationUser = applicationUserRepository.findByLogin(login).get();

        long newBalance = applicationUser.getBalance() - payment.getAmount();

        payment.setDate(new Date());

        if (newBalance >= 0) {
            ApplicationUser updatingUser = new ApplicationUser(
                    applicationUser.getId(),
                    applicationUser.getLogin(),
                    applicationUser.getPassword(),
                    newBalance);
            payment.setUser(updatingUser);
            updatingUser.setPaymentHistory(applicationUser.getPaymentHistory());
            updatingUser.addPayment(payment);
            applicationUserRepository.save(updatingUser);
            return "Payment is success";
        } else
            return "Not enough funds";
    }

    public List<Payment> history(String login, PageRequest pageRequest) {
        ApplicationUser user = applicationUserRepository.findByLogin(login).get();
        return paymentRepository.findAllByUser(user, pageRequest);
    }

    public ApplicationUser updateUserProfile(String login, ApplicationUser userData) {
        ApplicationUser applicationUser = applicationUserRepository.findByLogin(login).get();
        if (userData.getFio() != null) {
            applicationUser.setFio(userData.getFio());
        }
        if (userData.getEmail() != null) {
            applicationUser.setEmail(userData.getEmail());
        }
        if (userData.getGender() != null) {
            applicationUser.setGender(userData.getGender());
        }
        if (userData.getBirthday() != null) {
            applicationUser.setBirthday(userData.getBirthday());
        }
        return applicationUserRepository.save(applicationUser);
    }
}
