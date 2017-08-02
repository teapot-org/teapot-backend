package org.teapot.backend.repository.organization;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Override
    @PreAuthorize("@members.hasAnyStatus(#id, 'CREATOR', 'OWNER') " +
            "and !@members.memberHasStatus(#id, 'CREATOR') " +
            "or hasRole('ADMIN')")
    void delete(@Param("id") Long id);

    @Override
    @PreAuthorize("@members.hasAnyStatus(#member, 'CREATOR', 'OWNER') " +
            "and !@members.memberHasStatus(#member, 'CREATOR') " +
            "or hasRole('ADMIN')")
    void delete(@Param("member") Member member);

    @RestResource(exported = false)
    List<Member> findByStatus(MemberStatus status);

    @RestResource(path = "find-by-status")
    Page<Member> findByStatus(@Param("status") MemberStatus status, Pageable pageable);

    @RestResource(exported = false)
    List<Member> findByOrganizationAndStatus(Organization organization, MemberStatus status);

    @RestResource(path = "find-by-organization-id-and-status")
    Page<Member> findByOrganizationIdAndStatus(@Param("organizationId") Long organizationId,
                                               @Param("status") MemberStatus status,
                                               Pageable pageable);

    @RestResource(path = "find-by-organization-name-and-status")
    Page<Member> findByOrganizationNameAndStatus(@Param("organizationName") String organizationName,
                                                 @Param("status") MemberStatus status,
                                                 Pageable pageable);

    @RestResource(exported = false)
    List<Member> findByUserAndStatus(User user, MemberStatus status);

    @RestResource(path = "find-by-user-id-and-status")
    Page<Member> findByUserIdAndStatus(@Param("userId") Long userId,
                                       @Param("status") MemberStatus status,
                                       Pageable pageable);

    @RestResource(path = "find-by-user-name-and-status")
    Page<Member> findByUserNameAndStatus(@Param("userName") String userName,
                                         @Param("status") MemberStatus status,
                                         Pageable pageable);

    @RestResource(path = "find-by-user-email-and-status")
    Page<Member> findByUserEmailAndStatus(@Param("userEmail") String userEmail,
                                          @Param("status") MemberStatus status,
                                          Pageable pageable);

    @RestResource(exported = false)
    List<Member> findByOrganization(Organization organization);

    @RestResource(path = "find-by-organization-id")
    Page<Member> findByOrganizationId(@Param("organizationId") Long organizationId, Pageable pageable);

    @RestResource(path = "find-by-organization-name")
    Page<Member> findByOrganizationName(@Param("organizationName") String organizationName, Pageable pageable);

    @RestResource(exported = false)
    List<Member> findByUser(User user);

    @RestResource(path = "find-by-user-id")
    Page<Member> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @RestResource(path = "find-by-user-name")
    Page<Member> findByUserName(@Param("userName") String userName, Pageable pageable);

    @RestResource(path = "find-by-user-email")
    Page<Member> findByUserEmail(@Param("userEmail") String userEmail, Pageable pageable);

    @RestResource(exported = false)
    Member findByIdAndOrganization(Long id, Organization organization);

    @RestResource(path = "find-by-id-and-organization-id")
    Member findByIdAndOrganizationId(@Param("id") Long id, @Param("organizationId") Long organizationId);

    @RestResource(path = "find-by-id-and-organization-name")
    Member findByIdAndOrganizationName(@Param("id") Long id, @Param("organizationName") String organizationName);

    @RestResource(exported = false)
    Member findByOrganizationAndUser(Organization organization, User user);

    @RestResource(exported = false)
    Member findByOrganizationAndUserEmail(Organization organization, String userEmail);

    @RestResource(path = "find-by-organization-id-and-user-id")
    Member findByOrganizationIdAndUserId(@Param("organizationId") Long organizationId, @Param("userId") Long userId);

    @RestResource(path = "find-by-organization-name-and-user-id")
    Member findByOrganizationNameAndUserId(@Param("organizationName") String organizationName,
                                           @Param("userId") Long userId);

    @RestResource(path = "find-by-organization-id-and-user-name")
    Member findByOrganizationIdAndUserName(@Param("organizationId") Long organizationId,
                                           @Param("userName") String userName);

    @RestResource(path = "find-by-organization-id-and-user-email")
    Member findByOrganizationIdAndUserEmail(@Param("organizationId") Long organizationId,
                                            @Param("userEmail") String userEmail);

    @RestResource(path = "find-by-organization-name-and-user-name")
    Member findByOrganizationNameAndUserName(@Param("organizationName") String organizationName,
                                             @Param("userName") String userName);

    @RestResource(path = "find-by-organization-name-and-user-email")
    Member findByOrganizationNameAndUserEmail(@Param("organizationName") String organizationName,
                                              @Param("userEmail") String userEmail);
}
