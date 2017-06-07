package org.teapot.backend.dao.abstr;

import org.teapot.backend.model.User;


public interface UserDao extends AbstractDao<User> {

    User getByUsername(String username);

    void deleteByUsername(String username);
}
