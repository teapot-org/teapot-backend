package org.teapot.backend.test.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.kanban.KanbanRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;

import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.teapot.backend.controller.kanban.KanbanController.KANBANS_ENDPOINT;
import static org.teapot.backend.controller.kanban.KanbanController.SINGLE_KANBAN_ENDPOINT;

public class KanbanControllerIT extends AbstractControllerIT {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private KanbanRepository kanbanRepository;

    private Organization savedKanbanOwner = new Organization();

    private Kanban savedKanban = new Kanban("savedKanban", savedKanbanOwner);

    static ResultActions isKanbanJsonAsExpected(ResultActions resultActions, String jsonPath, Kanban kanban)
            throws Exception {
        return resultActions.andExpect(jsonPath(jsonPath + ".title", is(kanban.getTitle())));
    }

    @Before
    public void addTestUsers() {
        savedKanbanOwner.setName("getKanban1Owner");
        organizationRepository.save(savedKanbanOwner);

        kanbanRepository.save(savedKanban);
    }

    @Test
    public void getKanbansTest() throws Exception {
        List<Kanban> allKanbans = kanbanRepository.findAll();

        ResultActions result = mockMvc.perform(get(KANBANS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.kanbans", hasSize(allKanbans.size())));

        for (int i = 0; i < allKanbans.size(); i++) {
            isKanbanJsonAsExpected(result, format("$._embedded.kanbans[%d]", i), allKanbans.get(i));
        }
    }

    @Test
    public void getSingleKanbanByIdTest() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_KANBAN_ENDPOINT, savedKanban.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isKanbanJsonAsExpected(result, "$", savedKanban);
    }

    @Test
    public void getNotExistsKanbanByIdTest() throws Exception {
        mockMvc.perform(get(SINGLE_KANBAN_ENDPOINT, -1))
                .andExpect(status().isNotFound());
    }

//    getOwnerKanbansByAnonymousTest
//    getOwnerKanbansByAdminTest
//    createKanbanByOwnerTest
//            createKanbanNotByOwnerTest
//    changeKanbanAccessByOwnerTest
//            changeKanbanAccessByAnonymousTest
}
