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
