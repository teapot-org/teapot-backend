package org.teapot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.teapot.backend.model.UserRole;

import javax.transaction.Transactional;


@Transactional
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    UserRole getByName(String name);
}
