package org.teapot.backend.repository.meta;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.meta.TeapotProperty;

@RepositoryRestResource(path = "props", collectionResourceRel = "properties", itemResourceRel = "property")
public interface TeapotPropertyRepository extends JpaRepository<TeapotProperty, Long> {

    @RestResource(path = "find-by-name")
    TeapotProperty findByName(@Param("name") String name);

    @RestResource(path = "find-by-value")
    Page<TeapotProperty> findByValue(@Param("name") String value, Pageable pageable);
}
