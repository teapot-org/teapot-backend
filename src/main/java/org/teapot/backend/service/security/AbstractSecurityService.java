package org.teapot.backend.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.teapot.backend.model.AbstractPersistable;

public abstract class AbstractSecurityService<T extends AbstractPersistable> {

    @Autowired
    private CrudRepository<T, Long> repository;

    protected final String getLoggedUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    protected final void assertExists(Long id) {
        if (!repository.exists(id)) {
            throw new ResourceNotFoundException();
        }
    }
}
