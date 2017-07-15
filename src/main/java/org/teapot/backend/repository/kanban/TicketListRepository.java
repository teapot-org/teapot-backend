package org.teapot.backend.repository.kanban;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.teapot.backend.model.kanban.TicketList;

@RepositoryRestResource(path = "ticket-lists")
public interface TicketListRepository extends JpaRepository<TicketList, Long> {
}
