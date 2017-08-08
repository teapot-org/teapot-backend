package org.teapot.backend.controller.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.teapot.backend.controller.AbstractController;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.repository.organization.MemberRepository;

@RepositoryRestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MemberController extends AbstractController {

    public static final String MEMBERS_ENDPOINT = "/members";
    public static final String SINGLE_MEMBER_ENDPOINT = MEMBERS_ENDPOINT + "/{id:\\d+}";

    private final MemberRepository memberRepository;

    @PreAuthorize("canEdit(#id, 'Member')")
    @PatchMapping(SINGLE_MEMBER_ENDPOINT)
    public ResponseEntity<?> patchMember(
            @PathVariable Long id,
            @RequestParam("status") MemberStatus newStatus
    ) {
        Member member = memberRepository.findOne(id);

        boolean memberIsCreator = member.getStatus().equals(MemberStatus.CREATOR);
        boolean newStatusIsCreator = newStatus.equals(MemberStatus.CREATOR);

        if (memberIsCreator || newStatusIsCreator) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        member.setStatus(newStatus);
        memberRepository.save(member);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
