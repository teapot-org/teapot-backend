package org.teapot.backend.test.dao;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.teapot.backend.dao.abstr.UserRoleDao;
import org.teapot.backend.model.UserRole;
import org.teapot.backend.test.AbstractIT;


public class UserRoleDaoImplIT extends AbstractIT {

    @Autowired
    private UserRoleDao userRoleDao;

    @Test
    public void getByUserRoleNameTest(){
        UserRole role = new UserRole();
        role.setName("getByUserRoleNameTest");
        userRoleDao.insert(role);

        Assert.assertEquals(role,
                userRoleDao.getByUserRoleName(role.getName()));
    }
}
