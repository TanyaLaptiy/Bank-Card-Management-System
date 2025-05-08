package com.bank_card_management_system.springsecurity;

import com.bank_card_management_system.springsecurity.dto.*;
import com.bank_card_management_system.springsecurity.entities.Card;
import com.bank_card_management_system.springsecurity.entities.Role;
import com.bank_card_management_system.springsecurity.entities.Status;
import com.bank_card_management_system.springsecurity.entities.User;
import com.bank_card_management_system.springsecurity.repository.CardRepository;
import com.bank_card_management_system.springsecurity.repository.RequestRepository;
import com.bank_card_management_system.springsecurity.repository.UserRepository;
import com.bank_card_management_system.springsecurity.services.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = CardServiceImplTests.class)
class CardServiceImplTests {
    static List<Card> allCardsInBD = new ArrayList<>();
    static User user = new User();
    static Card card = new Card();

    @BeforeAll
    public static void setupData() {

        user = new User("test", "test2", "test@test.com", "123456", Role.USER);
        user.setId(1);

        card = new Card("9000000000000000", "1010", user, new Date(2030, 02, 11), Status.ACTIVE);
        card.setId(1);

        Card card2 = new Card("300000000000010990", "0990", user, new Date(2033, 03, 14), Status.ACTIVE);
        card2.setBalance(190000L);
        allCardsInBD.add(card2);
    }

    @Test
    void testSaveCardData() {
        CardRepository mockCardRepository = Mockito.mock(CardRepository.class);
        Mockito.when(mockCardRepository.findAll()).thenReturn(allCardsInBD);
        Mockito.when(mockCardRepository.findByNumber("100000")).thenReturn(null);
        Mockito.when(mockCardRepository.save(card)).thenReturn(null);


        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockUserRepository.findByEmail("test@test.com")).thenReturn(Optional.ofNullable(user));

        RequestRepository mockReqRepository = Mockito.mock(RequestRepository.class);


        CardServiceImpl cardService = new CardServiceImpl(mockCardRepository, mockUserRepository, mockReqRepository);

        CreateCardRequest createCardRequest = new CreateCardRequest();
        createCardRequest.setNumber("1234567891112345");
        createCardRequest.setValidityPeriod(new Date(2030, 02, 11));
        createCardRequest.setOwnerEmail("test@test.com");

        CreateCardResponse res = cardService.saveCardData(createCardRequest);
        assertEquals(Status.ACTIVE, res.getStatus());
        assertEquals("**** **** **** 2345", res.getNumber());
    }

    @Test
    void testSaveCardDataWithWrongNumber() {
        CardRepository mockCardRepository = Mockito.mock(CardRepository.class);
        Mockito.when(mockCardRepository.findAll()).thenReturn(allCardsInBD);
        Mockito.when(mockCardRepository.findByNumber("100000")).thenReturn(null);
        Mockito.when(mockCardRepository.save(card)).thenReturn(null);


        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockUserRepository.findByEmail("test@test.com")).thenReturn(Optional.ofNullable(user));

        RequestRepository mockReqRepository = Mockito.mock(RequestRepository.class);


        CardServiceImpl cardService = new CardServiceImpl(mockCardRepository, mockUserRepository, mockReqRepository);

        CreateCardRequest createCardRequest = new CreateCardRequest();
        createCardRequest.setNumber("100000");
        createCardRequest.setValidityPeriod(new Date(2030, 02, 11));
        createCardRequest.setOwnerEmail("test@test.com");

        try {
            CreateCardResponse res = cardService.saveCardData(createCardRequest);
        } catch (Exception e) {
            assertEquals("Некорректный номер карты: 100000. Номер должен состоять из 16 цифр", e.getMessage());

        }
    }

    @Test()
    void testSaveCardDataWithError() {
        try {
            CardRepository mockCardRepository = Mockito.mock(CardRepository.class);
            Mockito.when(mockCardRepository.findAll()).thenReturn(allCardsInBD);
            Mockito.when(mockCardRepository.findByNumber("100000")).thenReturn(null);
            Mockito.when(mockCardRepository.save(card)).thenReturn(null);


            UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
            Mockito.when(mockUserRepository.findByEmail("test@test.com")).thenReturn(null);

        } catch (Exception e) {
            assertEquals("Не удалось добавить карту: **** **** **** 0000", e.getMessage());
        }
    }

    @Test
    void testChangeCardBalanceWithCardError() {
        try {
            CardRepository mockCardRepository = Mockito.mock(CardRepository.class);
            Mockito.when(mockCardRepository.findByNumber("100000")).thenReturn(null);
            ChangeCardBalanceRequest changeCardBalanceRequest = new ChangeCardBalanceRequest();
            changeCardBalanceRequest.setNumber("9000000000000000");
            changeCardBalanceRequest.setCash(100000L);
            Authentication authentication = Mockito.mock(Authentication.class);

            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
            RequestRepository mockReqRepository = Mockito.mock(RequestRepository.class);


            CardServiceImpl cardService = new CardServiceImpl(mockCardRepository, mockUserRepository, mockReqRepository);

            CreateCardRequest createCardRequest = new CreateCardRequest();
            createCardRequest.setNumber("9000000000000000");
            createCardRequest.setValidityPeriod(new Date(2030, 02, 11));
            createCardRequest.setOwnerEmail("test@test.com");

            cardService.changeCardBalance(changeCardBalanceRequest, true);
        } catch (Exception e) {
            assertEquals("При изменении баланса на счету, не удалось найти карту по номеру: **** **** **** 0000", e.getMessage());
        }
    }

    @Test
    void testUpdateCardData() {
        CardRepository mockCardRepository = Mockito.mock(CardRepository.class);
        Mockito.when(mockCardRepository.findAll()).thenReturn(allCardsInBD);
        Mockito.when(mockCardRepository.findByNumber("Fja4H8JZmN6Owla6Sj+FFpFv+HRq7HOy1NJ1Ri+hYnE=")).thenReturn(Optional.of(card));

        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockUserRepository.findByEmail("test@test.com")).thenReturn(Optional.ofNullable(user));

        RequestRepository mockReqRepository = Mockito.mock(RequestRepository.class);
        CardServiceImpl cardService = new CardServiceImpl(mockCardRepository, mockUserRepository, mockReqRepository);

        UpdateCardRequest updateCardRequest = new UpdateCardRequest();
        updateCardRequest.setNumber("9000000000000000");

        CreateCardResponse res = cardService.updateCardData(updateCardRequest, Status.BLOCKED);
        assertEquals(Status.BLOCKED, res.getStatus());
        assertEquals("test@test.com", res.getOwner().getEmail());
        assertEquals("**** **** **** 0000", res.getNumber());
    }

    @Test
    void testUpdateCardDataWithExpiredError() {
        CardRepository mockCardRepository = Mockito.mock(CardRepository.class);
        Mockito.when(mockCardRepository.findAll()).thenReturn(allCardsInBD);
        card.setStatus(Status.EXPIRED);
        Mockito.when(mockCardRepository.findByNumber("Fja4H8JZmN6Owla6Sj+FFpFv+HRq7HOy1NJ1Ri+hYnE=")).thenReturn(Optional.of(card));

        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockUserRepository.findByEmail("test@test.com")).thenReturn(Optional.ofNullable(user));

        RequestRepository mockReqRepository = Mockito.mock(RequestRepository.class);
        CardServiceImpl cardService = new CardServiceImpl(mockCardRepository, mockUserRepository, mockReqRepository);

        UpdateCardRequest updateCardRequest = new UpdateCardRequest();
        updateCardRequest.setNumber("9000000000000000");
        try {
            cardService.updateCardData(updateCardRequest, Status.BLOCKED);
        } catch (Exception e) {
            assertEquals("Не удалось изменить статус карты: **** **** **** 0000, так как у нее истёк срок действия ", e.getMessage());
        }
    }

    @Test
    void testDeleteCardData() {
        CardRepository mockCardRepository = Mockito.mock(CardRepository.class);
        Mockito.when(mockCardRepository.findAll()).thenReturn(allCardsInBD);
        Mockito.when(mockCardRepository.findByNumber("Fja4H8JZmN6Owla6Sj+FFpFv+HRq7HOy1NJ1Ri+hYnE=")).thenReturn(Optional.of(card));

        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        Mockito.when(mockUserRepository.findByEmail("test@test.com")).thenReturn(Optional.ofNullable(user));

        RequestRepository mockReqRepository = Mockito.mock(RequestRepository.class);
        CardServiceImpl cardService = new CardServiceImpl(mockCardRepository, mockUserRepository, mockReqRepository);

        UpdateCardRequest updateCardRequest = new UpdateCardRequest();
        updateCardRequest.setNumber("9000000000000000");

        String res = cardService.deleteCard(updateCardRequest);
        assertEquals("Карта с номером: **** **** **** 0000 успешно удалена. Данные о владельце: test test2 test@test.com", res);
    }


    @Test
    void testChangeCardBalanceWithError() {
        try {
            CardRepository mockCardRepository = Mockito.mock(CardRepository.class);
            Mockito.when(mockCardRepository.findByNumber("Fja4H8JZmN6Owla6Sj+FFpFv+HRq7HOy1NJ1Ri+hYnE=")).thenReturn(Optional.of(card));
            ChangeCardBalanceRequest changeCardBalanceRequest = new ChangeCardBalanceRequest();
            changeCardBalanceRequest.setNumber("9000000000000000");
            changeCardBalanceRequest.setCash(100000L);
            Authentication authentication = Mockito.mock(Authentication.class);

            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
            RequestRepository mockReqRepository = Mockito.mock(RequestRepository.class);


            CardServiceImpl cardService = new CardServiceImpl(mockCardRepository, mockUserRepository, mockReqRepository);

            CreateCardRequest createCardRequest = new CreateCardRequest();
            createCardRequest.setNumber("9000000000000000");
            createCardRequest.setValidityPeriod(new Date(2030, 02, 11));
            createCardRequest.setOwnerEmail("test@test.com");

            cardService.changeCardBalance(changeCardBalanceRequest, true);
        } catch (Exception e) {
            assertEquals("Вы не имеете прав совершать транзакции со счета **** **** **** 0000", e.getMessage());
        }
    }

    @Test
    void testGetAll() {
        CardRepository mockCardRepository = Mockito.mock(CardRepository.class);
        Mockito.when(mockCardRepository.findAll()).thenReturn(allCardsInBD);

        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        RequestRepository mockReqRepository = Mockito.mock(RequestRepository.class);


        CardServiceImpl cardService = new CardServiceImpl(mockCardRepository, mockUserRepository, mockReqRepository);
        Integer size = cardService.getAllCard().size();
        assertEquals(1, size);
    }

}
