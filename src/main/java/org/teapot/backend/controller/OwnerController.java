package org.teapot.backend.controller;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RepositoryRestController
public class OwnerController extends AbstractController {

    public static final String OWNERS_ENDPOINT = "/owners";
    public static final String SINGLE_OWNER_ENDPOINT = OWNERS_ENDPOINT + "/{id}";

    @PostMapping(OWNERS_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedPostOwner() {
    }

    @PutMapping(SINGLE_OWNER_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedPutOwner() {
    }

    @PatchMapping(SINGLE_OWNER_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedPatchOwner() {
    }

    @DeleteMapping(SINGLE_OWNER_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedDeleteOwner() {
    }
}
