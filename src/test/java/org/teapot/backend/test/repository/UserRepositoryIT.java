package org.teapot.backend.test.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.teapot.backend.model.user.User;
import org.teapot.backend.model.user.UserAuthority;
import org.teapot.backend.repository.user.UserRepository;
import org.teapot.backend.test.AbstractIT;

public class UserRepositoryIT extends AbstractIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User findByNameTestUser = new User();
    private User findByEmailTestUser = new User();

    @Before
    public void init() {
        findByNameTestUser.setName("findByOwnerName");
        findByNameTestUser.setEmail("findByOwnerName@mail.com");
        findByNameTestUser.setPassword(passwordEncoder.encode("pass"));
        findByNameTestUser.setAuthority(UserAuthority.ADMIN);
        userRepository.save(findByNameTestUser);

        findByEmailTestUser.setName("findByEmail");
        findByEmailTestUser.setEmail("findByEmail@mail.com");
        findByEmailTestUser.setPassword(passwordEncoder.encode("pass"));
        findByEmailTestUser.setAuthority(UserAuthority.USER);
        userRepository.save(findByEmailTestUser);
    }

    @Test
    public void findByNameTest() {
        User foundUser = userRepository.findByName("findByOwnerName");
        Assert.assertNotNull(foundUser);
        Assert.assertEquals(findByNameTestUser, foundUser);
    }

    @Test
    public void findByEmailTest() {
        User foundUser = userRepository.findByEmail("findByEmail@mail.com");
        Assert.assertNotNull(foundUser);
        Assert.assertEquals(findByEmailTestUser, foundUser);
    }
}
