package org.teapot.backend.test.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.teapot.backend.model.User;
import org.teapot.backend.service.abstr.UserService;
import org.teapot.backend.test.AbstractIT;

import java.util.ArrayList;
import java.util.List;

@Transactional
public class UserServiceImplIT extends AbstractIT {

    @Autowired
    private UserService userService;

    private List<User> disabledUsers = new ArrayList<>();
    private List<User> enabledUsers = new ArrayList<>();

    @Before
    public void setUp() {
        for (int i = 0; i < 5; i++) {
            User user = new User();

            user.setUsername(String.format("s-%d", i));
            user.setPassword("pass");
            user.setAvailable(false);

            disabledUsers.add(user);
            userService.register(user);
        }

        for (int i = 5; i < 10; i++) {
            User user = new User();

            user.setUsername(String.format("s-%d", i));
            user.setPassword("pass");
            user.setAvailable(true);

            enabledUsers.add(user);
            userService.register(user);
        }
    }

    @Test
    public void enableUserTest() {
        for (User user : disabledUsers) {
            Assert.assertEquals(false, userService
                     .getByUsername(user.getUsername()).isAvailable());

            userService.enable(user);

            Assert.assertEquals(true, userService
                    .getByUsername(user.getUsername()).isAvailable());
        }
    }

    @Test
    public void disableUserTest() {
        for (User user : enabledUsers) {
            Assert.assertEquals(true, userService
                    .getByUsername(user.getUsername()).isAvailable());

            userService.disable(user);

            Assert.assertEquals(false, userService
                    .getByUsername(user.getUsername()).isAvailable());
        }
    }
}