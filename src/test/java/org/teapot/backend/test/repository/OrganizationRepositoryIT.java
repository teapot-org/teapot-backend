package org.teapot.backend.test.repository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.organization.OrganizationRepository;
import org.teapot.backend.test.AbstractIT;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OrganizationRepositoryIT extends AbstractIT {

    @Autowired
    private OrganizationRepository organizationRepository;

    private Organization findByNameTestOrganization = new Organization();

    @Before
    public void setup() {
        findByNameTestOrganization.setName("findByNameTestOrganization");
        findByNameTestOrganization.setRegistrationDateTime(LocalDateTime.now());
        organizationRepository.save(findByNameTestOrganization);
    }

    @Test
    public void findByNameTest() {
        Organization fromDataBase = organizationRepository
                .findByName("findByNameTestOrganization");
        assertNotNull(fromDataBase);
        assertEquals(findByNameTestOrganization, fromDataBase);
    }
}
