package org.teapot.backend.test.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.teapot.backend.model.Board;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.BoardRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;
import org.teapot.backend.util.LinkBuilder;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BoardControllerIT extends AbstractControllerIT {

    private static final String API_URL = "/boards";
    private static final String ORGANIZATIONS_URL = "organizations";
    private static final String USERS_URL = "users";

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private LinkBuilder linkBuilder;

    private User getBoard1Owner = new User();
    private Organization getBoard2Owner = new Organization();

    private Board getBoard1 = new Board("getBoard1", getBoard1Owner);
    private Board getBoard2 = new Board("getBoard2", getBoard2Owner);

    @Before
    public void addTestUsers() {
        getBoard1Owner.setName("getBoard1Owner");
        getBoard1Owner.setEmail("getBoard1Owner@mail.com");
        getBoard1Owner.setPassword("pass");
        userRepository.save(getBoard1Owner);

        getBoard2Owner.setName("getBoard2Owner");
        organizationRepository.save(getBoard2Owner);

        boardRepository.save(getBoard1);
        boardRepository.save(getBoard2);
    }

    // GET

    @Test
    public void getBoardsTest() throws Exception {
        List<Board> all = boardRepository.findAll();
        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(all.size())))
                .andExpect(jsonPath("$[0].id", is(all.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].title", is(all.get(0).getTitle())))
                .andExpect(jsonPath("$[1].id", is(all.get(1).getId().intValue())))
                .andExpect(jsonPath("$[1].title", is(all.get(1).getTitle())));
    }

    @Test
    public void getSingleBoardByIdTest() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", API_URL, getBoard1.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(getBoard1.getId().intValue())))
                .andExpect(jsonPath("$.title", is(getBoard1.getTitle())))
                .andExpect(jsonPath("$.owner",
                        is(linkBuilder.format("/%s/%d", USERS_URL,
                                getBoard1Owner.getId().intValue()))));
    }

    @Test
    public void getNotExistsBoardByIdTest() throws Exception {
        mockMvc.perform(get(String.format("%s/-1", API_URL)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getBoardsByOwnerIdTest() throws Exception {
        mockMvc.perform(get(String.format("%s?%s=%d", API_URL, "owner", getBoard2Owner.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$[0].id", is(getBoard2.getId().intValue())))
                .andExpect(jsonPath("$[0].title", is(getBoard2.getTitle())))
                .andExpect(jsonPath("$[0].owner",
                        is(linkBuilder.format("/%s/%d", ORGANIZATIONS_URL,
                                getBoard2Owner.getId().intValue()))));
    }

    @Test
    public void getNotExistsBoardsByOwnerIdTest() throws Exception {
        mockMvc.perform(get(String.format("%s?%s=%d", API_URL, "owner", -1)))
                .andExpect(status().isNotFound());
    }
}
