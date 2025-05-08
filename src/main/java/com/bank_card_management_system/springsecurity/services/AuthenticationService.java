package com.bank_card_management_system.springsecurity.services;

import com.bank_card_management_system.springsecurity.dto.JwtAuthenticationResponse;
import com.bank_card_management_system.springsecurity.dto.RefreshTokenRequest;
import com.bank_card_management_system.springsecurity.dto.SignUpRequest;
import com.bank_card_management_system.springsecurity.dto.SigninRequest;
import com.bank_card_management_system.springsecurity.entities.User;

public interface AuthenticationService {
    User signup(SignUpRequest signUpRequest);

    JwtAuthenticationResponse signin(SigninRequest signinRequest);

    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
