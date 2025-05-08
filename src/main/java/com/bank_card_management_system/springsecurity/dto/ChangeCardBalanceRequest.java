package com.bank_card_management_system.springsecurity.dto;

import lombok.Data;

@Data
public class ChangeCardBalanceRequest {
    private String number;
    private Long cash;
}
