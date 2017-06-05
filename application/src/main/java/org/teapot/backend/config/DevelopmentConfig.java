package org.teapot.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.teapot.backend.dao.abstr.UserDao;
import org.teapot.backend.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@Profile("development")
public class DevelopmentConfig {

    @Bean
    public CommandLineRunner loadData(UserDao userDao) {
        return args -> {
            User user1 = new User();

            user1.setUsername("dale_cooper@twin.peaks");
            user1.setPassword("1234");
            user1.setFirstName("Dale");
            user1.setLastName("Cooper");
            user1.setRegistrationDate(LocalDateTime.now());
            user1.setBirthday(LocalDate.now());
            user1.setDescription("a special FBI agent");

            User user2 = new User();

            user2.setUsername("lora_palmer@twin.peaks");
            user2.setPassword("1234");
            user2.setFirstName("Lora");
            user2.setLastName("Palmer");
            user2.setRegistrationDate(LocalDateTime.now());
            user2.setBirthday(LocalDate.now());
            user2.setDescription("a dead girl");

            userDao.insert(user1);
            userDao.insert(user2);
        };
    }
}
