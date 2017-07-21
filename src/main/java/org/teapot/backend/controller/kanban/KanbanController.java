package org.teapot.backend.controller.kanban;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.*;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.AbstractController;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.KanbanAccess;
import org.teapot.backend.repository.OwnerRepository;
import org.teapot.backend.repository.kanban.KanbanRepository;
import org.teapot.backend.repository.user.UserRepository;
import org.teapot.backend.util.PagedResourcesAssemblerHelper;

import static org.teapot.backend.controller.OwnerController.SINGLE_OWNER_ENDPOINT;
import static org.teapot.backend.service.KanbanService.USER_IS_KANBAN_OWNER;
import static org.teapot.backend.service.KanbanService.USER_IS_KANBAN_OWNER_BY_RESOURCE;

@RepositoryRestController
public class KanbanController extends AbstractController {

    public static final String KANBANS_ENDPOINT = "/kanbans";
    public static final String SINGLE_KANBAN_ENDPOINT = KANBANS_ENDPOINT + "/{id}";

    @Autowired
    private PagedResourcesAssemblerHelper<Kanban> helper;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private KanbanRepository kanbanRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(SINGLE_OWNER_ENDPOINT + KANBANS_ENDPOINT)
    public ResponseEntity<?> getOwnerKanbans(
            @PathVariable Long id,
            Pageable pageable,
            PersistentEntityResourceAssembler assembler
    ) {
        if (!ownerRepository.exists(id)) {
            throw new ResourceNotFoundException();
        }

        Page<Kanban> page = kanbanRepository.findByOwnerId(id, pageable);
        return ResponseEntity.ok(helper.toResource(Kanban.class, page, assembler));
    }

    @PreAuthorize(USER_IS_KANBAN_OWNER_BY_RESOURCE)
    @PostMapping(KANBANS_ENDPOINT)
    public ResponseEntity<?> createKanban(
            @RequestBody Resource<Kanban> resource,
            PersistentEntityResourceAssembler assembler,
            Authentication auth
    ) {
        Kanban kanban = resource.getContent();
        kanban.getContributors().add(userRepository.findByEmail(auth.getName()));

        kanbanRepository.save(kanban);

        PersistentEntityResource responseResource = assembler.toResource(kanban);
        HttpHeaders headers = headersPreparer.prepareHeaders(responseResource);
        addLocationHeader(headers, assembler, kanban);

        return ControllerUtils.toResponseEntity(HttpStatus.CREATED, headers, responseResource);
    }

    @PreAuthorize(USER_IS_KANBAN_OWNER + " or hasRole('ADMIN')")
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
