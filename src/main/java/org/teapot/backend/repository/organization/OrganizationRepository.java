package org.teapot.backend.repository.organization;

import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.AbstractOwnerRepository;

import javax.transaction.Transactional;

@Transactional
public interface OrganizationRepository extends AbstractOwnerRepository<Organization> {
}
