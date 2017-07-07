package org.teapot.backend.test.repository;

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
import org.teapot.backend.test.AbstractIT;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class MemberRepositoryIT extends AbstractIT {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;

    private Organization testOrganization = new Organization();
    private User testUser = new User();
    private Member testMember = new Member();

    @Before
    public void setup() {
        memberRepository.deleteAllInBatch();

        testOrganization.setName("memberRepositoryTestOrg");
        organizationRepository.save(testOrganization);

        testUser.setName("memberRepositoryTestOrgUser");
        testUser.setEmail("memberRepositoryTestOrgUser@mail");
        testUser.setPassword("pass");
        userRepository.save(testUser);

        testMember.setUser(testUser);
        testMember.setStatus(MemberStatus.CREATOR);
        testMember.setOrganization(testOrganization);
        memberRepository.save(testMember);
    }

    @Test
    public void findByStatusTest() {
        assertEquals(asList(testMember), memberRepository.findByStatus(
                MemberStatus.CREATOR,
                null
        ).getContent());
    }

    @Test
    public void findByOrganizationIdAndStatusTest() {
        assertEquals(asList(testMember), memberRepository.findByOrganizationIdAndStatus(
                testOrganization.getId(),
                MemberStatus.CREATOR,
                null
        ).getContent());
    }

    @Test
    public void findByOrganizationNameAndStatusTest() {
        assertEquals(asList(testMember), memberRepository.findByOrganizationNameAndStatus(
                testOrganization.getName(),
                MemberStatus.CREATOR,
                null
        ).getContent());
    }

    @Test
    public void findByUserIdAndStatusTest() {
        assertEquals(asList(testMember), memberRepository.findByUserIdAndStatus(
                testUser.getId(),
                MemberStatus.CREATOR,
                null
        ).getContent());
    }

    @Test
    public void findByUserNameAndStatusTest() {
        assertEquals(asList(testMember), memberRepository.findByUserNameAndStatus(
                testUser.getName(),
                MemberStatus.CREATOR,
                null
        ).getContent());
    }

    @Test
    public void findByUserEmailAndStatusTest() {
        assertEquals(asList(testMember), memberRepository.findByUserEmailAndStatus(
                testUser.getEmail(),
                MemberStatus.CREATOR,
                null
        ).getContent());
    }

    @Test
    public void findByOrganizationIdTest() {
        assertEquals(asList(testMember), memberRepository.findByOrganizationId(
                testOrganization.getId(),
                null
        ).getContent());
    }

    public void findByOrganizationNameTest() {
        assertEquals(asList(testMember), memberRepository.findByOrganizationName(
                testOrganization.getName(),
                null
        ).getContent());
    }

    public void findByUserIdTest() {
        assertEquals(asList(testMember), memberRepository.findByUserId(
                testUser.getId(),
                null
        ).getContent());
    }

    public void findByUserNameTest() {
        assertEquals(asList(testMember), memberRepository.findByUserName(
                testUser.getName(),
                null
        ).getContent());
    }

    public void findByUserEmailTest() {
        assertEquals(asList(testMember), memberRepository.findByUserEmail(
                testUser.getEmail(),
                null
        ).getContent());
    }

    public void findByIdAndOrganizationIdTest() {
        assertEquals(testMember, memberRepository.findByIdAndOrganizationId(
                testMember.getId(),
                testOrganization.getId()
        ));
    }

    public void findByIdAndOrganizationNameTest() {
        assertEquals(testMember, memberRepository.findByIdAndOrganizationName(
                testMember.getId(),
                testOrganization.getName()
        ));
    }

    public void findByOrganizationIdAndUserIdTest() {
        assertEquals(testMember, memberRepository.findByOrganizationIdAndUserId(
                testOrganization.getId(),
                testUser.getId()
        ));
    }

    public void findByOrganizationNameAndUserIdTest() {
        assertEquals(testMember, memberRepository.findByOrganizationNameAndUserId(
                testOrganization.getName(),
                testUser.getId()
        ));
    }

    public void findByOrganizationIdAndUserNameTest() {
        assertEquals(testMember, memberRepository.findByOrganizationIdAndUserName(
                testOrganization.getId(),
                testUser.getName()
        ));
    }

    public void findByOrganizationIdAndUserEmailTest() {
        assertEquals(testMember, memberRepository.findByOrganizationIdAndUserEmail(
                testOrganization.getId(),
                testUser.getEmail()
        ));
    }

    public void findByOrganizationNameAndUserNameTest() {
        assertEquals(testMember, memberRepository.findByOrganizationNameAndUserName(
                testOrganization.getName(),
                testUser.getName()
        ));

    }

    public void findByOrganizationNameAndUserEmailTest() {
        assertEquals(testMember, memberRepository.findByOrganizationNameAndUserEmail(
                testOrganization.getName(),
                testUser.getEmail()
        ));
    }

}
