package com.bank_card_management_system.springsecurity.dto;

import lombok.Data;

@Data
public class MoneyTransferRequest {
    private String numberFrom;
    private String numberTo;
    private Long cash;
}
