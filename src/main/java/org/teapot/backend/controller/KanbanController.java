package org.teapot.backend.controller;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RepositoryRestController
public class KanbanController extends AbstractController {

    public static final String BOARDS_ENDPOINT = "/kanbans";
    public static final String SINGLE_BOARD_ENDPOINT = BOARDS_ENDPOINT + "/{id}";

    @PostMapping(BOARDS_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedPostBoard() {
    }

    @PutMapping(SINGLE_BOARD_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedPutBoard() {
    }

    @PatchMapping(SINGLE_BOARD_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedPatchBoard() {
    }
}
