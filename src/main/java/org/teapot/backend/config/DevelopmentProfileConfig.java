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
import org.teapot.backend.model.TeapotProperty;
import org.teapot.backend.model.User;
import org.teapot.backend.model.UserAuthority;
import org.teapot.backend.repository.TeapotPropertyRepository;
import org.teapot.backend.repository.UserRepository;
import org.teapot.backend.repository.UserAuthorityRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@Profile("development")
public class DevelopmentProfileConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    @Autowired
    private TeapotPropertyRepository propertyRepository;

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
            UserAuthority userAuthority = new UserAuthority();
            UserAuthority leaderRole = new UserAuthority();
            UserAuthority adminRole = new UserAuthority();

            userAuthority.setAuthority("user");
            leaderRole.setAuthority("leader");
            adminRole.setAuthority("admin");

            userAuthorityRepository.save(userAuthority);
            userAuthorityRepository.save(leaderRole);
            userAuthorityRepository.save(adminRole);

            User admin = new User();

            admin.setUsername("admin@teapot.org");
            admin.setPassword(passwordEncoder.encode("1234"));
            admin.setFirstName("Cake");
            admin.setLastName("Lover");
            admin.setRegistrationDate(LocalDateTime.now());
            admin.setBirthday(LocalDate.now());
            admin.setDescription("i manage everything");
            admin.getAuthorities().add(userAuthority);
            admin.getAuthorities().add(adminRole);

            userRepository.save(admin);

            registerDaleCooper();
            registerLoraPalmer();
            registerSherlockHolmes();
            registerDoctorWatson();

            assignLeaderRoleToDaleCooper();
            assignLeaderRoleToSherlockHolmes();

            addProperties();
        };
    }

    private void registerDaleCooper() {
        User user = new User();
        UserAuthority userAuthority = userAuthorityRepository.getByAuthority("user");

        user.setUsername("dale_cooper@twin.peaks");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setFirstName("Dale");
        user.setLastName("Cooper");
        user.setRegistrationDate(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("a special FBI agent");
        user.getAuthorities().add(userAuthority);

        userRepository.save(user);
    }

    private void registerLoraPalmer() {
        User user = new User();
        UserAuthority userAuthority = userAuthorityRepository.getByAuthority("user");

        user.setUsername("lora_palmer@twin.peaks");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setFirstName("Lora");
        user.setLastName("Palmer");
        user.setRegistrationDate(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("a dead girl");
        user.getAuthorities().add(userAuthority);

        userRepository.save(user);
    }

    private void registerSherlockHolmes() {
        User user = new User();
        UserAuthority userAuthority = userAuthorityRepository.getByAuthority("user");

        user.setUsername("sherlock_holmes@baker.st");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setFirstName("Sherlock");
        user.setLastName("Holmes");
        user.setRegistrationDate(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("private detective");
        user.getAuthorities().add(userAuthority);

        userRepository.save(user);
    }

    private void registerDoctorWatson() {
        User user = new User();
        UserAuthority userAuthority = userAuthorityRepository.getByAuthority("user");

        user.setUsername("dr_watson@baker.st");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setFirstName("John");
        user.setLastName("Watson");
        user.setRegistrationDate(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("Sherlock Holmes' mate");
        user.getAuthorities().add(userAuthority);

        userRepository.save(user);
    }

    private void assignLeaderRoleToDaleCooper() {
        User user = userRepository.findOne(2L);
        UserAuthority leaderRole = userAuthorityRepository.getByAuthority("leader");
        user.getAuthorities().add(leaderRole);
    }

    private void assignLeaderRoleToSherlockHolmes() {
        User user = userRepository.findOne(4L);
        UserAuthority leaderRole = userAuthorityRepository.getByAuthority("leader");
        user.getAuthorities().add(leaderRole);
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
}
