package org.teapot.backend.controller.organization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositorySearchesResource;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.organization.MemberRepository;

import java.util.Collections;

import static java.lang.String.format;
import static org.teapot.backend.controller.organization.OrganizationController.ORGANIZATIONS_ENDPOINT;

@BasePathAwareController
@RequestMapping(ORGANIZATIONS_ENDPOINT)
public class OrganizationSearchController implements ResourceProcessor<RepositorySearchesResource> {

    public static final String FIND_BY_USER_ID_ENDPOINT = "/search/find-by-user-id";

    @Autowired
    private EntityLinks entityLinks;

    @Autowired
    private PagedResourcesAssembler<Organization> pagedResourcesAssembler;

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping(FIND_BY_USER_ID_ENDPOINT)
    public ResponseEntity<PagedResources> findByUserId(
            @RequestParam Long userId,
            Pageable pageable,
            PersistentEntityResourceAssembler assembler
    ) {
        Page<Organization> page = memberRepository.findByUserId(userId, pageable).map(Member::getOrganization);
        PagedResources resources = pagedResourcesAssembler.toResource(page, assembler::toResource);

        if ((page.getContent() == null) || page.getContent().isEmpty()) {
            EmbeddedWrapper embeddedWrapper = new EmbeddedWrappers(false)
                    .emptyCollectionOf(Organization.class);

            resources = new PagedResources<>(
                    Collections.singletonList(embeddedWrapper),
                    resources.getMetadata(),
                    resources.getLinks()
            );
        }

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @Override
    public RepositorySearchesResource process(RepositorySearchesResource resource) {
        if (resource.getDomainType().equals(Organization.class)) {
            resource.add(new Link(format("%s%s%s",
                    entityLinks.linkFor(Organization.class),
                    FIND_BY_USER_ID_ENDPOINT,
                    "{?userId,page,size,sort}"))
                    .withRel("findByUserId"));
        }

        return resource;
    }
}
