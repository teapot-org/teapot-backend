package org.teapot.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.exception.BadRequestException;
import org.teapot.backend.controller.exception.ResourceNotFoundException;
import org.teapot.backend.model.meta.TeapotResource;
import org.teapot.backend.repository.TeapotResourceRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/resources")
public class TeapotResourceController {

    @Autowired
    private TeapotResourceRepository resourceRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private TeapotResource addResource(@RequestBody TeapotResource resource,
                                       HttpServletResponse response) {

        if (resourceRepository.findByName(resource.getName()) != null) {
            throw new BadRequestException();
        }

        resource = resourceRepository.save(resource);
        response.setHeader("Location", "/resources/" + resource.getId());

        return resource;
    }

    @GetMapping("/{resourceNameOrId}")
    public TeapotResource getResource(@PathVariable String resourceNameOrId) {
        TeapotResource resource;

        Long id = Long.valueOf(resourceNameOrId);

        if (id != null) {
            resource = resourceRepository.findOne(id);
        } else {
            resource = resourceRepository.findByName(resourceNameOrId);
        }

        if (resource == null) {
            throw new ResourceNotFoundException();
        }

        return resource;
    }

    @GetMapping
    public List<TeapotResource> getResources(Pageable pageable) {
        return resourceRepository.findAll(pageable).getContent();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateResource(@PathVariable Long id,
                               @RequestBody TeapotResource resource) {

        TeapotResource original = resourceRepository.findOne(id);

        if (original == null) {
            throw new ResourceNotFoundException();
        }

        resource.setId(original.getId());

        resourceRepository.save(resource);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResource(@PathVariable Long id) {
        if (!resourceRepository.exists(id)) {
            throw new ResourceNotFoundException();
        }

        resourceRepository.delete(id);
    }
}
