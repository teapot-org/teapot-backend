package org.teapot.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.teapot.backend.model.user.User;

import javax.transaction.Transactional;


@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByUsername(String username);
}
