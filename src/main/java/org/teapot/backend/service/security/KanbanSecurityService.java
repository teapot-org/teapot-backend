package org.teapot.backend.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Service;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.KanbanAccess;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.kanban.KanbanRepository;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.repository.user.UserRepository;

@Service("kanbans")
public class KanbanSecurityService extends AbstractSecurityService<Kanban> {

    @Autowired
    private KanbanRepository kanbanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    public boolean isContributor(Long kanbanId) {
        assertExists(kanbanId);
        return isContributor(kanbanRepository.findOne(kanbanId));
    }

    public boolean isContributor(Resource<Kanban> resource) {
        return isContributor(resource.getContent());
    }

    public boolean isContributor(Kanban kanban) {
        User user = userRepository.findByEmail(getLoggedUserEmail());
        return (kanban != null) && (user != null) && kanban.getContributors().contains(user);
    }

    public boolean isOwner(Long kanbanId) {
        assertExists(kanbanId);
        return isOwner(kanbanRepository.findOne(kanbanId));
    }

    public boolean isOwner(Resource<Kanban> resource) {
        return isOwner(resource.getContent());
    }

    public boolean isOwner(Kanban kanban) {
        Owner owner = kanban.getOwner();
        if (owner == null) {
            return false;
        }

        User user = userRepository.findByEmail(getLoggedUserEmail());
        if (user == null) {
            return false;
        }

        boolean returnValue = false;

        if (owner instanceof User) {
            returnValue = user.equals(owner);
        } else if (owner instanceof Organization) {
            returnValue = memberRepository.findByOrganization((Organization) owner).stream().anyMatch(member -> {
                boolean matchingUser = member.getUser().equals(user);
                boolean isCreator = member.getStatus() == MemberStatus.CREATOR;
                boolean isOwner = member.getStatus() == MemberStatus.OWNER;
                return matchingUser && (isCreator || isOwner);
            });
        }

        return returnValue;
    }

    public boolean isPublic(Long kanbanId) {
        assertExists(kanbanId);
        return isPublic(kanbanRepository.findOne(kanbanId));
    }

    public boolean isPublic(Resource<Kanban> resource) {
        return isPublic(resource.getContent());
    }

    public boolean isPublic(Kanban kanban) {
        return (kanban != null) && (kanban.getAccess() == KanbanAccess.PUBLIC);
    }
}
