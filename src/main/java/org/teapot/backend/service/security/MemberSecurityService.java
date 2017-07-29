package org.teapot.backend.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.repository.organization.MemberRepository;

@Service("members")
public class MemberSecurityService extends AbstractSecurityService<Member> {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationSecurityService organizationSecurityService;

    public boolean hasAnyStatus(Long memberId, String... statuses) {
        assertExists(memberId);
        return hasAnyStatus(memberRepository.findOne(memberId), statuses);
    }

    public boolean hasAnyStatus(Member member, String... statuses) {
        return organizationSecurityService.hasAnyStatus(member.getOrganization(), statuses);
    }

    public boolean memberHasStatus(Long memberId, String status) {
        assertExists(memberId);
        return memberHasStatus(memberRepository.findOne(memberId), status);
    }

    public boolean memberHasStatus(Member member, String status) {
        return member.getStatus() == MemberStatus.valueOf(status);
    }
}
