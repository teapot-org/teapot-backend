package org.teapot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.Owner;

@NoRepositoryBean
public interface AbstractOwnerRepository<T extends Owner> extends JpaRepository<T, Long> {

    @RestResource(path = "find-by-name")
    T findByName(@Param("name") String name);
}
