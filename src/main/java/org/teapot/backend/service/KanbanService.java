package org.teapot.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.kanban.KanbanRepository;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.repository.user.UserRepository;

@Service
public class KanbanService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KanbanRepository kanbanRepository;

    @Autowired
    private MemberRepository memberRepository;

    public boolean isUserContributor(Long kanbanId, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        Kanban kanban = kanbanRepository.findOne(kanbanId);

        return kanban.getContributors().contains(user);
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
