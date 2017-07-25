package org.teapot.backend.controller.organization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.*;
import org.springframework.hateoas.PagedResources;
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
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;
import org.teapot.backend.repository.user.UserRepository;
import org.teapot.backend.util.PagedResourcesAssemblerHelper;

import static org.teapot.backend.controller.user.UserController.SINGLE_USER_ENDPOINT;

@RepositoryRestController
public class OrganizationController extends AbstractController {

    public static final String ORGANIZATIONS_ENDPOINT = "/organizations";
    public static final String SINGLE_ORGANIZATION_ENDPOINT = ORGANIZATIONS_ENDPOINT + "/{id}";

    @Autowired
    private PagedResourcesAssemblerHelper<Organization> helper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping(SINGLE_USER_ENDPOINT + ORGANIZATIONS_ENDPOINT)
    public ResponseEntity<PagedResources> getUserOrganizations(
            @PathVariable Long id,
            Pageable pageable,
            PersistentEntityResourceAssembler assembler
    ) {
        Page<Organization> page = memberRepository.findByUserId(id, pageable).map(Member::getOrganization);
        return ResponseEntity.ok(helper.toResource(Organization.class, page, assembler));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(ORGANIZATIONS_ENDPOINT)
    public ResponseEntity<?> createOrganization(
            @RequestBody Resource<Organization> requestResource,
            PersistentEntityResourceAssembler assembler,
            Authentication auth
    ) {
        Organization organization = requestResource.getContent();

        if (organizationRepository.findByName(organization.getName()) != null) {
            throw new DataIntegrityViolationException("Already exists");
        }

        organizationRepository.save(organization);

        Member creator = new Member();
        creator.setStatus(MemberStatus.CREATOR);
        creator.setUser(userRepository.findByEmail(auth.getName()));
        creator.setOrganization(organization);
        memberRepository.save(creator);

        PersistentEntityResource responseResource = assembler.toResource(organization);
        HttpHeaders headers = headersPreparer.prepareHeaders(responseResource);
        addLocationHeader(headers, assembler, organization);

        return ControllerUtils.toResponseEntity(HttpStatus.CREATED, headers, responseResource);
    }

    @PreAuthorize("@organizations.hasAnyStatus(#id, 'CREATOR', 'OWNER') or hasRole('ADMIN')")
    @PatchMapping(SINGLE_ORGANIZATION_ENDPOINT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchOrganization(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String fullName
    ) {
        Organization organization = organizationRepository.findOne(id);
        if (organization == null) {
            throw new ResourceNotFoundException();
        }

        if (name != null) organization.setName(name);
        if (fullName != null) organization.setFullName(fullName);

        organizationRepository.save(organization);
    }

    @PutMapping(SINGLE_ORGANIZATION_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedPutOrganization() {
    }
}
