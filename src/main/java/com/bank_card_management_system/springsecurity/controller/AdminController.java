package com.bank_card_management_system.springsecurity.controller;

import com.bank_card_management_system.springsecurity.dto.*;
import com.bank_card_management_system.springsecurity.entities.Status;
import com.bank_card_management_system.springsecurity.services.CardService;
import com.bank_card_management_system.springsecurity.services.RequestService;
import com.bank_card_management_system.springsecurity.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/")
@RequiredArgsConstructor
public class AdminController {
    private final CardService cardService;
    private final UserService userService;
    private final RequestService requestService;

    @PostMapping("card/add")
    public ResponseEntity<?> addCard(@RequestBody CreateCardRequest createCardRequest) {
        try {
            return new ResponseEntity<CreateCardResponse>(cardService.saveCardData(createCardRequest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PatchMapping("card/block")
    public ResponseEntity<?> blockCard(@RequestBody UpdateCardRequest updateCardRequest) {
        try {
            return new ResponseEntity<CreateCardResponse>(cardService.updateCardData(updateCardRequest, Status.BLOCKED), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("card/activate")
    public ResponseEntity<?> activateCard(@RequestBody UpdateCardRequest updateCardRequest) {
        try {
            return new ResponseEntity<CreateCardResponse>(cardService.updateCardData(updateCardRequest, Status.ACTIVE), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("card/delete")
    public ResponseEntity<?> deleteCard(@RequestBody UpdateCardRequest deleteCardRequest) {
        try {
            return new ResponseEntity<String>(cardService.deleteCard(deleteCardRequest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("card")
    public ResponseEntity<?> getAllCards() {
        try {
            return new ResponseEntity<List<CreateCardResponse>>(cardService.getAllCard(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("user/edit")
    public ResponseEntity<?> updateUser(@RequestBody UserCardInfoRequest userCardInfoRequest) {
        try {
            return new ResponseEntity<UserCardInfoResponse>(userService.updateByEmail(userCardInfoRequest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("user/delete")
    public ResponseEntity<?> deleteUser(@RequestBody DeleteUserRequest deleteUserRequest) {
        try {
            return new ResponseEntity<UserCardInfoResponse>(userService.deleteByEmail(deleteUserRequest.getEmail()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("request")
    public ResponseEntity<?> getAllRequest() {
        try {
            return new ResponseEntity<List<DeleteResponse>>(requestService.getAllRequests(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
