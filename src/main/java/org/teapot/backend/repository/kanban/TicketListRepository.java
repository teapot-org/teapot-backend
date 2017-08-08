package org.teapot.backend.repository.kanban;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.teapot.backend.model.kanban.TicketList;
import org.teapot.backend.repository.BaseEntityRepository;

@RepositoryRestResource(path = "ticket-lists")
public interface TicketListRepository extends BaseEntityRepository<TicketList> {

    String TICKET_LIST_IS_PUBLIC = "resource.kanban.access = ?#{T(org.teapot.backend.model.kanban.KanbanAccess).PUBLIC}";

    @Override
    @Query("select resource from TicketList resource where (" + TICKET_LIST_IS_PUBLIC + " or " + HAS_ADMIN_ROLE + ")")
    Page<TicketList> findAll(Pageable pageable);
}
