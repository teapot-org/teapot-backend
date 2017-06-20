package org.teapot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.teapot.backend.model.meta.TeapotResource;

public interface TeapotResourceRepository extends JpaRepository<TeapotResource, Long> {

    TeapotResource findByName(String name);
}
