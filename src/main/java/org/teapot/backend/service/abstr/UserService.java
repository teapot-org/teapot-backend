package org.teapot.backend.service.abstr;

import org.teapot.backend.model.User;


public interface UserService extends AbstractService<User> {

    User getByUsername(String username);

    void disable(User user);

    void enable(User user);

    void register(User user);

    void update(User user);
}
