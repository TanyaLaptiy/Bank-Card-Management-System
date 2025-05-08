package com.bank_card_management_system.springsecurity.repository;

import com.bank_card_management_system.springsecurity.entities.Role;
import com.bank_card_management_system.springsecurity.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    void deleteByEmail(String email);

    User findByRole(Role role);

    @Modifying
    @Query("update User set firstName = :firstName, secondName = :secondName where email = :email")
    void updateByEmail(@Param("firstName") String firstName, @Param("secondName") String secondName, @Param("email") String email);
}
