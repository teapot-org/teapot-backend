package org.teapot.backend.repository.kanban;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.teapot.backend.model.kanban.Ticket;
import org.teapot.backend.repository.BaseEntityRepository;

public interface TicketRepository extends BaseEntityRepository<Ticket> {

    String TICKET_IS_PUBLIC = "resource.ticketList.kanban.access = ?#{T(org.teapot.backend.model.kanban.KanbanAccess).PUBLIC}";

    @Override
    @Query("select resource from Ticket resource where (" + TICKET_IS_PUBLIC + " or " + HAS_ADMIN_ROLE + ")")
    Page<Ticket> findAll(Pageable pageable);
}
