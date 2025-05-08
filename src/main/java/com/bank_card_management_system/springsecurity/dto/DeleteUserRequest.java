package com.bank_card_management_system.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteUserRequest {
    private String email;
}
