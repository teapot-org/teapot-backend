package org.teapot.backend.repository.kanban;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.KanbanAccess;
import org.teapot.backend.model.kanban.Project;
import org.teapot.backend.repository.AbstractOwnerItemRepository;

import java.util.List;

public interface KanbanRepository extends AbstractOwnerItemRepository<Kanban> {

    @Override
    @PreAuthorize("@kanbanService.isUserOwner(#id, authentication.name) or hasRole('ADMIN')")
    void delete(@Param("id") Long id);

    @Override
    @PreAuthorize("@kanbanService.isUserOwner(#kanban?.id, authentication.name) or hasRole('ADMIN')")
    void delete(@Param("kanban") Kanban kanban);

    @RestResource(exported = false)
    List<Kanban> findByProject(Project project);

    @RestResource(path = "find-by-project-id")
    Page<Kanban> findByProjectId(@Param("projectId") Long projectId, Pageable pageable);

    @RestResource(exported = false)
    List<Kanban> findByAccess(KanbanAccess access);

    @RestResource(path = "find-by-access")
    Page<Kanban> findByAccess(@Param("access") KanbanAccess access, Pageable pageable);
}
