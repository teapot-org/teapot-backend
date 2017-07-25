package org.teapot.backend.controller.kanban;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ControllerUtils;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.AbstractController;
import org.teapot.backend.model.kanban.Ticket;
import org.teapot.backend.model.kanban.TicketList;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.kanban.TicketListRepository;
import org.teapot.backend.repository.kanban.TicketRepository;
import org.teapot.backend.repository.user.UserRepository;
import org.teapot.backend.util.PagedResourcesAssemblerHelper;

import javax.transaction.Transactional;

@RepositoryRestController
public class TicketController extends AbstractController {

    public static final String TICKETS_ENDPOINT = "/tickets";
    public static final String SINGLE_TICKET_ENDPOINT = TICKETS_ENDPOINT + "/{id}";

    @Autowired
    private PagedResourcesAssemblerHelper<Ticket> helper;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketListRepository ticketListRepository;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(TICKETS_ENDPOINT)
    public ResponseEntity<?> getTickets(
            Pageable pageable,
            PersistentEntityResourceAssembler assembler
    ) {
        Page<Ticket> page = ticketRepository.findAll(pageable);
        PagedResources resources = helper.toResource(Ticket.class, page, assembler);
        return ResponseEntity.ok(resources);
    }

    @PreAuthorize("@tickets.isContributor(#id) or @tickets.isOwner(#id) or hasRole('ADMIN')")
    @GetMapping(SINGLE_TICKET_ENDPOINT)
    public ResponseEntity<?> getTicket(
            @PathVariable Long id,
            PersistentEntityResourceAssembler assembler
    ) {
        Ticket ticket = ticketRepository.findOne(id);

        PersistentEntityResource responseResource = assembler.toResource(ticket);
        HttpHeaders headers = headersPreparer.prepareHeaders(responseResource);

        return ControllerUtils.toResponseEntity(HttpStatus.OK, headers, responseResource);
    }

    @PreAuthorize("@tickets.isContributor(#resource) or @tickets.isOwner(#resource) or hasRole('ADMIN')")
    @PostMapping(TICKETS_ENDPOINT)
    public ResponseEntity<?> createTicket(
            @RequestBody Resource<Ticket> resource,
            PersistentEntityResourceAssembler assembler

    ) {
        Ticket ticket = ticketRepository.save(resource.getContent());

        PersistentEntityResource responseResource = assembler.toResource(ticket);
        HttpHeaders headers = headersPreparer.prepareHeaders(responseResource);
        addLocationHeader(headers, assembler, ticket);

        return ControllerUtils.toResponseEntity(HttpStatus.CREATED, headers, responseResource);
    }

    @PreAuthorize("@tickets.isContributor(#id) or @tickets.isOwner(#id) or hasRole('ADMIN')")
    @PatchMapping(SINGLE_TICKET_ENDPOINT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void renameTicket(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description
    ) {
        Ticket ticket = ticketRepository.findOne(id);

        if (title != null) ticket.setTitle(title);
        if (description != null) ticket.setDescription(description);

        ticketRepository.save(ticket);
    }

    @PreAuthorize("@tickets.isContributor(#id) or @tickets.isOwner(#id) or hasRole('ADMIN')")
    @PatchMapping(TICKETS_ENDPOINT + "/shift")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeTicketPosition(@RequestParam("ticket") Long id, @RequestParam Integer position) {
        Ticket ticket = ticketRepository.findOne(id);

        TicketList ticketList = ticket.getTicketList();
        ticketList.getTickets().remove(ticket);
        ticketList.getTickets().add(position, ticket);

        ticketListRepository.save(ticketList);
    }

    @PreAuthorize("@tickets.inSameList(#id, #listId) " +
            "and (@tickets.isContributor(#id) or @tickets.isOwner(#id) or hasRole('ADMIN'))")
    @PatchMapping(TICKETS_ENDPOINT + "/move")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void moveTicketToAnotherList(
            @RequestParam("ticket") Long id,
            @RequestParam("list") Long listId,
            @RequestParam(required = false) Integer position
    ) {
        Ticket ticket = ticketRepository.findOne(id);
        TicketList ticketList = ticketListRepository.findOne(listId);

        ticket.getTicketList().getTickets().remove(ticket);

        if (position == null) {
            ticketList.getTickets().add(ticket);
        } else {
            ticketList.getTickets().add(position, ticket);
        }

        ticketListRepository.save(ticketList);
    }

    @PreAuthorize("@tickets.isContributor(#id, #userId) " +
            "and (@tickets.isContributor(#id) or @tickets.isOwner(#id) or hasRole('ADMIN'))")
    @PatchMapping(TICKETS_ENDPOINT + "/subscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void subscribe(@RequestParam("ticket") Long id, @RequestParam("user") Long userId) {
        Ticket ticket = ticketRepository.findOne(id);
        User user = userRepository.findOne(userId);

        ticket.getSubscribers().add(user);
        ticketRepository.save(ticket);
    }

    @PreAuthorize("@tickets.isSubscriber(#id, #userId) " +
            "and (@tickets.isContributor(#id) or @tickets.isOwner(#id) or hasRole('ADMIN'))")
    @PatchMapping(TICKETS_ENDPOINT + "/unsubscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@RequestParam("ticket") Long id, @RequestParam("user") Long userId) {
        Ticket ticket = ticketRepository.findOne(id);
        User user = userRepository.findOne(userId);

        ticket.getSubscribers().remove(user);
        ticketRepository.save(ticket);
    }
}
