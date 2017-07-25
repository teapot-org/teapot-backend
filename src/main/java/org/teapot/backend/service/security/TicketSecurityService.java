package org.teapot.backend.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Service;
import org.teapot.backend.model.kanban.Ticket;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.kanban.TicketRepository;
import org.teapot.backend.repository.user.UserRepository;

@Service("tickets")
public class TicketSecurityService extends AbstractSecurityService<Ticket> {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

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

    public boolean isSubscriber(Long ticketId) {
        assertExists(ticketId);
        return isSubscriber(ticketRepository.findOne(ticketId));
    }

    public boolean isSubscriber(Resource<Ticket> resource) {
        return isSubscriber(resource.getContent());
    }

    public boolean isSubscriber(Ticket ticket) {
        User user = userRepository.findByEmail(getLoggedUserEmail());
        return ticket.getSubscribers().contains(user);
    }

    public boolean inSameList(Long kanbanId, Long ticketListId) {
        assertExists(kanbanId);
        ticketListSecurityService.assertExists(ticketListId);
        return ticketRepository.findOne(kanbanId).getTicketList().getId().equals(ticketListId);
    }
}
