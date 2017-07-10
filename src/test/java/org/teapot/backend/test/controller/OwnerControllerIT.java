package org.teapot.backend.test.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.OwnerRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OwnerControllerIT extends AbstractControllerIT {

    private static final String API_URL = "/owners";

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private User getOwnerUser = new User();
    private Organization getOwnerOrganization = new Organization();

    @Before
    public void addTestUsers() {
        getOwnerUser.setName("getOwnerUser");
        getOwnerUser.setEmail("getOwnerUser@mail.com");
        getOwnerUser.setPassword("pass");
        userRepository.save(getOwnerUser);

        getOwnerOrganization.setName("getUserTwo");
        organizationRepository.save(getOwnerOrganization);
    }

    // GET

    @Test
    public void getOwnersTest() throws Exception {
        List<Owner> all = ownerRepository.findAll();
        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(all.size())))
                .andExpect(jsonPath("$[0].id", is(all.get(0).getId().intValue())))
//                .andExpect(jsonPath("$[0].name", is(all.get(0).getName())))
                .andExpect(jsonPath("$[1].id", is(all.get(1).getId().intValue())));
//                .andExpect(jsonPath("$[1].name", is(all.get(1).getName())));
    }

    @Test
    public void getSingleOwnerUserByIdTest() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", API_URL, getOwnerUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(getOwnerUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is(getOwnerUser.getName())))
                .andExpect(jsonPath("$.email", is(getOwnerUser.getEmail())));
    }

    @Test
    public void getSingleOwnerOrganizationByIdTest() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", API_URL, getOwnerOrganization.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(getOwnerOrganization.getId().intValue())))
                .andExpect(jsonPath("$.name", is(getOwnerOrganization.getName())));
    }

    @Test
    public void getNotExistsOwnerByIdTest() throws Exception {
        mockMvc.perform(get(String.format("%s/-1", API_URL)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getSingleOwnerByNameTest() throws Exception {
        mockMvc.perform(get(String.format("%s/%s", API_URL, getOwnerUser.getName())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(getOwnerUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is(getOwnerUser.getName())))
                .andExpect(jsonPath("$.email", is(getOwnerUser.getEmail())));
    }

    @Test
    public void getNotExistsOwnerByNameTest() throws Exception {
        mockMvc.perform(get(String.format("%s/%s", API_URL, "not_exists")))
                .andExpect(status().isNotFound());
    }
}
