package org.teapot.backend.repository.organization;

import org.springframework.security.access.prepost.PreAuthorize;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.AbstractOwnerRepository;

public interface OrganizationRepository extends AbstractOwnerRepository<Organization> {

    @PreAuthorize("hasRole('ADMIN') or @memberService.isCreator(#id, authentication?.name)")
    @Override
    void delete(Long id);
}
