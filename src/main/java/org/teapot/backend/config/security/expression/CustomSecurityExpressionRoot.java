package org.teapot.backend.config.security.expression;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.teapot.backend.model.BaseEntity;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.Ticket;
import org.teapot.backend.model.kanban.TicketList;
import org.teapot.backend.model.meta.TeapotProperty;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.BaseEntityRepository;
import org.teapot.backend.repository.kanban.KanbanRepository;
import org.teapot.backend.repository.kanban.TicketListRepository;
import org.teapot.backend.repository.kanban.TicketRepository;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;
import org.teapot.backend.repository.user.UserRepository;

import java.util.HashMap;
import java.util.Map;

import static org.teapot.backend.model.kanban.KanbanAccess.PUBLIC;
import static org.teapot.backend.model.organization.MemberStatus.CREATOR;
import static org.teapot.backend.model.organization.MemberStatus.OWNER;
import static org.teapot.backend.util.SecurityUtils.getAuthenticatedUser;

@SuppressWarnings("WeakerAccess")
public class CustomSecurityExpressionRoot extends SecurityExpressionRoot
        implements MethodSecurityExpressionOperations {

    private final Map<String, BaseEntityRepository<? extends BaseEntity>> repositories;

    private @Getter @Setter Object filterObject;
    private @Getter @Setter Object returnObject;
    private Object target;

    public CustomSecurityExpressionRoot(Authentication authentication,
                                        UserRepository userRepository,
                                        OrganizationRepository organizationRepository,
                                        MemberRepository memberRepository,
                                        KanbanRepository kanbanRepository,
                                        TicketListRepository ticketListRepository,
                                        TicketRepository ticketRepository) {
        super(authentication);
        repositories = new HashMap<>();
        repositories.put(User.class.getSimpleName(), userRepository);
        repositories.put(Organization.class.getSimpleName(), organizationRepository);
        repositories.put(Member.class.getSimpleName(), memberRepository);
        repositories.put(Kanban.class.getSimpleName(), kanbanRepository);
        repositories.put(TicketList.class.getSimpleName(), ticketListRepository);
        repositories.put(Ticket.class.getSimpleName(), ticketRepository);
    }

    public boolean canRead(Long resourceId, String resourceType) {
        boolean result = false;
        BaseEntity resource = repositories.get(resourceType).findOne(resourceId);

        if (resource != null) {
            if (Kanban.class.isInstance(resource)) {
                result = canRead((Kanban) resource);
            } else if (TicketList.class.isInstance(resource)) {
                result = canRead((TicketList) resource);
            } else if (Ticket.class.isInstance(resource)) {
                result = canRead((Ticket) resource);
            }
        }
        return result;
    }

    public boolean canRead(Kanban resource) {
        User authenticatedUser = getAuthenticatedUser();
        boolean result = PUBLIC.equals(resource.getAccess());
        if (!result) {
            result = isAuthenticated() && (hasRole("ADMIN")
                    || isOwner(resource, authenticatedUser)
                    || isContributor(resource, authenticatedUser));
        }
        return result;
    }

    public boolean canRead(TicketList resource) {
        return canRead(resource.getKanban());
    }

    public boolean canRead(Ticket resource) {
        return canRead(resource.getTicketList());
    }

    public boolean canDelete(Long resourceId, String resourceType) {
        boolean result = false;
        BaseEntity resource = repositories.get(resourceType).findOne(resourceId);

        if (resource != null) {
            if (User.class.isInstance(resource)) {
                result = canDelete((User) resource);
            } else if (Organization.class.isInstance(resource)) {
                result = canDelete((Organization) resource);
            } else if (Member.class.isInstance(resource)) {
                result = canDelete((Member) resource);
            } else if (Kanban.class.isInstance(resource)) {
                result = canDelete((Kanban) resource);
            } else if (TicketList.class.isInstance(resource)) {
                result = canDelete((TicketList) resource);
            } else if (Ticket.class.isInstance(resource)) {
                result = canDelete((Ticket) resource);
            }
        }
        return result;
    }

    public boolean canDelete(User resource) {
        return hasRole("ADMIN");
    }

    public boolean canDelete(Organization resource) {
        boolean result = false;
        if (isAuthenticated()) {
            result = hasRole("ADMIN");
            if (!result) {
                MemberRepository memberRepository = (MemberRepository) repositories.get(Member.class.getSimpleName());
                Member member = memberRepository.findByOrganizationAndUser(resource, getAuthenticatedUser());
                result = (member != null) && CREATOR.equals(member.getStatus());
            }
        }
        return result;
    }

    public boolean canDelete(Member resource) {
        boolean result = false;
        User authenticatedUser = getAuthenticatedUser();
        if ((authenticatedUser != null) && !hasRole("ADMIN")) {
            boolean memberIsCreator = CREATOR.equals(resource.getStatus());
            boolean authenticatedAsOwner = isOwner(resource.getOrganization(), authenticatedUser);
            boolean memberIsAuthenticatedUser = authenticatedUser.equals(resource.getUser());
            result = !memberIsCreator && (authenticatedAsOwner || memberIsAuthenticatedUser);
        }
        return result;
    }

    public boolean canDelete(Kanban resource) {
        boolean result = false;
        if (isAuthenticated()) {
            result = isOwner(resource, getAuthenticatedUser()) || hasRole("ADMIN");
        }
        return result;
    }

    public boolean canDelete(TicketList resource) {
        boolean result = false;
        User authenticatedUser = getAuthenticatedUser();
        if (isAuthenticated()) {
            result = hasRole("ADMIN")
                    || isOwner(resource, authenticatedUser)
                    || isContributor(resource, authenticatedUser);
        }
        return result;
    }

    public boolean canDelete(Ticket resource) {
        return canDelete(resource.getTicketList());
    }

    public boolean canEdit(Long resourceId, String resourceType) {
        boolean result = false;
        BaseEntity resource = repositories.get(resourceType).findOne(resourceId);

        if (resource != null) {
            if (User.class.isInstance(resource)) {
                result = canEdit((User) resource);
            } else if (Organization.class.isInstance(resource)) {
                result = canEdit((Organization) resource);
            } else if (Member.class.isInstance(resource)) {
                result = canEdit((Member) resource);
            } else if (Kanban.class.isInstance(resource)) {
                result = canEdit((Kanban) resource);
            } else if (TicketList.class.isInstance(resource)) {
                result = canEdit((TicketList) resource);
            } else if (Ticket.class.isInstance(resource)) {
                result = canEdit((Ticket) resource);
            }
        }
        return result;
    }

    public boolean canEdit(User resource) {
        boolean result = false;
        if (isAuthenticated()) {
            result = hasRole("ADMIN")
                    || resource.equals(getAuthenticatedUser());
        }
        return result;
    }

    public boolean canEdit(Organization resource) {
        boolean result = false;
        if (isAuthenticated()) {
            result = hasRole("ADMIN")
                    || isOwner(resource, getAuthenticatedUser());
        }
        return result;
    }

    public boolean canEdit(Member resource) {
        boolean result = false;
        if (isAuthenticated()) {
            result = hasRole("ADMIN")
                    || isOwner(resource.getOrganization(), getAuthenticatedUser())
                    && !isOwner(resource.getOrganization(), resource.getUser());
        }
        return result;
    }

    public boolean canEdit(Kanban resource) {
        boolean result = false;
        if (isAuthenticated()) {
            result = hasRole("ADMIN") || isOwner(resource, getAuthenticatedUser());
        }
        return result;
    }

    public boolean canEdit(TicketList resource) {
        boolean result = false;
        if (isAuthenticated()) {
            result = hasRole("ADMIN")
                    || isOwner(resource, getAuthenticatedUser())
                    || isContributor(resource, getAuthenticatedUser());
        }
        return result;
    }

    public boolean canEdit(Ticket resource) {
        boolean result = false;
        if (isAuthenticated()) {
            result = hasRole("ADMIN")
                    || isOwner(resource, getAuthenticatedUser())
                    || isContributor(resource, getAuthenticatedUser());
        }
        return result;
    }

    public boolean canSubscribe(Long ticketId, Long userId) {
        Ticket ticket = (Ticket) repositories.get(Ticket.class.getSimpleName()).findOne(ticketId);
        User user = (User) repositories.get(User.class.getSimpleName()).findOne(userId);
        boolean result = false;
        if (isAuthenticated()) {
            result = isContributor(ticket, user)
                    && (hasRole("ADMIN") || user.equals(getAuthenticatedUser()) || isOwner(ticket, getAuthenticatedUser()));

        }
        return result;
    }

    public boolean canUnsubscribe(Long ticketId, Long userId) {
        Ticket ticket = (Ticket) repositories.get(Ticket.class.getSimpleName()).findOne(ticketId);
        User user = (User) repositories.get(User.class.getSimpleName()).findOne(userId);
        boolean result = false;
        if (isAuthenticated()) {
            boolean sameUser = user.equals(getAuthenticatedUser());
            boolean isOwner = isOwner(ticket, getAuthenticatedUser());
            result = isSubscriber(ticket, user) && (hasRole("ADMIN") || sameUser || isOwner);

        }
        return result;
    }

    public boolean canCreate(User resource) {
        return isAnonymous() || hasRole("ADMIN");
    }

    public boolean canCreate(Organization resource) {
        return isAuthenticated();
    }

    public boolean canCreate(Member resource) {
        boolean result = false;
        if (isAuthenticated()) {
            MemberRepository memberRepository = (MemberRepository) repositories.get(Member.class.getSimpleName());
            boolean alreadyIsMember = memberRepository.findByOrganizationAndUser(
                    resource.getOrganization(), resource.getUser()) != null;
            boolean loggedAsOwner = isOwner(resource.getOrganization(), getAuthenticatedUser());
            result = !alreadyIsMember &&
                    (hasRole("ADMIN") || loggedAsOwner || resource.getUser().equals(getAuthenticatedUser()));
        }
        return result;
    }

    public boolean canCreate(Kanban resource) {
        boolean result = false;
        if (isAuthenticated()) {
            result = hasRole("ADMIN") || isOwner(resource, getAuthenticatedUser());
        }
        return result;
    }

    public boolean canCreate(TicketList resource) {
        boolean result = false;
        User authenticatedUser = getAuthenticatedUser();
        if (isAuthenticated()) {
            result = hasRole("ADMIN")
                    || isOwner(resource, authenticatedUser)
                    || isContributor(resource, authenticatedUser);
        }
        return result;
    }

    public boolean canCreate(Ticket resource) {
        return canCreate(resource.getTicketList());
    }

    public boolean canCreate(TeapotProperty resource) {
        return true;
    }

    public boolean isOwner(@NonNull Organization resource, @NonNull User user) {
        MemberRepository memberRepository = (MemberRepository) repositories.get(Member.class.getSimpleName());
        Member member = memberRepository.findByOrganizationAndUser(resource, user);
        return (member != null) && (CREATOR.equals(member.getStatus()) || OWNER.equals(member.getStatus()));
    }

    public boolean isOwner(@NonNull Kanban resource, @NonNull User user) {
        boolean result = false;
        Owner owner = resource.getOwner();
        if (User.class.isInstance(owner)) {
            result = user.equals(owner);
        } else if (Organization.class.isInstance(owner)) {
            result = isOwner((Organization) owner, user);
        }
        return result;
    }

    public boolean isOwner(@NonNull TicketList resource, @NonNull User user) {
        return isOwner(resource.getKanban(), user);
    }

    public boolean isOwner(@NonNull Ticket resource, @NonNull User user) {
        return isOwner(resource.getTicketList(), user);
    }

    public boolean isContributor(@NonNull Kanban resource, @NonNull User user) {
        return resource.getContributors().contains(user);
    }

    public boolean isContributor(@NonNull TicketList resource, @NonNull User user) {
        return isContributor(resource.getKanban(), user);
    }

    public boolean isContributor(@NonNull Ticket resource, @NonNull User user) {
        return isContributor(resource.getTicketList(), user);
    }

    public boolean isSubscriber(@NonNull Ticket resource, @NonNull User user) {
        return resource.getSubscribers().contains(user);
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    void setThis(Object target) {
        this.target = target;
    }
}
