package org.teapot.backend.controller.kanban;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.teapot.backend.controller.AbstractController;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.TicketList;
import org.teapot.backend.repository.kanban.KanbanRepository;
import org.teapot.backend.repository.kanban.TicketListRepository;

@RepositoryRestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TicketListController extends AbstractController {

    public static final String TICKET_LISTS_ENDPOINT = "/ticket-lists";
    public static final String SINGLE_TICKET_LIST_ENDPOINT = TICKET_LISTS_ENDPOINT + "/{id:\\d+}";

    private final TicketListRepository ticketListRepository;
    private final KanbanRepository kanbanRepository;

    @PreAuthorize("canEdit(#id, 'TicketList')")
    @PatchMapping(TICKET_LISTS_ENDPOINT + "/shift")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeTicketListPosition(
            @RequestParam("list") Long id,
            @RequestParam Integer position
    ) {
        TicketList ticketList = ticketListRepository.findOne(id);

        Kanban kanban = ticketList.getKanban();
        kanban.getTicketLists().remove(ticketList);
        kanban.getTicketLists().add(position, ticketList);

        kanbanRepository.save(kanban);
    }

    @PreAuthorize("canEdit(#id, 'TicketList')")
    @PatchMapping(SINGLE_TICKET_LIST_ENDPOINT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeTicketListTitle(@PathVariable Long id, @RequestParam String title) {
        TicketList ticketList = ticketListRepository.findOne(id);
        ticketList.setTitle(title);
        ticketListRepository.save(ticketList);
    }
}
