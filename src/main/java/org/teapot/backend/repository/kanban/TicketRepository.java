package org.teapot.backend.repository.kanban;

import org.springframework.data.jpa.repository.JpaRepository;
import org.teapot.backend.model.kanban.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
