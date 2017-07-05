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
        return member != null
                && Arrays.stream(statuses)
                .map(MemberStatus::valueOf)
                .anyMatch(status -> member.getStatus().equals(status));
    }

    public boolean isCreator(Long organizationId, String userEmail) {
        Member member = memberRepository.findByOrganizationIdAndUserEmail(organizationId, userEmail);
        return (member != null) && (member.getStatus().equals(MemberStatus.CREATOR));
    }

    public boolean isOwner(Long organizationId, String userEmail) {
        Member member = memberRepository.findByOrganizationIdAndUserEmail(organizationId, userEmail);
        return (member != null) && (member.getStatus().equals(MemberStatus.OWNER));
    }

    public boolean isCreatorOrOwner(Long organizationId, String userEmail) {
        Member member = memberRepository.findByOrganizationIdAndUserEmail(organizationId, userEmail);
        return (member != null)
                && (member.getStatus().equals(MemberStatus.CREATOR)
                || member.getStatus().equals(MemberStatus.OWNER));
    }

    public boolean isWorker(Long organizationId, String userEmail) {
        Member member = memberRepository.findByOrganizationIdAndUserEmail(organizationId, userEmail);
        return (member != null) && member.getStatus().equals(MemberStatus.WORKER);
    }

    public boolean isMember(Long organizationId, String userEmail) {
        return memberRepository.findByOrganizationIdAndUserEmail(organizationId, userEmail) != null;
    }
}
