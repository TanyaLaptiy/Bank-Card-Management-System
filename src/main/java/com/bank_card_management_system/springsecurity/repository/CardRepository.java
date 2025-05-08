package com.bank_card_management_system.springsecurity.repository;

import com.bank_card_management_system.springsecurity.entities.Card;

import com.bank_card_management_system.springsecurity.entities.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByNumber(String hash);

    Page<Card> findAllByOwnerId(Long userId, Pageable pageable);

    @Override
    List<Card> findAll();

    @Override
    void deleteById(Long id);

    @Modifying
    @Query("update Card set status = :status where id = :cardId")
    void setStatusById(@Param("status") Status status, @Param("cardId") Integer cardId);

    @Modifying
    @Query("update Card set balance = COALESCE(balance, 0) + :sum where id = :cardId")
    void changeBalanceById(@Param("sum") Long sum, @Param("cardId") Integer cardId);

    @Modifying
    @Query("update Card set status = :status where validityPeriod < DATE(NOW())")
    void scheduledExpiring(@Param("status") Status status);
}
