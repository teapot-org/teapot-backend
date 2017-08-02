package org.teapot.backend.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.user.UserRepository;

import java.util.Objects;

@Service("users")
public class UserSecurityService extends AbstractSecurityService<User> {

    @Autowired
    private UserRepository userRepository;

    public boolean isLoggedUser(Long userId) {
        assertExists(userId);
        return isLoggedUser(userRepository.findOne(userId));
    }

    public boolean isLoggedUser(User user) {
        User loggedUser = userRepository.findByEmail(getLoggedUserEmail());
        return Objects.equals(user, loggedUser);
    }
}
