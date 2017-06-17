package org.teapot.backend.test.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.teapot.backend.model.User;
import org.teapot.backend.model.UserAuthority;
import org.teapot.backend.repository.UserRepository;
import org.teapot.backend.test.AbstractIT;


public class UserRepositoryIT extends AbstractIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User findByUsernameTestUser = new User();
    private User deleteByUsernameTestUser = new User();

    @Before
    public void init() {
        findByUsernameTestUser.setUsername("findByUsername");
        findByUsernameTestUser.setPassword(passwordEncoder.encode("pass"));
        findByUsernameTestUser.setAuthority(UserAuthority.USER);
        findByUsernameTestUser.setAuthority(UserAuthority.ADMIN);
        userRepository.save(findByUsernameTestUser);

        deleteByUsernameTestUser.setUsername("deleteByUsername");
        deleteByUsernameTestUser.setPassword(passwordEncoder.encode("pass"));
        deleteByUsernameTestUser.setAuthority(UserAuthority.USER);
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
