package org.teapot.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.Ticket;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.kanban.KanbanRepository;
import org.teapot.backend.repository.kanban.TicketRepository;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.repository.user.UserRepository;

@Service
public class KanbanService {

    public static final String USER_IS_KANBAN_OWNER =
            "@kanbanService.isUserOwner(#id, authentication?.name)";

    public static final String USER_IS_KANBAN_OWNER_BY_KANBAN =
            "@kanbanService.isUserOwner(#kanban?.id, authentication?.name)";

    public static final String USER_IS_KANBAN_OWNER_BY_RESOURCE =
            "@kanbanService.isUserOwner(#resource?.content?.owner, authentication?.name)";

    public static final String USER_IS_TICKET_LIST_CONTRIBUTOR =
            "@kanbanService.isUserContributor(@ticketListRepository.findOne(#id)?.kanban?.id, authentication?.name)";

    public static final String USER_IS_TICKET_LIST_CONTRIBUTOR_BY_LIST =
            "@kanbanService.isUserContributor(#ticketList?.kanban?.id, authentication?.name)";

    public static final String USER_IS_TICKET_LIST_CONTRIBUTOR_BY_RESOURCE =
            "@kanbanService.isUserContributor(#resource?.content?.kanban?.id, authentication?.name)";

    public static final String USER_IS_TICKET_CONTRIBUTOR =
            "@kanbanService.isUserContributor(@ticketRepository.findOne(#id)?.ticketList?.kanban?.id, authentication?.name)";

    public static final String USER_IS_TICKET_CONTRIBUTOR_BY_RESOURCE =
            "@kanbanService.isUserContributor(#resource?.content?.ticketList?.kanban?.id, authentication?.name)";

    public static final String USER_IS_TICKET_CONTRIBUTOR_BY_TICKET =
            "@kanbanService.isUserContributor(#ticket?.ticketList?.kanban?.id, authentication?.name)";

    public static final String TICKET_IN_SAME_LIST = "@ticketRepository.findOne(#id)?.ticketList?.id == #listId";

    public static final String SUBSCRIBER_IS_TICKET_CONTRIBUTOR =
            "@kanbanService.isUserContributor(@ticketRepository.findOne(#id).ticketList?.kanban?.id, #userId)";

    public static final String USER_IS_TICKET_OWNER =
            "@kanbanService.isUserOwner(@ticketRepository.findOne(#id)?.ticketList?.kanban?.owner, authentication?.name)";

    public static final String USER_IS_SUBSCRIBER =
            "@kanbanService.isUserSubscriber(#id, authentication?.name)";

    public static final String MAY_SUBSCRIBE =
            SUBSCRIBER_IS_TICKET_CONTRIBUTOR + " and (" + USER_IS_TICKET_OWNER + " or " + USER_IS_SUBSCRIBER + " or hasRole('ADMIN'))";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KanbanRepository kanbanRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private MemberRepository memberRepository;

    public boolean isUserContributor(Long kanbanId, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        Kanban kanban = kanbanRepository.findOne(kanbanId);

        return kanban.getContributors().contains(user);
    }

    public boolean isUserContributor(Long kanbanId, Long userID) {
        User user = userRepository.findOne(userID);
        Kanban kanban = kanbanRepository.findOne(kanbanId);

        return kanban.getContributors().contains(user);
    }

    public boolean isUserSubscriber(Long ticketId, String userEmail) {
        Ticket ticket = ticketRepository.findOne(ticketId);
        User user = userRepository.findByEmail(userEmail);

        return ticket.getSubscribers().contains(user);
    }

    public boolean isUserOwner(Owner kanbanOwner, String userEmail) {
        boolean returnValue = false;

        User user = userRepository.findByEmail(userEmail);

        if (kanbanOwner instanceof User) {
            returnValue = user.equals(kanbanOwner);
        } else if (kanbanOwner instanceof Organization) {
            returnValue = memberRepository.findByOrganization((Organization) kanbanOwner).stream().anyMatch(member -> {
                boolean matchingUser = member.getUser().equals(user);
                boolean isCreator = member.getStatus() == MemberStatus.CREATOR;
                boolean isOwner = member.getStatus() == MemberStatus.OWNER;
                return matchingUser && (isCreator || isOwner);
            });
        }

        return returnValue;
    }

    public boolean isUserOwner(Long kanbanId, String userEmail) {
        return isUserOwner(kanbanRepository.findOne(kanbanId).getOwner(), userEmail);
    }
}
