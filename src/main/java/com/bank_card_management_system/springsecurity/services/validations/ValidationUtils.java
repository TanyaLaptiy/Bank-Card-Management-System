package com.bank_card_management_system.springsecurity.services.validations;

public class ValidationUtils {

    public static void isValidEmail(String email) {
        if (!email.matches("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$")) {
            throw new UnsupportedOperationException(String.format("Некорректный email: %s", email));
        }
    }

    public static void isValidCardNumber(String cardNumber) {
        if (!cardNumber.matches("\\d{16}")) {
            throw new UnsupportedOperationException(String.format("Некорректный номер карты: %s. Номер должен состоять из 16 цифр", cardNumber));
        }
    }
}
