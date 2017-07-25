package org.teapot.backend.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Service;
import org.teapot.backend.model.kanban.TicketList;
import org.teapot.backend.repository.kanban.TicketListRepository;

@Service("ticketLists")
public class TicketListSecurityService extends AbstractSecurityService<TicketList> {

    @Autowired
    private TicketListRepository ticketListRepository;

    @Autowired
    private KanbanSecurityService kanbanSecurityService;

    public boolean isOwner(Long ticketListId) {
        assertExists(ticketListId);
        return isOwner(ticketListRepository.findOne(ticketListId));
    }

    public boolean isOwner(Resource<TicketList> resource) {
        return isOwner(resource.getContent());
    }

    public boolean isOwner(TicketList ticketList) {
        return kanbanSecurityService.isOwner(ticketList.getKanban());
    }

    public boolean isContributor(Long ticketListId) {
        assertExists(ticketListId);
        return isContributor(ticketListRepository.findOne(ticketListId));
    }

    public boolean isContributor(Resource<TicketList> resource) {
        return isContributor(resource.getContent());
    }

    public boolean isContributor(TicketList ticketList) {
        return kanbanSecurityService.isContributor(ticketList.getKanban());
    }

}
