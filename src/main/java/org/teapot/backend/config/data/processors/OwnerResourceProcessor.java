package org.teapot.backend.config.data.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.Project;

@Component
public class OwnerResourceProcessor implements ResourceProcessor<Resource<? extends Owner>> {

    @Autowired
    private RepositoryEntityLinks entityLinks;

    @Override
    public Resource<? extends Owner> process(Resource<? extends Owner> resource) {
        resource.add(entityLinks.linksToSearchResources(Kanban.class).getLink("findByOwnerId")
                .expand(resource.getContent().getId())
                .withRel("kanbans"));

        resource.add(entityLinks.linksToSearchResources(Project.class).getLink("findByOwnerId")
                .expand(resource.getContent().getId())
                .withRel("projects"));

        return resource;
    }
}
