package org.teapot.backend.test.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.teapot.backend.model.user.User;
import org.teapot.backend.model.user.UserAuthority;

import java.time.LocalDate;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.teapot.backend.controller.user.UserController.SINGLE_USER_ENDPOINT;
import static org.teapot.backend.controller.user.UserController.USERS_ENDPOINT;

public class UserControllerIT extends AbstractControllerIT {

    private static final String FIND_USER_BY_NAME_ENDPOINT = USERS_ENDPOINT + "/search/find-by-name";
    private static final String FIND_USER_BY_EMAIL_ENDPOINT = USERS_ENDPOINT + "/search/find-by-email";

    private User notSavedUser = new User();
    private User savedUser = new User();

    static ResultActions isUserJsonAsExpected(ResultActions resultActions, String jsonPath, User user)
            throws Exception {
        return resultActions
                .andExpect(jsonPath(jsonPath + ".name", is(user.getName())))
                .andExpect(jsonPath(jsonPath + ".available", is(user.getAvailable())))
                .andExpect(jsonPath(jsonPath + ".email", is(user.getEmail())))
                .andExpect(jsonPath(jsonPath + ".activated", is(user.getActivated())))
                .andExpect(jsonPath(jsonPath + ".firstName", is(user.getFirstName())))
                .andExpect(jsonPath(jsonPath + ".lastName", is(user.getLastName())))
                .andExpect(jsonPath(jsonPath + ".lastName", is(user.getLastName())))
                .andExpect(jsonPath(jsonPath + ".authority", is(user.getAuthority().name())))
                .andExpect(jsonPath(jsonPath + ".description", is(user.getDescription())));
    }

    @Before
    public void addTestUsers() {
        savedUser.setName("savedUser");
        savedUser.setEmail("savedUser@mail.com");
        savedUser.setPassword("pass");
        savedUser.setFirstName("savedUser");
        savedUser.setLastName("savedUser");
        savedUser.setDescription("savedUser");

        userRepository.save(savedUser);

        notSavedUser.setName("notSavedUser");
        notSavedUser.setEmail("notSavedUser@mail.com");
        notSavedUser.setPassword("pass");
    }

    // GET

    @Test
    public void getUsersTest() throws Exception {
        List<User> allUsers = userRepository.findAll();

        ResultActions result = mockMvc.perform(get(USERS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.owners", hasSize(allUsers.size())));

        for (int i = 0; i < allUsers.size(); i++) {
            result = isUserJsonAsExpected(result, format("$._embedded.owners[%d]", i), allUsers.get(i));
        }
    }

    @Test
    public void getSingleUserByIdTest() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_USER_ENDPOINT, savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isUserJsonAsExpected(result, "$", savedUser);
    }

    @Test
    public void getNotExistsUserByIdTest() throws Exception {
        mockMvc.perform(get(SINGLE_USER_ENDPOINT, -1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getSingleUserByNameTest() throws Exception {
        ResultActions result = mockMvc.perform(get(FIND_USER_BY_NAME_ENDPOINT)
                .param("name", savedUser.getName()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isUserJsonAsExpected(result, "$", savedUser);
    }

    @Test
    public void getNotExistsUserByNameTest() throws Exception {
        mockMvc.perform(get(FIND_USER_BY_NAME_ENDPOINT)
                .param("name", notSavedUser.getName()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getSingleUserByEmailTest() throws Exception {
        ResultActions result = mockMvc.perform(get(FIND_USER_BY_EMAIL_ENDPOINT)
                .param("email", savedUser.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isUserJsonAsExpected(result, "$", savedUser);
    }

    @Test
    public void getNotExistsUserByEmailTest() throws Exception {
        mockMvc.perform(get(FIND_USER_BY_EMAIL_ENDPOINT)
                .param("email", notSavedUser.getName()))
                .andExpect(status().isNotFound());
    }

    // POST

    @Test
    public void registerUserTestByAnonymous() throws Exception {
        ResultActions result = mockMvc.perform(post(USERS_ENDPOINT)
                .content(json(notSavedUser))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(header().string(HttpHeaders.LOCATION, containsString(USERS_ENDPOINT)));

        isUserJsonAsExpected(result, "$", userRepository.findByName(notSavedUser.getName()));
    }

    @Test
    public void registerUserTestByUser() throws Exception {
        mockMvc.perform(post(USERS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken))
                .content(json(notSavedUser))
                .contentType(contentType))
                .andExpect(status().isForbidden());
    }

    @Test
    public void registerUserTestByAdmin() throws Exception {
        ResultActions result = mockMvc.perform(post(USERS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(json(notSavedUser))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(header().string(HttpHeaders.LOCATION, containsString(USERS_ENDPOINT)));

        isUserJsonAsExpected(result, "$", userRepository.findByName(notSavedUser.getName()));
    }

    @Test
    public void repeatRegisterUserTestByAdmin() throws Exception {
        mockMvc.perform(post(USERS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(json(savedUser))
                .contentType(contentType))
                .andExpect(status().isConflict())
                .andExpect(header().doesNotExist(HttpHeaders.LOCATION));
    }

    // PUT

    @Test
    public void updateUserTestByAnonymous() throws Exception {
        mockMvc.perform(put(SINGLE_USER_ENDPOINT, savedUser.getId())
                .content(json(savedUser))
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateUserTestByUser() throws Exception {
        mockMvc.perform(put(SINGLE_USER_ENDPOINT, savedUser.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken))
                .content(json(savedUser))
                .contentType(contentType))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateUserTestByAdmin() throws Exception {
        savedUser.setBirthday(LocalDate.now());
        mockMvc.perform(put(SINGLE_USER_ENDPOINT, savedUser.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(json(savedUser))
                .contentType(contentType))
                .andExpect(status().isNoContent());

        assertEquals(savedUser, userRepository.getOne(savedUser.getId()));
    }

    @Test
    public void updateNotExistsUserTestByAdmin() throws Exception {
        mockMvc.perform(put(SINGLE_USER_ENDPOINT, -1)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(json(notSavedUser))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    // DELETE

    @Test
    public void deleteUserTestByAnonymous() throws Exception {
        mockMvc.perform(delete(SINGLE_USER_ENDPOINT, savedUser.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteUserTestByUser() throws Exception {
        mockMvc.perform(delete(SINGLE_USER_ENDPOINT, savedUser.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteUserTestByAdmin() throws Exception {
        assertNotNull(userRepository.getOne(savedUser.getId()));

        mockMvc.perform(delete(SINGLE_USER_ENDPOINT, savedUser.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isNoContent());

        assertNull(userRepository.findOne(savedUser.getId()));
    }

    @Test
    public void deleteNotExistsUserTestByAdmin() throws Exception {
        mockMvc.perform(delete(SINGLE_USER_ENDPOINT, -1)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isNotFound());
    }

    // PATCH

    @Test
    public void patchUserTestByAnonymous() throws Exception {
        mockMvc.perform(patch(SINGLE_USER_ENDPOINT, savedUser.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void patchUserTestByUserSameId() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", "newUserWithUserRole");
        params.add("firstName", "Bob");
        params.add("lastName", "Brown");

        mockMvc.perform(patch(SINGLE_USER_ENDPOINT, userWithUserRole.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken))
                .params(params))
                .andExpect(status().isNoContent());

        User newUserWithUserRole = userRepository.findOne(userWithUserRole.getId());
        assertEquals("newUserWithUserRole", newUserWithUserRole.getName());
        assertEquals("Bob", newUserWithUserRole.getFirstName());
        assertEquals("Brown", newUserWithUserRole.getLastName());
    }

    @Test
    public void patchUserTestByUserNotSameId() throws Exception {
        mockMvc.perform(patch(SINGLE_USER_ENDPOINT, savedUser.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void patchUserTestByAdmin() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", "newName");
        params.add("email", "newName@test.org");
        params.add("password", "123456");
        params.add("available", "false");
        params.add("firstName", "Bob");
        params.add("lastName", "Brown");
        params.add("authority", "ADMIN");
        params.add("description", "newName");

        mockMvc.perform(patch(SINGLE_USER_ENDPOINT, savedUser.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken))
                .params(params))
                .andExpect(status().isNoContent());

        User newPatchUser = userRepository.findOne(savedUser.getId());
        assertEquals("newName", newPatchUser.getName());
        assertEquals("newName@test.org", newPatchUser.getEmail());
        Assert.assertTrue(User.PASSWORD_ENCODER.matches("123456", newPatchUser.getPassword()));
        Assert.assertFalse(newPatchUser.getAvailable());
        assertEquals("Bob", newPatchUser.getFirstName());
        assertEquals("Brown", newPatchUser.getLastName());
        assertEquals(UserAuthority.ADMIN, newPatchUser.getAuthority());
        assertEquals("newName", newPatchUser.getDescription());
    }

    @Test
    public void patchNotExistsUserTestByAdmin() throws Exception {
        mockMvc.perform(patch(SINGLE_USER_ENDPOINT, -1)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isNotFound());
    }
}
