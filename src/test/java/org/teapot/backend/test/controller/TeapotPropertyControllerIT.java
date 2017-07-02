package org.teapot.backend.test.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.teapot.backend.model.meta.TeapotProperty;
import org.teapot.backend.repository.meta.TeapotPropertyRepository;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TeapotPropertyControllerIT extends AbstractControllerIT {

    private static final String API_URL = "/props";
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

    @Test
    public void getProperties() throws Exception {
        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(properties.size())))
                .andExpect(jsonPath("$[0].id", is(properties.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(properties.get(0).getName())))
                .andExpect(jsonPath("$[0].value", is(properties.get(0).getValue())))
                .andExpect(jsonPath("$[1].id", is(properties.get(1).getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(properties.get(1).getName())))
                .andExpect(jsonPath("$[1].value", is(properties.get(1).getValue())));
    }

    @Test
    public void getExistentPropertyTest() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", API_URL, properties.get(2).getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(properties.get(2).getId().intValue())))
                .andExpect(jsonPath("$.name", is(properties.get(2).getName())))
                .andExpect(jsonPath("$.value", is(properties.get(2).getValue())));
    }

    @Test
    public void getNonexistentPropertyTest() throws Exception {
        mockMvc.perform(get(String.format("%s/666", API_URL)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addNonexistentPropertyTest() throws Exception {
        mockMvc.perform(post(API_URL).content(json(nonexistentProperty))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString(API_URL)));
    }

    @Test
    public void addExistentPropertyTest() throws Exception {
        mockMvc.perform(post(API_URL).content(json(existentProperty))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist(HttpHeaders.LOCATION));
    }

    @Test
    public void updateExistentPropertyTest() throws Exception {
        mockMvc.perform(put(String.format("%s/%d", API_URL, updateExistentProperty.getId()))
                .content(json(updateExistentProperty)).contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateNonexistentPropertyTest() throws Exception {
        mockMvc.perform(put(String.format("%s/666", API_URL))
                .content(json(updateNonexistentProperty)).contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteExistentPropertyTest() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", API_URL, properties.get(5).getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNonexistentPropertyTest() throws Exception {
        mockMvc.perform(delete(String.format("%s/666", API_URL)))
                .andExpect(status().isNotFound());
    }
}
