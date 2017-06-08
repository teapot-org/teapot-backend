package org.teapot.backend.config;

import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.teapot.backend.dao.abstr.UserRoleDao;
import org.teapot.backend.model.User;
import org.teapot.backend.model.UserRole;
import org.teapot.backend.service.abstr.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@Profile("development")
public class DevelopmentProfileConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleDao userRoleDao;

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
            UserRole userRole = new UserRole();
            UserRole leaderRole = new UserRole();
            UserRole adminRole = new UserRole();

            userRole.setName("user");
            leaderRole.setName("leader");
            adminRole.setName("admin");

            userRoleDao.insert(userRole);
            userRoleDao.insert(leaderRole);
            userRoleDao.insert(adminRole);

            User user1 = new User();
            User user2 = new User();
            User user3 = new User();
            User user4 = new User();

            user1.setUsername("dale_cooper@twin.peaks");
            user1.setPassword("1234");
            user1.setFirstName("Dale");
            user1.setLastName("Cooper");
            user1.setRegistrationDate(LocalDateTime.now());
            user1.setBirthday(LocalDate.now());
            user1.setDescription("a special FBI agent");

            user2.setUsername("lora_palmer@twin.peaks");
            user2.setPassword("1234");
            user2.setFirstName("Lora");
            user2.setLastName("Palmer");
            user2.setRegistrationDate(LocalDateTime.now());
            user2.setBirthday(LocalDate.now());
            user2.setDescription("a dead girl");

            user3.setUsername("sherlock_holmes@baker.st");
            user3.setPassword("1234");
            user3.setFirstName("Sherlock");
            user3.setLastName("Holmes");
            user3.setRegistrationDate(LocalDateTime.now());
            user3.setBirthday(LocalDate.now());
            user3.setDescription("private detective");

            user4.setUsername("dr_watson@baker.st");
            user4.setPassword("1234");
            user4.setFirstName("John");
            user4.setLastName("Watson");
            user4.setRegistrationDate(LocalDateTime.now());
            user4.setBirthday(LocalDate.now());
            user4.setDescription("Sherlock Holmes' mate");

            userService.register(user1);
            userService.register(user2);
            userService.register(user3);
            userService.register(user4);

            userService.assignUserRole(user1, userRole);
            userService.assignUserRole(user2, userRole);
            userService.assignUserRole(user3, userRole);
            userService.assignUserRole(user4, userRole);
//            userService.assignUserRole(user3, leaderRole);
//            userService.assignUserRole(user4, adminRole);
        };
    }
}
