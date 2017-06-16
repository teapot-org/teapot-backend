package org.teapot.backend.test.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.teapot.backend.model.UserAuthority;
import org.teapot.backend.repository.UserAuthorityRepository;
import org.teapot.backend.test.AbstractIT;


public class UserAuthorityRepositoryIT extends AbstractIT {

    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    private UserAuthority role = new UserAuthority();

    @Before
    public void init() {
        role.setAuthority("getByName");
        userAuthorityRepository.save(role);
    }

    @Test
    public void getByAuthorityTest() {
        UserAuthority testRole = userAuthorityRepository.getByAuthority("getByName");
        Assert.assertNotNull(testRole);
        Assert.assertEquals(role, testRole);
    }
}
