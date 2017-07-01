package org.teapot.backend.repository.meta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.teapot.backend.model.meta.TeapotProperty;

import javax.transaction.Transactional;

@Transactional
public interface TeapotPropertyRepository extends JpaRepository<TeapotProperty, Long> {

    TeapotProperty findByName(String name);
}
