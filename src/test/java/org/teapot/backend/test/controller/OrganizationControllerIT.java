package org.teapot.backend.test.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.teapot.backend.controller.organization.OrganizationController.ORGANIZATIONS_ENDPOINT;
import static org.teapot.backend.controller.organization.OrganizationController.SINGLE_ORGANIZATION_ENDPOINT;

public class OrganizationControllerIT extends AbstractControllerIT {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Organization savedOrganization = new Organization();
    private Organization notSavedOrganization = new Organization();

    private User creatorUser = new User();
    private User ownerUser = new User();

    private Member creator = new Member();
    private Member owner = new Member();

    private String creatorAccessToken;
    private String ownerAccessToken;

    static ResultActions isOrganizationJsonAsExpected(ResultActions resultActions, String jsonPath, Organization organization)
            throws Exception {
        return resultActions
                .andExpect(jsonPath(jsonPath + ".name", is(organization.getName())))
                .andExpect(jsonPath(jsonPath + ".fullName", is(organization.getFullName())));
    }

    @Before
    public void addTestDate() throws Exception {

        savedOrganization.setName("OrganizationControllerTest1");
        organizationRepository.save(savedOrganization);

        notSavedOrganization.setName("OrganizationControllerTest2");

        creatorUser.setName("creatorUser");
        creatorUser.setEmail("creatorUser@email.com");
        creatorUser.setPassword("pass");
        creatorUser.setActivated(true);

        ownerUser.setName("ownerUser");
        ownerUser.setEmail("ownerUser@email.com");
        ownerUser.setPassword("pass");
        ownerUser.setActivated(true);

        userRepository.save(Arrays.asList(creatorUser, ownerUser));

        creator.setUser(creatorUser);
        creator.setOrganization(savedOrganization);
        creator.setStatus(MemberStatus.CREATOR);

        owner.setUser(ownerUser);
        owner.setOrganization(savedOrganization);
        owner.setStatus(MemberStatus.OWNER);

        memberRepository.save(Arrays.asList(creator, owner));

        creatorAccessToken = obtainAccessToken(creatorUser.getEmail(), "pass");
        ownerAccessToken = obtainAccessToken(ownerUser.getEmail(), "pass");
    }

    // GET ORGANIZATIONS

    @Test
    public void getOrganizationsTestByUser() throws Exception {
        List<Organization> all = organizationRepository.findAll();

        ResultActions result = mockMvc.perform(get(ORGANIZATIONS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.owners", hasSize(all.size())));

        for (int i = 0; i < all.size(); i++) {
            isOrganizationJsonAsExpected(result, format("$._embedded.owners[%d]", i), all.get(i));
        }
    }

    @Test
    public void getSingleOrganizationByIdTestByUser() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_ORGANIZATION_ENDPOINT, savedOrganization.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isOrganizationJsonAsExpected(result, "$", savedOrganization);
    }

    @Test
    public void getNotExistsOrganizationTestByUser() throws Exception {
        mockMvc.perform(get(SINGLE_ORGANIZATION_ENDPOINT, -1)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isNotFound());
    }

    // DELETE ORGANIZATIONS

    @Test
    public void deleteOrganizationTestByAnonymous() throws Exception {
        mockMvc.perform(delete(SINGLE_ORGANIZATION_ENDPOINT, savedOrganization.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteOrganizationTestByUser() throws Exception {
        mockMvc.perform(delete(SINGLE_ORGANIZATION_ENDPOINT, savedOrganization.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteOrganizationTestByAdmin() throws Exception {
        mockMvc.perform(delete(SINGLE_ORGANIZATION_ENDPOINT, savedOrganization.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNotExistsOrganizationTestByAdmin() throws Exception {
        mockMvc.perform(delete(SINGLE_ORGANIZATION_ENDPOINT, -1)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteOrganizationTestByCreator() throws Exception {
        mockMvc.perform(delete(SINGLE_ORGANIZATION_ENDPOINT, savedOrganization.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, creatorAccessToken)))
                .andExpect(status().isNoContent());
    }

    // POST ORGANIZATIONS

    @Test
    public void createOrganizationByAdmin() throws Exception {
        mockMvc.perform(post(ORGANIZATIONS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(json(notSavedOrganization))
                .contentType(contentType))
                .andExpect(status().isCreated());

        User orgCreator = memberRepository
                .findByOrganizationNameAndStatus(notSavedOrganization.getName(), MemberStatus.CREATOR, null)
                .getContent()
                .get(0).getUser();

        assertEquals(userWithAdminRole, orgCreator);
    }

    @Test
    public void createExistingOrganizationByAdmin() throws Exception {
        mockMvc.perform(post(ORGANIZATIONS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(json(savedOrganization))
                .contentType(contentType))
                .andExpect(status().isConflict());
    }

    // PATCH ORGANIZATIONS

    @Test
    public void changeNameTestByAdmin() throws Exception {
        mockMvc.perform(patch(SINGLE_ORGANIZATION_ENDPOINT, savedOrganization.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("name", "newName"))
                .andExpect(status().isNoContent());

        Organization org = organizationRepository.findOne(savedOrganization.getId());
        Assert.assertEquals("newName", org.getName());
    }

    @Test
    public void changeFullNameTestByAdmin() throws Exception {
        mockMvc.perform(patch(SINGLE_ORGANIZATION_ENDPOINT, savedOrganization.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("fullName", "newFullName"))
                .andExpect(status().isNoContent());

        Organization org = organizationRepository.findOne(savedOrganization.getId());
        Assert.assertEquals("newFullName", org.getFullName());
    }

    @Test
    public void changeNameAndFullNameTestByAdmin() throws Exception {
        mockMvc.perform(patch(SINGLE_ORGANIZATION_ENDPOINT, savedOrganization.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("name", "newName")
                .param("fullName", "newFullName"))
                .andExpect(status().isNoContent());

        Organization org = organizationRepository.findOne(savedOrganization.getId());
        Assert.assertEquals("newName", org.getName());
        Assert.assertEquals("newFullName", org.getFullName());
    }

    @Test
    public void changeNameAndFullNameTestByCreator() throws Exception {
        mockMvc.perform(patch(SINGLE_ORGANIZATION_ENDPOINT, savedOrganization.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, creatorAccessToken))
                .param("name", "newName")
                .param("fullName", "newFullName"))
                .andExpect(status().isNoContent());

        Organization org = organizationRepository.findOne(savedOrganization.getId());
        Assert.assertEquals("newName", org.getName());
        Assert.assertEquals("newFullName", org.getFullName());
    }

    @Test
    public void changeNameAndFullNameTestByOwner() throws Exception {
        mockMvc.perform(patch(SINGLE_ORGANIZATION_ENDPOINT, savedOrganization.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken))
                .param("name", "newName")
                .param("fullName", "newFullName"))
                .andExpect(status().isNoContent());

        Organization org = organizationRepository.findOne(savedOrganization.getId());
        Assert.assertEquals("newName", org.getName());
        Assert.assertEquals("newFullName", org.getFullName());
    }
}
