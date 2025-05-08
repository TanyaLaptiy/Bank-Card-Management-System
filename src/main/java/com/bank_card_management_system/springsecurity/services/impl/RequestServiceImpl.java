package com.bank_card_management_system.springsecurity.services.impl;

import com.bank_card_management_system.springsecurity.dto.*;
import com.bank_card_management_system.springsecurity.entities.Card;
import com.bank_card_management_system.springsecurity.entities.Request;
import com.bank_card_management_system.springsecurity.entities.Status;
import com.bank_card_management_system.springsecurity.entities.User;
import com.bank_card_management_system.springsecurity.repository.CardRepository;
import com.bank_card_management_system.springsecurity.repository.RequestRepository;
import com.bank_card_management_system.springsecurity.repository.UserRepository;
import com.bank_card_management_system.springsecurity.services.CardService;
import com.bank_card_management_system.springsecurity.services.RequestService;
import com.bank_card_management_system.springsecurity.services.utils.CardNumberUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private static final Logger logger = LoggerFactory.getLogger(RequestServiceImpl.class);

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public CreateDeleteRequest createRequest(CreateDeleteRequest request) {
        String visiblePart = request.getNumber().substring(request.getNumber().length() - 4);
        String maskedCardNumber = CardNumberUtils.getMaskOfCardNumber(visiblePart);

        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email).orElseThrow();

            String hashCard = CardNumberUtils.hashCardNumber(request.getNumber());
            Request createRequest = new Request(hashCard, visiblePart, user);
            requestRepository.save(createRequest);
        } catch (Exception e) {
            logger.error(String.format("Не удалось добавить запрос на удаление карты по номеру: %s. По причине: %s", maskedCardNumber, e.getMessage()));
            throw new UnsupportedOperationException(String.format("Не удалось добавить запрос на удаление карты по номеру: %s", maskedCardNumber));
        }
        return request;
    }

    @Override
    public List<DeleteResponse> getAllRequests() {
        try {
            return requestRepository.findAll()
                    .stream().map(request ->
                            {
                                String visiblePart = request.getLast4number();
                                String maskNumber = CardNumberUtils.getMaskOfVisibleCardNumber(visiblePart);
                                return new DeleteResponse(maskNumber, new UserCardInfoResponse(request.getOwner().getFirstName(), request.getOwner().getSecondName(), request.getOwner().getEmail()));
                            }
                    ).toList();
        } catch (Exception e) {
            logger.error(String.format("Произошла ошибка при получении всех запросов на удаление карт. По причине: %s", e.getMessage()));
            throw new UnsupportedOperationException(String.format("Произошла ошибка при получении всех запросов на удаление карт"));
        }
    }
}
