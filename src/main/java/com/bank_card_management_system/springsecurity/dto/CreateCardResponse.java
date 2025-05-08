package com.bank_card_management_system.springsecurity.dto;

import com.bank_card_management_system.springsecurity.entities.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class CreateCardResponse {
    private Date validityPeriod;
    private Status status;
    private UserCardInfoResponse owner;
    private String number;
}
