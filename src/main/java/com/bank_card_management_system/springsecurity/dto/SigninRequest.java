package com.bank_card_management_system.springsecurity.dto;

import lombok.Data;

@Data
public class SigninRequest {
    private String email;
    private String password;
}
