package org.teapot.backend.repository.user;

import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.AbstractOwnerRepository;

public interface UserRepository extends AbstractOwnerRepository<User> {

    User findByEmail(String email);
}
