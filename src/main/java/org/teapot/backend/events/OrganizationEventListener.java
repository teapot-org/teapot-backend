package org.teapot.backend.events;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.util.SecurityUtils;

import java.util.Collections;

import static org.teapot.backend.model.organization.MemberStatus.CREATOR;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrganizationEventListener extends AbstractRepositoryEventListener<Organization> {

    @Override
    protected void onBeforeCreate(Organization organization) {
        User authenticatedUser = SecurityUtils.getAuthenticatedUser();
        if (authenticatedUser != null) {
            organization.setMembers(Collections.singleton(new Member(authenticatedUser, CREATOR, organization)));
        } else {
            organization.setMembers(Collections.emptySet());
        }
    }
}
