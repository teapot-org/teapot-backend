package org.teapot.backend.test.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.teapot.backend.model.User;
import org.teapot.backend.model.UserRole;
import org.teapot.backend.repository.UserRepository;
import org.teapot.backend.repository.UserRoleRepository;
import org.teapot.backend.test.AbstractIT;

import java.util.List;


public class UserRepositoryIT extends AbstractIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    private User findByUsernameTestUser = new User();
    private User deleteByUsernameTestUser = new User();

    @Before
    public void init() {
        UserRole testRole1 = new UserRole();
        testRole1.setName("testRole1");
        userRoleRepository.save(testRole1);

        UserRole testRole2 = new UserRole();
        testRole2.setName("testRole2");
        userRoleRepository.save(testRole2);

        findByUsernameTestUser.setUsername("findByUsername");
        findByUsernameTestUser.setPassword("pass");
        findByUsernameTestUser.getRoles().add(testRole1);
        findByUsernameTestUser.getRoles().add(testRole2);
        userRepository.save(findByUsernameTestUser);

        deleteByUsernameTestUser.setUsername("deleteByUsername");
        deleteByUsernameTestUser.setPassword("pass");
        deleteByUsernameTestUser.getRoles().add(testRole2);
        userRepository.save(deleteByUsernameTestUser);
    }

    @Test
    public void findByUsernameTest() {
        User foundUser = userRepository.findByUsername("findByUsername");
        Assert.assertNotNull(foundUser);
        Assert.assertEquals(findByUsernameTestUser, foundUser);
    }

    @Test
    public void deleteByUsernameTest() {
        userRepository.deleteByUsername("deleteByUsername");
        Assert.assertNull(userRepository.findOne(deleteByUsernameTestUser.getId()));
        System.out.println();
    }
}
