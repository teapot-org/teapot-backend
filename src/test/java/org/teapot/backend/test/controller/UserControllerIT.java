package org.teapot.backend.test.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.teapot.backend.model.user.User;
import org.teapot.backend.model.user.UserAuthority;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerIT extends AbstractControllerIT {

    private static final String API_URL = "/users";

    private User getUserOne = new User();
    private User getUserTwo = new User();
    private User postUserOne = new User();
    private User postUserTwo = new User();
    private User repeatedPostUser = new User();
    private User updateUser = new User();
    private User deleteUser = new User();
    private User patchUser = new User();

    @Before
    public void addTestUsers() {
        getUserOne.setName("getUserOne");
        getUserOne.setEmail("getUserOne@mail.com");
        getUserOne.setPassword(passwordEncoder.encode("pass"));
        userRepository.save(getUserOne);

        getUserTwo.setName("getUserTwo");
        getUserTwo.setEmail("getUserTwo@mail.com");
        getUserTwo.setPassword(passwordEncoder.encode("pass"));
        userRepository.save(getUserTwo);

        postUserOne.setName("postUser");
        postUserOne.setEmail("postUser@mail.com");
        postUserOne.setPassword(passwordEncoder.encode("pass"));

        postUserTwo.setName("postUser");
        postUserTwo.setEmail("postUser@mail.com");
        postUserTwo.setPassword(passwordEncoder.encode("pass"));

        repeatedPostUser.setName("repeatedPostUser");
        repeatedPostUser.setEmail("repeatedPostUser@mail.com");
        repeatedPostUser.setPassword(passwordEncoder.encode("pass"));
        userRepository.save(repeatedPostUser);

        updateUser.setName("updateUser");
        updateUser.setEmail("updateUser@mail.com");
        updateUser.setPassword(passwordEncoder.encode("pass"));
        userRepository.save(updateUser);

        deleteUser.setName("deleteUser");
        deleteUser.setEmail("deleteUser@mail.com");
        deleteUser.setPassword(passwordEncoder.encode("pass"));
        userRepository.save(deleteUser);

        patchUser.setName("patchUser");
        patchUser.setEmail("patchUser@mail.com");
        patchUser.setPassword(passwordEncoder.encode("pass"));
        userRepository.save(patchUser);
    }

    // GET

    @Test
    public void getUsersTest() throws Exception {
        List<User> all = userRepository.findAll();
        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(all.size())))
                .andExpect(jsonPath("$[0].id", is(all.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].username", is(all.get(0).getName())))
                .andExpect(jsonPath("$[0].email", is(all.get(0).getEmail())))
                .andExpect(jsonPath("$[1].id", is(all.get(1).getId().intValue())))
                .andExpect(jsonPath("$[1].username", is(all.get(1).getName())))
                .andExpect(jsonPath("$[1].email", is(all.get(1).getEmail())));
    }

    @Test
    public void getSingleUserByIdTest() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", API_URL, getUserOne.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(getUserOne.getId().intValue())))
                .andExpect(jsonPath("$.username", is(getUserOne.getName())))
                .andExpect(jsonPath("$.email", is(getUserOne.getEmail())));
    }

    @Test
    public void getNotExistsUserByIdTest() throws Exception {
        mockMvc.perform(get(String.format("%s/-1", API_URL)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getSingleUserByUsernameTest() throws Exception {
        mockMvc.perform(get(String.format("%s/%s", API_URL, getUserOne.getName())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(getUserOne.getId().intValue())))
                .andExpect(jsonPath("$.username", is(getUserOne.getName())))
                .andExpect(jsonPath("$.email", is(getUserOne.getEmail())));
    }

    @Test
    public void getNotExistsUserByUsernameTest() throws Exception {
        mockMvc.perform(get(String.format("%s/%s", API_URL, "not_exists")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getSingleUserByEmailTest() throws Exception {
        mockMvc.perform(get(String.format("%s/%s", API_URL, getUserOne.getEmail())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(getUserOne.getId().intValue())))
                .andExpect(jsonPath("$.username", is(getUserOne.getName())))
                .andExpect(jsonPath("$.email", is(getUserOne.getEmail())));
    }

    @Test
    public void getNotExistsUserByIEmailTest() throws Exception {
        mockMvc.perform(get(String.format("%s/%s", API_URL, "not_exists@email.com")))
                .andExpect(status().isNotFound());
    }

    // POST

    @Test
    public void registerUserTestByAnonymous() throws Exception {
        mockMvc.perform(post(API_URL)
                .content(json(postUserOne))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString(API_URL)));
    }

    @Test
    public void registerUserTestByUser() throws Exception {
        mockMvc.perform(post(API_URL)
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, userAccessToken))
                .content(json(postUserTwo))
                .contentType(contentType))
                .andExpect(status().isForbidden());
    }

    @Test
    public void registerUserTestByAdmin() throws Exception {
        mockMvc.perform(post(API_URL)
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(json(postUserTwo))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString(API_URL)));
    }

    @Test
    public void repeatRegisterUserTestByAdmin() throws Exception {
        mockMvc.perform(post(API_URL)
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(json(repeatedPostUser))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist(HttpHeaders.LOCATION));
    }

    // PUT

    @Test
    public void updateUserTestByAnonymous() throws Exception {
        updateUser.setBirthday(LocalDate.now());
        mockMvc.perform(put(String.format("%s/%d", API_URL, updateUser.getId()))
                .content(json(updateUser))
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateUserTestByUser() throws Exception {
        updateUser.setBirthday(LocalDate.now());
        mockMvc.perform(put(String.format("%s/%d", API_URL, updateUser.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, userAccessToken))
                .content(json(updateUser))
                .contentType(contentType))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateUserTestByAdmin() throws Exception {
        updateUser.setBirthday(LocalDate.now());
        mockMvc.perform(put(String.format("%s/%d", API_URL, updateUser.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(json(updateUser))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateNotExistsUserTestByAdmin() throws Exception {
        mockMvc.perform(put(String.format("%s/-1", API_URL))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(json(updateUser))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    // DELETE

    @Test
    public void deleteUserTestByAnonymous() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", API_URL, deleteUser.getId())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteUserTestByUser() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", API_URL, deleteUser.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteUserTestByAdmin() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", API_URL, deleteUser.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNotExistsUserTestByAdmin() throws Exception {
        mockMvc.perform(delete(String.format("%s/-1", API_URL))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isNotFound());
    }

    // PATCH

    @Test
    public void patchUserTestByAnonymous() throws Exception {
        mockMvc.perform(patch(String.format("%s/%d", API_URL, patchUser.getId())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void patchUserTestByUserSameId() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "newUserWithUserRole");
        params.add("firstName", "Bob");
        params.add("lastName", "Brown");

        mockMvc.perform(patch(String.format("%s/%d", API_URL, userWithUserRole.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, userAccessToken))
                .params(params))
                .andExpect(status().isNoContent());

        User newUserWithUserRole = userRepository.findOne(userWithUserRole.getId());
        Assert.assertEquals("newUserWithUserRole", newUserWithUserRole.getName());
        Assert.assertEquals("Bob", newUserWithUserRole.getFirstName());
        Assert.assertEquals("Brown", newUserWithUserRole.getLastName());
    }

    @Test
    public void patchUserTestByUserNotSameId() throws Exception {
        mockMvc.perform(patch(String.format("%s/%d", API_URL, patchUser.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void patchUserTestByAdmin() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "newPatchUser");
        params.add("email", "newPatchUser@test.org");
        params.add("password", "123456");
        params.add("available", "false");
        params.add("firstName", "Bob");
        params.add("lastName", "Brown");
        params.add("authority", "ADMIN");
        params.add("description", "newPatchUser");

        mockMvc.perform(patch(String.format("%s/%d", API_URL, patchUser.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .params(params))
                .andExpect(status().isNoContent());

        User newPatchUser = userRepository.findOne(patchUser.getId());
        Assert.assertEquals("newPatchUser", newPatchUser.getName());
        Assert.assertEquals("newPatchUser@test.org", newPatchUser.getEmail());
        Assert.assertTrue(passwordEncoder.matches("123456", newPatchUser.getPassword()));
        Assert.assertFalse(newPatchUser.isAvailable());
        Assert.assertEquals("Bob", newPatchUser.getFirstName());
        Assert.assertEquals("Brown", newPatchUser.getLastName());
        Assert.assertEquals(UserAuthority.ADMIN, newPatchUser.getAuthority());
        Assert.assertEquals("newPatchUser", newPatchUser.getDescription());
    }

    @Test
    public void patchNotExistsUserTestByAdmin() throws Exception {
        mockMvc.perform(patch(String.format("%s/-1", API_URL))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isNotFound());
    }
}
