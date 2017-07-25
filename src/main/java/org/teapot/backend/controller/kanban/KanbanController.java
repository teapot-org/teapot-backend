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
import org.teapot.backend.model.user.User;
import org.teapot.backend.model.user.UserAuthority;
import org.teapot.backend.repository.OwnerRepository;
import org.teapot.backend.repository.kanban.KanbanRepository;
import org.teapot.backend.repository.user.UserRepository;
import org.teapot.backend.util.PagedResourcesAssemblerHelper;

import java.util.Objects;

import static org.teapot.backend.controller.OwnerController.SINGLE_OWNER_ENDPOINT;

@RepositoryRestController
public class KanbanController extends AbstractController {

    public static final String KANBANS_ENDPOINT = "/kanbans";
    public static final String SINGLE_KANBAN_ENDPOINT = KANBANS_ENDPOINT + "/{id}";
    public static final String SINGLE_OWNER_KANBANS = SINGLE_OWNER_ENDPOINT + KANBANS_ENDPOINT;

    @Autowired
    private PagedResourcesAssemblerHelper<Kanban> helper;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private KanbanRepository kanbanRepository;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("@kanbans.isPublic(#id) or @kanbans.isOwner(#id) or @kanbans.isContributor(#id) or hasRole('ADMIN')")
    @GetMapping(SINGLE_KANBAN_ENDPOINT)
    public ResponseEntity<?> getKanban(
            @PathVariable Long id,
            PersistentEntityResourceAssembler assembler
    ) {
        Kanban kanban = kanbanRepository.findOne(id);

        PersistentEntityResource responseResource = assembler.toResource(kanban);
        HttpHeaders headers = headersPreparer.prepareHeaders(responseResource);

        return ControllerUtils.toResponseEntity(HttpStatus.OK, headers, responseResource);
    }

    @GetMapping(KANBANS_ENDPOINT)
    public ResponseEntity<?> getKanbans(
            Pageable pageable,
            PersistentEntityResourceAssembler assembler,
            Authentication auth
    ) {
        User user = (auth != null) ? userRepository.findByEmail(auth.getName()) : null;
        boolean userIsAdmin = (user != null) && user.getAuthority() == UserAuthority.ADMIN;

        Page<Kanban> page;
        if (userIsAdmin) {
            page = kanbanRepository.findAll(pageable);
        } else {
            page = kanbanRepository.findByAccess(KanbanAccess.PUBLIC, pageable);
        }

        return ResponseEntity.ok(helper.toResource(Kanban.class, page, assembler));
    }

    @GetMapping(SINGLE_OWNER_KANBANS)
    public ResponseEntity<?> getOwnerKanbans(
            @PathVariable Long id,
            Pageable pageable,
            PersistentEntityResourceAssembler assembler,
            Authentication auth
    ) {
        if (!ownerRepository.exists(id)) {
            throw new ResourceNotFoundException();
        }

        boolean userIsAdmin = false;
        boolean userIsOwner = false;

        User user = (auth != null) ? userRepository.findByEmail(auth.getName()) : null;
        if (user != null) {
            userIsAdmin = user.getAuthority() == UserAuthority.ADMIN;
            userIsOwner = Objects.equals(user.getId(), id);
        }

        Page<Kanban> page;
        if (userIsAdmin || userIsOwner) {
            page = kanbanRepository.findByOwnerId(id, pageable);
        } else {
            page = kanbanRepository.findByOwnerIdAndAccess(id, KanbanAccess.PUBLIC, pageable);
        }

        return ResponseEntity.ok(helper.toResource(Kanban.class, page, assembler));
    }

    @PreAuthorize("@kanbans.isOwner(#resource)")
    @PostMapping(KANBANS_ENDPOINT)
    public ResponseEntity<?> createKanban(
            @RequestBody Resource<Kanban> resource,
            PersistentEntityResourceAssembler assembler
    ) {
        Kanban kanban = resource.getContent();

        kanbanRepository.save(kanban);

        PersistentEntityResource responseResource = assembler.toResource(kanban);
        HttpHeaders headers = headersPreparer.prepareHeaders(responseResource);
        addLocationHeader(headers, assembler, kanban);

        return ControllerUtils.toResponseEntity(HttpStatus.CREATED, headers, responseResource);
    }

    @PreAuthorize("@kanbans.isOwner(#id) or hasRole('ADMIN')")
    @PatchMapping(SINGLE_KANBAN_ENDPOINT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeKanbanAccess(
            @PathVariable Long id,
            @RequestParam KanbanAccess access
    ) {
        Kanban kanban = kanbanRepository.findOne(id);
        kanban.setAccess(access);
        kanbanRepository.save(kanban);
    }

    @PutMapping(SINGLE_KANBAN_ENDPOINT)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void notAllowedPutKanban() {
    }
}
