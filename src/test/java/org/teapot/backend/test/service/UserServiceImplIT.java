package org.teapot.backend.test.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.teapot.backend.model.User;
import org.teapot.backend.service.abstr.UserService;
import org.teapot.backend.test.AbstractIT;

import java.util.ArrayList;
import java.util.List;


public class UserServiceImplIT extends AbstractIT {

    @Autowired
    private UserService userService;

    private List<User> disabledUsers = new ArrayList<>();
    private List<User> enabledUsers = new ArrayList<>();

    public User createUser(int id, boolean available) {
        User user = new User();
        user.setUsername(String.format("s-%d", id));
        user.setPassword("pass");
        user.setAvailable(available);
        return user;
    }

    @Before
    public void initDisabledUsers() {
        for (int i = 0; i < 5; i++) {
            User user = createUser(i, false);
            userService.register(user);
            disabledUsers.add(user);
        }
    }

    @Before
    public void initEnabledUsers() {
        for (int i = 5; i < 10; i++) {
            User user = createUser(i, true);
            userService.register(user);
            enabledUsers.add(user);
        }
    }

    @Test
    public void disableUserTest() {
        for (User user : enabledUsers) {
            Assert.assertEquals(true, user.isAvailable());
            userService.disable(user);
            Assert.assertEquals(false, user.isAvailable());
        }
    }

    @Test
    public void enableUserTest() {
        for (User user : disabledUsers) {
            Assert.assertEquals(false, user.isAvailable());
            userService.enable(user);
            Assert.assertEquals(true, user.isAvailable());
        }
    }
}