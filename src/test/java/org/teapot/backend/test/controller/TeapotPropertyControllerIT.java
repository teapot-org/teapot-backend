package org.teapot.backend.test.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.teapot.backend.model.meta.TeapotProperty;
import org.teapot.backend.repository.meta.TeapotPropertyRepository;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.teapot.backend.controller.meta.TeapotPropertyController.PROPERTIES_ENDPOINT;
import static org.teapot.backend.controller.meta.TeapotPropertyController.SINGLE_PROPERTY_ENDPOINT;

public class TeapotPropertyControllerIT extends AbstractControllerIT {

    @Autowired
    private TeapotPropertyRepository propertyRepository;
    private List<TeapotProperty> properties;

    private TeapotProperty existentProperty = new TeapotProperty("property-4", "value-4");
    private TeapotProperty nonexistentProperty = new TeapotProperty("property-7", "value-7");
    private TeapotProperty updateExistentProperty = new TeapotProperty("property-5", "new-value-5");
    private TeapotProperty updateNonexistentProperty = new TeapotProperty("property-8", "new-value-8");

    @Before
    public void setUp() {
        propertyRepository.deleteAllInBatch();

        properties = propertyRepository.save(Arrays.asList(
                new TeapotProperty("property-1", "value-1"),
                new TeapotProperty("property-2", "value-2"),
                new TeapotProperty("property-3", "value-3"),
                new TeapotProperty("property-4", "value-4"),
                new TeapotProperty("property-5", "value-5"),
                new TeapotProperty("property-6", "value-6")
        ));

        updateExistentProperty.setId(properties.get(4).getId());
    }

    private ResultActions isPropertyJsonAsExpected(ResultActions resultActions, String jsonPath, TeapotProperty prop)
            throws Exception {
        return resultActions
                .andExpect(jsonPath(jsonPath + ".name", is(prop.getName())))
                .andExpect(jsonPath(jsonPath + ".value", is(prop.getValue())));
    }

    @Test
    public void getProperties() throws Exception {
        List<TeapotProperty> all = propertyRepository.findAll();

        ResultActions result = mockMvc.perform(get(PROPERTIES_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.properties", hasSize(all.size())));

        for (int i = 0; i < properties.size(); i++) {
            result = isPropertyJsonAsExpected(result, format("$._embedded.properties[%d]", i), all.get(i));
        }
    }

    @Test
    public void getExistentPropertyTest() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_PROPERTY_ENDPOINT, properties.get(2).getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isPropertyJsonAsExpected(result, "$", properties.get(2));
    }

    @Test
    public void getNonexistentPropertyTest() throws Exception {
        mockMvc.perform(get(SINGLE_PROPERTY_ENDPOINT, 666))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addNonexistentPropertyTest() throws Exception {
        mockMvc.perform(post(PROPERTIES_ENDPOINT)
                .content(json(nonexistentProperty))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString(PROPERTIES_ENDPOINT)));
    }

    @Test
    public void addExistentPropertyTest() throws Exception {
        mockMvc.perform(post(PROPERTIES_ENDPOINT)
                .content(json(existentProperty))
                .contentType(contentType))
                .andExpect(status().isConflict())
                .andExpect(header().doesNotExist(HttpHeaders.LOCATION));
    }

    @Test
    public void updateExistentPropertyTest() throws Exception {
        mockMvc.perform(put(SINGLE_PROPERTY_ENDPOINT, updateExistentProperty.getId())
                .content(json(updateExistentProperty)).contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateNonexistentPropertyTest() throws Exception {
        mockMvc.perform(put(SINGLE_PROPERTY_ENDPOINT, 666)
                .content(json(updateNonexistentProperty)).contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteExistentPropertyTest() throws Exception {
        assertNotNull(propertyRepository.findOne(properties.get(5).getId()));

        mockMvc.perform(delete(SINGLE_PROPERTY_ENDPOINT, properties.get(5).getId()))
                .andExpect(status().isNoContent());

        assertNull(propertyRepository.findOne(properties.get(5).getId()));
    }

    @Test
    public void deleteNonexistentPropertyTest() throws Exception {
        mockMvc.perform(delete(SINGLE_PROPERTY_ENDPOINT, 666))
                .andExpect(status().isNotFound());
    }
}
