package org.teapot.backend.test.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.teapot.backend.model.UserRole;
import org.teapot.backend.repository.UserRoleRepository;
import org.teapot.backend.test.AbstractIT;


public class UserRoleRepositoryIT extends AbstractIT {

    @Autowired
    private UserRoleRepository userRoleRepository;

    private UserRole role = new UserRole();

    @Before
    public void init() {
        role.setName("getByName");
        userRoleRepository.save(role);
    }

    @Test
    public void getByNameTest() {
        UserRole testRole = userRoleRepository.getByName("getByName");
        Assert.assertNotNull(testRole);
        Assert.assertEquals(role, testRole);
    }
}
