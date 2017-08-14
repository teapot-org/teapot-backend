package org.teapot.backend.test.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.user.UserRepository;
import org.teapot.backend.test.AbstractIT;

public class UserRepositoryIT extends AbstractIT {

    @Autowired
    private UserRepository userRepository;

    private User testUser = new User();

    @Before
    public void init() {
        testUser.setName("findByNameAndEmail");
        testUser.setEmail("findByNameAndEmail@mail.com");
        testUser.setPassword("pass");
        userRepository.save(testUser);
    }

    @Test
    public void findByNameTest() {
        User foundUser = userRepository.findByName("findByNameAndEmail");
        Assert.assertEquals(testUser, foundUser);
    }

    @Test
    public void findByEmailTest() {
        User foundUser = userRepository.findByEmail("findByNameAndEmail@mail.com");
        Assert.assertEquals(testUser, foundUser);
    }
}
