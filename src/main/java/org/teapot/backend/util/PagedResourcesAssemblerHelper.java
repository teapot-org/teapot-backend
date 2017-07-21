package org.teapot.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PagedResourcesAssemblerHelper<T> {

    private final PagedResourcesAssembler<T> pagedResourcesAssembler;

    @SuppressWarnings("unchecked")
    @Autowired
    public PagedResourcesAssemblerHelper(PagedResourcesAssembler<T> pagedResourcesAssembler) {
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    public PagedResources toResource(Class<T> clazz, Page<T> page, PersistentEntityResourceAssembler assembler) {
        PagedResources resources = pagedResourcesAssembler.toResource(page, assembler::toResource);

        if ((page.getContent() == null) || page.getContent().isEmpty()) {
            EmbeddedWrapper embeddedWrapper = new EmbeddedWrappers(false)
                    .emptyCollectionOf(clazz);

            resources = new PagedResources<>(
                    Collections.singletonList(embeddedWrapper),
                    resources.getMetadata(),
                    resources.getLinks()
            );
        }

        return resources;
    }
}
