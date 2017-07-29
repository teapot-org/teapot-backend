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
import static org.junit.Assert.assertNotNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.teapot.backend.controller.organization.MemberController.MEMBERS_ENDPOINT;
import static org.teapot.backend.controller.organization.MemberController.SINGLE_MEMBER_ENDPOINT;

public class MemberControllerIT extends AbstractControllerIT {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Organization savedOrganization = new Organization();
    private Organization notSavedOrganization = new Organization();

    private User creatorUser = new User();
    private User owner1User = new User();
    private User owner2User = new User();
    private User worker1User = new User();
    private User worker2User = new User();
    private User notSavedMemberUser = new User();

    private Member creator = new Member();
    private Member owner1 = new Member();
    private Member owner2 = new Member();
    private Member worker1 = new Member();
    private Member worker2 = new Member();
    private Member notSavedMember = new Member();

    private String creatorAccessToken;
    private String owner1AccessToken;
    private String worker1AccessToken;

    static ResultActions isMemberJsonAsExpected(ResultActions resultActions, String jsonPath, Member member)
            throws Exception {
        return resultActions.andExpect(jsonPath(jsonPath + ".status", is(member.getStatus().name())));
    }

    @Before
    public void setUp() throws Exception {
        savedOrganization.setName("OrganizationControllerTest1");
        organizationRepository.save(savedOrganization);

        notSavedOrganization.setName("OrganizationControllerTest2");

        creatorUser.setName("creatorUser");
        creatorUser.setEmail("creatorUser@email.com");
        creatorUser.setPassword("pass");
        creatorUser.setActivated(true);

        owner1User.setName("owner1User");
        owner1User.setEmail("owner1User@email.com");
        owner1User.setPassword("pass");
        owner1User.setActivated(true);

        owner2User.setName("owner2User");
        owner2User.setEmail("owner2User@email.com");
        owner2User.setPassword("pass");
        owner2User.setActivated(true);

        worker1User.setName("worker1User");
        worker1User.setEmail("worker1User@email.com");
        worker1User.setPassword("pass");
        worker1User.setActivated(true);

        worker2User.setName("worker2User");
        worker2User.setEmail("worker2User@email.com");
        worker2User.setPassword("pass");
        worker2User.setActivated(true);

        notSavedMemberUser.setName("notSavedMemberUser");
        notSavedMemberUser.setEmail("notSavedMemberUser@email.com");
        notSavedMemberUser.setPassword("pass");
        notSavedMemberUser.setActivated(true);

        userRepository.save(Arrays.asList(
                creatorUser, owner1User, owner2User, worker1User, worker2User, notSavedMemberUser));

        creator.setUser(creatorUser);
        creator.setOrganization(savedOrganization);
        creator.setStatus(MemberStatus.CREATOR);

        owner1.setUser(owner1User);
        owner1.setOrganization(savedOrganization);
        owner1.setStatus(MemberStatus.OWNER);

        owner2.setUser(owner2User);
        owner2.setOrganization(savedOrganization);
        owner2.setStatus(MemberStatus.OWNER);

        worker1.setUser(worker1User);
        worker1.setOrganization(savedOrganization);
        worker1.setStatus(MemberStatus.WORKER);

        worker2.setUser(worker2User);
        worker2.setOrganization(savedOrganization);
        worker2.setStatus(MemberStatus.WORKER);

        memberRepository.save(Arrays.asList(creator, owner1, owner2, worker1, worker2));

        notSavedMember.setUser(notSavedMemberUser);
        notSavedMember.setOrganization(savedOrganization);
        notSavedMember.setStatus(MemberStatus.WORKER);

        creatorAccessToken = obtainAccessToken(creatorUser.getEmail(), "pass");
        worker1AccessToken = obtainAccessToken(worker1User.getEmail(), "pass");
        owner1AccessToken = obtainAccessToken(owner1User.getEmail(), "pass");
    }

    // GET MEMBERS

    @Test
    public void getAllMembersTest() throws Exception {
        List<Member> members = memberRepository.findAll();

        ResultActions result = mockMvc.perform(get(MEMBERS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.members", hasSize(members.size())));

        for (int i = 0; i < members.size(); i++) {
            isMemberJsonAsExpected(result, format("$._embedded.members[%d]", i), members.get(i));
        }
    }

    @Test
    public void getMemberInOrganizationTest() throws Exception {
        Member member = memberRepository.findOne(creator.getId());

        ResultActions result = mockMvc.perform(get(SINGLE_MEMBER_ENDPOINT, creator.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isMemberJsonAsExpected(result, "$", member);
    }

    // DELETE MEMBERS

    @Test
    public void deleteCreatorTestByOwner() throws Exception {
        mockMvc.perform(delete(SINGLE_MEMBER_ENDPOINT, creator.getId())
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, owner1AccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteCreatorTestByWorker() throws Exception {
        mockMvc.perform(delete(SINGLE_MEMBER_ENDPOINT, creator.getId())
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, worker1AccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteOwnerTestByWorker() throws Exception {
        mockMvc.perform(delete(SINGLE_MEMBER_ENDPOINT, owner2.getId())
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, worker1AccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteOwnerTestByCreator() throws Exception {
        mockMvc.perform(delete(SINGLE_MEMBER_ENDPOINT, owner2.getId())
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, creatorAccessToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteOwnerTestByOwner() throws Exception {
        mockMvc.perform(delete(SINGLE_MEMBER_ENDPOINT, owner2.getId())
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, owner1AccessToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteWorkerTestByWorker() throws Exception {
        mockMvc.perform(delete(SINGLE_MEMBER_ENDPOINT, worker2.getId())
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, worker1AccessToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteWorkerTestByOwner() throws Exception {
        mockMvc.perform(delete(SINGLE_MEMBER_ENDPOINT, worker2.getId())
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, owner1AccessToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteWorkerTestByCreator() throws Exception {
        mockMvc.perform(delete(SINGLE_MEMBER_ENDPOINT, worker2.getId())
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, creatorAccessToken)))
                .andExpect(status().isNoContent());
    }

    // POST MEMBERS

    @Test
    public void addMemberToOrganizationByAdmin() throws Exception {
        mockMvc.perform(post(MEMBERS_ENDPOINT)
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(json(notSavedMember))
                .contentType(contentType))
                .andExpect(status().isCreated());

        assertNotNull(memberRepository.findByOrganizationIdAndUserName(savedOrganization.getId(),
                notSavedMemberUser.getName()));
    }

    @Test
    public void addExistingMemberToOrganizationByAdmin() throws Exception {
        mockMvc.perform(post(MEMBERS_ENDPOINT)
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .content(json(creator))
                .contentType(contentType))
                .andExpect(status().isConflict());
    }

    // PATCH MEMBERS

    @Test
    public void changeCreatorStatusToWorkerTestByAdmin() throws Exception {
        mockMvc.perform(patch(SINGLE_MEMBER_ENDPOINT, creator.getId())
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("status", "WORKER"))
                .andExpect(status().isConflict());
    }

    @Test
    public void changeOwnerStatusToCreatorTestByAdmin() throws Exception {
        mockMvc.perform(patch(SINGLE_MEMBER_ENDPOINT, owner2.getId())
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("status", "CREATOR"))
                .andExpect(status().isConflict());
    }

    @Test
    public void changeOwnerStatusToWorkerTestByAdmin() throws Exception {
        mockMvc.perform(patch(SINGLE_MEMBER_ENDPOINT, owner2.getId())
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("status", "WORKER"))
                .andExpect(status().isNoContent());

        assertEquals(MemberStatus.WORKER,
                memberRepository.findByIdAndOrganizationId(owner2.getId(), savedOrganization.getId()).getStatus());
    }

    @Test
    public void changeWorkerStatusToOwnerTestByAdmin() throws Exception {
        mockMvc.perform(patch(SINGLE_MEMBER_ENDPOINT, worker2.getId())
                .header(AUTHORIZATION, String.format("%s %s", BEARER_TYPE, adminAccessToken))
                .param("status", "OWNER"))
                .andExpect(status().isNoContent());
        Assert.assertEquals(MemberStatus.OWNER,
                memberRepository.findByIdAndOrganizationId(worker2.getId(), savedOrganization.getId()).getStatus());
    }
}
