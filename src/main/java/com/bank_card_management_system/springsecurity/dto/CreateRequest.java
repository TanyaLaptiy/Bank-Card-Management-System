package com.bank_card_management_system.springsecurity.dto;

import lombok.Data;

@Data
public class CreateRequest {
    private String number;
    private String email;
}
