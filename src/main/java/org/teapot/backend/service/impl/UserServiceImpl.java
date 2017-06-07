package org.teapot.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.teapot.backend.dao.abstr.UserDao;
import org.teapot.backend.model.User;
import org.teapot.backend.service.abstr.UserService;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User getById(long id) {
        return userDao.getById(id);
    }

    @Override
    public User getByUsername(String username) {
        return userDao.getByUsername(username);
    }

    @Override
    public void disable(User user) {
        user.setAvailable(false);
        userDao.update(user);
    }

    @Override
    public void enable(User user) {
        user.setAvailable(true);
        userDao.update(user);
    }

    @Override
    public void register(User user) {
        userDao.insert(user);
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }
}
