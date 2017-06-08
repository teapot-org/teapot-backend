package org.teapot.backend.test.service;

import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.teapot.backend.dao.abstr.UserRoleDao;
import org.teapot.backend.model.User;
import org.teapot.backend.model.UserRole;
import org.teapot.backend.service.abstr.UserService;
import org.teapot.backend.test.AbstractIT;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@Transactional
public class UserServiceImplIT extends AbstractIT {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleDao userRoleDao;

    private List<User> disabledUsers = new ArrayList<>();
    private List<User> enabledUsers = new ArrayList<>();
    private UserRole adminRole;
    private UserRole moderatorRole;

    private User createUser(String username, boolean available) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("pass");
        user.setAvailable(available);
        return user;
    }

    @Before
    public void setUp() {
        for (int i = 0; i < 5; i++) {
            User user = createUser("s" + i, false);

            disabledUsers.add(user);
            userService.register(user);
        }

        for (int i = 5; i < 10; i++) {
            User user = createUser("s" + i, true);

            enabledUsers.add(user);
            userService.register(user);
        }

        adminRole = new UserRole();
        adminRole.setName("admin");
        userRoleDao.insert(adminRole);

        moderatorRole = new UserRole();
        moderatorRole.setName("moderator");
        userRoleDao.insert(moderatorRole);

        User user = createUser("roled_user", true);
        userService.register(user);
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

    @Test
    public void getAllByUserRoleTest() {
        User user1 = createUser("all-by-role-1", true);
        user1.setRoles(Sets.newSet(moderatorRole));
        userService.register(user1);

        User user2 = createUser("all-by-role-2", true);
        user2.setRoles(Sets.newSet(adminRole));
        userService.register(user2);

        User user3 = createUser("all-by-role-3", true);
        user3.setRoles(Sets.newSet(moderatorRole));
        userService.register(user3);

        Assert.assertEquals(Lists.newArrayList(user1, user3),
                userService.getAllByUserRole(moderatorRole));

        Assert.assertEquals(Lists.newArrayList(user2),
                userService.getAllByUserRole(adminRole));
    }

    @Test
    public void assignUserRoleTest() {
        Assert.assertFalse(userService.getByUsername("roled_user")
                .getRoles().contains(adminRole));

        userService.assignUserRole(
                userService.getByUsername("roled_user"), adminRole);

        Assert.assertTrue(userService.getByUsername("roled_user")
                .getRoles().contains(adminRole));
    }

    @Test
    public void removeUserRoleTest() {
        userService.assignUserRole(
                userService.getByUsername("roled_user"), moderatorRole);
        Assert.assertTrue(userService.getByUsername("roled_user")
                .getRoles().contains(moderatorRole));

        userService.removeUserRole(
                userService.getByUsername("roled_user"), moderatorRole);
        Assert.assertFalse(userService.getByUsername("roled_user")
                .getRoles().contains(moderatorRole));
    }

    @Test
    public void hasUserRoleTest() {
        UserRole role = new UserRole();
        role.setName("test_has_role");
        userRoleDao.insert(role);
        Assert.assertFalse(userService.hasUserRole(
                userService.getByUsername("roled_user"), role));

        userService.assignUserRole(
                userService.getByUsername("roled_user"), role);
        Assert.assertTrue(userService.hasUserRole(
                userService.getByUsername("roled_user"), role));
    }
}