package com.bank_card_management_system.springsecurity.services.impl;

import com.bank_card_management_system.springsecurity.dto.*;
import com.bank_card_management_system.springsecurity.entities.Card;
import com.bank_card_management_system.springsecurity.entities.Status;
import com.bank_card_management_system.springsecurity.entities.User;
import com.bank_card_management_system.springsecurity.repository.CardRepository;
import com.bank_card_management_system.springsecurity.repository.RequestRepository;
import com.bank_card_management_system.springsecurity.repository.UserRepository;
import com.bank_card_management_system.springsecurity.services.CardService;
import com.bank_card_management_system.springsecurity.services.utils.CardNumberUtils;
import com.bank_card_management_system.springsecurity.services.validations.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private static final Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void automaticBalanceIncrease() {
        cardRepository.scheduledExpiring(Status.EXPIRED);
    }

    public CreateCardResponse saveCardData(CreateCardRequest card) {
        ValidationUtils.isValidCardNumber(card.getNumber());
        ValidationUtils.isValidEmail(card.getOwnerEmail());

        Card cardEntity;
        String visiblePart = card.getNumber().substring(card.getNumber().length() - 4);
        String maskNumber = CardNumberUtils.getMaskOfVisibleCardNumber(visiblePart);

        try {
            User user = userRepository.findByEmail(card.getOwnerEmail()).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

            String hashCardNumber = CardNumberUtils.hashCardNumber(card.getNumber());
            cardRepository.findByNumber(hashCardNumber).ifPresent(existingCard -> {
                throw new UnsupportedOperationException(
                        String.format("Номер карты %s уже был добавлен. Данные карты: Статус - %s Срок действия - %s Баланс - %s",
                                maskNumber, existingCard.getStatus(), existingCard.getValidityPeriod(),
                                existingCard.getBalance()));
            });

            cardEntity = new Card(hashCardNumber, visiblePart, user, card.getValidityPeriod(), Status.ACTIVE);

            cardRepository.save(cardEntity);
            return new CreateCardResponse(card.getValidityPeriod(), Status.ACTIVE, new UserCardInfoResponse(user.getFirstName(), user.getSecondName(), user.getEmail()), maskNumber);

        } catch (Exception e) {
            logger.error(String.format("Не удалось добавить карту: %s. По причине: %s", maskNumber, e.getMessage()));
            throw new UnsupportedOperationException(String.format("Не удалось добавить карту: %s", maskNumber));
        }
    }

    @Transactional
    @Override
    public BalanceCardResponse changeCardBalance(ChangeCardBalanceRequest card, boolean isAdding) {
        ValidationUtils.isValidCardNumber(card.getNumber());

        String maskedCardNumber = CardNumberUtils.getMaskOfCardNumber(card.getNumber());

        String hashCardNumber = CardNumberUtils.hashCardNumber(card.getNumber());
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Card existingCard = cardRepository.findByNumber(hashCardNumber).orElseThrow(() -> new UnsupportedOperationException(String.format("При изменении баланса на счету, не удалось найти карту по номеру: %s", maskedCardNumber)));

        if (!Objects.equals(existingCard.getOwner().getEmail(), email)) {
            throw new UnsupportedOperationException(String.format("Вы не имеете прав совершать транзакции со счета %s", maskedCardNumber));
        }

        Long sum = isAdding ? card.getCash() : -card.getCash();
        Long existingBalance = existingCard.getBalance() == null ? 0 : existingCard.getBalance();
        Long resultBalance = existingBalance + sum;

        if (resultBalance < 0) {
            throw new UnsupportedOperationException(String.format("На счету: %s недостаточно средств", maskedCardNumber));
        }

        try {
            cardRepository.changeBalanceById(sum, existingCard.getId());
        } catch (Exception e) {
            logger.error(String.format("Произошла ошибка при совершании транзакции на счету: %s. По причине: %s", maskedCardNumber, e.getMessage()));
            throw new UnsupportedOperationException(String.format("Произошла ошибка при совершании транзакции на счету: %s", maskedCardNumber));
        }

        return new BalanceCardResponse(existingCard.getValidityPeriod(), resultBalance, new UserCardInfoResponse(existingCard.getOwner().getFirstName(), existingCard.getOwner().getSecondName(), existingCard.getOwner().getEmail()), maskedCardNumber);
    }

    @Transactional
    @Override
    public List<BalanceCardResponse> makeMoneyTransfer(MoneyTransferRequest moneyTransferRequest) {
        List<BalanceCardResponse> cardResponseList = new ArrayList<>();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        String cardNumberFrom = moneyTransferRequest.getNumberFrom();
        String cardNumberTo = moneyTransferRequest.getNumberTo();

        ValidationUtils.isValidCardNumber(cardNumberFrom);
        ValidationUtils.isValidCardNumber(cardNumberTo);

        String maskNumberFrom = CardNumberUtils.getMaskOfCardNumber(cardNumberFrom);
        String maskNumberTo = CardNumberUtils.getMaskOfCardNumber(cardNumberTo);

        try {
            String hashCardNumberFrom = CardNumberUtils.hashCardNumber(cardNumberFrom);
            String hashCardNumberTo = CardNumberUtils.hashCardNumber(cardNumberTo);

            Card existingCardFrom = cardRepository.findByNumber(hashCardNumberFrom).orElseThrow(() -> new UnsupportedOperationException(String.format("При изменении баланса на счету, не удалось найти карту по номеру: %s", moneyTransferRequest.getNumberFrom())));
            Card existingCardTo = cardRepository.findByNumber(hashCardNumberTo).orElseThrow(() -> new UnsupportedOperationException(String.format("При изменении баланса на счету, не удалось найти карту по номеру: %s", moneyTransferRequest.getNumberTo())));
            Long existingBalance = existingCardFrom.getBalance() == null ? 0 : existingCardFrom.getBalance();
            Long existingBalanceTo = existingCardTo.getBalance() == null ? 0 : existingCardTo.getBalance();

            if (!Objects.equals(existingCardFrom.getOwner().getEmail(), email)) {
                throw new UnsupportedOperationException(String.format("Счет: %s принадлежит не вам, вы можете совершать транзакции только между вашими картами", maskNumberFrom));
            }

            if (!Objects.equals(existingCardTo.getOwner().getEmail(), email)) {
                throw new UnsupportedOperationException(String.format("Счет: %s принадлежит не вам, вы можете переводить деньги только на карту, принадлежащую вам", cardNumberTo));
            }

            if (existingBalance - moneyTransferRequest.getCash() < 0) {
                throw new UnsupportedOperationException(String.format("На счету: %s недостаточно средств", cardNumberFrom));
            }

            cardRepository.changeBalanceById(-moneyTransferRequest.getCash(), existingCardFrom.getId());
            cardRepository.changeBalanceById(moneyTransferRequest.getCash(), existingCardTo.getId());

            cardResponseList.add(new BalanceCardResponse(existingCardFrom.getValidityPeriod(), existingBalance - moneyTransferRequest.getCash(), new UserCardInfoResponse(existingCardFrom.getOwner().getFirstName(), existingCardFrom.getOwner().getSecondName(), existingCardFrom.getOwner().getEmail()), maskNumberFrom));
            cardResponseList.add(new BalanceCardResponse(existingCardTo.getValidityPeriod(), existingBalanceTo + moneyTransferRequest.getCash(), new UserCardInfoResponse(existingCardTo.getOwner().getFirstName(), existingCardTo.getOwner().getSecondName(), existingCardTo.getOwner().getEmail()), maskNumberTo));
        } catch (Exception e) {
            logger.error(String.format("Произошла ошибка при переводе денежных средств со счета: %s на счет: %s. По причине: %s", maskNumberFrom, maskNumberTo, e.getMessage()));
            throw new UnsupportedOperationException(String.format("Произошла ошибка при переводе денежных средств со счета: %s на счет: %s", maskNumberFrom, maskNumberTo));
        }
        return cardResponseList;
    }

    @Transactional
    @Override
    public CreateCardResponse updateCardData(UpdateCardRequest card, Status status) {
        ValidationUtils.isValidCardNumber(card.getNumber());

        String hashCardNumber = CardNumberUtils.hashCardNumber(card.getNumber());

        String maskedCardNumber = CardNumberUtils.getMaskOfCardNumber(card.getNumber());
        Card existingCard = cardRepository.findByNumber(hashCardNumber).orElseThrow(() -> {
            throw new UnsupportedOperationException(String.format("При изменении статуса, не удалось найти карту по номеру: %s", maskedCardNumber));
        });
        if (Objects.equals(existingCard.getStatus(), Status.EXPIRED)) {
            throw new UnsupportedOperationException(String.format("Не удалось изменить статус карты: %s, так как у нее истёк срок действия ", maskedCardNumber));
        }
        try {
            cardRepository.setStatusById(status, existingCard.getId());
        } catch (Exception e) {
            logger.error(String.format("Не удалось изменить статус карты: %s. По причине: %s", maskedCardNumber, e.getMessage()));
            throw new UnsupportedOperationException(String.format("Не удалось изменить статус карты: %s", maskedCardNumber));
        }

        String maskNumber = CardNumberUtils.getMaskOfCardNumber(card.getNumber());
        return new CreateCardResponse(existingCard.getValidityPeriod(), status, new UserCardInfoResponse(existingCard.getOwner().getFirstName(), existingCard.getOwner().getSecondName(), existingCard.getOwner().getEmail()), maskNumber);
    }


    @Transactional
    @Override
    public String deleteCard(UpdateCardRequest card) {
        ValidationUtils.isValidCardNumber(card.getNumber());

        String hashCardNumber = CardNumberUtils.hashCardNumber(card.getNumber());
        String maskedCardNumber = CardNumberUtils.getMaskOfCardNumber(card.getNumber());
        Card existingCard = cardRepository.findByNumber(hashCardNumber).orElseThrow(() -> {
            throw new UnsupportedOperationException(String.format("Не удалось найти карту по номеру: %s", maskedCardNumber));
        });
        try {
            cardRepository.deleteById(Long.valueOf(existingCard.getId()));
            requestRepository.deleteByNumber(hashCardNumber);
        } catch (Exception e) {
            logger.error(String.format("Не удалось удалить карту по номеру: %s. По причине: %s", maskedCardNumber, e.getMessage()));
            throw new UnsupportedOperationException(String.format("Не удалось удалить карту по номеру: %s", maskedCardNumber));
        }
        return String.format("Карта с номером: %s успешно удалена. Данные о владельце: %s %s %s", maskedCardNumber, existingCard.getOwner().getFirstName(), existingCard.getOwner().getSecondName(), existingCard.getOwner().getEmail());
    }

    @Override
    public List<CreateCardResponse> getAllCard() {
        try {
            return cardRepository.findAll()
                    .stream().map(card ->
                            {
                                String visiblePart = card.getLast4number();
                                String maskNumber = CardNumberUtils.getMaskOfVisibleCardNumber(visiblePart);
                                return new CreateCardResponse(card.getValidityPeriod(), card.getStatus(),
                                        new UserCardInfoResponse(card.getOwner().getFirstName(), card.getOwner().getSecondName(), card.getOwner().getEmail()), maskNumber);
                            }
                    ).toList();
        } catch (Exception e) {
            logger.error(String.format("Произошла ошибка при получении всех карт. По причине: %s", e.getMessage()));
            throw new UnsupportedOperationException(String.format("Произошла ошибка при получении всех карт"));
        }
    }

    @Override
    public List<CreateCardResponse> getFilteredCard(Integer offset, Integer limit) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            User user = userRepository.findByEmail(email).orElseThrow();
            return cardRepository.findAllByOwnerId(Long.valueOf(user.getId()), PageRequest.of(offset, limit))
                    .stream().map(card ->
                            {
                                String visiblePart = card.getLast4number();
                                String maskNumber = CardNumberUtils.getMaskOfVisibleCardNumber(visiblePart);
                                return new CreateCardResponse(card.getValidityPeriod(), card.getStatus(),
                                        new UserCardInfoResponse(card.getOwner().getFirstName(), card.getOwner().getSecondName(), card.getOwner().getEmail()), maskNumber);
                            }
                    ).toList();
        } catch (Exception e) {
            logger.error(String.format("Произошла ошибка при постраничном получении всех карт. По причине: %s", e.getMessage()));
            throw new UnsupportedOperationException(String.format("Произошла ошибка при постраничном получении карт"));
        }
    }

}
