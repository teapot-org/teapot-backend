package org.teapot.backend.test.repository;

import org.assertj.core.util.Lists;
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
import org.teapot.backend.test.AbstractIT;

import java.time.LocalDate;
import java.util.Arrays;


public class MemberRepositoryIT extends AbstractIT {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;

    private Organization memberRepositoryTestOrg = new Organization();
    private Organization findByUserTestOrg = new Organization();
    private User user1;
    private User user2;
    private Member member1;
    private Member member2;
    private Member member3;

    @Before
    public void setup() {
        memberRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        organizationRepository.deleteAllInBatch();

        memberRepositoryTestOrg.setName("memberRepositoryTestOrg");
        memberRepositoryTestOrg.setCreationDate(LocalDate.now());
        organizationRepository.save(memberRepositoryTestOrg);

        findByUserTestOrg.setName("findByUserTestOrg");
        organizationRepository.save(findByUserTestOrg);

        user1 = new User();
        user1.setUsername("u1");
        user1.setEmail("u1@email.com");
        user1.setPassword("pass");
        userRepository.save(user1);

        member1 = new Member();
        member1.setOrganization(memberRepositoryTestOrg);
        member1.setAdmissionDate(LocalDate.now());
        member1.setStatus(MemberStatus.CREATOR);
        member1.setUser(user1);
        memberRepository.save(member1);

        user2 = new User();
        user2.setUsername("u2");
        user2.setEmail("u2@email.com");
        user2.setPassword("pass");
        userRepository.save(user2);

        member2 = new Member();
        member2.setOrganization(memberRepositoryTestOrg);
        member2.setAdmissionDate(LocalDate.now());
        member2.setStatus(MemberStatus.WORKER);
        member2.setUser(user2);
        memberRepository.save(member2);

        member3 = new Member();
        member3.setOrganization(findByUserTestOrg);
        member3.setAdmissionDate(LocalDate.now());
        member3.setStatus(MemberStatus.CREATOR);
        member3.setUser(user1);
        memberRepository.save(member3);
    }

    @Test
    public void findAllByOrganizationTest() {
        Assert.assertEquals(Arrays.asList(member1, member2),
                memberRepository.findAllByOrganization(memberRepositoryTestOrg));
    }

    @Test
    public void findByOrganizationAndIdTest() {
        Assert.assertEquals(member1, memberRepository
                .findByOrganizationAndId(memberRepositoryTestOrg, member1.getId()));
    }

    @Test
    public void findByOrganizationAndUserTest() {
        Assert.assertEquals(member2, memberRepository
                .findByOrganizationAndUser(memberRepositoryTestOrg, user2));
    }

    @Test
    public void findByUserTest() {
        Assert.assertEquals(Lists.newArrayList(member1, member3)
                ,memberRepository.findByUser(user1)
        );
    }
}
