package org.teapot.backend.config.data.processors;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.teapot.backend.controller.organization.OrganizationSearchController;
import org.teapot.backend.model.user.User;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class UserResourceProcessor implements ResourceProcessor<Resource<User>> {

    @Override
    public Resource<User> process(Resource<User> resource) {
        resource.add(linkTo(methodOn(OrganizationSearchController.class)
                .findByUserId(resource.getContent().getId(), null, null))
                .withRel("organizations"));

        return resource;
    }
}
