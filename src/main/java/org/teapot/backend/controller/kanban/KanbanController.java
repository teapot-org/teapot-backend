package org.teapot.backend.controller.kanban;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.AbstractController;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.KanbanAccess;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.OwnerRepository;
import org.teapot.backend.repository.kanban.KanbanRepository;
import org.teapot.backend.util.PagedResourcesAssemblerHelper;

import static org.teapot.backend.controller.OwnerController.SINGLE_OWNER_ENDPOINT;
import static org.teapot.backend.model.user.UserAuthority.ADMIN;
import static org.teapot.backend.util.SecurityUtils.getAuthenticatedUser;

@RepositoryRestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KanbanController extends AbstractController {

    public static final String KANBANS_ENDPOINT = "/kanbans";
    public static final String SINGLE_KANBAN_ENDPOINT = KANBANS_ENDPOINT + "/{id:\\d+}";
    public static final String SINGLE_OWNER_KANBANS = SINGLE_OWNER_ENDPOINT + KANBANS_ENDPOINT;

    private final PagedResourcesAssemblerHelper<Kanban> helper;
    private final OwnerRepository ownerRepository;
    private final KanbanRepository kanbanRepository;

    @GetMapping(SINGLE_OWNER_KANBANS)
    public ResponseEntity<?> getOwnerKanbans(
            @PathVariable Long id,
            Pageable pageable,
            PersistentEntityResourceAssembler assembler
    ) {
        if (!ownerRepository.exists(id)) {
            throw new ResourceNotFoundException();
        }

        boolean userIsAdmin = false;
        boolean userIsOwner = false;

        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser != null) {
            userIsAdmin = ADMIN.equals(authenticatedUser.getAuthority());
            userIsOwner = id.equals(authenticatedUser.getId());
        }

        Page<Kanban> page;
        if (userIsAdmin || userIsOwner) {
            page = kanbanRepository.findByOwnerId(id, pageable);
        } else {
            page = kanbanRepository.findByOwnerIdAndAccess(id, KanbanAccess.PUBLIC, pageable);
        }

        return ResponseEntity.ok(helper.toResource(Kanban.class, page, assembler));
    }

    @PreAuthorize("canEdit(#id, 'Kanban')")
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
}
