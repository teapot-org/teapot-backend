package org.teapot.backend.controller.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.AbstractController;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;
import org.teapot.backend.util.PagedResourcesAssemblerHelper;

import static org.teapot.backend.controller.user.UserController.SINGLE_USER_ENDPOINT;

@RepositoryRestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrganizationController extends AbstractController {

    public static final String ORGANIZATIONS_ENDPOINT = "/organizations";
    public static final String SINGLE_ORGANIZATION_ENDPOINT = ORGANIZATIONS_ENDPOINT + "/{id:\\d+}";

    private final PagedResourcesAssemblerHelper<Organization> helper;
    private final OrganizationRepository organizationRepository;
    private final MemberRepository memberRepository;

    @GetMapping(SINGLE_USER_ENDPOINT + ORGANIZATIONS_ENDPOINT)
    public ResponseEntity<PagedResources> getUserOrganizations(
            @PathVariable Long id,
            Pageable pageable,
            PersistentEntityResourceAssembler assembler
    ) {
        Page<Organization> page = memberRepository.findByUserId(id, pageable).map(Member::getOrganization);
        return ResponseEntity.ok(helper.toResource(Organization.class, page, assembler));
    }

    @PreAuthorize("canEdit(#id, 'Organization')")
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
}
