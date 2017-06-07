package org.teapot.backend.test.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.teapot.backend.dao.abstr.UserDao;
import org.teapot.backend.model.User;
import org.teapot.backend.test.AbstractIT;

import java.util.ArrayList;
import java.util.List;

public class UserDaoImplIT extends AbstractIT {

    @Autowired
    private UserDao userDao;

    private List<User> users = new ArrayList<>();

    @Before
    public void initTestData() {
        for (int i = 0; i < 5; i++) {
            User user = new User();

            user.setUsername(String.format("user-%d@teapot.org", i));
            user.setPassword("pass");

            users.add(user);

            userDao.insert(user);
        }
    }

    @Test
    public void getByUsernameTest() {
        for (User user : users) {
            Assert.assertNotNull(user);
            Assert.assertEquals(user, userDao.getByUsername(user.getUsername()));
        }
    }

    @Test
    public void deleteByUsernameTest() {
        for (User user : users) {
            userDao.deleteByUsername(user.getUsername());
            Assert.assertNull(userDao.getByUsername(user.getUsername()));
        }
    }
}
