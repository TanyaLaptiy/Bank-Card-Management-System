package com.bank_card_management_system.springsecurity.services;

import com.bank_card_management_system.springsecurity.dto.UserCardInfoRequest;
import com.bank_card_management_system.springsecurity.dto.UserCardInfoResponse;
import com.bank_card_management_system.springsecurity.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();

    User getById(Long id);

    UserCardInfoResponse updateByEmail(UserCardInfoRequest user);

    UserCardInfoResponse deleteByEmail(String email);
}
