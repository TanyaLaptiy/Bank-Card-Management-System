package com.bank_card_management_system.springsecurity.services.impl;

import com.bank_card_management_system.springsecurity.dto.JwtAuthenticationResponse;
import com.bank_card_management_system.springsecurity.dto.RefreshTokenRequest;
import com.bank_card_management_system.springsecurity.dto.SignUpRequest;
import com.bank_card_management_system.springsecurity.dto.SigninRequest;
import com.bank_card_management_system.springsecurity.entities.Role;
import com.bank_card_management_system.springsecurity.entities.User;
import com.bank_card_management_system.springsecurity.repository.UserRepository;
import com.bank_card_management_system.springsecurity.services.AuthenticationService;
import com.bank_card_management_system.springsecurity.services.JWTService;
import com.bank_card_management_system.springsecurity.services.validations.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public User signup(SignUpRequest signUpRequest) {
        ValidationUtils.isValidEmail(signUpRequest.getEmail());

        User user = new User(signUpRequest.getFirstName(), signUpRequest.getLastName(), signUpRequest.getEmail(), passwordEncoder.encode(signUpRequest.getPassword()), Role.USER);
        return userRepository.save(user);
    }

    public JwtAuthenticationResponse signin(SigninRequest signinRequest) {
        ValidationUtils.isValidEmail(signinRequest.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signinRequest.getEmail(),
                        signinRequest.getPassword()));
        var user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(() -> new IllegalArgumentException("Некорректные данные (email или пароль)"));
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
        jwtAuthenticationResponse.setToken(jwtToken);
        jwtAuthenticationResponse.setRefreshToken(refreshToken);
        return jwtAuthenticationResponse;
    }

    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userEmail = jwtService.extractUserName(refreshTokenRequest.getToken());
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
            var jwt = jwtService.generateToken(user);

            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
            return jwtAuthenticationResponse;
        }
        return null;
    }
}
