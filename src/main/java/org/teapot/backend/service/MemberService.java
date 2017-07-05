package org.teapot.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.repository.organization.MemberRepository;

import java.util.Arrays;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public boolean hasAnyMemberStatus(Long organizationId, String userEmail, String... statuses) {
        Member member = memberRepository.findByOrganizationIdAndUserEmail(organizationId, userEmail);
        return (member != null)
                && Arrays.stream(statuses)
                .map(MemberStatus::valueOf)
                .anyMatch(status -> member.getStatus().equals(status));
    }

    public boolean isCreator(Long organizationId, String userEmail) {
        return hasAnyMemberStatus(organizationId, userEmail, MemberStatus.CREATOR.toString());
    }

    public boolean isOwner(Long organizationId, String userEmail) {
        return hasAnyMemberStatus(organizationId, userEmail, MemberStatus.OWNER.toString());
    }

    public boolean isCreatorOrOwner(Long organizationId, String userEmail) {
        return hasAnyMemberStatus(organizationId, userEmail,
                MemberStatus.CREATOR.toString(), MemberStatus.OWNER.toString());
    }

    public boolean isWorker(Long organizationId, String userEmail) {
        return hasAnyMemberStatus(organizationId, userEmail, MemberStatus.WORKER.toString());
    }

    public boolean isMember(Long organizationId, String userEmail) {
        return memberRepository.findByOrganizationIdAndUserEmail(organizationId, userEmail) != null;
    }
}
