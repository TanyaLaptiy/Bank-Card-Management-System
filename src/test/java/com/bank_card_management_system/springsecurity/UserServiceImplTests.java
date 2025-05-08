package com.bank_card_management_system.springsecurity;

import com.bank_card_management_system.springsecurity.dto.CreateDeleteRequest;
import com.bank_card_management_system.springsecurity.dto.DeleteResponse;
import com.bank_card_management_system.springsecurity.dto.UserCardInfoRequest;
import com.bank_card_management_system.springsecurity.dto.UserCardInfoResponse;
import com.bank_card_management_system.springsecurity.entities.Request;
import com.bank_card_management_system.springsecurity.entities.Role;
import com.bank_card_management_system.springsecurity.entities.User;
import com.bank_card_management_system.springsecurity.repository.RequestRepository;
import com.bank_card_management_system.springsecurity.repository.UserRepository;
import com.bank_card_management_system.springsecurity.services.impl.RequestServiceImpl;
import com.bank_card_management_system.springsecurity.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = UserServiceImplTests.class)
class UserServiceImplTests {
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
    void testUpdateUser() {
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockUserRepository.findByEmail("lee123@test.com")).thenReturn(Optional.ofNullable(user));

        UserServiceImpl userService = new UserServiceImpl(mockUserRepository);

        UserCardInfoRequest userCardInfoRequest = new UserCardInfoRequest("Petr", "Lee", "lee123@test.com");

        UserCardInfoResponse res = userService.updateByEmail(userCardInfoRequest);
        assertEquals("lee123@test.com", res.getEmail());
        assertEquals("Lee", res.getLastName());
        assertEquals("Petr", res.getFirstName());
    }

    @Test()
    void testDeleteUser() {
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockUserRepository.findByEmail("test@test.com")).thenReturn(Optional.ofNullable(user));

        UserServiceImpl userService = new UserServiceImpl(mockUserRepository);

        UserCardInfoResponse res = userService.deleteByEmail("test@test.com");
        assertEquals("test@test.com", res.getEmail());
        assertEquals("test2", res.getLastName());
        assertEquals("test", res.getFirstName());
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
