package org.teapot.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.exception.BadRequestException;
import org.teapot.backend.controller.exception.ResourceNotFoundException;
import org.teapot.backend.model.TeapotProperty;
import org.teapot.backend.repository.TeapotPropertyRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/props")
public class TeapotPropertyController {

    @Autowired
    private TeapotPropertyRepository propertyRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeapotProperty addProperty(@RequestBody TeapotProperty property,
                                      HttpServletResponse response) {

        if (propertyRepository.findByName(property.getName()) != null) {
            throw new BadRequestException();
        }

        property = propertyRepository.save(property);
        response.setHeader("Location", "/props/" + property.getId());

        return property;
    }

    @GetMapping("/{propertyId}")
    public TeapotProperty getPropertyById(@PathVariable Long propertyId) {
        TeapotProperty property = propertyRepository.findOne(propertyId);

        if (property == null) {
            throw new ResourceNotFoundException();
        }

        return property;
    }

    @GetMapping
    public List<TeapotProperty> getProperties(Pageable pageable) {
        return propertyRepository.findAll(pageable).getContent();
    }

    @PutMapping("/{propertyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable Long propertyId,
                           @RequestBody TeapotProperty property) {

        if (!propertyRepository.exists(propertyId)) {
            throw new ResourceNotFoundException();
        }

        property.setId(propertyId);
        propertyRepository.save(property);
    }

    @DeleteMapping("/{propertyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long propertyId) {
        if (!propertyRepository.exists(propertyId)) {
            throw new ResourceNotFoundException();
        }

        propertyRepository.delete(propertyId);
    }
}
