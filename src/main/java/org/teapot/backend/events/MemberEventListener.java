package org.teapot.backend.events;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.organization.MemberRepository;

import static org.teapot.backend.model.organization.MemberStatus.CREATOR;
import static org.teapot.backend.model.organization.MemberStatus.OWNER;
import static org.teapot.backend.model.user.UserAuthority.ADMIN;
import static org.teapot.backend.util.SecurityUtils.getAuthenticatedUser;

@Component
@RepositoryEventHandler
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MemberEventListener extends AbstractRepositoryEventListener<Member> {

    private final MemberRepository memberRepository;

    @Override
    @PreAuthorize("canCreate(#member)")
    public void onBeforeCreate(Member member) {
        User authenticatedUser = getAuthenticatedUser();
        Member loggedMember = memberRepository.findByOrganizationAndUser(member.getOrganization(), authenticatedUser);
        boolean loggedAsOwner = (loggedMember != null)
                && (CREATOR.equals(loggedMember.getStatus()) || OWNER.equals(loggedMember.getStatus()));

        // добавление участника в организацию напрямую
        if (ADMIN.equals(authenticatedUser.getAuthority())) {
            if (MemberStatus.CREATOR.equals(member.getStatus())) {
                // нельзя добавить нового создателя организации
                member.setStatus(MemberStatus.OWNER);
            }
        }
        // создание приглашения
        else if (loggedAsOwner) {
            member.setStatus(MemberStatus.INVITEE);
        }
        // создание заявки
        else if (authenticatedUser.equals(member.getUser())) {
            member.setStatus(MemberStatus.APPLICANT);
        }
    }

    @HandleBeforeSave
    @PreAuthorize("canEdit(#resource)")
    public void onBeforeEdit(Member resource) {
        // only security check
    }
}
