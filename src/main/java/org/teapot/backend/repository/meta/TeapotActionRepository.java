package org.teapot.backend.repository.meta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.teapot.backend.model.meta.TeapotAction;

import javax.transaction.Transactional;

@Transactional
public interface TeapotActionRepository extends JpaRepository<TeapotAction, Long> {

    TeapotAction findByName(String name);
}
