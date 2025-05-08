package com.bank_card_management_system.springsecurity.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CreateCardRequest {
    private String ownerEmail;
    private String number;
    private Date validityPeriod;
}
