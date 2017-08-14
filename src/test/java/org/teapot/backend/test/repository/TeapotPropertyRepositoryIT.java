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
        propertyRepository.save(new TeapotProperty("property-name", "property-value"));
    }

    @Test
    public void findByNameExistsPropertyTest() {
        TeapotProperty property = propertyRepository.findByName("property-name");
        Assert.assertEquals("property-value", property.getValue());
    }

    @Test
    public void findByNameNotExistsPropertyTest() {
        TeapotProperty property = propertyRepository.findByName("test");
        Assert.assertNull(property);
    }
}
