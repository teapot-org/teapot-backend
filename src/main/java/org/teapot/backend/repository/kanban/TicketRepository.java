package org.teapot.backend.repository.kanban;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.kanban.Ticket;
import org.teapot.backend.model.kanban.TicketList;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @RestResource(exported = false)
    List<Ticket> findByTicketList(TicketList ticketList);

    @RestResource(path = "find-by-ticket-list-id")
    Page<Ticket> findByTicketListId(@Param("ticketListId") Long ticketListId, Pageable pageable);
}
