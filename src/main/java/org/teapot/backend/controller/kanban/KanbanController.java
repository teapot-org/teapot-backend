package org.teapot.backend.controller.kanban;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.*;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.AbstractController;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.KanbanAccess;
import org.teapot.backend.repository.kanban.KanbanRepository;

@RepositoryRestController
public class KanbanController extends AbstractController {

    public static final String KANBANS_ENDPOINT = "/kanbans";
    public static final String SINGLE_KANBAN_ENDPOINT = KANBANS_ENDPOINT + "/{id}";

    @Autowired
    private KanbanRepository kanbanRepository;

    @PreAuthorize("@kanbanService.isUserOwner(#requestResource?.content?.owner, authentication?.name)")
    @PostMapping(KANBANS_ENDPOINT)
    public ResponseEntity<?> createKanban(
            @RequestBody Resource<Kanban> requestResource,
            PersistentEntityResourceAssembler assembler
    ) {
        Kanban kanban = kanbanRepository.save(requestResource.getContent());

        PersistentEntityResource responseResource = assembler.toResource(kanban);
        HttpHeaders headers = headersPreparer.prepareHeaders(responseResource);
        addLocationHeader(headers, assembler, kanban);

        return ControllerUtils.toResponseEntity(HttpStatus.CREATED, headers, responseResource);
    }

    @PreAuthorize("hasRole('ADMIN') or @kanbanService.isUserOwner(#id, authentication?.name)")
    @PatchMapping(SINGLE_KANBAN_ENDPOINT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeKanbanAccess(
            @PathVariable Long id,
            @RequestParam KanbanAccess access
    ) {
        Kanban kanban = kanbanRepository.findOne(id);
        if (kanban == null) {
            throw new ResourceNotFoundException();
        }

        kanban.setAccess(access);

        kanbanRepository.save(kanban);
    }

    @PutMapping(SINGLE_KANBAN_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedPutKanban() {
    }
}
