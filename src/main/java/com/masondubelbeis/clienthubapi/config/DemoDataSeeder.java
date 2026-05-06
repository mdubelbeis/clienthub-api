package com.masondubelbeis.clienthubapi.config;

import com.masondubelbeis.clienthubapi.model.User;
import com.masondubelbeis.clienthubapi.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("seed")
public class DemoDataSeeder implements CommandLineRunner {

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
            return;
        }

        User demoUser = new User();
        demoUser.setFirstName("Demo");
        demoUser.setLastName("User");
        demoUser.setEmail(DEMO_EMAIL);
        demoUser.setPassword(passwordEncoder.encode(DEMO_PASSWORD));

        userRepository.save(demoUser);
    }
}