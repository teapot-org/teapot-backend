package org.teapot.backend.repository.kanban;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.kanban.Kanban;

import java.util.List;

public interface KanbanRepository extends JpaRepository<Kanban, Long> {

    @RestResource(exported = false)
    List<Kanban> findByOwner(Owner owner);

    @RestResource(path = "find-by-owner-id")
    Page<Kanban> findByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    @RestResource(path = "find-by-owner-name")
    Page<Kanban> findByOwnerName(@Param("ownerName") String ownerName, Pageable pageable);
}
