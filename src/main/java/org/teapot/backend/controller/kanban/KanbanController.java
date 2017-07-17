package org.teapot.backend.controller.kanban;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.teapot.backend.controller.AbstractController;

@RepositoryRestController
public class KanbanController extends AbstractController {

    public static final String KANBANS_ENDPOINT = "/kanbans";
    public static final String SINGLE_KANBAN_ENDPOINT = KANBANS_ENDPOINT + "/{id}";

    @PostMapping(KANBANS_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedPostBoard() {
    }

    @PutMapping(SINGLE_KANBAN_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedPutBoard() {
    }

    @PatchMapping(SINGLE_KANBAN_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedPatchBoard() {
    }
}
