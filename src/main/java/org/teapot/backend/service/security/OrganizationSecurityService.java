package org.teapot.backend.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.organization.MemberRepository;

import java.util.Arrays;

@Service("organizations")
public class OrganizationSecurityService extends AbstractSecurityService<Organization> {

    @Autowired
    private MemberRepository memberRepository;

    public boolean hasStatus(Long organizationId, String status) {
        return hasAnyStatus(organizationId, status);
    }

    public boolean hasStatus(Organization organization, String status) {
        return hasAnyStatus(organization.getId(), status);
    }

    public boolean hasAnyStatus(Organization organization, String... statuses) {
        return hasAnyStatus(organization.getId(), statuses);
    }

    public boolean hasAnyStatus(Long organizationId, String... statuses) {
        assertExists(organizationId);
        Member member = memberRepository.findByOrganizationIdAndUserEmail(organizationId, getLoggedUserEmail());
        return (member != null)
                && Arrays.stream(statuses)
                .map(MemberStatus::valueOf)
                .anyMatch(status -> member.getStatus().equals(status));
    }

    public boolean isMember(Organization organization, User user) {
        if ((organization == null) || (user == null)) {
            throw new ResourceNotFoundException();
        }

        return memberRepository.findByOrganizationAndUser(organization, user) != null;
    }
}
