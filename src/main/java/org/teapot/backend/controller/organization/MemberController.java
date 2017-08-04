package org.teapot.backend.controller.organization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ControllerUtils;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.AbstractController;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.model.user.UserAuthority;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.service.security.OrganizationSecurityService;
import org.teapot.backend.service.security.UserSecurityService;

@RepositoryRestController
public class MemberController extends AbstractController {

    public static final String MEMBERS_ENDPOINT = "/members";
    public static final String SINGLE_MEMBER_ENDPOINT = MEMBERS_ENDPOINT + "/{id:\\d+}";

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationSecurityService organizationSecurityService;

    @Autowired
    private UserSecurityService userSecurityService;

    @PreAuthorize("!@organizations.isMember(#res?.content?.organization, #res?.content?.user) and isAuthenticated()")
    @PostMapping(MEMBERS_ENDPOINT)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addMember(
            @RequestBody Resource<Member> res,
            PersistentEntityResourceAssembler assembler,
            Authentication auth
    ) {
        Member member = res.getContent();

        // добавление участника в организацию напрямую
        if (auth.getAuthorities().contains(UserAuthority.ADMIN)) {
            if (member.getStatus().equals(MemberStatus.CREATOR)) {
                // нельзя добавить нового создателя организации
                member.setStatus(MemberStatus.OWNER);
            }
        }
        // создание приглашения
        else if (organizationSecurityService.hasAnyStatus(member.getOrganization(), "CREATOR", "OWNER")) {
            member.setStatus(MemberStatus.INVITEE);
        }
        // создание заявки
        else if (userSecurityService.isLoggedUser(member.getUser())) {
            member.setStatus(MemberStatus.APPLICANT);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        memberRepository.save(member);

        PersistentEntityResource responseResource = assembler.toResource(member);
        HttpHeaders headers = headersPreparer.prepareHeaders(responseResource);
        addLocationHeader(headers, assembler, member);

        return ControllerUtils.toResponseEntity(HttpStatus.CREATED, headers, responseResource);
    }

    @PreAuthorize("@members.hasAnyStatus(#id, 'CREATOR', 'OWNER') or hasRole('ADMIN')")
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

    @PutMapping(SINGLE_MEMBER_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedPutMember() {
    }
}
