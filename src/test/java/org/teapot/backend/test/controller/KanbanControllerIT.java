package org.teapot.backend.test.controller;

import lombok.Getter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.KanbanAccess;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.kanban.KanbanRepository;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.teapot.backend.controller.kanban.KanbanController.*;

public class KanbanControllerIT extends AbstractControllerIT {

    @Autowired
    private KanbanRepository kanbanRepository;

    private User kanbanOwnerUser = new User();
    private Kanban savedPublicKanban = new Kanban("savedPublicKanban", kanbanOwnerUser);
    private Kanban savedPrivateKanban = new Kanban("savedPrivateKanban", kanbanOwnerUser);
    private Kanban notSavedPublicKanban = new Kanban("notSavedPublicKanban", kanbanOwnerUser);
    private KanbanDto notSavedPublicKanbanDto;
    private String ownerAccessToken;

    @Before
    public void addTestUsers() throws Exception {
        kanbanOwnerUser.setName("kanbanOwnerUser");
        kanbanOwnerUser.setEmail("kanbanOwnerUser@mail");
        kanbanOwnerUser.setPassword("pass");
        kanbanOwnerUser.setActivated(true);
        userRepository.save(kanbanOwnerUser);

        ownerAccessToken = obtainAccessToken("kanbanOwnerUser@mail", "pass");

        savedPrivateKanban.setAccess(KanbanAccess.PRIVATE);
        kanbanRepository.save(Arrays.asList(savedPublicKanban, savedPrivateKanban));

        notSavedPublicKanbanDto = new KanbanDto(this, notSavedPublicKanban);
    }

    public ResultActions isKanbanJsonAsExpected(ResultActions resultActions, String jsonPath, Kanban kanban)
            throws Exception {
        return resultActions
                .andExpect(jsonPath(jsonPath + ".title", is(kanban.getTitle())))
                .andExpect(jsonPath(jsonPath + ".access", is(kanban.getAccess().toString())));
    }

    @Test
    public void getKanbansTestByAdmin() throws Exception {
        List<Kanban> allKanbans = kanbanRepository.findAll();

        ResultActions result = mockMvc.perform(get(KANBANS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.kanbans", hasSize(allKanbans.size())));

        for (int i = 0; i < allKanbans.size(); i++) {
            result = isKanbanJsonAsExpected(result, format("$._embedded.kanbans[%d]", i), allKanbans.get(i));
        }
    }

    // GET

    @Test
    public void getKanbansTestByAnonymous() throws Exception {
        List<Kanban> allPublicKanbans = kanbanRepository.findByAccess(KanbanAccess.PUBLIC);

        ResultActions result = mockMvc.perform(get(KANBANS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.kanbans", hasSize(allPublicKanbans.size())));

        for (int i = 0; i < allPublicKanbans.size(); i++) {
            result = isKanbanJsonAsExpected(result, format("$._embedded.kanbans[%d]", i), allPublicKanbans.get(i));
        }
    }

    @Test
    public void getPublicKanbanByIdByAdminTest() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_KANBAN_ENDPOINT, savedPublicKanban.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isKanbanJsonAsExpected(result, "$", savedPublicKanban);
    }

    @Test
    public void getPublicKanbanByIdByUserTest() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_KANBAN_ENDPOINT, savedPublicKanban.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isKanbanJsonAsExpected(result, "$", savedPublicKanban);
    }

    @Test
    public void getPublicKanbanByIdByAnonymousTest() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_KANBAN_ENDPOINT, savedPublicKanban.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isKanbanJsonAsExpected(result, "$", savedPublicKanban);
    }

    @Test
    public void getPrivateKanbanByIdByAdminTest() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_KANBAN_ENDPOINT, savedPrivateKanban.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isKanbanJsonAsExpected(result, "$", savedPrivateKanban);
    }

    @Test
    public void getPrivateKanbanByIdByOwnerTest() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_KANBAN_ENDPOINT, savedPrivateKanban.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isKanbanJsonAsExpected(result, "$", savedPrivateKanban);
    }

    @Test
    public void getPrivateKanbanByIdNotByOwnerTest() throws Exception {
        mockMvc.perform(get(SINGLE_KANBAN_ENDPOINT, savedPrivateKanban.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getPrivateKanbanByIdByAnonymousTest() throws Exception {
        mockMvc.perform(get(SINGLE_KANBAN_ENDPOINT, savedPrivateKanban.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getNotExistsKanbanByIdTest() throws Exception {
        mockMvc.perform(get(SINGLE_KANBAN_ENDPOINT, -1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getOwnerKanbansByAnonymousTest() throws Exception {
        List<Kanban> kanbans = kanbanRepository.findByOwnerAndAccess(kanbanOwnerUser, KanbanAccess.PUBLIC);

        ResultActions result = mockMvc.perform(get(SINGLE_OWNER_KANBANS, kanbanOwnerUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.kanbans", hasSize(kanbans.size())));

        for (int i = 0; i < kanbans.size(); i++) {
            result = isKanbanJsonAsExpected(result, format("$._embedded.kanbans[%d]", i), kanbans.get(i));
        }
    }

    @Test
    public void getOwnerKanbansByAdminTest() throws Exception {
        List<Kanban> kanbans = kanbanRepository.findByOwner(kanbanOwnerUser);

        ResultActions result = mockMvc.perform(get(SINGLE_OWNER_KANBANS, kanbanOwnerUser.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.kanbans", hasSize(kanbans.size())));

        for (int i = 0; i < kanbans.size(); i++) {
            result = isKanbanJsonAsExpected(result, format("$._embedded.kanbans[%d]", i), kanbans.get(i));
        }
    }

    @Test
    public void getKanbansByOwnerTest() throws Exception {
        List<Kanban> kanbans = kanbanRepository.findByOwner(kanbanOwnerUser);

        ResultActions result = mockMvc.perform(get(SINGLE_OWNER_KANBANS, kanbanOwnerUser.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.kanbans", hasSize(kanbans.size())));

        for (int i = 0; i < kanbans.size(); i++) {
            result = isKanbanJsonAsExpected(result, format("$._embedded.kanbans[%d]", i), kanbans.get(i));
        }
    }

    // POST

    @Test
    public void createKanbanByOwnerTest() throws Exception {
        ResultActions result = mockMvc.perform(post(KANBANS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken))
                .contentType(contentType)
                .content(json(notSavedPublicKanbanDto)))
                .andExpect(status().isCreated());

        isKanbanJsonAsExpected(result, "$", notSavedPublicKanban);
    }

    @Test
    public void createKanbanNotByOwnerTest() throws Exception {
        mockMvc.perform(post(KANBANS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken))
                .contentType(contentType)
                .content(json(notSavedPublicKanbanDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createKanbanByAnonymousTest() throws Exception {
        mockMvc.perform(post(KANBANS_ENDPOINT)
                .contentType(contentType)
                .content(json(notSavedPublicKanban)))
                .andExpect(status().isUnauthorized());
    }

    // PATCH

    @Test
    public void changeKanbanAccessByOwnerTest() throws Exception {
        mockMvc.perform(patch(SINGLE_KANBAN_ENDPOINT, savedPublicKanban.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken))
                .param("access", "PRIVATE"))
                .andExpect(status().isNoContent());

        Kanban changedKanban = kanbanRepository.findOne(savedPublicKanban.getId());
        assertNotNull(changedKanban);
        assertEquals(KanbanAccess.PRIVATE, changedKanban.getAccess());
    }

    @Test
    public void changeKanbanAccessByAdminTest() throws Exception {
        mockMvc.perform(patch(SINGLE_KANBAN_ENDPOINT, savedPublicKanban.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("access", "PRIVATE"))
                .andExpect(status().isNoContent());

        Kanban changedKanban = kanbanRepository.findOne(savedPublicKanban.getId());
        assertNotNull(changedKanban);
        assertEquals(KanbanAccess.PRIVATE, changedKanban.getAccess());
    }

    @Test
    public void changeKanbanAccessNotByOwnerTest() throws Exception {
        mockMvc.perform(patch(SINGLE_KANBAN_ENDPOINT, savedPublicKanban.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken))
                .param("access", "PRIVATE"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void changeKanbanAccessByAnonymousTest() throws Exception {
        mockMvc.perform(patch(SINGLE_KANBAN_ENDPOINT, savedPublicKanban.getId())
                .param("access", "PRIVATE"))
                .andExpect(status().isUnauthorized());
    }

    // DELETE

    @Test
    public void deleteKanbanByOwnerTest() throws Exception {
        mockMvc.perform(delete(SINGLE_KANBAN_ENDPOINT, savedPublicKanban.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken)))
                .andExpect(status().isNoContent());

        assertNull(kanbanRepository.findOne(savedPublicKanban.getId()));
    }

    @Test
    public void deleteKanbanByAdminTest() throws Exception {
        mockMvc.perform(delete(SINGLE_KANBAN_ENDPOINT, savedPublicKanban.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isNoContent());

        assertNull(kanbanRepository.findOne(savedPublicKanban.getId()));
    }

    @Test
    public void deleteKanbanNotByOwnerTest() throws Exception {
        mockMvc.perform(delete(SINGLE_KANBAN_ENDPOINT, savedPublicKanban.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteKanbanByAnonymousTest() throws Exception {
        mockMvc.perform(delete(SINGLE_KANBAN_ENDPOINT, savedPublicKanban.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Getter
    static class KanbanDto {
        private String title;
        private String owner;
        private String project;
        private KanbanAccess access;

        KanbanDto(AbstractControllerIT test, Kanban kanban) {
            title = kanban.getTitle();
            owner = test.linkFor(kanban.getOwner());
            project = test.linkFor(kanban.getProject());
            access = kanban.getAccess();
        }
    }
}
