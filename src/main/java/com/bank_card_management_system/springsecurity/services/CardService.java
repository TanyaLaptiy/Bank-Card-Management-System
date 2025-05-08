package com.bank_card_management_system.springsecurity.services;

import com.bank_card_management_system.springsecurity.dto.*;
import com.bank_card_management_system.springsecurity.entities.Status;

import java.util.List;

public interface CardService {
    CreateCardResponse saveCardData(CreateCardRequest card);

    BalanceCardResponse changeCardBalance(ChangeCardBalanceRequest card, boolean isAdding);

    List<BalanceCardResponse> makeMoneyTransfer(MoneyTransferRequest moneyTransferRequest);

    CreateCardResponse updateCardData(UpdateCardRequest card, Status status);

    String deleteCard(UpdateCardRequest card);

    List<CreateCardResponse> getAllCard();

    List<CreateCardResponse> getFilteredCard(Integer offset, Integer limit);
}
