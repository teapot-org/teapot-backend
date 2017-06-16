package org.teapot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.teapot.backend.model.UserAuthority;

import javax.transaction.Transactional;


@Transactional
public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {

    UserAuthority getByAuthority(String authority);
}
