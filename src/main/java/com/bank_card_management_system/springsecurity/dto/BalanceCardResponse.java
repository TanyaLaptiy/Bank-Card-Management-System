package com.bank_card_management_system.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class BalanceCardResponse {
    private Date validityPeriod;
    private Long balance;
    private UserCardInfoResponse owner;
    private String number;
}
