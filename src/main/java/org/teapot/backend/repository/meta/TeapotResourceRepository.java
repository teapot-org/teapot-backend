package org.teapot.backend.repository.meta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.teapot.backend.model.meta.TeapotResource;

import javax.transaction.Transactional;

@Transactional
public interface TeapotResourceRepository extends JpaRepository<TeapotResource, Long> {

    TeapotResource findByName(String name);
}
