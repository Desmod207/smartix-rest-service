package com.example.api;

import com.example.entities.ApplicationUser;
import com.example.entities.Balance;
import com.example.entities.Payment;

import com.example.security.ApplicationUserDetails;
import com.example.services.ApplicationUserService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class AccountController {

    private final ApplicationUserService applicationUserService;

    public AccountController(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    @PostMapping("/api/registration")
    @Transactional
    public ResponseEntity<?> handleRegistrationNewUser(@RequestBody  ApplicationUser applicationUser) {
        Optional<ApplicationUser> optionalUser = applicationUserService.registrationNewUser(applicationUser);
        if (optionalUser.isPresent()) {
            return ResponseEntity.created(URI.create("/api/profile"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(optionalUser.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/balance")
    public ResponseEntity<Balance> handleBalance(@AuthenticationPrincipal ApplicationUserDetails user) {
        ApplicationUser applicationUser = applicationUserService.findByLogin(user.getUsername()).get();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Balance(applicationUser));
    }

    @Transactional
    @PostMapping("/api/payment")
    public ResponseEntity<String> handleMakingPayment(
            @AuthenticationPrincipal ApplicationUserDetails user,
            @RequestBody Payment payment) {
        return ResponseEntity.ok(applicationUserService.makingPayment(
                user.getUsername(), payment));
    }

    @GetMapping("/api/history")
    public ResponseEntity<List<Payment>> handleHistory(
            @RequestParam(value = "page", required = false , defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10" ) int size,
            @AuthenticationPrincipal ApplicationUserDetails user) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(applicationUserService.history(user.getUsername(), pageRequest));
    }

    @PostMapping("/api/edit")
    public ResponseEntity<ApplicationUser> handleUpdateUserProfile(
            @AuthenticationPrincipal ApplicationUserDetails user,
            @RequestBody ApplicationUser applicationUser) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(applicationUserService.updateUserProfile(user.getUsername(), applicationUser));
    }

}
