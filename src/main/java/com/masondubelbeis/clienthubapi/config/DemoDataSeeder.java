package com.masondubelbeis.clienthubapi.config;

import com.masondubelbeis.clienthubapi.model.User;
import com.masondubelbeis.clienthubapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("seed")
public class DemoDataSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);
    private static final String DEMO_EMAIL = "demo@clienthub.com";
    private static final String DEMO_PASSWORD = "DemoPassword123!";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeeder(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail(DEMO_EMAIL).isPresent()) {
            log.info("Demo user already exists. Skipping demo user creation.");
            return;
        }

        User demoUser = new User();
        demoUser.setFirstName("Demo");
        demoUser.setLastName("User");
        demoUser.setEmail(DEMO_EMAIL);
        demoUser.setPassword(passwordEncoder.encode(DEMO_PASSWORD));

        userRepository.save(demoUser);
        log.info("Demo user created successfully: {}", DEMO_EMAIL);
    }
}