package com.bank_card_management_system.springsecurity.services.impl;

import com.bank_card_management_system.springsecurity.dto.UserCardInfoRequest;
import com.bank_card_management_system.springsecurity.dto.UserCardInfoResponse;
import com.bank_card_management_system.springsecurity.entities.User;
import com.bank_card_management_system.springsecurity.repository.UserRepository;
import com.bank_card_management_system.springsecurity.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
            }
        };
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    @Transactional
    @Override
    public UserCardInfoResponse updateByEmail(UserCardInfoRequest user) {
        if (!user.getEmail().matches("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$")) {
            throw new UnsupportedOperationException(String.format("Некорректный email: %s", user.getEmail()));
        }
        try {
            userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
            userRepository.updateByEmail(user.getFirstName(), user.getLastName(), user.getEmail());
        } catch (Exception e) {
            logger.error(String.format("Не удалось внести изменения в аккаунт пользователя с Email: %s. По причине: %s", user.getEmail(), e.getMessage()));
            throw new UnsupportedOperationException(String.format("Не удалось внести изменения в аккаунт пользователя с Email: %s", user.getEmail()));
        }
        return new UserCardInfoResponse(user.getFirstName(), user.getLastName(), user.getEmail());
    }

    @Transactional
    @Override
    public UserCardInfoResponse deleteByEmail(String email) {
        if (!email.matches("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$")) {
            throw new UnsupportedOperationException(String.format("Некорректный email: %s", email));
        }
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        try {
            userRepository.deleteByEmail(email);
        } catch (Exception e) {
            logger.error(String.format("Не удалось удалить карту по email: %s. По причине: %s", email, e.getMessage()));
            throw new UnsupportedOperationException(String.format("Не удалось удалить карту по email: %s", email));
        }
        return new UserCardInfoResponse(user.getFirstName(), user.getSecondName(), user.getEmail());
    }


}
