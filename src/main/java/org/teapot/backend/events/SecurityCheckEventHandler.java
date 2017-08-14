package org.teapot.backend.events;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.teapot.backend.model.BaseEntity;

@Component
@RepositoryEventHandler
public class SecurityCheckEventHandler {

    @HandleBeforeCreate
    @PreAuthorize("canCreate(#entity)")
    public void onBeforeCreate(BaseEntity entity) {
        // only security check
    }
}
