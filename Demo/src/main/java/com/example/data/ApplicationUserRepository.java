package com.example.data;

import com.example.entities.ApplicationUser;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ApplicationUserRepository extends CrudRepository<ApplicationUser, Long> {

    Optional<ApplicationUser> findByLogin(String login);

}
