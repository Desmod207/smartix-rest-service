package com.example.entities;

public record Balance(String login, double balance) {

    public Balance(ApplicationUser applicationUser) {
        this(applicationUser.getLogin(), (double)applicationUser.getBalance() / 100);
    }

}
