package com.bank_card_management_system.springsecurity;

import com.bank_card_management_system.springsecurity.entities.Role;
import com.bank_card_management_system.springsecurity.entities.User;
import com.bank_card_management_system.springsecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SpringsecurityApplication implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;


    public static void main(String[] args) {
        SpringApplication.run(SpringsecurityApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User adminAccount = userRepository.findByRole(Role.ADMIN);
        if (null == adminAccount) {
            User user = new User("Admin",
                    "Admin",
                    "admin@gmail.com",
                    new BCryptPasswordEncoder().encode("admin"),
                    Role.ADMIN);
            userRepository.save(user);

        }
    }
}
