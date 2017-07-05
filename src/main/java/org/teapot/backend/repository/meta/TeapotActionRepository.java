package org.teapot.backend.repository.meta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.teapot.backend.model.meta.TeapotAction;

@RepositoryRestResource(exported = false, path = "actions", collectionResourceRel = "actions", itemResourceRel = "action")
public interface TeapotActionRepository extends JpaRepository<TeapotAction, Long> {

    TeapotAction findByName(String name);
}
