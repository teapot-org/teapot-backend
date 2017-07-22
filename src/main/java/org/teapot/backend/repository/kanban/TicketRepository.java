package org.teapot.backend.repository.kanban;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.teapot.backend.model.kanban.Ticket;
import org.teapot.backend.model.kanban.TicketList;

import java.util.List;

import static org.teapot.backend.service.KanbanService.USER_IS_TICKET_CONTRIBUTOR;
import static org.teapot.backend.service.KanbanService.USER_IS_TICKET_CONTRIBUTOR_BY_TICKET;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Override
    @PreAuthorize(USER_IS_TICKET_CONTRIBUTOR)
    void delete(Long id);

    @Override
    @PreAuthorize(USER_IS_TICKET_CONTRIBUTOR_BY_TICKET)
    void delete(Ticket ticket);

    @RestResource(exported = false)
    List<Ticket> findByTicketListOrderByPosition(TicketList ticketList);

    @RestResource(path = "find-by-ticket-list-id")
    Page<Ticket> findByTicketListIdOrderByPosition(@Param("ticketListId") Long ticketListId, Pageable pageable);
}
