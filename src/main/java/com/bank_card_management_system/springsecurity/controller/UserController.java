package com.bank_card_management_system.springsecurity.controller;

import com.bank_card_management_system.springsecurity.dto.*;
import com.bank_card_management_system.springsecurity.services.CardService;
import com.bank_card_management_system.springsecurity.services.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final CardService cardService;
    private final RequestService requestService;

    @PostMapping("/replenish")
    public ResponseEntity<?> replenishAccount(@RequestBody ChangeCardBalanceRequest changeCardBalanceRequest) {
        try {
            return new ResponseEntity<BalanceCardResponse>(cardService.changeCardBalance(changeCardBalanceRequest, true), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawMoney(@RequestBody ChangeCardBalanceRequest changeCardBalanceRequest) {
        try {
            return new ResponseEntity<BalanceCardResponse>(cardService.changeCardBalance(changeCardBalanceRequest, false), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> makeMoneyTransfer(@RequestBody MoneyTransferRequest moneyTransferRequest) {
        try {
            return new ResponseEntity<List<BalanceCardResponse>>(cardService.makeMoneyTransfer(moneyTransferRequest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteCard(@RequestBody CreateDeleteRequest request) {
        try {
            return new ResponseEntity<CreateDeleteRequest>(requestService.createRequest(request), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getFilteredCards(@RequestParam("limit") Integer limit,
                                           @RequestParam("offset") Integer offset) {
        try {
            return new ResponseEntity<List<CreateCardResponse>>(cardService.getFilteredCard(offset, limit), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
