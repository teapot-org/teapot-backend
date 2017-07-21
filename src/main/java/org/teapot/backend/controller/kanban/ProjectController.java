package org.teapot.backend.controller.kanban;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.teapot.backend.repository.kanban.ProjectRepository;

@RepositoryRestController
public class ProjectController {

    public static final String PROJECTS_ENDPOINT = "/projects";
    public static final String SINGLE_PROJECT_ENDPOINT = PROJECTS_ENDPOINT + "/{id}";

    @Autowired
    private ProjectRepository projectRepository;
}
