package org.teapot.backend.controller.kanban;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.*;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.AbstractController;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.TicketList;
import org.teapot.backend.repository.kanban.KanbanRepository;
import org.teapot.backend.repository.kanban.TicketListRepository;
import org.teapot.backend.util.PagedResourcesAssemblerHelper;

import static org.teapot.backend.service.KanbanService.USER_IS_TICKET_LIST_CONTRIBUTOR_OR_OWNER;
import static org.teapot.backend.service.KanbanService.USER_IS_TICKET_LIST_CONTRIBUTOR_OR_OWNER_BY_RESOURCE;

@RepositoryRestController
public class TicketListController extends AbstractController {

    public static final String TICKET_LISTS_ENDPOINT = "/ticket-lists";
    public static final String SINGLE_TICKET_LIST_ENDPOINT = TICKET_LISTS_ENDPOINT + "/{id}";

    @Autowired
    private PagedResourcesAssemblerHelper<TicketList> helper;

    @Autowired
    private TicketListRepository ticketListRepository;

    @Autowired
    private KanbanRepository kanbanRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(TICKET_LISTS_ENDPOINT)
    public ResponseEntity<?> getTicketLists(
            Pageable pageable,
            PersistentEntityResourceAssembler assembler
    ) {
        Page<TicketList> page = ticketListRepository.findAll(pageable);
        PagedResources resources = helper.toResource(TicketList.class, page, assembler);
        return ResponseEntity.ok(resources);
    }

    @PreAuthorize(USER_IS_TICKET_LIST_CONTRIBUTOR_OR_OWNER + " or hasRole('ADMIN')")
    @GetMapping(SINGLE_TICKET_LIST_ENDPOINT)
    public ResponseEntity<?> getTicketList(
            @PathVariable Long id,
            PersistentEntityResourceAssembler assembler
    ) {
        TicketList ticketList = ticketListRepository.findOne(id);
        if (ticketList == null) {
            throw new ResourceNotFoundException();
        }

        PersistentEntityResource responseResource = assembler.toResource(ticketList);
        HttpHeaders headers = headersPreparer.prepareHeaders(responseResource);

        return ControllerUtils.toResponseEntity(HttpStatus.OK, headers, responseResource);
    }

    @PreAuthorize(USER_IS_TICKET_LIST_CONTRIBUTOR_OR_OWNER_BY_RESOURCE + " or hasRole('ADMIN')")
    @PostMapping(TICKET_LISTS_ENDPOINT)
    public ResponseEntity<?> createTicketList(
            @RequestBody Resource<TicketList> resource,
            PersistentEntityResourceAssembler assembler

    ) {
        TicketList ticketList = ticketListRepository.save(resource.getContent());

        PersistentEntityResource responseResource = assembler.toResource(ticketList);
        HttpHeaders headers = headersPreparer.prepareHeaders(responseResource);
        addLocationHeader(headers, assembler, ticketList);

        return ControllerUtils.toResponseEntity(HttpStatus.CREATED, headers, responseResource);
    }

    @PreAuthorize(USER_IS_TICKET_LIST_CONTRIBUTOR_OR_OWNER + " or hasRole('ADMIN')")
    @PatchMapping(TICKET_LISTS_ENDPOINT + "/shift")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeTicketListPosition(
            @RequestParam("list") Long id,
            @RequestParam Integer position
    ) {
        TicketList ticketList = ticketListRepository.findOne(id);
        if (ticketList == null) {
            throw new ResourceNotFoundException();
        }

        Kanban kanban = ticketList.getKanban();
        kanban.getTicketLists().remove(ticketList);
        kanban.getTicketLists().add(position, ticketList);

        kanbanRepository.save(kanban);
    }

    @PreAuthorize(USER_IS_TICKET_LIST_CONTRIBUTOR_OR_OWNER + " or hasRole('ADMIN')")
    @PatchMapping(SINGLE_TICKET_LIST_ENDPOINT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeTicketListTitle(@PathVariable Long id, @RequestParam String title) {
        TicketList ticketList = ticketListRepository.findOne(id);
        if (ticketList == null) {
            throw new ResourceNotFoundException();
        }

        ticketList.setTitle(title);

        ticketListRepository.save(ticketList);
    }
}
