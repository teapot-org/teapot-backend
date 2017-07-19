package org.teapot.backend.repository.organization;

import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.AbstractOwnerRepository;

public interface OrganizationRepository extends AbstractOwnerRepository<Organization> {

    @Override
    @PreAuthorize("hasRole('ADMIN') or @memberService.isUserCreator(#id, authentication?.name)")
    void delete(@Param("id") Long id);

    @Override
    @PreAuthorize("hasRole('ADMIN') or @memberService.isUserCreator(#organization?.id, authentication?.name)")
    void delete(@Param("organization") Organization organization);
}
