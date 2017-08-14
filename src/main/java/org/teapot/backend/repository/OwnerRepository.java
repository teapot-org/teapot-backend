package org.teapot.backend.repository;

import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.Owner;

public interface OwnerRepository extends AbstractOwnerRepository<Owner> {

    @Override
    @RestResource(exported = false)
    <S extends Owner> S save(S resource);

    @Override
    @RestResource(exported = false)
    void delete(Long id);

    @Override
    @RestResource(exported = false)
    void delete(Owner resource);
}
