package org.teapot.backend.test.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.teapot.backend.model.meta.TeapotProperty;
import org.teapot.backend.repository.meta.TeapotPropertyRepository;
import org.teapot.backend.test.AbstractIT;

public class TeapotPropertyRepositoryIT extends AbstractIT {

    @Autowired
    private TeapotPropertyRepository propertyRepository;

    @Before
    public void setUp() {
        TeapotProperty property = new TeapotProperty();

        property.setName("property-name");
        property.setValue("property-value");

        propertyRepository.save(property);
    }

    @Test
    public void findByNameTest1() {
        TeapotProperty property = propertyRepository.findByName("property-name");
        Assert.assertNotNull(property);
        Assert.assertEquals("property-value", property.getValue());
    }

    @Test
    public void findByNameTest2() {
        TeapotProperty property = propertyRepository.findByName("test");
        Assert.assertNull(property);
    }
}
