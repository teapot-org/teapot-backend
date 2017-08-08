package org.teapot.backend.test.controller;

import lombok.Getter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.Ticket;
import org.teapot.backend.model.kanban.TicketList;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.kanban.KanbanRepository;
import org.teapot.backend.repository.kanban.TicketListRepository;
import org.teapot.backend.repository.kanban.TicketRepository;

import java.util.Arrays;

import static java.lang.String.format;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.teapot.backend.controller.kanban.TicketController.SINGLE_TICKET_ENDPOINT;
import static org.teapot.backend.controller.kanban.TicketController.TICKETS_ENDPOINT;

public class TicketControllerIT extends AbstractControllerIT {

    @Autowired
    private KanbanRepository kanbanRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketListRepository ticketListRepository;

    private User owner = new User();
    private User subscriber1 = new User();
    private User subscriber2 = new User();
    private User notSubscriber = new User();
    private String ownerAccessToken;
    private String subscriber1AccessToken;
    private String subscriber2AccessToken;
    private String notSubscriberAccessToken;
    private Kanban kanban1 = new Kanban("testKanban1", owner);
    private Kanban kanban2 = new Kanban("testKanban2", owner);
    private TicketList ticketList1 = new TicketList("nana");
    private TicketList ticketList2 = new TicketList("iaia");
    private TicketList ticketList3 = new TicketList("foo");
    private Ticket savedTicket = new Ticket("lala1", "lalala");
    private Ticket notSavedTicket = new Ticket("ooooo", "papapa");
    private TicketDto notSavedTicketDto;

    @Before
    public void setup() throws Exception {
        owner.setName("owner");
        owner.setEmail("owner@mail");
        owner.setPassword("pass");
        owner.setActivated(true);

        subscriber1.setName("subscriber1");
        subscriber1.setEmail("subscriber1@mail");
        subscriber1.setPassword("pass");
        subscriber1.setActivated(true);

        subscriber2.setName("subscriber2");
        subscriber2.setEmail("subscriber2@mail");
        subscriber2.setPassword("pass");
        subscriber2.setActivated(true);

        notSubscriber.setName("notSubscriber");
        notSubscriber.setEmail("notSubscriber@mail");
        notSubscriber.setPassword("pass");
        notSubscriber.setActivated(true);

        userRepository.save(Arrays.asList(owner, subscriber1, subscriber2, notSubscriber));
        ownerAccessToken = obtainAccessToken(owner.getEmail(), "pass");
        subscriber1AccessToken = obtainAccessToken(subscriber1.getEmail(), "pass");
        subscriber2AccessToken = obtainAccessToken(subscriber2.getEmail(), "pass");
        notSubscriberAccessToken = obtainAccessToken(notSubscriber.getEmail(), "pass");

        kanban1.getContributors().addAll(Arrays.asList(subscriber1, subscriber2, notSubscriber));
        kanban1.addTicketList(ticketList1);
        kanban1.addTicketList(ticketList2);
        kanban2.addTicketList(ticketList3);

        ticketList1.addTicket(savedTicket);

        savedTicket.getSubscribers().addAll(Arrays.asList(subscriber1, subscriber2));

        kanbanRepository.save(Arrays.asList(kanban1, kanban2));

        ticketList1.addTicket(notSavedTicket);
        notSavedTicketDto = new TicketDto(this, notSavedTicket);
    }

    public ResultActions isTicketJsonAsExpected(ResultActions resultActions, String jsonPath, Ticket ticket)
            throws Exception {
        return resultActions
                .andExpect(jsonPath(jsonPath + ".title", is(ticket.getTitle())))
                .andExpect(jsonPath(jsonPath + ".description", is(ticket.getDescription())));
    }

    // GET

    @Test
    public void getTicketByOwner() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_TICKET_ENDPOINT, savedTicket.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isTicketJsonAsExpected(result, "$", savedTicket);
    }

    @Test
    public void getTicketByContributor() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_TICKET_ENDPOINT, savedTicket.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, subscriber1AccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isTicketJsonAsExpected(result, "$", savedTicket);
    }

    @Test
    public void getTicketByUser() throws Exception {
        mockMvc.perform(get(SINGLE_TICKET_ENDPOINT, savedTicket.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isOk());
    }

    @Test
    public void getTicketByAnonymous() throws Exception {
        mockMvc.perform(get(SINGLE_TICKET_ENDPOINT, savedTicket.getId()))
                .andExpect(status().isOk());
    }

    // POST

    @Test
    public void createTicketByOwner() throws Exception {
        ResultActions result = mockMvc.perform(post(TICKETS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken))
                .contentType(contentType)
                .content(json(notSavedTicketDto)))
                .andExpect(status().isCreated());

        isTicketJsonAsExpected(result, "$", notSavedTicket);
    }

    @Test
    public void createTicketByContributor() throws Exception {
        ResultActions result = mockMvc.perform(post(TICKETS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, subscriber1AccessToken))
                .contentType(contentType)
                .content(json(notSavedTicketDto)))
                .andExpect(status().isCreated());

        isTicketJsonAsExpected(result, "$", notSavedTicket);
    }

    @Test
    public void createTicketByUser() throws Exception {
        mockMvc.perform(post(TICKETS_ENDPOINT)
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken))
                .contentType(contentType)
                .content(json(notSavedTicketDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createTicketByAnonymous() throws Exception {
        mockMvc.perform(post(TICKETS_ENDPOINT)
                .contentType(contentType)
                .content(json(notSavedTicketDto)))
                .andExpect(status().isUnauthorized());
    }

    // PATCH - rename

    @Test
    public void renameTicketByOwner() throws Exception {
        mockMvc.perform(patch(SINGLE_TICKET_ENDPOINT, savedTicket.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken))
                .param("title", "qwerty")
                .param("description", "ytrewq"))
                .andExpect(status().isNoContent());

        Ticket ticket = ticketRepository.findOne(savedTicket.getId());
        assertEquals("qwerty", ticket.getTitle());
        assertEquals("ytrewq", ticket.getDescription());
    }

    @Test
    public void renameTicketByContributor() throws Exception {
        mockMvc.perform(patch(SINGLE_TICKET_ENDPOINT, savedTicket.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, subscriber1AccessToken))
                .param("title", "qwerty")
                .param("description", "ytrewq"))
                .andExpect(status().isNoContent());

        Ticket ticket = ticketRepository.findOne(savedTicket.getId());
        assertEquals("qwerty", ticket.getTitle());
        assertEquals("ytrewq", ticket.getDescription());
    }

    @Test
    public void renameTicketByUser() throws Exception {
        mockMvc.perform(patch(SINGLE_TICKET_ENDPOINT, savedTicket.getId())
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken))
                .param("title", "qwerty")
                .param("description", "ytrewq"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void renameTicketByAnonymous() throws Exception {
        mockMvc.perform(patch(SINGLE_TICKET_ENDPOINT, savedTicket.getId())
                .param("title", "qwerty")
                .param("description", "ytrewq"))
                .andExpect(status().isUnauthorized());
    }

    // PATCH - shift

    @Test
    public void moveTicketToAnotherListByOwner() throws Exception {
        mockMvc.perform(patch(TICKETS_ENDPOINT + "/move")
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, ownerAccessToken))
                .param("ticket", savedTicket.getId().toString())
                .param("list", ticketList2.getId().toString()))
                .andExpect(status().isNoContent());

        assertFalse(ticketListRepository.findOne(ticketList1.getId()).getTickets().contains(savedTicket));
        assertTrue(ticketListRepository.findOne(ticketList2.getId()).getTickets().contains(savedTicket));
    }

    @Test
    public void moveTicketToAnotherListByContributor() throws Exception {
        mockMvc.perform(patch(TICKETS_ENDPOINT + "/move")
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, subscriber1AccessToken))
                .param("ticket", savedTicket.getId().toString())
                .param("list", ticketList2.getId().toString()))
                .andExpect(status().isNoContent());

        assertFalse(ticketListRepository.findOne(ticketList1.getId()).getTickets().contains(savedTicket));
        assertTrue(ticketListRepository.findOne(ticketList2.getId()).getTickets().contains(savedTicket));
    }

    @Test
    public void moveTicketToAnotherListByUser() throws Exception {
        mockMvc.perform(patch(TICKETS_ENDPOINT + "/move")
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, userAccessToken))
                .param("ticket", savedTicket.getId().toString())
                .param("list", ticketList2.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void moveTicketToAnotherListByAnonymous() throws Exception {
        mockMvc.perform(patch(TICKETS_ENDPOINT + "/move")
                .param("ticket", savedTicket.getId().toString())
                .param("list", ticketList2.getId().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void moveTicketToAnotherListNotSameKanbanByAdmin() throws Exception {
        mockMvc.perform(patch(TICKETS_ENDPOINT + "/move")
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("ticket", savedTicket.getId().toString())
                .param("list", ticketList3.getId().toString()))
                .andExpect(status().isForbidden());
    }

    // PATCH - subscribe

    @Test
    public void subscribeContributorByAdmin() throws Exception {
        mockMvc.perform(patch(TICKETS_ENDPOINT + "/subscribe")
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("ticket", savedTicket.getId().toString())
                .param("user", notSubscriber.getId().toString()))
                .andExpect(status().isNoContent());

        assertTrue(ticketRepository.findOne(savedTicket.getId()).getSubscribers().contains(notSubscriber));
    }

    @Test
    public void subscribeContributorByHimself() throws Exception {
        mockMvc.perform(patch(TICKETS_ENDPOINT + "/subscribe")
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, notSubscriberAccessToken))
                .param("ticket", savedTicket.getId().toString())
                .param("user", notSubscriber.getId().toString()))
                .andExpect(status().isNoContent());

        assertTrue(ticketRepository.findOne(savedTicket.getId()).getSubscribers().contains(notSubscriber));
    }

    @Test
    public void subscribeContributorByContributor() throws Exception {
        mockMvc.perform(patch(TICKETS_ENDPOINT + "/subscribe")
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, subscriber1AccessToken))
                .param("ticket", savedTicket.getId().toString())
                .param("user", notSubscriber.getId().toString()))
                .andExpect(status().isForbidden());
    }

    // PATCH - unsubscribe

    @Test
    public void unsubscribeContributorByAdmin() throws Exception {
        mockMvc.perform(patch(TICKETS_ENDPOINT + "/unsubscribe")
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("ticket", savedTicket.getId().toString())
                .param("user", subscriber1.getId().toString()))
                .andExpect(status().isNoContent());

        assertFalse(ticketRepository.findOne(savedTicket.getId()).getSubscribers().contains(subscriber1));
    }

    @Test
    public void unsubscribeContributorByHimself() throws Exception {
        mockMvc.perform(patch(TICKETS_ENDPOINT + "/unsubscribe")
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, subscriber1AccessToken))
                .param("ticket", savedTicket.getId().toString())
                .param("user", subscriber1.getId().toString()))
                .andExpect(status().isNoContent());

        assertFalse(ticketRepository.findOne(savedTicket.getId()).getSubscribers().contains(subscriber1));
    }

    @Test
    public void unsubscribeContributorByAnotherContributor() throws Exception {
        mockMvc.perform(patch(TICKETS_ENDPOINT + "/unsubscribe")
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, subscriber2AccessToken))
                .param("ticket", savedTicket.getId().toString())
                .param("user", subscriber1.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void unsubscribeNotSubscriberByAdmin() throws Exception {
        mockMvc.perform(patch(TICKETS_ENDPOINT + "/unsubscribe")
                .header(AUTHORIZATION, format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("ticket", savedTicket.getId().toString())
                .param("user", notSubscriber.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Getter
    static class TicketDto {
        private String title;
        private String description;
        private String ticketList;

        TicketDto(AbstractControllerIT test, Ticket ticket) {
            title = ticket.getTitle();
            description = ticket.getDescription();
            ticketList = test.linkFor(ticket.getTicketList());
        }
    }
}
