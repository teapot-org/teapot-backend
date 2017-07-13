package org.teapot.backend.test.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.organization.OrganizationRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.teapot.backend.controller.OwnerController.OWNERS_ENDPOINT;
import static org.teapot.backend.controller.OwnerController.SINGLE_OWNER_ENDPOINT;
import static org.teapot.backend.test.controller.OrganizationControllerIT.isOrganizationJsonAsExpected;

public class OwnerControllerIT extends AbstractControllerIT {

    private static final String FIND_OWNER_BY_NAME_ENDPOINT = OWNERS_ENDPOINT + "/search/find-by-name";

    @Autowired
    private OrganizationRepository organizationRepository;

    private User savedUser = new User();
    private Organization savedOrganization = new Organization();
    private Organization notSavedOrganization = new Organization();

    @Before
    public void addTestUsers() {
        savedUser.setName("OwnerControllerTestUser");
        savedUser.setEmail("OwnerControllerTestUser@mail");
        savedUser.setPassword("pass");
        savedUser.setFirstName("OwnerControllerTestUser");
        savedUser.setLastName("OwnerControllerTestUser");
        savedUser.setDescription("OwnerControllerTestUser");

        savedOrganization.setName("OwnerControllerTestOrg1");
        savedOrganization.setFullName("OwnerControllerTestOrg1");
        organizationRepository.save(savedOrganization);

        notSavedOrganization.setName("OwnerControllerTestOrg2");
    }

    @Test
    public void getSingleOwnerByIdTest() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_OWNER_ENDPOINT, savedOrganization.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isOrganizationJsonAsExpected(result, "$", savedOrganization);
    }

    @Test
    public void getNotExistsOwnerByIdTest() throws Exception {
        mockMvc.perform(get(SINGLE_OWNER_ENDPOINT, -1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getSingleOwnerByNameTest() throws Exception {
        ResultActions result = mockMvc.perform(get(FIND_OWNER_BY_NAME_ENDPOINT)
                .param("name", savedOrganization.getName()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isOrganizationJsonAsExpected(result, "$", savedOrganization);
    }

    @Test
    public void getNotExistsOwnerByNameTest() throws Exception {
        mockMvc.perform(get(FIND_OWNER_BY_NAME_ENDPOINT)
                .param("name", notSavedOrganization.getName()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createOwnerTest() throws Exception {
        mockMvc.perform(post(OWNERS_ENDPOINT))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void updateOwnerTest() throws Exception {
        mockMvc.perform(put(SINGLE_OWNER_ENDPOINT, savedOrganization.getId()))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void deleteOwnerTest() throws Exception {
        mockMvc.perform(delete(SINGLE_OWNER_ENDPOINT, savedOrganization.getId()))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void patchOwnerTest() throws Exception {
        mockMvc.perform(patch(SINGLE_OWNER_ENDPOINT, savedOrganization.getId()))
                .andExpect(status().isMethodNotAllowed());
    }
}
