package org.teapot.backend.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;

import java.util.Arrays;

@Service("organizations")
public class OrganizationSecurityService extends AbstractSecurityService<Organization> {

    public static final String USER_IS_CREATOR =
            "@memberService.isUserCreator(#id, authentication?.name)";

    public static final String USER_IS_CREATOR_BY_ORG =
            "@memberService.isUserCreator(#organization?.id, authentication?.name)";

    public static final String USER_IS_CREATOR_OR_OWNER_BY_MEMBER_ID =
            "@memberService.isUserCreatorOrOwner(@memberRepository.findOne(#id)?.organization?.id, authentication.name)";

    public static final String USER_IS_CREATOR_OR_OWNER_BY_MEMBER =
            "@memberService.isUserCreatorOrOwner(#member?.organization?.id, authentication.name)";

    public static final String MEMBER_IS_NOT_CREATOR_BY_ID =
            "!@memberService.isMemberCreator(#id)";

    public static final String MEMBER_IS_NOT_CREATOR =
            "!@memberService.isMemberCreator(#member?.id)";

    public static final String USER_IS_CREATOR_OR_OWNER =
            "@memberService.isUserCreatorOrOwner(#id, authentication?.name)";

    @Autowired
    private OrganizationRepository organizationRepository;

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
}
