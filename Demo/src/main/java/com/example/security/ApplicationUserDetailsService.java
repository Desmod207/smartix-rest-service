package com.example.security;

import com.example.services.ApplicationUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final ApplicationUserService service;

    public ApplicationUserDetailsService(ApplicationUserService service) {
        this.service = service;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new ApplicationUserDetails(service.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Couldn't find user " + username)));
    }

}
