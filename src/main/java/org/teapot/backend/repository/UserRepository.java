package org.teapot.backend.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.teapot.backend.model.User;

import javax.transaction.Transactional;
import java.util.List;


@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    void deleteByUsername(String username);
}
