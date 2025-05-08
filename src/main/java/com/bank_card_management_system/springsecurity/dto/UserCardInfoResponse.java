package com.bank_card_management_system.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCardInfoResponse {
    private String firstName;
    private String lastName;
    private String email;
}
