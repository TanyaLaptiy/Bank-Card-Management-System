package com.bank_card_management_system.springsecurity.services.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CardNumberUtils {
    private static final Logger logger = LoggerFactory.getLogger(CardNumberUtils.class);

    public static String getMaskOfVisibleCardNumber(String visibleNumberPart) {
        return "**** **** **** " + visibleNumberPart;
    }

    public static String getMaskOfCardNumber(String bankNumber) {
        String visibleNumberPart = bankNumber.substring(bankNumber.length() - 4);
        return "**** **** **** " + visibleNumberPart;
    }

    public static String hashCardNumber(String cardNumber) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(cardNumber.getBytes());

            String encodedHash = Base64.getEncoder().encodeToString(hash);
            return encodedHash;
        } catch (NoSuchAlgorithmException e) {
            logger.error(String.format("Ошибка при создании объекта MessageDigest с использованием алгоритма SHA-256. По причине: %s", e.getMessage()));
        }
        return null;
    }
}
