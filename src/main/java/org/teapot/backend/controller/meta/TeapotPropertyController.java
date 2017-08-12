package org.teapot.backend.controller.meta;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.teapot.backend.controller.AbstractController;
import org.teapot.backend.model.meta.TeapotProperty;
import org.teapot.backend.repository.meta.TeapotPropertyRepository;

@RepositoryRestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TeapotPropertyController extends AbstractController {

    public static final String PROPERTIES_ENDPOINT = "/props";
    public static final String SINGLE_PROPERTY_ENDPOINT = PROPERTIES_ENDPOINT + "/{id:\\d+}";

    private final TeapotPropertyRepository propertyRepository;

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
}
