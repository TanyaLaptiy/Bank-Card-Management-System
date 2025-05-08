package com.bank_card_management_system.springsecurity.repository;

import com.bank_card_management_system.springsecurity.entities.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    void deleteByNumber(String hashCardNumber);
}
