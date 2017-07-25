package org.teapot.backend.repository.kanban;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.KanbanAccess;
import org.teapot.backend.model.kanban.Project;
import org.teapot.backend.repository.AbstractOwnerItemRepository;

import java.util.List;

public interface KanbanRepository extends AbstractOwnerItemRepository<Kanban> {

    @Override
    @PreAuthorize("@kanbans.isOwner(#id) or hasRole('ADMIN')")
    void delete(@Param("id") Long id);

    @Override
    @PreAuthorize("@kanbans.isOwner(#entity) or hasRole('ADMIN')")
    void delete(@Param("entity") Kanban entity);

    @RestResource(exported = false)
    List<Kanban> findByProject(Project project);

    @RestResource(exported = false)
    Page<Kanban> findByProject(Project project, Pageable pageable);

    @RestResource(exported = false)
    List<Kanban> findByAccess(KanbanAccess access);

    @RestResource(exported = false)
    Page<Kanban> findByAccess(KanbanAccess access, Pageable pageable);

    @RestResource(exported = false)
    List<Kanban> findByOwnerAndAccess(Owner owner, KanbanAccess access);

    @RestResource(exported = false)
    Page<Kanban> findByOwnerAndAccess(Owner owner, KanbanAccess access, Pageable pageable);

    @RestResource(exported = false)
    Page<Kanban> findByOwnerIdAndAccess(Long ownerId, KanbanAccess access, Pageable pageable);
}
