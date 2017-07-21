package org.teapot.backend.repository.kanban;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.TicketList;

import java.util.List;

@RepositoryRestResource(path = "ticket-lists")
public interface TicketListRepository extends JpaRepository<TicketList, Long> {

    @Override
    @PreAuthorize("@kanbanService.isUserContributor(@ticketListRepository.findOne(#id)?.kanban?.id, authentication?.name)")
    void delete(Long id);

    @Override
    @PreAuthorize("@kanbanService.isUserContributor(#entity?.kanban?.id, authentication?.name)")
    void delete(TicketList entity);

    @RestResource(exported = false)
    List<TicketList> findByKanbanOrderByPosition(Kanban kanban);

    @RestResource(path = "find-by-kanban-id")
    Page<TicketList> findByKanbanIdOrderByPosition(@Param("kanbanId") Long kanbanId, Pageable pageable);
}
