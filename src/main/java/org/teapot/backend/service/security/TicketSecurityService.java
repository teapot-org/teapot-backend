package org.teapot.backend.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Service;
import org.teapot.backend.model.kanban.Ticket;
import org.teapot.backend.model.kanban.TicketList;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.kanban.TicketListRepository;
import org.teapot.backend.repository.kanban.TicketRepository;
import org.teapot.backend.repository.user.UserRepository;

@Service("tickets")
public class TicketSecurityService extends AbstractSecurityService<Ticket> {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketListRepository ticketListRepository;

    @Autowired
    private TicketListSecurityService ticketListSecurityService;

    public boolean isContributor(Long ticketId) {
        assertExists(ticketId);
        return isContributor(ticketRepository.findOne(ticketId));
    }

    public boolean isContributor(Resource<Ticket> resource) {
        return isContributor(resource.getContent());
    }

    public boolean isContributor(Ticket ticket) {
        return ticketListSecurityService.isContributor(ticket.getTicketList());
    }

    public boolean isContributor(Long ticketId, Long userId) {
        assertExists(ticketId);
        Ticket ticket = ticketRepository.findOne(ticketId);

        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new ResourceNotFoundException();
        }

        return ticket.getTicketList().getKanban().getContributors().contains(user);
    }

    public boolean isOwner(Long ticketId) {
        assertExists(ticketId);
        return isOwner(ticketRepository.findOne(ticketId));
    }

    public boolean isOwner(Resource<Ticket> resource) {
        return isOwner(resource.getContent());
    }

    public boolean isOwner(Ticket ticket) {
        return ticketListSecurityService.isOwner(ticket.getTicketList());
    }

    public boolean isSubscriber(Long ticketId, Long userId) {
        assertExists(ticketId);

        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new ResourceNotFoundException();
        }

        Ticket ticket = ticketRepository.findOne(ticketId);

        return ticket.getSubscribers().contains(user);
    }

    public boolean inSameKanban(Long ticketId, Long ticketListId) {
        assertExists(ticketId);
        ticketListSecurityService.assertExists(ticketListId);
        Ticket ticket = ticketRepository.findOne(ticketId);
        TicketList ticketList = ticketListRepository.findOne(ticketListId);

        return ticketList.getKanban().equals(
                ticket.getTicketList().getKanban());
    }
}
