package org.teapot.backend.controller.organization;

import com.google.common.primitives.Longs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.exception.BadRequestException;
import org.teapot.backend.controller.exception.ForbiddenException;
import org.teapot.backend.controller.exception.ResourceNotFoundException;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.model.user.UserAuthority;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;
import org.teapot.backend.repository.user.UserRepository;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Organization findOrganizationByIdOrName(String idOrName) {
        Long id = Longs.tryParse(idOrName);
        return ofNullable((id != null)
                ? organizationRepository.findOne(id)
                : organizationRepository.findByName(idOrName))
                .orElseThrow(ResourceNotFoundException::new);
    }

    /**
     * GET /organizations
     * ?[pageNumber(number)]&[pageSize(number)]&[offset(number)]&[sort(string)]
     * <p>
     * Доступен всем пользователям.
     * Возвращает список всех организаций (или не всех, при указании параметров в запросе).
     * <p>
     * Возможные коды состояний:
     * 200 OK
     */
    @GetMapping
    public List<Organization> getOrganizations(Pageable pageable) {
        return organizationRepository.findAll(pageable).getContent();
    }

    /**
     * GET /organizations
     * ?user(number|string)&[pageNumber(number)]&[pageSize(number)]&[offset(number)]&[sort(string)]
     * <p>
     * Доступен всем пользователям
     * Возвращает список организаций, в которых состоит пользователь с указанным id или username.
     * <p>
     * Возможные коды состояний:
     * 200 OK
     * 404 Not Found
     */
    @GetMapping(params = "user")
    public List<Organization> getOrganizationsOfUser(
            @RequestParam("user") String idOrUsername,
            Pageable pageable
    ) {
        Long id = Longs.tryParse(idOrUsername);
        User user = ofNullable((id != null)
                ? userRepository.findOne(id)
                : userRepository.findByUsername(idOrUsername))
                .orElseThrow(ResourceNotFoundException::new);

        return memberRepository.findByUser(user, pageable)
                .stream()
                .map(Member::getOrganization)
                .collect(Collectors.toList());
    }

    /**
     * GET /organizations/{idOrName}
     * <p>
     * Доступен всем пользователям.
     * Возвращает организацию с указанным id или name.
     * <p>
     * Возможные коды состояний:
     * 200 OK
     * 404 Not Found
     */
    @GetMapping("/{idOrName:.+}")
    public Organization getOrganization(
            @PathVariable String idOrName
    ) {
        Organization organization = findOrganizationByIdOrName(idOrName);
        if (organization == null) {
            throw new ResourceNotFoundException();
        }

        return organization;
    }

    /**
     * DELETE /organizations/{id}
     * <p>
     * Доступен администраторам или создателю организации.
     * Удаляет организацию с указанным id.
     * <p>
     * Возможные коды состояний:
     * 204 No Content
     * 401 Unauthorized
     * 403 Forbidden
     * 404 Not Found
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{organizationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrganization(
            @PathVariable Long organizationId,
            Authentication auth
    ) {
        Organization organization = organizationRepository
                .findOne(organizationId);
        if (organization == null) {
            throw new ResourceNotFoundException();
        }

        Member authMember = memberRepository
                .findByOrganizationAndUser(organization,
                        userRepository.findByEmail(auth.getName()));

        if ((auth.getAuthorities().contains(UserAuthority.ADMIN))
                || (authMember != null) && (authMember.getStatus().equals(MemberStatus.CREATOR))) {
            organizationRepository.delete(organization);
        } else {
            throw new ForbiddenException();
        }
    }

    /**
     * POST /organizations
     * Тело запроса:
     * {
     * "name": "название",
     * "fillName": "полное название" (не обязательно)
     * }
     * <p>
     * Метод доступен всем авторизованным пользователям.
     * Создает новую организацию с указанными данными. Устаналивает пользователя, выполнившего запрос, создателем
     * организации. Устаналивает заголовок Location и возвращает созданную организацию.
     * <p>
     * Возможные коды состояний:
     * 201 Created
     * 400 Bad Request
     * 401 Unauthorized
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Organization createOrganization(
            @RequestBody Organization organization,
            HttpServletResponse response,
            Authentication auth
    ) {
        if (organizationRepository.findByName(organization.getName()) != null) {
            throw new BadRequestException();
        }

        organization.setCreationDate(LocalDate.now());
        organizationRepository.saveAndFlush(organization);

        Member creator = new Member();
        creator.setAdmissionDate(LocalDate.now());
        creator.setOrganization(organization);
        creator.setStatus(MemberStatus.CREATOR);
        creator.setUser(userRepository.findByEmail(auth.getName()));
        memberRepository.save(creator);
        organization.getMembers().add(creator);

        response.setHeader(HttpHeaders.LOCATION,
                "/organizations/" + organization.getId());
        return organization;
    }

    /**
     * PATCH /organizations/{id}
     * ?[name(string)]&[fullName(string)]
     * <p>
     * Доступен администраторам, создателю организации и владельцам организации.
     * Изменяет name и fullName организации, если соответсвующие параметры указаны.
     * <p>
     * Возможные коды состояний:
     * 204 No Content
     * 401 Unauthorized
     * 403 Forbidden
     * 404 Not Found
     */
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchOrganization(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String fullName,
            Authentication auth
    ) {
        Organization organization = organizationRepository.findOne(id);
        if (organization == null) {
            throw new ResourceNotFoundException();
        }

        Member authMember = memberRepository.findByOrganizationAndUser(organization,
                userRepository.findByEmail(auth.getName()));

        if ((auth.getAuthorities().contains(UserAuthority.ADMIN))
                || (authMember != null) && (authMember.getStatus().equals(MemberStatus.CREATOR))
                || (authMember != null) && (authMember.getStatus().equals(MemberStatus.OWNER))) {

            if (name != null) {
                organization.setName(name);
            }
            if (fullName != null) {
                organization.setFullName(fullName);
            }
            organizationRepository.save(organization);

        } else {
            throw new ForbiddenException();
        }
    }

    /**
     * GET /organizations/{organizationId}/members
     * ?[pageNumber(number)]&[pageSize(number)]&[offset(number)]&[sort(string)]
     * <p>
     * Доступен всем пользователям.
     * Возвращает список всех участников организации (или не всех, при указании параметров в запросе).
     * <p>
     * Возможные коды состояний:
     * 200 OK
     * 404 Not Found
     */
    @GetMapping("/{organizationIdOrName}/members")
    public List<Member> getOrganizationMembers(
            @PathVariable String organizationIdOrName,
            Pageable pageable
    ) {
        Organization organization = ofNullable(
                findOrganizationByIdOrName(organizationIdOrName))
                .orElseThrow(ResourceNotFoundException::new);

        return memberRepository
                .findAllByOrganization(organization, pageable)
                .getContent();
    }

    /**
     * GET /organizations/{organizationId}/members/{memberId}
     * <p>
     * <p>
     * Доступен администраторам, создателю и владельцам организации.
     * Возвращает участника memberId организации organizationId.
     * <p>
     * Возможные коды состояний:
     * 200 OK
     * 404 Not Found
     */
    @GetMapping("/{organizationIdOrName}/members/{memberId}")
    public Member getOrganizationMember(
            @PathVariable String organizationIdOrName,
            @PathVariable Long memberId,
            Authentication auth
    ) {

        Organization organization = ofNullable(
                findOrganizationByIdOrName(organizationIdOrName))
                .orElseThrow(ResourceNotFoundException::new);

        return ofNullable(memberRepository
                .findByOrganizationAndId(organization, memberId))
                .orElseThrow(ResourceNotFoundException::new);
    }

    /**
     * POST /organizations/{organizationId}/members
     * Тело запроса:
     * {
     * "status": "статус",
     * "userId": 123
     * }
     * <p>
     * Доступен только администраторам.
     * Добавляет в организацию участника с полученными данными. Нельзя добавить участника со статусом создателя, так
     * как создатель может быть только один.
     * <p>
     * Возможные коды состояний:
     * 201 Created
     * 400 Bad Request
     * 401 Unauthorized
     * 403 Forbidden
     * 404 Not Found
     */
    // todo: приглашение нового участника
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{organizationId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public Member addMember(
            @PathVariable Long organizationId,
            @RequestBody Member member,
            HttpServletResponse response
    ) {
        Organization organization = ofNullable(
                organizationRepository.findOne(organizationId))
                .orElseThrow(ResourceNotFoundException::new);

        if (memberRepository.findByOrganizationAndUser(organization,
                member.getUser()) != null) {
            throw new BadRequestException();
        }

        member.setAdmissionDate(LocalDate.now());
        member.setOrganization(organization);
        if (member.getStatus().equals(MemberStatus.CREATOR)) {
            // нельзя добавить нового создателя организации
            member.setStatus(MemberStatus.OWNER);
        }
        memberRepository.save(member);

        response.setHeader(HttpHeaders.LOCATION,
                String.format("/organizations/%d/members/%d",
                        organizationId, member.getId()));
        return member;
    }

    /**
     * PATCH /organizations/{organizationId}/members/{memberId}
     * ?status(string)
     * <p>
     * Доступен администраторам, создателю и владельцам организации.
     * Изменяет статус участника. Нельзя изменить статус создателя и статус другого участника на статус создателя.
     * <p>
     * Возможные коды состояний:
     * 204 No Content
     * 401 Unauthorized
     * 403 Forbidden
     * 404 Not Found
     */
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{organizationId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchMember(
            @PathVariable Long organizationId,
            @PathVariable Long memberId,
            @RequestParam("status") MemberStatus newStatus,
            Authentication auth
    ) {
        Organization organization = ofNullable(
                organizationRepository.findOne(organizationId))
                .orElseThrow(ResourceNotFoundException::new);

        Member member = ofNullable(memberRepository
                .findByOrganizationAndId(organization, memberId))
                .orElseThrow(ResourceNotFoundException::new);

        Member authMember = memberRepository
                .findByOrganizationAndUser(organization,
                        userRepository.findByEmail(auth.getName()));

        if ((auth.getAuthorities().contains(UserAuthority.ADMIN))
                || (authMember != null) && (authMember.getStatus().equals(MemberStatus.CREATOR))
                || (authMember != null) && (authMember.getStatus().equals(MemberStatus.OWNER))) {

            if (!member.getStatus().equals(MemberStatus.CREATOR)
                    && !newStatus.equals(MemberStatus.CREATOR)) {

                member.setStatus(newStatus);
                memberRepository.save(member);
                return;
            }
        }
        throw new ForbiddenException();
    }

    /**
     * DELETE /organizations/{organizationId}/members/{memberId}
     * <p>
     * Доступен администраторам, создателю и владельцам организации.
     * Удаляет участника memberId из организации organizationId.
     * <p>
     * Возможные коды состояний:
     * 204 No Content
     * 401 Unauthorized
     * 403 Forbidden
     * 404 Not Found
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{organizationId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(
            @PathVariable Long organizationId,
            @PathVariable Long memberId,
            Authentication auth
    ) {
        Organization organization = ofNullable(
                organizationRepository.findOne(organizationId))
                .orElseThrow(ResourceNotFoundException::new);

        Member member = ofNullable(memberRepository
                .findByOrganizationAndId(organization, memberId))
                .orElseThrow(ResourceNotFoundException::new);

        Member authMember = memberRepository
                .findByOrganizationAndUser(organization,
                        userRepository.findByEmail(auth.getName()));

        if (auth.getAuthorities().contains(UserAuthority.ADMIN)) {
            memberRepository.delete(member);
            return;

        } else if ((authMember != null) && (authMember.getStatus().equals(MemberStatus.CREATOR))
                || (authMember != null) && (authMember.getStatus().equals(MemberStatus.OWNER))) {

            if (!member.getStatus().equals(MemberStatus.CREATOR)) {
                memberRepository.delete(member);
                return;
            }
        }
        throw new ForbiddenException();
    }
}
