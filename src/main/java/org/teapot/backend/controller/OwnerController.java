package org.teapot.backend.controller;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RepositoryRestController
public class OwnerController {

    public static final String OWNERS_ENDPOINT = "/owners";
    public static final String SINGLE_OWNER_ENDPOINT = OWNERS_ENDPOINT + "/{id:\\d+}";

    @PostMapping(OWNERS_ENDPOINT)
    public ResponseEntity<?> notAllowedPost() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @PatchMapping(SINGLE_OWNER_ENDPOINT)
    public ResponseEntity<?> notAllowedPatch() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
