package com.bank_card_management_system.springsecurity.services;

import com.bank_card_management_system.springsecurity.dto.*;

import java.util.List;

public interface RequestService {
    CreateDeleteRequest createRequest(CreateDeleteRequest request);

    List<DeleteResponse> getAllRequests();
}
