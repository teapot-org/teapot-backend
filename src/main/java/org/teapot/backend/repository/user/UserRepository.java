package org.teapot.backend.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.AbstractOwnerRepository;

public interface UserRepository extends AbstractOwnerRepository<User> {

    @RestResource(path = "find-by-email")
    User findByEmail(@Param("email") String email);

    @RestResource(path = "find-by-first-name-or-last-name")
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', ?1, '%')) or " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<User> findByFirstNameOrLastName(@Param("name") String firstNameOrLastName, Pageable pageable);
}
