package org.teapot.backend.repository.organization;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Page<Member> findAllByOrganization(Organization organization, Pageable pageable);

    List<Member> findAllByOrganization(Organization organization);

    Member findByOrganizationAndId(Organization organization, Long id);

    Member findByOrganizationAndUser(Organization organization, User user);

    List<Member> findByUser(User user);

    List<Member> findByUser(User user, Pageable pageable);
}
