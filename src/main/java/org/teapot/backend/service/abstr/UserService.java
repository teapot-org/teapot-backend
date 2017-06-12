package org.teapot.backend.service.abstr;

import org.teapot.backend.model.User;
import org.teapot.backend.model.UserRole;

import java.util.List;


public interface UserService extends AbstractService<User> {

    User getByUsername(String username);

    void disable(User user);

    void enable(User user);

    void register(User user);

    void update(User user);

    List<User> getAllByUserRole(UserRole role);

    void assignUserRole(User user, UserRole role);

    void removeUserRole(User user, UserRole role);

    boolean hasUserRole(User user, UserRole role);

    void delete(Long id);

    void delete(User user);

    List<User> getList();

    List<User> getList(Integer offset, Integer count);
}
