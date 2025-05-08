package com.bank_card_management_system.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteResponse {
    private String number;
    private UserCardInfoResponse userCardInfoResponse;
}
