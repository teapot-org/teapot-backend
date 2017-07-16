package org.teapot.backend.repository.kanban;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.TicketList;

import java.util.List;

@RepositoryRestResource(path = "ticket-lists", collectionResourceRel = "ticket-lists", itemResourceRel = "ticket-list")
public interface TicketListRepository extends JpaRepository<TicketList, Long> {

    @RestResource(exported = false)
    List<TicketList> findByKanban(Kanban kanban);

    @RestResource(path = "find-by-kanban-id")
    Page<TicketList> findByKanbanId(@Param("kanbanId") Long kanbanId, Pageable pageable);
}
