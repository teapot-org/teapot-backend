package org.teapot.backend.repository.organization;

import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.AbstractOwnerRepository;

import static org.teapot.backend.service.MemberService.USER_IS_CREATOR;
import static org.teapot.backend.service.MemberService.USER_IS_CREATOR_BY_ORG;

public interface OrganizationRepository extends AbstractOwnerRepository<Organization> {

    @Override
    @PreAuthorize(USER_IS_CREATOR + " or hasRole('ADMIN')")
    void delete(@Param("id") Long id);

    @Override
    @PreAuthorize(USER_IS_CREATOR_BY_ORG + "or hasRole('ADMIN')")
    void delete(@Param("organization") Organization organization);
}
