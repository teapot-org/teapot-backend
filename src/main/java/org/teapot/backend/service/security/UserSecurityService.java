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

    public final boolean isLoggedUser(Long userId) {
        assertExists(userId);
        User user = userRepository.findOne(userId);
        User loggedUser = userRepository.findByEmail(getLoggedUserEmail());
        return Objects.equals(user, loggedUser);
    }
}
