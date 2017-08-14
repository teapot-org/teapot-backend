package org.teapot.backend.test.controller;

import lombok.Getter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.KanbanAccess;
import org.teapot.backend.model.kanban.TicketList;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.OwnerRepository;
import org.teapot.backend.repository.kanban.KanbanRepository;
import org.teapot.backend.repository.kanban.TicketListRepository;
import org.teapot.backend.repository.organization.MemberRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.teapot.backend.controller.kanban.TicketListController.SINGLE_TICKET_LIST_ENDPOINT;
import static org.teapot.backend.controller.kanban.TicketListController.TICKET_LISTS_ENDPOINT;

public class TicketListControllerIT extends AbstractControllerIT {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private KanbanRepository kanbanRepository;

    @Autowired
    private TicketListRepository ticketListRepository;

    private Organization organization = new Organization();
    private User owner = new User();
    private User organizationWorker = new User();
    private User kanbanContributor = new User();
    private String ownerAccessToken;
    private String workerAccessToken;
    private String contributorAccessToken;
    private Kanban organizationKanban = new Kanban("testKanban", organization);
    private TicketList savedOrganizationTicketList = new TicketList("test");
    private TicketList notSavedOrganizationTicketList = new TicketList("notsaved");
    private TicketListDto notSavedOrganizationTicketListDto;

    private Kanban userKanban = new Kanban("testKanban2", owner, KanbanAccess.PRIVATE);
    private TicketList savedUserTicketList = new TicketList("teeest");
    private TicketList notSavedUserTicketList = new TicketList("teeest_notsaved");
    private TicketListDto notSavedUserTicketListDto;

    @Before
    public void setup() throws Exception {
        organization.setName("test");
        ownerRepository.save(organization);

        owner.setName("owner");
        owner.setEmail("owner@mail");
        owner.setPassword("pass");
        owner.setActivated(true);

        organizationWorker.setName("organizationWorker");
        organizationWorker.setEmail("organizationWorker@mail");
        organizationWorker.setPassword("pass");
        organizationWorker.setActivated(true);

        kanbanContributor.setName("kanbanContributor");
        kanbanContributor.setEmail("kanbanContributor@mail");
        kanbanContributor.setPassword("pass");
        kanbanContributor.setActivated(true);

        ownerRepository.save(Arrays.asList(owner, organizationWorker, kanbanContributor));
        ownerAccessToken = obtainAccessToken(owner.getEmail(), "pass");
        workerAccessToken = obtainAccessToken(organizationWorker.getEmail(), "pass");
        contributorAccessToken = obtainAccessToken(kanbanContributor.getEmail(), "pass");

        memberRepository.save(new Member(owner, MemberStatus.OWNER, organization));
        memberRepository.save(new Member(organizationWorker, MemberStatus.WORKER, organization));

        organizationKanban.getContributors().add(kanbanContributor);
        organizationKanban.addTicketList(savedOrganizationTicketList);

        userKanban.getContributors().add(kanbanContributor);
        userKanban.addTicketList(savedUserTicketList);

        kanbanRepository.save(Arrays.asList(organizationKanban, userKanban));

        organizationKanban.addTicketList(notSavedOrganizationTicketList);
        notSavedOrganizationTicketListDto = new TicketListDto(this, notSavedOrganizationTicketList);

        userKanban.addTicketList(notSavedUserTicketList);
        notSavedUserTicketListDto = new TicketListDto(this, notSavedUserTicketList);
    }

    public ResultActions isTicketListJsonAsExpected(ResultActions resultActions, String jsonPath, TicketList ticketList)
            throws Exception {
        return resultActions.andExpect(jsonPath(jsonPath + ".title", is(ticketList.getTitle())));
    }

    // GET

    @Test
    public void getTicketListsByAdminTest() throws Exception {
        List<TicketList> all = ticketListRepository.findAll();

        ResultActions result = mockMvc.perform(get(TICKET_LISTS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        for (int i = 0; i < all.size(); i++) {
            result = isTicketListJsonAsExpected(result, format("$._embedded.ticketLists[%d]", i), all.get(i));
        }
    }

    @Test
    public void getTicketListsByUser() throws Exception {
        List<TicketList> publicTicketLists = ticketListRepository.findAll().stream()
                .filter(ticketList -> ticketList.getKanban().getAccess().equals(KanbanAccess.PUBLIC))
                .collect(Collectors.toList());

        mockMvc.perform(get(TICKET_LISTS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.ticketLists", hasSize(publicTicketLists.size())));
    }

    @Test
    public void getTicketListsByAnonymousTest() throws Exception {
        List<TicketList> publicTicketLists = ticketListRepository.findAll().stream()
                .filter(ticketList -> ticketList.getKanban().getAccess().equals(KanbanAccess.PUBLIC))
                .collect(Collectors.toList());

        mockMvc.perform(get(TICKET_LISTS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.ticketLists", hasSize(publicTicketLists.size())));
    }

    @Test
    public void getOrganizationTicketListByAdmin() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isTicketListJsonAsExpected(result, "$", savedOrganizationTicketList);
    }

    @Test
    public void getOrganizationTicketListByOwner() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isTicketListJsonAsExpected(result, "$", savedOrganizationTicketList);
    }

    @Test
    public void getOrganizationTicketListByWorker() throws Exception {
        mockMvc.perform(get(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, workerAccessToken)))
                .andExpect(status().isOk());
    }

    @Test
    public void getOrganizationTicketListByContributor() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, contributorAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isTicketListJsonAsExpected(result, "$", savedOrganizationTicketList);
    }

    @Test
    public void getOrganizationTicketListByUser() throws Exception {
        mockMvc.perform(get(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isOk());
    }

    @Test
    public void getOrganizationTicketListByAnonymous() throws Exception {
        mockMvc.perform(get(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserTicketListByAdmin() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_TICKET_LIST_ENDPOINT, savedUserTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isTicketListJsonAsExpected(result, "$", savedUserTicketList);
    }

    @Test
    public void getUserTicketListByOwner() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_TICKET_LIST_ENDPOINT, savedUserTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isTicketListJsonAsExpected(result, "$", savedUserTicketList);
    }

    @Test
    public void getUserTicketListByContributor() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_TICKET_LIST_ENDPOINT, savedUserTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, contributorAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isTicketListJsonAsExpected(result, "$", savedUserTicketList);
    }

    @Test
    public void getUserTicketListByUser() throws Exception {
        mockMvc.perform(get(SINGLE_TICKET_LIST_ENDPOINT, savedUserTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getUserTicketListByAnonymous() throws Exception {
        mockMvc.perform(get(SINGLE_TICKET_LIST_ENDPOINT, savedUserTicketList.getId()))
                .andExpect(status().isUnauthorized());
    }

    // POST

    @Test
    public void createOrganizationTicketListByOwnerTest() throws Exception {
        ResultActions result = mockMvc.perform(post(TICKET_LISTS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken))
                .contentType(contentType)
                .content(json(notSavedOrganizationTicketListDto)))
                .andExpect(status().isCreated());

        isTicketListJsonAsExpected(result, "$", notSavedOrganizationTicketList);
    }

    @Test
    public void createOrganizationTicketListByContributorTest() throws Exception {
        ResultActions result = mockMvc.perform(post(TICKET_LISTS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, contributorAccessToken))
                .contentType(contentType)
                .content(json(notSavedOrganizationTicketListDto)))
                .andExpect(status().isCreated());

        isTicketListJsonAsExpected(result, "$", notSavedOrganizationTicketList);
    }

    @Test
    public void createOrganizationTicketListByWorkerTest() throws Exception {
        mockMvc.perform(post(TICKET_LISTS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, workerAccessToken))
                .contentType(contentType)
                .content(json(notSavedOrganizationTicketListDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createOrganizationTicketListByUserTest() throws Exception {
        mockMvc.perform(post(TICKET_LISTS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken))
                .contentType(contentType)
                .content(json(notSavedOrganizationTicketListDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createOrganizationTicketListByAnonymousTest() throws Exception {
        mockMvc.perform(post(TICKET_LISTS_ENDPOINT)
                .contentType(contentType)
                .content(json(notSavedOrganizationTicketListDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void createUserTicketListByOwnerTest() throws Exception {
        ResultActions result = mockMvc.perform(post(TICKET_LISTS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken))
                .contentType(contentType)
                .content(json(notSavedUserTicketListDto)))
                .andExpect(status().isCreated());

        isTicketListJsonAsExpected(result, "$", notSavedUserTicketList);
    }

    @Test
    public void createUserTicketListByContributorTest() throws Exception {
        ResultActions result = mockMvc.perform(post(TICKET_LISTS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, contributorAccessToken))
                .contentType(contentType)
                .content(json(notSavedUserTicketListDto)))
                .andExpect(status().isCreated());

        isTicketListJsonAsExpected(result, "$", notSavedUserTicketList);
    }

    @Test
    public void createUserTicketListByUserTest() throws Exception {
        mockMvc.perform(post(TICKET_LISTS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken))
                .contentType(contentType)
                .content(json(notSavedUserTicketListDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createUserTicketListByAnonymousTest() throws Exception {
        mockMvc.perform(post(TICKET_LISTS_ENDPOINT)
                .contentType(contentType)
                .content(json(notSavedUserTicketListDto)))
                .andExpect(status().isUnauthorized());
    }

    // PATCH

    @Test
    public void changeOrganizationTicketListTitleByOwnerTest() throws Exception {
        mockMvc.perform(patch(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken))
                .param("title", "qwerty"))
                .andExpect(status().isNoContent());

        assertEquals("qwerty", ticketListRepository.findOne(savedOrganizationTicketList.getId()).getTitle());
    }

    @Test
    public void changeOrganizationTicketListTitleByContributorTest() throws Exception {
        mockMvc.perform(patch(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, contributorAccessToken))
                .param("title", "qwerty"))
                .andExpect(status().isNoContent());

        assertEquals("qwerty", ticketListRepository.findOne(savedOrganizationTicketList.getId()).getTitle());
    }

    @Test
    public void changeOrganizationTicketListTitleByWorkerTest() throws Exception {
        mockMvc.perform(patch(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, workerAccessToken))
                .param("title", "qwerty"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void changeOrganizationTicketListTitleByUserTest() throws Exception {
        mockMvc.perform(patch(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken))
                .param("title", "qwerty"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void changeOrganizationTicketListTitleByAnonymousTest() throws Exception {
        mockMvc.perform(patch(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .param("title", "qwerty"))
                .andExpect(status().isUnauthorized());
    }

    // DELETE

    @Test
    public void deleteOrganizationTicketListByAdminTest() throws Exception {
        mockMvc.perform(delete(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isNoContent());

        assertNull(ticketListRepository.findOne(savedOrganizationTicketList.getId()));
    }

    @Test
    public void deleteOrganizationTicketListByOwnerTest() throws Exception {
        mockMvc.perform(delete(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken)))
                .andExpect(status().isNoContent());

        assertNull(ticketListRepository.findOne(savedOrganizationTicketList.getId()));
    }

    @Test
    public void deleteOrganizationTicketListByContributorTest() throws Exception {
        mockMvc.perform(delete(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, contributorAccessToken)))
                .andExpect(status().isNoContent());

        assertNull(ticketListRepository.findOne(savedOrganizationTicketList.getId()));
    }

    @Test
    public void deleteOrganizationTicketListByWorkerTest() throws Exception {
        mockMvc.perform(delete(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, workerAccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteOrganizationTicketListByUserTest() throws Exception {
        mockMvc.perform(delete(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, workerAccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteOrganizationTicketListByAnonymousTest() throws Exception {
        mockMvc.perform(delete(SINGLE_TICKET_LIST_ENDPOINT, savedOrganizationTicketList.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, workerAccessToken)))
                .andExpect(status().isForbidden());
    }

    @Getter
    static class TicketListDto {
        private String title;
        private String kanban;

        TicketListDto(AbstractControllerIT test, TicketList ticketList) {
            title = ticketList.getTitle();
            kanban = test.linkFor(ticketList.getKanban());
        }
    }
}
