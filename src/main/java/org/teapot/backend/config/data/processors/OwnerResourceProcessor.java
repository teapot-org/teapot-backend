package org.teapot.backend.config.data.processors;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.teapot.backend.controller.kanban.KanbanController;
import org.teapot.backend.controller.kanban.ProjectController;
import org.teapot.backend.model.Owner;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class OwnerResourceProcessor implements ResourceProcessor<Resource<? extends Owner>> {

    @Override
    public Resource<? extends Owner> process(Resource<? extends Owner> resource) {
        resource.add(linkTo(methodOn(KanbanController.class)
                .getOwnerKanbans(resource.getContent().getId(), null, null, null))
                .withRel("kanbans"));

        resource.add(linkTo(methodOn(ProjectController.class)
                .getOwnerProjects(resource.getContent().getId(), null, null))
                .withRel("projects"));

        return resource;
    }
}
