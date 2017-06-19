package org.teapot.backend.config;

import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.teapot.backend.model.meta.TeapotAction;
import org.teapot.backend.model.meta.TeapotProperty;
import org.teapot.backend.model.User;
import org.teapot.backend.model.UserAuthority;
import org.teapot.backend.model.meta.TeapotResource;
import org.teapot.backend.repository.TeapotActionRepository;
import org.teapot.backend.repository.TeapotPropertyRepository;
import org.teapot.backend.repository.TeapotResourceRepository;
import org.teapot.backend.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;


@Configuration
@Profile("development")
public class DevelopmentProfileConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeapotPropertyRepository propertyRepository;

    @Autowired
    private TeapotActionRepository actionRepository;

    @Autowired
    private TeapotResourceRepository resourceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    ServletRegistrationBean h2ServletRegistrationBean() {
        ServletRegistrationBean registrationBean =
                new ServletRegistrationBean(new WebServlet());

        registrationBean.addUrlMappings("/h2/*");

        return registrationBean;
    }

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            registerAdmin();
            registerDaleCooper();
            registerLoraPalmer();
            registerSherlockHolmes();
            registerDoctorWatson();

            addProperties();
            addResources();
            addActions();
        };
    }

    private void registerAdmin() {
        User admin = new User();

        admin.setUsername("admin");
        admin.setEmail("admin@teapot.org");
        admin.setPassword(passwordEncoder.encode("1234"));
        admin.setFirstName("Cake");
        admin.setLastName("Lover");
        admin.setRegistrationDate(LocalDateTime.now());
        admin.setBirthday(LocalDate.now());
        admin.setDescription("i manage everything");
        admin.setAuthority(UserAuthority.ADMIN);

        userRepository.save(admin);
    }

    private void registerDaleCooper() {
        User user = new User();

        user.setUsername("dale_cooper");
        user.setEmail("dale_cooper@twin.peaks");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setFirstName("Dale");
        user.setLastName("Cooper");
        user.setRegistrationDate(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("a special FBI agent");
        user.setAuthority(UserAuthority.USER);

        userRepository.save(user);
    }

    private void registerLoraPalmer() {
        User user = new User();

        user.setUsername("lora_palmer");
        user.setEmail("lora_palmer@twin.peaks");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setFirstName("Lora");
        user.setLastName("Palmer");
        user.setRegistrationDate(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("a dead girl");
        user.setAuthority(UserAuthority.USER);

        userRepository.save(user);
    }

    private void registerSherlockHolmes() {
        User user = new User();

        user.setUsername("sherlock_holmes");
        user.setEmail("sherlock_holmes@baker.st");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setFirstName("Sherlock");
        user.setLastName("Holmes");
        user.setRegistrationDate(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("private detective");
        user.setAuthority(UserAuthority.USER);

        userRepository.save(user);
    }

    private void registerDoctorWatson() {
        User user = new User();

        user.setUsername("dr_watson");
        user.setEmail("dr_watson@baker.st");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setFirstName("John");
        user.setLastName("Watson");
        user.setRegistrationDate(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("Sherlock Holmes' mate");
        user.setAuthority(UserAuthority.USER);

        userRepository.save(user);
    }

    private void addProperties() {
        TeapotProperty property1 = new TeapotProperty();
        TeapotProperty property2 = new TeapotProperty();

        property1.setName("verification-token-expire-days");
        property1.setValue("1");

        property2.setName("site-uri");
        property2.setValue("localhost:8080");

        propertyRepository.save(property1);
        propertyRepository.save(property2);
    }

    private void addResources() {
        TeapotResource resource1 = new TeapotResource();

        resource1.setName("user");
        resource1.setUri("/users");
        resource1.setDescription(
                "Available methods:\n\nGET /users/{id|username}\n" +
                "POST /users\nPUT /users\nDELETE /users/{id}\n\n");

        resourceRepository.save(resource1);
    }

    private void addActions() {
        TeapotAction action1 = new TeapotAction();
        TeapotAction action2 = new TeapotAction();

        action1.setName("help");
        action1.setUsage("/actions/help?resource={name|id}|action={name|id}");

        action2.setName("activate");
        action2.setUsage("/actions/activate?user={username|id}&token={token}");

        actionRepository.save(Arrays.asList(action1, action2));
    }
}
