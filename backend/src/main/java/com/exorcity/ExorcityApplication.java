package com.exorcity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Users authenticate with JWTs issued by AuthService, so Boot's in-memory default user is not needed.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class ExorcityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExorcityApplication.class, args);
    }
}
