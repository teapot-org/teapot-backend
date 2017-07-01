package org.teapot.backend.repository.user;

import org.teapot.backend.repository.AbstractOwnerRepository;
import org.teapot.backend.model.user.User;

import javax.transaction.Transactional;

@Transactional
public interface UserRepository extends AbstractOwnerRepository<User> {

    User findByEmail(String email);
}
