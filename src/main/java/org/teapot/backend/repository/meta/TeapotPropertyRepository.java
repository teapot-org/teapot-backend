package org.teapot.backend.repository.meta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.teapot.backend.model.meta.TeapotProperty;

@RepositoryRestResource(exported = false, path = "props", collectionResourceRel = "properties", itemResourceRel = "property")
public interface TeapotPropertyRepository extends JpaRepository<TeapotProperty, Long> {

    TeapotProperty findByName(String name);
}
