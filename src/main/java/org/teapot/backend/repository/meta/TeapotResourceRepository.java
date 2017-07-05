package org.teapot.backend.repository.meta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.teapot.backend.model.meta.TeapotResource;

@RepositoryRestResource(exported = false, path = "resources", collectionResourceRel = "resources", itemResourceRel = "resource")
public interface TeapotResourceRepository extends JpaRepository<TeapotResource, Long> {

    TeapotResource findByName(String name);
}
