package org.teapot.backend.repository.organization;

import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.AbstractOwnerRepository;

public interface OrganizationRepository extends AbstractOwnerRepository<Organization> {

    @Override
    @PreAuthorize("@organizations.hasStatus(#id, 'CREATOR') or hasRole('ADMIN')")
    void delete(@Param("id") Long id);

    @Override
    @PreAuthorize("@organizations.hasStatus(#organization, 'CREATOR') or hasRole('ADMIN')")
    void delete(@Param("organization") Organization organization);
}
