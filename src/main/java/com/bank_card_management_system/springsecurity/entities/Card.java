package com.bank_card_management_system.springsecurity.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "card")
public class Card {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String number;
    @Getter
    private String last4number;
    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    @Getter
    private Date validityPeriod;
    @Getter
    @Setter
    private Status status;
    @Setter
    @Getter
    private Long balance;

    public Card() {}

    public Card(String number, String last4number, User owner, Date validityPeriod, Status status) {
        this.number = number;
        this.last4number = last4number;
        this.owner = owner;
        this.validityPeriod = validityPeriod;
        this.status = status;
    }
}
