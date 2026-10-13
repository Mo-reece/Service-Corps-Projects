package com.example.rbac;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class RbacApplication {

    public static void main(String[] args) {
        SpringApplication.run(RbacApplication.class, args);
    }

    // Seed one demo account per role on startup: admin/admin123, user/user123, guest/guest123
    @Bean
    CommandLineRunner seedUsers(UserRepository users, PasswordEncoder passwordEncoder) {
        return args -> {
            for (User.Role role : User.Role.values()) {
                String name = role.name().toLowerCase();
                if (!users.existsByUsername(name)) {
                    users.save(new User(name, passwordEncoder.encode(name + "123"), role));
                }
            }
        };
    }
}
