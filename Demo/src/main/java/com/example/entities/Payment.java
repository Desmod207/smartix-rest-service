package com.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Payment {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private Date date;

        @Column(nullable = false)
        private String phoneNumber;

        @Column(nullable = false)
        private long amount;

        @ManyToOne
        @JoinColumn(name = "user_id", referencedColumnName = "login", nullable = false,
                foreignKey = @ForeignKey(name = "user_payment_fk"))
        @JsonIgnore
        private ApplicationUser user;

        public  Payment() {}

        public Payment(Long id, Date date, String phoneNumber, long amount, ApplicationUser user) {
                this.id = id;
                this.date = date;
                this.phoneNumber = phoneNumber;
                this.amount = amount;
                this.user = user;
        }

        public Long getId() {
                return id;
        }

        public Date getDate() {
                return date;
        }

        public void setDate(Date date) {
                this.date = date;
        }

        public String getPhoneNumber() {
                return phoneNumber;
        }

        public long getAmount() {
                return amount;
        }

        public ApplicationUser getUser() {
                return user;
        }

        public void setUser(ApplicationUser applicationUser) {
                this.user = applicationUser;
        }

}
