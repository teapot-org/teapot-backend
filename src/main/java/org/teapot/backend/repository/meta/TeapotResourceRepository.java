package org.teapot.backend.repository.meta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.meta.TeapotResource;

@RepositoryRestResource(path = "resources", collectionResourceRel = "resources", itemResourceRel = "resource")
public interface TeapotResourceRepository extends JpaRepository<TeapotResource, Long> {

    @RestResource(path = "find-by-name")
    TeapotResource findByName(@Param("name") String name);

    @RestResource(path = "find-by-uri")
    TeapotResource findByUri(@Param("uri") String uri);
}
