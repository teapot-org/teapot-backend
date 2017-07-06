package org.teapot.backend.controller.meta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.AbstractController;
import org.teapot.backend.model.meta.TeapotProperty;
import org.teapot.backend.repository.meta.TeapotPropertyRepository;

@RepositoryRestController
public class TeapotPropertyController extends AbstractController {

    public static final String PROPERTIES_ENDPOINT = "/props";
    public static final String SINGLE_PROPERTY_ENDPOINT = PROPERTIES_ENDPOINT + "/{id}";

    @Autowired
    private TeapotPropertyRepository propertyRepository;

    @PutMapping(SINGLE_PROPERTY_ENDPOINT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProperty(
            @PathVariable Long id,
            @RequestBody Resource<TeapotProperty> requestResource
    ) {
        if (!propertyRepository.exists(id)) {
            throw new ResourceNotFoundException();
        }

        TeapotProperty property = requestResource.getContent();

        property.setId(id);
        propertyRepository.save(property);
    }

    @PatchMapping(SINGLE_PROPERTY_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedPutProperty() {
    }
}
