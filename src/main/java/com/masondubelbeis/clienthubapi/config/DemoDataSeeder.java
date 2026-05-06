package com.masondubelbeis.clienthubapi.config;

import com.masondubelbeis.clienthubapi.model.*;
import com.masondubelbeis.clienthubapi.repository.ActivityRepository;
import com.masondubelbeis.clienthubapi.repository.ClientRepository;
import com.masondubelbeis.clienthubapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Profile("seed")
public class DemoDataSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);
    private static final String DEMO_EMAIL = "demo@clienthub.com";
    private static final String DEMO_PASSWORD = "DemoPassword123!";

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ActivityRepository activityRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeeder(UserRepository userRepository,
                          ClientRepository clientRepository,
                          ActivityRepository activityRepository,
                          PasswordEncoder passwordEncoder
                          ) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.activityRepository = activityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        log.info("Demo data seeder started.");

        User demoUser = getOrCreateDemoUser();

        log.info("Demo user ready: {}", demoUser.getEmail());

        if (clientRepository.countByUser(demoUser) > 0) {
            log.info("Demo clients already exist. Skipping client and activity seeding.");
            return;
        }

        seedClientsAndActivities(demoUser);

        log.info("Demo data seeder finished.");
    }

    private User getOrCreateDemoUser() {
        return userRepository.findByEmail(DEMO_EMAIL)
                .orElseGet(this::createDemoUser);
    }

    private User createDemoUser() {
        User demoUser = new User();
        demoUser.setFirstName("Demo");
        demoUser.setLastName("User");
        demoUser.setEmail(DEMO_EMAIL);
        demoUser.setPassword(passwordEncoder.encode(DEMO_PASSWORD));

        return userRepository.save(demoUser);
    }

    private void seedClientsAndActivities(User demoUser) {
        Client austinRoofing = createClient(
                demoUser,
                "Austin Roofing Co.",
                "contact@austinroofingco.com",
                "512-555-0142"
        );

        Client hillCountryDental = createClient(
                demoUser,
                "Hill Country Dental",
                "office@hillcountrydental.com",
                "512-555-0188"
        );

        Client loneStarFitness = createClient(
                demoUser,
                "Lone Star Fitness",
                "manager@lonestarfitness.com",
                "512-555-0194"
        );

        Client cedarParkAuto = createClient(
                demoUser,
                "Cedar Park Auto Repair",
                "service@cedarparkauto.com",
                "512-555-0129"
        );

        Client bluebonnetRealty = createClient(
                demoUser,
                "Bluebonnet Realty Group",
                "hello@bluebonnetrealty.com",
                "512-555-0167"
        );

        Client roundRockWeb = createClient(
                demoUser,
                "Round Rock Web Solutions",
                "sales@rrwebsolutions.com",
                "512-555-0113"
        );

        Client georgetownClinic = createClient(
                demoUser,
                "Georgetown Family Clinic",
                "admin@gtfamilyclinic.com",
                "512-555-0175"
        );

        Client leanderOutdoor = createClient(
                demoUser,
                "Leander Outdoor Supply",
                "support@leanderoutdoor.com",
                "512-555-0106"
        );

        createActivity(
                austinRoofing,
                ActivityType.CALL,
                ActivityStatus.COMPLETED,
                "Initial discovery call completed. Client is interested in a CRM workflow for tracking estimates and follow-ups."
        );

        createActivity(
                austinRoofing,
                ActivityType.EMAIL,
                ActivityStatus.OPEN,
                "Send follow-up summary and pricing information."
        );

        createActivity(
                hillCountryDental,
                ActivityType.EMAIL,
                ActivityStatus.COMPLETED,
                "Sent website maintenance proposal and support package overview."
        );

        createActivity(
                hillCountryDental,
                ActivityType.CALL,
                ActivityStatus.OPEN,
                "Follow up about patient intake form workflow."
        );

        createActivity(
                loneStarFitness,
                ActivityType.MEETING,
                ActivityStatus.COMPLETED,
                "Reviewed membership tracking pain points and reporting needs."
        );

        createActivity(
                loneStarFitness,
                ActivityType.NOTE,
                ActivityStatus.OPEN,
                "Client requested examples of dashboard metrics for monthly membership trends."
        );

        createActivity(
                cedarParkAuto,
                ActivityType.CALL,
                ActivityStatus.COMPLETED,
                "Discussed appointment scheduling issues and missed follow-up reminders."
        );

        createActivity(
                cedarParkAuto,
                ActivityType.EMAIL,
                ActivityStatus.OPEN,
                "Send proposal for customer reminder workflow."
        );

        createActivity(
                bluebonnetRealty,
                ActivityType.NOTE,
                ActivityStatus.COMPLETED,
                "Added notes from referral conversation. Client wants better lead organization."
        );

        createActivity(
                bluebonnetRealty,
                ActivityType.CALL,
                ActivityStatus.OPEN,
                "Follow up next week regarding CRM migration timeline."
        );

        createActivity(
                roundRockWeb,
                ActivityType.EMAIL,
                ActivityStatus.COMPLETED,
                "Sent API integration overview and implementation timeline."
        );

        createActivity(
                roundRockWeb,
                ActivityType.MEETING,
                ActivityStatus.OPEN,
                "Technical review meeting scheduled with development team."
        );

        createActivity(
                georgetownClinic,
                ActivityType.CALL,
                ActivityStatus.COMPLETED,
                "Discussed secure client communication workflow and reporting needs."
        );

        createActivity(
                georgetownClinic,
                ActivityType.EMAIL,
                ActivityStatus.OPEN,
                "Send documentation checklist and next steps."
        );

        createActivity(
                leanderOutdoor,
                ActivityType.NOTE,
                ActivityStatus.COMPLETED,
                "Client wants seasonal campaign tracking and customer purchase follow-up."
        );

        createActivity(
                leanderOutdoor,
                ActivityType.CALL,
                ActivityStatus.OPEN,
                "Follow up about inventory-related customer outreach."
        );

        log.info("Seeded demo clients and activities for {}", demoUser.getEmail());
    }

    private Client createClient(User user, String name, String email, String phone) {
        Client client = new Client();
        client.setName(name);
        client.setEmail(email);
        client.setPhone(phone);
        client.setUser(user);

        return clientRepository.save(client);
    }

    private Activity createActivity(Client client,
                                    ActivityType type,
                                    ActivityStatus status,
                                    String notes) {
        Activity activity = new Activity();
        activity.setClient(client);
        activity.setType(type);
        activity.setStatus(status);
        activity.setNotes(notes);

        if (status == ActivityStatus.COMPLETED) {
            activity.setCompletedAt(Instant.now());
        }

        return activityRepository.save(activity);
    }
}