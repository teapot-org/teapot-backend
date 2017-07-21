package org.teapot.backend.controller.kanban;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.teapot.backend.model.kanban.Project;
import org.teapot.backend.repository.OwnerRepository;
import org.teapot.backend.repository.kanban.ProjectRepository;
import org.teapot.backend.util.PagedResourcesAssemblerHelper;

import static org.teapot.backend.controller.OwnerController.SINGLE_OWNER_ENDPOINT;

@RepositoryRestController
public class ProjectController {

    public static final String PROJECTS_ENDPOINT = "/projects";
    public static final String SINGLE_PROJECT_ENDPOINT = PROJECTS_ENDPOINT + "/{id}";

    @Autowired
    private PagedResourcesAssemblerHelper<Project> helper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @GetMapping(SINGLE_OWNER_ENDPOINT + PROJECTS_ENDPOINT)
    public ResponseEntity<?> getOwnerProjects(
            @PathVariable Long id,
            Pageable pageable,
            PersistentEntityResourceAssembler assembler
    ) {
        if (!ownerRepository.exists(id)) {
            throw new ResourceNotFoundException();
        }

        Page<Project> page = projectRepository.findByOwnerId(id, pageable);
        return ResponseEntity.ok(helper.toResource(Project.class, page, assembler));
    }
}
