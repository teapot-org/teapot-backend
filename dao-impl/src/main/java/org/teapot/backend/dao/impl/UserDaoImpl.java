package org.teapot.backend.dao.impl;

import org.springframework.transaction.annotation.Transactional;
import org.teapot.backend.dao.abstr.UserDao;
import org.teapot.backend.model.User;

import javax.persistence.NoResultException;


@Transactional
public class UserDaoImpl extends AbstractDaoImpl<User> implements UserDao {

    public UserDaoImpl(Class<User> clazz) {
        super(clazz);
    }

    @Override
    public User getByUsername(String username) {
        User user = null;
        try {
            user = entityManager
                    .createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException ignored) {
        }

        return user;
    }

    @Override
    public void deleteByUsername(String username) {
        User user = getByUsername(username);
        if (user != null) {
            entityManager.remove(user);
        }
    }
}
