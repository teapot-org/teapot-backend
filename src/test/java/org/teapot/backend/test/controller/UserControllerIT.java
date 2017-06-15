package org.teapot.backend.test.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.teapot.backend.model.User;
import org.teapot.backend.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class UserControllerIT extends AbstractControllerIT {

    private static final String API_URL = "/users";

    @Autowired
    private UserRepository userRepository;

    private User getUserOne = new User();
    private User getUserTwo = new User();
    private User postUser = new User();
    private User repeatedPostUser = new User();
    private User updateUser = new User();
    private User deleteUser = new User();

    @Before
    public void init() {
        userRepository.deleteAllInBatch();

        getUserOne.setUsername("getUserOne");
        getUserOne.setPassword("pass");
        userRepository.save(getUserOne);

        getUserTwo.setUsername("getUserTwo");
        getUserTwo.setPassword("pass");
        userRepository.save(getUserTwo);

        postUser.setUsername("postUser");
        postUser.setPassword("pass");

        repeatedPostUser.setUsername("repeatedPostUser");
        repeatedPostUser.setPassword("pass");
        userRepository.save(repeatedPostUser);

        updateUser.setUsername("updateUser");
        updateUser.setPassword("pass");
        userRepository.save(updateUser);

        deleteUser.setUsername("deleteUser");
        deleteUser.setPassword("pass");
        userRepository.save(deleteUser);
    }

    @Test
    public void getUsersTest() throws Exception {
        List<User> all = userRepository.findAll();
        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(all.size())))
                .andExpect(jsonPath("$[0].id", is(all.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].username", is(all.get(0).getUsername())))
                .andExpect(jsonPath("$[0].password", is(all.get(0).getPassword())))
                .andExpect(jsonPath("$[1].id", is(all.get(1).getId().intValue())))
                .andExpect(jsonPath("$[1].username", is(all.get(1).getUsername())))
                .andExpect(jsonPath("$[1].password", is(all.get(1).getPassword())));
    }

    @Test
    public void getSingleUserTest() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", API_URL, getUserOne.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(getUserOne.getId().intValue())))
                .andExpect(jsonPath("$.username", is(getUserOne.getUsername())))
                .andExpect(jsonPath("$.password", is(getUserOne.getPassword())));
    }

    @Test
    public void getNotExistsUserTest() throws Exception {
        mockMvc.perform(get(String.format("%s/-1", API_URL)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void registerUserTest() throws Exception {
        mockMvc.perform(post(API_URL)
                .content(json(postUser))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(API_URL)));
    }

    @Test
    public void repeatRegisterUserTest() throws Exception {
        mockMvc.perform(post(API_URL)
                .content(json(repeatedPostUser))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Location"));
    }

    @Test
    public void updateUserTest() throws Exception {
        updateUser.setBirthday(LocalDate.now());
        mockMvc.perform(put(String.format("%s/%d", API_URL, updateUser.getId()))
                .content(json(updateUser))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateNotExistsUserTest() throws Exception {
        mockMvc.perform(put(String.format("%s/-1", API_URL))
                .content(json(updateUser))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUserTest() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", API_URL, deleteUser.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNotExistsUserTest() throws Exception {
        mockMvc.perform(delete(String.format("%s/-1", API_URL)))
                .andExpect(status().isNotFound());
    }
}
