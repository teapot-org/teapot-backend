package org.teapot.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.teapot.backend.dao.abstr.UserRoleDao;
import org.teapot.backend.model.UserRole;
import org.teapot.backend.service.abstr.UserRoleService;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    @Override
    public UserRole getById(long id) {
        return userRoleDao.getById(id);
    }

    @Override
    public UserRole getByName(String name) {
        return userRoleDao.getByUserRoleName(name);
    }
}
