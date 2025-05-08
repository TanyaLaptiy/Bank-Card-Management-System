package com.bank_card_management_system.springsecurity;

import com.bank_card_management_system.springsecurity.dto.*;
import com.bank_card_management_system.springsecurity.entities.*;
import com.bank_card_management_system.springsecurity.repository.RequestRepository;
import com.bank_card_management_system.springsecurity.repository.UserRepository;
import com.bank_card_management_system.springsecurity.services.impl.RequestServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = RequestServiceImplTests.class)
class RequestServiceImplTests {
    static List<Request> allRequestsInBD = new ArrayList<>();
    static User user = new User();

    @BeforeAll
    public static void setupData() {

        user = new User("test", "test2", "test@test.com", "123456", Role.USER);
        user.setId(1);

        Request request = new Request("111111111111111", "1111", user);
        request.setId(1);
        allRequestsInBD.add(request);


        Request request2 = new Request("22222222", "2222", user);
        request2.setId(2);
        allRequestsInBD.add(request2);
    }

    @Test()
    void testCreateRequestWithError() {
        try {
            RequestRepository mockReqRepository = Mockito.mock(RequestRepository.class);


            UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
            Mockito.when(mockUserRepository.findByEmail("test@test.com")).thenReturn(null);

            RequestServiceImpl requestService = new RequestServiceImpl(mockUserRepository, mockReqRepository);

            CreateDeleteRequest createDeleteRequest = new CreateDeleteRequest();
            createDeleteRequest.setNumber("100000");
            requestService.createRequest(createDeleteRequest);

        } catch (Exception e) {
            assertEquals("Не удалось добавить запрос на удаление карты по номеру: **** **** **** 0000", e.getMessage());
        }
    }

    @Test()
    void testGetRequestsData() {
        RequestRepository mockReqRepository = Mockito.mock(RequestRepository.class);
        Mockito.when(mockReqRepository.findAll()).thenReturn(allRequestsInBD);


        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockUserRepository.findByEmail("test@test.com")).thenReturn(null);

        RequestServiceImpl requestService = new RequestServiceImpl(mockUserRepository, mockReqRepository);

        CreateDeleteRequest createDeleteRequest = new CreateDeleteRequest();
        createDeleteRequest.setNumber("100000");
        List<DeleteResponse> res = requestService.getAllRequests();
        assertEquals(2, res.size());
    }
}
