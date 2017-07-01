package org.teapot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.teapot.backend.model.Owner;

@NoRepositoryBean
public interface AbstractOwnerRepository<T extends Owner> extends JpaRepository<T, Long> {

    T findByName(String name);
}
