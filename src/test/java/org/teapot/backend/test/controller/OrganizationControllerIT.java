package org.teapot.backend.test.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;
import org.teapot.backend.repository.user.UserRepository;
import org.teapot.backend.util.LinkBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrganizationControllerIT extends AbstractControllerIT {

    private static final String ORGANIZATIONS_URL = "organizations";
    private static final String MEMBERS_URL = "members";
    private static final String USERS_URL = "users";

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LinkBuilder linkBuilder;

    private Organization getOrganization = new Organization();
    private User user1;
    private User user2;
    private Member creator;
    private Member member2;
    private Organization deleteOrganizationAdmin;
    private Organization deleteOrganizationCreator;
    private Member deleteOrganizationCreatorCreator;
    private User user3;
    private User user4;
    private User user5;
    private User user6;
    private User user7;
    private User user8;
    private User user9;
    private Member worker;
    private Member owner1;
    private Member owner2;
    private Member owner3;
    private Member worker2;
    private Member worker3;
    private Member postMember;
    private Organization postOrganization;
    private Organization repeatPostOrganization;

    private String creatorAccessToken;
    private String workerAccessToken;
    private String owner1AccessToken;

    @Before
    public void addTestDate() throws Exception {
        getOrganization.setName("getOrganization");
        getOrganization.setRegistrationDateTime(LocalDateTime.now());
        organizationRepository.save(getOrganization);

        user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@email.com");
        user1.setPassword("pass");
        user1.setActivated(true);
        user1.setFirstName("user1");
        user1.setLastName("user1");
        userRepository.save(user1);

        creatorAccessToken = obtainAccessToken("user1@email.com", "pass");

        creator = new Member();
        creator.setOrganization(getOrganization);
        creator.setAdmissionDate(LocalDate.now());
        creator.setStatus(MemberStatus.CREATOR);
        creator.setUser(user1);
        memberRepository.save(creator);

        user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@email.com");
        user2.setPassword("pass");
        userRepository.save(user2);

        member2 = new Member();
        member2.setOrganization(getOrganization);
        member2.setAdmissionDate(LocalDate.now());
        member2.setStatus(MemberStatus.WORKER);
        member2.setUser(user2);
        memberRepository.save(member2);

        deleteOrganizationAdmin = new Organization();
        deleteOrganizationAdmin.setName("deleteOrganizationAdmin");
        organizationRepository.save(deleteOrganizationAdmin);

        deleteOrganizationCreator = new Organization();
        deleteOrganizationCreator.setName("deleteOrganizationCreator");
        organizationRepository.save(deleteOrganizationCreator);

        deleteOrganizationCreatorCreator = new Member();
        deleteOrganizationCreatorCreator.setStatus(MemberStatus.CREATOR);
        deleteOrganizationCreatorCreator.setOrganization(deleteOrganizationCreator);
        deleteOrganizationCreatorCreator.setUser(user1);
        memberRepository.save(member2);
        deleteOrganizationCreator.getMembers().add(deleteOrganizationCreatorCreator);

        user3 = new User();
        user3.setName("user3");
        user3.setEmail("user3@email.com");
        user3.setPassword("pass");
        user3.setActivated(true);
        userRepository.save(user3);

        workerAccessToken = obtainAccessToken("user3@email.com", "pass");

        user4 = new User();
        user4.setName("user4");
        user4.setEmail("user4@email.com");
        user4.setPassword("pass");
        user4.setActivated(true);
        userRepository.save(user4);

        user5 = new User();
        user5.setName("user5");
        user5.setEmail("user5@email.com");
        user5.setPassword("pass");
        user5.setActivated(true);
        userRepository.save(user5);

        user6 = new User();
        user6.setName("user6");
        user6.setEmail("user6@email.com");
        user6.setPassword("pass");
        user6.setActivated(true);
        userRepository.save(user6);

        user7 = new User();
        user7.setName("user7");
        user7.setEmail("user7@email.com");
        user7.setPassword("pass");
        user7.setActivated(true);
        userRepository.save(user7);

        user8 = new User();
        user8.setName("user8");
        user8.setEmail("user8@email.com");
        user8.setPassword("pass");
        user8.setActivated(true);
        userRepository.save(user8);

        user9 = new User();
        user9.setName("user9");
        user9.setEmail("user9@email.com");
        user9.setPassword("pass");
        user9.setActivated(true);
        userRepository.save(user9);

        worker = new Member();
        worker.setUser(user3);
        worker.setOrganization(getOrganization);
        worker.setStatus(MemberStatus.WORKER);
        memberRepository.save(worker);

        owner1 = new Member();
        owner1.setUser(user4);
        owner1.setOrganization(getOrganization);
        owner1.setStatus(MemberStatus.OWNER);
        memberRepository.save(owner1);

        owner1AccessToken = obtainAccessToken("user4@email.com", "pass");

        owner2 = new Member();
        owner2.setUser(user5);
        owner2.setOrganization(getOrganization);
        owner2.setStatus(MemberStatus.OWNER);
        memberRepository.save(owner2);

        owner3 = new Member();
        owner3.setUser(user6);
        owner3.setOrganization(getOrganization);
        owner3.setStatus(MemberStatus.OWNER);
        memberRepository.save(owner3);

        worker2 = new Member();
        worker2.setUser(user7);
        worker2.setOrganization(getOrganization);
        worker2.setStatus(MemberStatus.WORKER);
        memberRepository.save(worker2);

        worker3 = new Member();
        worker3.setUser(user8);
        worker3.setOrganization(getOrganization);
        worker3.setStatus(MemberStatus.WORKER);
        memberRepository.save(worker3);

        postOrganization = new Organization();
        postOrganization.setName("postOrganization");

        repeatPostOrganization = new Organization();
        repeatPostOrganization.setName("repeatPostOrganization");
        organizationRepository.save(repeatPostOrganization);

        postMember = new Member();
        postMember.setUser(user9);
        postMember.setStatus(MemberStatus.WORKER);
    }

    // GET ORGANIZATIONS

    @Test
    public void getOrganizationsTestByUser() throws Exception {
        List<Organization> all = organizationRepository.findAll();
        mockMvc.perform(get(String.format("/%s", ORGANIZATIONS_URL))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(all.size())))
                .andExpect(jsonPath("$[0].id", is(all.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(all.get(0).getName())))
                .andExpect(jsonPath("$[0].fullName", is(all.get(0).getFullName())))
                .andExpect(jsonPath("$[0].members",
                        is(linkBuilder.format("/%s/%d/%s", ORGANIZATIONS_URL,
                                all.get(0).getId().intValue(), MEMBERS_URL))));
    }

    @Test
    public void getSingleOrganizationByIdTestByUser() throws Exception {
        mockMvc.perform(get(String.format("/%s/%d", ORGANIZATIONS_URL, getOrganization.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(getOrganization.getId().intValue())))
                .andExpect(jsonPath("$.name", is(getOrganization.getName())))
                .andExpect(jsonPath("$.fullName", is(getOrganization.getFullName())))
                .andExpect(jsonPath("$.members",
                        is(linkBuilder.format("/%s/%d/%s", ORGANIZATIONS_URL,
                                getOrganization.getId().intValue(), MEMBERS_URL))));
    }

    @Test
    public void getNotExistsOrganizationTestByUser() throws Exception {
        mockMvc.perform(get(String.format("/%s/-1", ORGANIZATIONS_URL))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isNotFound());
    }

    // GET MEMBERS

    @Test
    public void getMembersInOrganizationByAdmin() throws Exception {
        List<Member> members = memberRepository.findAllByOrganization(getOrganization);

        mockMvc.perform(get(String.format("/%s/%d/%s", ORGANIZATIONS_URL, getOrganization.getId(), MEMBERS_URL))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(members.size())))
                .andExpect(jsonPath("$[0].id", is(members.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].status", is(members.get(0).getStatus().toString())))
                .andExpect(jsonPath("$[0].user",
                        is(linkBuilder.format("/%s/%d",
                                USERS_URL, members.get(0).getUser().getId()))))
                .andExpect(jsonPath("$[0].organization",
                        is(linkBuilder.format("/%s/%d",
                                ORGANIZATIONS_URL, members.get(0).getOrganization().getId()))));
    }

    @Test
    public void getMembersInOrganizationByMember() throws Exception {
        List<Member> members = memberRepository.findAllByOrganization(getOrganization);

        mockMvc.perform(get(String.format("/%s/%d/%s", ORGANIZATIONS_URL, getOrganization.getId(), MEMBERS_URL))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, creatorAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(members.size())))
                .andExpect(jsonPath("$[0].id", is(members.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].status", is(members.get(0).getStatus().toString())))
                .andExpect(jsonPath("$[0].user",
                        is(linkBuilder.format("/%s/%d",
                                USERS_URL, members.get(0).getUser().getId()))))
                .andExpect(jsonPath("$[0].organization",
                        is(linkBuilder.format("/%s/%d",
                                ORGANIZATIONS_URL, members.get(0).getOrganization().getId()))));
    }

    @Test
    public void getMemberInOrganizationByAdmin() throws Exception {
        Member member = memberRepository.findAllByOrganization(getOrganization).get(0);

        mockMvc.perform(get(String.format("/%s/%d/%s/%d",
                ORGANIZATIONS_URL, member.getOrganization().getId(), MEMBERS_URL, member.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(member.getId().intValue())))
                .andExpect(jsonPath("$.status", is(member.getStatus().toString())))
                .andExpect(jsonPath("$.user",
                        is(linkBuilder.format("/%s/%d",
                                USERS_URL, member.getUser().getId()))))
                .andExpect(jsonPath("$.organization",
                        is(linkBuilder.format("/%s/%d",
                                ORGANIZATIONS_URL, member.getOrganization().getId()))));
    }

    @Test
    public void getMemberInOrganizationByMember() throws Exception {
        Member member = memberRepository.findAllByOrganization(getOrganization).get(0);

        mockMvc.perform(get(String.format("/%s/%d/%s/%d",
                ORGANIZATIONS_URL, member.getOrganization().getId(), MEMBERS_URL, member.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, creatorAccessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(member.getId().intValue())))
                .andExpect(jsonPath("$.status", is(member.getStatus().toString())))
                .andExpect(jsonPath("$.user",
                        is(linkBuilder.format("/%s/%d",
                                USERS_URL, member.getUser().getId()))))
                .andExpect(jsonPath("$.organization",
                        is(linkBuilder.format("/%s/%d",
                                ORGANIZATIONS_URL, member.getOrganization().getId()))));
    }

    @Test
    public void getNotExistsMemberInOrganizationByAdmin() throws Exception {
        Member member = memberRepository.findAllByOrganization(getOrganization).get(0);

        mockMvc.perform(get(String.format("/%s/%d/%s/%d",
                ORGANIZATIONS_URL, member.getOrganization().getId(), MEMBERS_URL, -1))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isNotFound());
    }

    // DELETE ORGANIZATIONS

    @Test
    public void deleteOrganizationTestByAnonymous() throws Exception {
        mockMvc.perform(delete(String.format("/%s/%d", ORGANIZATIONS_URL, getOrganization.getId())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteOrganizationTestByUser() throws Exception {
        mockMvc.perform(delete(String.format("/%s/%d", ORGANIZATIONS_URL, getOrganization.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, userAccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteOrganizationTestByAdmin() throws Exception {
        mockMvc.perform(delete(String.format("/%s/%d", ORGANIZATIONS_URL, deleteOrganizationAdmin.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNotExistsOrganizationTestByAdmin() throws Exception {
        mockMvc.perform(delete(String.format("/%s/-1", ORGANIZATIONS_URL))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteOrganizationTestByCreator() throws Exception {
        mockMvc.perform(delete(String.format("/%s/%d", ORGANIZATIONS_URL, deleteOrganizationCreator.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, creatorAccessToken)))
                .andExpect(status().isNoContent());
    }

    // DELETE MEMBERS

    @Test
    public void deleteCreatorTestByOwner() throws Exception {
        mockMvc.perform(delete(String.format("/%s/%d/%s/%d", ORGANIZATIONS_URL, getOrganization.getId(),
                MEMBERS_URL, creator.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, owner1AccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteCreatorTestByWorker() throws Exception {
        mockMvc.perform(delete(String.format("/%s/%d/%s/%d", ORGANIZATIONS_URL, getOrganization.getId(),
                MEMBERS_URL, creator.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, workerAccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteOwnerTestByWorker() throws Exception {
        mockMvc.perform(delete(String.format("/%s/%d/%s/%d", ORGANIZATIONS_URL, getOrganization.getId(),
                MEMBERS_URL, owner2.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, workerAccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteOwnerTestByCreator() throws Exception {
        mockMvc.perform(delete(String.format("/%s/%d/%s/%d", ORGANIZATIONS_URL, getOrganization.getId(),
                MEMBERS_URL, owner2.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, creatorAccessToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteOwnerTestByOwner() throws Exception {
        mockMvc.perform(delete(String.format("/%s/%d/%s/%d", ORGANIZATIONS_URL, getOrganization.getId(),
                MEMBERS_URL, owner3.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, owner1AccessToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteWorkerTestByWorker() throws Exception {
        mockMvc.perform(delete(String.format("/%s/%d/%s/%d", ORGANIZATIONS_URL, getOrganization.getId(),
                MEMBERS_URL, worker.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, workerAccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteWorkerTestByOwner() throws Exception {
        mockMvc.perform(delete(String.format("/%s/%d/%s/%d", ORGANIZATIONS_URL, getOrganization.getId(),
                MEMBERS_URL, worker2.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, owner1AccessToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteWorkerTestByCreator() throws Exception {
        mockMvc.perform(delete(String.format("/%s/%d/%s/%d", ORGANIZATIONS_URL, getOrganization.getId(),
                MEMBERS_URL, worker3.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, creatorAccessToken)))
                .andExpect(status().isNoContent());
    }

    // POST ORGANIZATIONS

    @Test
    public void createOrganizationByAdmin() throws Exception {
        mockMvc.perform(post(String.format("/%s", ORGANIZATIONS_URL))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(json(postOrganization))
                .contentType(contentType))
                .andExpect(status().isCreated());
        Assert.assertNotNull(organizationRepository.findByName(postOrganization.getName()));
    }

    @Test
    public void createExistingOrganizationByAdmin() throws Exception {
        mockMvc.perform(post(String.format("/%s", ORGANIZATIONS_URL))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(json(repeatPostOrganization))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    // POST MEMBERS

    @Test
    public void addMemberToOrganizationByAdmin() throws Exception {
        mockMvc.perform(post(String.format("/%s/%d/%s", ORGANIZATIONS_URL, getOrganization.getId(), MEMBERS_URL))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(String.format("{\"userId\":%d,\"status\":\"%s\"}",
                        postMember.getUser().getId(), postMember.getStatus()))
                .contentType(contentType))
                .andExpect(status().isCreated());
        Assert.assertNotNull(memberRepository.findByOrganizationAndUser(getOrganization, postMember.getUser()));
    }

    @Test
    public void addExistingMemberToOrganizationByAdmin() throws Exception {
        mockMvc.perform(post(String.format("/%s/%d/%s", ORGANIZATIONS_URL, getOrganization.getId(), MEMBERS_URL))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(String.format("{\"userId\":%d,\"status\":\"%s\"}",
                        creator.getUser().getId(), creator.getStatus()))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    // PATCH ORGANIZATIONS

    @Test
    public void changeNameTestByAdmin() throws Exception {
        mockMvc.perform(patch(String.format("/%s/%d", ORGANIZATIONS_URL, getOrganization.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("name", "newName"))
                .andExpect(status().isNoContent());

        Organization org = organizationRepository.findOne(getOrganization.getId());
        Assert.assertEquals("newName", org.getName());
    }

    @Test
    public void changeFullNameTestByAdmin() throws Exception {
        mockMvc.perform(patch(String.format("/%s/%d", ORGANIZATIONS_URL, getOrganization.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("fullName", "newFullName"))
                .andExpect(status().isNoContent());

        Organization org = organizationRepository.findOne(getOrganization.getId());
        Assert.assertEquals("newFullName", org.getFullName());
    }

    @Test
    public void changeNameAndFullNameTestByAdmin() throws Exception {
        mockMvc.perform(patch(String.format("/%s/%d", ORGANIZATIONS_URL, getOrganization.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("name", "newName1")
                .param("fullName", "newFullName1"))
                .andExpect(status().isNoContent());

        Organization org = organizationRepository.findOne(getOrganization.getId());
        Assert.assertEquals("newName1", org.getName());
        Assert.assertEquals("newFullName1", org.getFullName());
    }

    @Test
    public void changeNameAndFullNameTestByCreator() throws Exception {
        mockMvc.perform(patch(String.format("/%s/%d", ORGANIZATIONS_URL, getOrganization.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, creatorAccessToken))
                .param("name", "newName1")
                .param("fullName", "newFullName1"))
                .andExpect(status().isNoContent());

        Organization org = organizationRepository.findOne(getOrganization.getId());
        Assert.assertEquals("newName1", org.getName());
        Assert.assertEquals("newFullName1", org.getFullName());
    }

    @Test
    public void changeNameAndFullNameTestByOwner() throws Exception {
        mockMvc.perform(patch(String.format("/%s/%d", ORGANIZATIONS_URL, getOrganization.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, owner1AccessToken))
                .param("name", "newName1")
                .param("fullName", "newFullName1"))
                .andExpect(status().isNoContent());

        Organization org = organizationRepository.findOne(getOrganization.getId());
        Assert.assertEquals("newName1", org.getName());
        Assert.assertEquals("newFullName1", org.getFullName());
    }

    // PATCH MEMBERS

    @Test
    public void changeCreatorStatusToWorkerTestByAdmin() throws Exception {
        mockMvc.perform(patch(String.format("/%s/%d/%s/%d",
                ORGANIZATIONS_URL, getOrganization.getId(), MEMBERS_URL, creator.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("status", "WORKER"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void changeOwnerStatusToCreatorTestByAdmin() throws Exception {
        mockMvc.perform(patch(String.format("/%s/%d/%s/%d",
                ORGANIZATIONS_URL, getOrganization.getId(), MEMBERS_URL, owner1.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("status", "CREATOR"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void changeOwnerStatusToWorkerTestByAdmin() throws Exception {
        mockMvc.perform(patch(String.format("/%s/%d/%s/%d",
                ORGANIZATIONS_URL, getOrganization.getId(), MEMBERS_URL, owner2.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("status", "WORKER"))
                .andExpect(status().isNoContent());
        Assert.assertEquals(MemberStatus.WORKER,
                memberRepository.findByOrganizationAndId(getOrganization, owner2.getId()).getStatus());
    }

    @Test
    public void changeWorkerStatusToOwnerTestByAdmin() throws Exception {
        mockMvc.perform(patch(String.format("/%s/%d/%s/%d",
                ORGANIZATIONS_URL, getOrganization.getId(), MEMBERS_URL, worker2.getId()))
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("status", "OWNER"))
                .andExpect(status().isNoContent());
        Assert.assertEquals(MemberStatus.OWNER,
                memberRepository.findByOrganizationAndId(getOrganization, worker2.getId()).getStatus());
    }
}
