package org.teapot.backend.test.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.OwnerRepository;
import org.teapot.backend.test.AbstractIT;

public class OwnerRepositoryIT extends AbstractIT {

    @Autowired
    private OwnerRepository ownerRepository;

    private Owner testOwner = new Organization();

    @Before
    public void setupOwner() {
        testOwner.setName("findByNameTest");
        ownerRepository.save(testOwner);
    }

    @Test
    public void findByNameTest() {
        Owner owner = ownerRepository.findByName("findByNameTest");
        Assert.assertEquals(testOwner, owner);
    }
}
