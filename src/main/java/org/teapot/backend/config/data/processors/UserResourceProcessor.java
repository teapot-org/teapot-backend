package org.teapot.backend.config.data.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;

import static java.lang.String.format;
import static org.teapot.backend.controller.organization.OrganizationSearchController.FIND_BY_USER_ID_ENDPOINT;

@Component
public class UserResourceProcessor implements ResourceProcessor<Resource<User>> {

    @Autowired
    private EntityLinks entityLinks;

    @Override
    public Resource<User> process(Resource<User> resource) {
        resource.add(new Link(format("%s%s?userId=%s",
                entityLinks.linkFor(Organization.class),
                FIND_BY_USER_ID_ENDPOINT,
                resource.getContent().getId()))
                .withRel("organizations"));
        return resource;
    }
}
