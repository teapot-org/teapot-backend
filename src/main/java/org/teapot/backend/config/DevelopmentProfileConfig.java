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
import org.teapot.backend.service.abstr.UserRoleService;
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

    @Autowired
    private UserRoleService userRoleService;

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

            User admin = new User();

            admin.setUsername("admin@teapot.org");
            admin.setPassword("1234");
            admin.setFirstName("Cake");
            admin.setLastName("Lover");
            admin.setRegistrationDate(LocalDateTime.now());
            admin.setBirthday(LocalDate.now());
            admin.setDescription("i manage everything");
            admin.getRoles().add(userRole);
            admin.getRoles().add(adminRole);

            userService.register(admin);

            registerDaleCooper();
            registerLoraPalmer();
            registerSherlockHolmes();
            registerDoctorWatson();

            assignLeaderRoleToDaleCooper();
            assignLeaderRoleToSherlockHolmes();
        };
    }

    private void registerDaleCooper() {
        User user = new User();
        UserRole userRole = userRoleService.getByName("user");

        user.setUsername("dale_cooper@twin.peaks");
        user.setPassword("1234");
        user.setFirstName("Dale");
        user.setLastName("Cooper");
        user.setRegistrationDate(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("a special FBI agent");
        user.getRoles().add(userRole);

        userService.register(user);
    }

    private void registerLoraPalmer() {
        User user = new User();
        UserRole userRole = userRoleService.getByName("user");

        user.setUsername("lora_palmer@twin.peaks");
        user.setPassword("1234");
        user.setFirstName("Lora");
        user.setLastName("Palmer");
        user.setRegistrationDate(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("a dead girl");
        user.getRoles().add(userRole);

        userService.register(user);
    }

    private void registerSherlockHolmes() {
        User user = new User();
        UserRole userRole = userRoleService.getByName("user");

        user.setUsername("sherlock_holmes@baker.st");
        user.setPassword("1234");
        user.setFirstName("Sherlock");
        user.setLastName("Holmes");
        user.setRegistrationDate(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("private detective");
        user.getRoles().add(userRole);

        userService.register(user);
    }

    private void registerDoctorWatson() {
        User user = new User();
        UserRole userRole = userRoleService.getByName("user");

        user.setUsername("dr_watson@baker.st");
        user.setPassword("1234");
        user.setFirstName("John");
        user.setLastName("Watson");
        user.setRegistrationDate(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("Sherlock Holmes' mate");
        user.getRoles().add(userRole);

        userService.register(user);
    }

    private void assignLeaderRoleToDaleCooper() {
        User user = userService.getById(2);
        UserRole leaderRole = userRoleService.getByName("leader");
        userService.assignUserRole(user, leaderRole);
    }

    private void assignLeaderRoleToSherlockHolmes() {
        User user = userService.getById(4);
        UserRole leaderRole = userRoleService.getByName("leader");
        userService.assignUserRole(user, leaderRole);
    }
}
