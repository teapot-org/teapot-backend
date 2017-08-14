package org.teapot.backend.repository.kanban;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.KanbanAccess;
import org.teapot.backend.model.kanban.Project;
import org.teapot.backend.repository.AbstractOwnerItemRepository;

import java.util.List;

public interface KanbanRepository extends AbstractOwnerItemRepository<Kanban> {

    String KANBAN_IS_PUBLIC = "resource.access = ?#{T(org.teapot.backend.model.kanban.KanbanAccess).PUBLIC}";

    @Override
    @Query("select resource from Kanban resource where (" + KANBAN_IS_PUBLIC + " or " + HAS_ADMIN_ROLE + ")")
    Page<Kanban> findAll(Pageable pageable);

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
