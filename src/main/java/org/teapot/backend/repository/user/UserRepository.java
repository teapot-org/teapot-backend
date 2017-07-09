package org.teapot.backend.repository.user;

import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.AbstractOwnerRepository;

public interface UserRepository extends AbstractOwnerRepository<User> {

    @RestResource(path = "find-by-email")
    User findByEmail(@Param("email") String email);
}
