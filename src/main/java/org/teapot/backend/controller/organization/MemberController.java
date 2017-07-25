package org.teapot.backend.controller.organization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ControllerUtils;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.AbstractController;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.repository.organization.MemberRepository;

@RepositoryRestController
public class MemberController extends AbstractController {

    public static final String MEMBERS_ENDPOINT = "/members";
    public static final String SINGLE_MEMBER_ENDPOINT = MEMBERS_ENDPOINT + "/{id}";

    @Autowired
    private MemberRepository memberRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(MEMBERS_ENDPOINT)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addMember(
            @RequestBody Resource<Member> requestResource,
            PersistentEntityResourceAssembler assembler
    ) {
        Member member = requestResource.getContent();

        if (memberRepository.findByOrganizationIdAndUserId(member.getOrganization().getId(),
                member.getUser().getId()) != null) {
            throw new DataIntegrityViolationException("Already exists");
        }

        if (member.getStatus().equals(MemberStatus.CREATOR)) {
            // нельзя добавить нового создателя организации
            member.setStatus(MemberStatus.OWNER);
        }
        memberRepository.save(member);

        PersistentEntityResource responseResource = assembler.toResource(member);
        HttpHeaders headers = headersPreparer.prepareHeaders(responseResource);
        addLocationHeader(headers, assembler, member);

        return ControllerUtils.toResponseEntity(HttpStatus.CREATED, headers, responseResource);
    }

    @PreAuthorize("@members.hasAnyStatus(#id, 'CREATOR', 'OWNER') or hasRole('ADMIN')")
    @PatchMapping(SINGLE_MEMBER_ENDPOINT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchMember(
            @PathVariable Long id,
            @RequestParam("status") MemberStatus newStatus
    ) {
        Member member = memberRepository.findOne(id);

        boolean memberIsCreator = member.getStatus().equals(MemberStatus.CREATOR);
        boolean newStatusIsCreator = newStatus.equals(MemberStatus.CREATOR);

        if (memberIsCreator || newStatusIsCreator) {
            throw new DataIntegrityViolationException("Not allowed for 'CREATOR' status");
        }

        member.setStatus(newStatus);
        memberRepository.save(member);
    }

    @PutMapping(SINGLE_MEMBER_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedPutMember() {
    }
}
