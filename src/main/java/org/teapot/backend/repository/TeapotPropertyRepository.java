package org.teapot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.teapot.backend.model.TeapotProperty;

@Repository
@Transactional
public interface TeapotPropertyRepository extends JpaRepository<TeapotProperty, Long> {

    TeapotProperty findByName(String name);
}
