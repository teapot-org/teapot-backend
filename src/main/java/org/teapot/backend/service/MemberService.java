package org.teapot.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.repository.organization.MemberRepository;

import java.util.Arrays;

@Service
public class MemberService {

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
    private MemberRepository memberRepository;

    public boolean hasUserAnyMemberStatus(Long organizationId, String userEmail, String... statuses) {
        Member member = memberRepository.findByOrganizationIdAndUserEmail(organizationId, userEmail);
        return (member != null)
                && Arrays.stream(statuses)
                .map(MemberStatus::valueOf)
                .anyMatch(status -> member.getStatus().equals(status));
    }

    public boolean isUserCreator(Long organizationId, String userEmail) {
        return hasUserAnyMemberStatus(organizationId, userEmail, MemberStatus.CREATOR.toString());
    }

    public boolean isUserOwner(Long organizationId, String userEmail) {
        return hasUserAnyMemberStatus(organizationId, userEmail, MemberStatus.OWNER.toString());
    }

    public boolean isUserCreatorOrOwner(Long organizationId, String userEmail) {
        return hasUserAnyMemberStatus(organizationId, userEmail,
                MemberStatus.CREATOR.toString(), MemberStatus.OWNER.toString());
    }

    public boolean isUserWorker(Long organizationId, String userEmail) {
        return hasUserAnyMemberStatus(organizationId, userEmail, MemberStatus.WORKER.toString());
    }

    public boolean isUserMember(Long organizationId, String userEmail) {
        return memberRepository.findByOrganizationIdAndUserEmail(organizationId, userEmail) != null;
    }

    public boolean isMemberCreator(Long memberId) {
        return memberRepository.findOne(memberId).getStatus() == MemberStatus.CREATOR;
    }
}
