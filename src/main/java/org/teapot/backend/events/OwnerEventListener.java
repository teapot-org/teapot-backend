package org.teapot.backend.events;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;
import org.teapot.backend.model.Owner;
import org.teapot.backend.repository.OwnerRepository;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OwnerEventListener extends AbstractRepositoryEventListener<Owner> {

    private final OwnerRepository ownerRepository;

    @Override
    protected void onBeforeCreate(Owner entity) {
        if (ownerRepository.findByName(entity.getName()) != null) {
            throw new DataIntegrityViolationException("Already exists");
        }
    }
}
