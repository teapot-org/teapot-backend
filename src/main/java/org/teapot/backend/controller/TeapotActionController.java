package org.teapot.backend.controller;

import com.google.common.primitives.Longs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.exception.ResourceNotFoundException;
import org.teapot.backend.model.meta.TeapotAction;
import org.teapot.backend.model.meta.TeapotResource;
import org.teapot.backend.repository.TeapotActionRepository;
import org.teapot.backend.repository.TeapotResourceRepository;

@RestController
@RequestMapping("/actions")
public class TeapotActionController {

    @Autowired
    private TeapotActionRepository actionRepository;

    @Autowired
    private TeapotResourceRepository resourceRepository;

    @GetMapping("/{nameOrId}")
    public TeapotAction getAction(@PathVariable String nameOrId) {
        TeapotAction action;

        Long id = Long.valueOf(nameOrId);

        if (id != null) {
            action = actionRepository.findOne(id);
        } else {
            action = actionRepository.findByName(nameOrId);
        }

        if (action == null) {
            throw new ResourceNotFoundException();
        }

        return action;
    }

    @GetMapping("/help")
    public ResponseEntity<?> help(
            @RequestParam(name = "resource") String resourceNameOrId
    ) {
        if (resourceNameOrId != null) {
            TeapotResource resource;

            Long id = Longs.tryParse(resourceNameOrId);

            if (id != null) {
                resource = resourceRepository.findOne(id);
            } else {
                resource = resourceRepository.findByName(resourceNameOrId);
            }

            if (resource == null) {
                throw new ResourceNotFoundException();
            }

            return new ResponseEntity<>(resource, HttpStatus.OK);
        }

        return new ResponseEntity(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/activate")
    public void activate(
            @RequestParam(name = "user") String usernameOrId,
            @RequestParam String token
    ) {
        // todo
    }
}
