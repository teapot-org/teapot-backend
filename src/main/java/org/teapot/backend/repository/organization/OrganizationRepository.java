package org.teapot.backend.repository.organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.teapot.backend.model.organization.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Organization findByName(String name);
}
