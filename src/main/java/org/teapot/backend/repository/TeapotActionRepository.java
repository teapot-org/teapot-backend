package org.teapot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.teapot.backend.model.meta.TeapotAction;

@Repository
@Transactional
public interface TeapotActionRepository extends JpaRepository<TeapotAction, Long> {

    TeapotAction findByName(String name);
}
