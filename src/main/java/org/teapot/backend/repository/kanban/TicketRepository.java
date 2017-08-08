package org.teapot.backend.repository.kanban;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.kanban.Ticket;
import org.teapot.backend.model.kanban.TicketList;
import org.teapot.backend.repository.BaseEntityRepository;

import java.util.List;

public interface TicketRepository extends BaseEntityRepository<Ticket> {

    String TICKET_IS_PUBLIC = "resource.ticketList.kanban.access = ?#{T(org.teapot.backend.model.kanban.KanbanAccess).PUBLIC}";

    @Override
    @Query("select resource from Ticket resource where (" + TICKET_IS_PUBLIC + " or " + HAS_ADMIN_ROLE + ")")
    Page<Ticket> findAll(Pageable pageable);

    @RestResource(exported = false)
    List<Ticket> findByTicketListOrderByPosition(TicketList ticketList);

    // todo: only if have access
    @RestResource(path = "find-by-ticket-list-id")
    Page<Ticket> findByTicketListIdOrderByPosition(
            @Param("ticketListId") Long ticketListId, Pageable pageable);
}
