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
import org.teapot.backend.model.user.UserAuthority;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;
import org.teapot.backend.repository.user.UserRepository;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

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
     * Метод доступен только пользователям с ролью ADMIN.
     * Возвращает список всех организаций, либо список не всех организаций,
     * если в параметрах запроса указаны номер страницы, количество элементов
     * на странице и порядок сортировки.
     * Устаналивает код состояния 200 OK
     *
     * @param pageable объект, содержащий параметры запроса - номер страницы,
     *                 кол-во элементов на странице, порядок сортировки
     * @return сформированный список организаций
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Organization> getOrganizations(Pageable pageable) {
        return organizationRepository.findAll(pageable).getContent();
    }

    /**
     * Метод доступен только пользователям с ролью ADMIN или участникам
     * организации.
     * Ищет в базе данных организацию с указанным id или name. Если
     * организация найдена, возвращает эту организацию и устаналивает код
     * состояния 200 OK; если организация не найдена - устаналивает код
     * состояния 404 Not Found, выбрасывая исключение
     * {@link ResourceNotFoundException}
     *
     * @param idOrName id или name организации
     * @param auth     данные об аутентификации пользователя
     * @return найденная организация
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{idOrName:.+}")
    public Organization getOrganization(
            @PathVariable String idOrName,
            Authentication auth
    ) {
        Organization organization = findOrganizationByIdOrName(idOrName);
        if (organization == null) {
            throw new ResourceNotFoundException();
        }

        Member authMember = memberRepository
                .findByOrganizationAndUser(organization,
                        userRepository.findByEmail(auth.getName()));

        if ((auth.getAuthorities().contains(UserAuthority.ADMIN))
                || (authMember != null)) {
            return organization;
        }
        throw new ForbiddenException();
    }

    /**
     * Метод доступен только пользователям с ролью ADMIN или владельцам
     * (или создателям) организации.
     * Удаляет организацию с указанным id, если такая организация существует.
     * После удаления устаналивает код состояния 204 No Content. Если
     * организация с указанным id не найдена, устаналивает код состояния
     * 404 Not Found, выбрасывая исключение {@link ResourceNotFoundException}.
     *
     * @param organizationId id удаляемой организации
     * @param auth           данные об аутентификации пользователя
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
                || (authMember != null) && (authMember.getStatus().equals(MemberStatus.CREATOR))
                || (authMember != null) && (authMember.getStatus().equals(MemberStatus.OWNER))) {
            organizationRepository.delete(organization);
        } else {
            throw new ForbiddenException();
        }
    }

    /**
     * Метод доступен всем авторизованным пользователям.
     * Принимает на вход объект, содержащий данные о новой организации.
     * Общее назначение метода - проверка на наличие в базе организации с
     * указанным name (в таком случае устаналивает код состояния 400 Bad
     * Request, выбрасывая исключение {@link BadRequestException}) и, в
     * случае отсутствия таковой, добавление новой организации в базу с
     * добавлением в список members создателя организации со статусом
     * CREATOR
     *
     * @param organization данные нового пользователя
     * @param response     объект, содержащий заголовки ответа
     * @param auth         данные об аутентификации пользователя
     * @return добавленный пользователь
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
     * Метод доступен только пользователям с ролью ADMIN или владельцам
     * (или создателям) организации.
     * Изменяет поля организации с указанным id на те, что указаны в параметрах
     * запроса, если такая организация существует. Можно изменить только поля
     * name и fullName. Если организация с указанным id не найдена,
     * устанавливает код состояния 404 Not Found, выбрасывая исключение
     * {@link ResourceNotFoundException}.
     *
     * @param id       идентификатор изменяемой организации
     * @param name     новое название организации
     * @param fullName новое полное название организации
     * @param auth     данные об аутентификации пользователя
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

        Member authMember = memberRepository
                .findByOrganizationAndUser(organization,
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
     * Метод доступен только пользователям с ролью ADMIN или участникам
     * организации.
     * Возвращает список всех участников организации с полем id или name
     * равным organizationIdOrName, либо список не всех участников,
     * если в параметрах запроса указаны номер страницы, количество
     * элементов на странице и порядок сортировки. Устаналивает код
     * состояния 200 OK в случае успеха. Если организация с указанным
     * идентификатором не найдена - устаналивает код состояния 404 Not
     * Found, выбрасывая исключение {@link ResourceNotFoundException}
     *
     * @param organizationIdOrName id или name организации
     * @param pageable             объект, содержащий параметры запроса -
     *                             номер страницы,
     *                             кол-во элементов на странице, порядок
     *                             сортировки
     * @param auth                 данные об аутентификации пользователя
     * @return сформированный список участников организации
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{organizationIdOrName}/members")
    public List<Member> getOrganizationMembers(
            @PathVariable String organizationIdOrName,
            Pageable pageable,
            Authentication auth
    ) {
        Organization organization = ofNullable(
                findOrganizationByIdOrName(organizationIdOrName))
                .orElseThrow(ResourceNotFoundException::new);

        List<Member> members = memberRepository
                .findAllByOrganization(organization, pageable)
                .getContent();

        Member authMember = memberRepository
                .findByOrganizationAndUser(organization,
                        userRepository.findByEmail(auth.getName()));

        if ((auth.getAuthorities().contains(UserAuthority.ADMIN))
                || (authMember != null)) {
            return members;
        }
        throw new ForbiddenException();
    }

    /**
     * Метод доступен только пользователям с ролью ADMIN или участникам
     * организации.
     * Ищет в базе данных участника с id равным memberId организации с id
     * или name равным organizationIdOrName. Если организация и участник
     * этой организации найдены, возвращает этого участника и устаналивает
     * код состояния 200 OK; если организация или участник не найдены -
     * устаналивает код остояния 404 Not Found, выбрасывая исключение
     * {@link ResourceNotFoundException}
     *
     * @param organizationIdOrName id или name организации
     * @param memberId             id участника организации
     * @param auth                 данные об аутентификации пользователя
     * @return найденный учатсник организации
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{organizationIdOrName}/members/{memberId}")
    public Member getOrganizationMember(
            @PathVariable String organizationIdOrName,
            @PathVariable Long memberId,
            Authentication auth
    ) {

        Organization organization = ofNullable(
                findOrganizationByIdOrName(organizationIdOrName))
                .orElseThrow(ResourceNotFoundException::new);

        Member returnMember = ofNullable(memberRepository
                .findByOrganizationAndId(organization, memberId))
                .orElseThrow(ResourceNotFoundException::new);

        Member authMember = memberRepository
                .findByOrganizationAndUser(organization,
                        userRepository.findByEmail(auth.getName()));

        if ((auth.getAuthorities().contains(UserAuthority.ADMIN))
                || (authMember != null)) {
            return returnMember;
        }
        throw new ForbiddenException();
    }

    /**
     * Метод доступен только пользователям с ролью ADMIN.
     * Добавляет нового участника в организацию, если организация существует.
     * В случае успеха устаналивает код состояния 201 CREATED, устаналивает
     * заголовок Location и возвращает добавленного участника. Если
     * организация не существует, устаналивает код состояния 404 Not Found.
     *
     * @param organizationId id организации
     * @param member         новый участник
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
     * Метод доступен только администраторам и владельцам организации.
     * Позволяет изменить статус участника. Можно изменить статус
     * любого участника (кроме создателя) на любой другой статус
     * (кроме статуса CREATOR). В случае успеха устаналивается код
     * состояния 200 OK. Если организация или участник с указанным
     * идентификатором не существуют, устаналивает код состояния
     * 404 Not Found.
     *
     * @param organizationId id организации
     * @param memberId       id участника организации
     * @param newStatus      новый статус участника
     * @param auth           данные об аутентификации пользователя
     */
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{organizationId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchMember(
            @PathVariable Long organizationId,
            @PathVariable Long memberId,
            @RequestParam MemberStatus newStatus,
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
     * Метод доступен пользователям с ролью ADMIN и владельцам
     * (и создателю) организации. Удаляет участника из организации с
     * указанными идентифкаторами организации и участника. В случае
     * успеха устаналивает код состояния 204 No Content. Если о
     * рганизации или участника с указанным id не найдено,
     * устаналивает код состояния 404 Not Found.
     *
     * @param organizationId id организации
     * @param memberId       id участника организации
     * @param auth           данные об аутентификации пользователя
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

        if ((auth.getAuthorities().contains(UserAuthority.ADMIN))
                || (authMember != null) && (authMember.getStatus().equals(MemberStatus.CREATOR))
                || (authMember != null) && (authMember.getStatus().equals(MemberStatus.OWNER))) {
            memberRepository.delete(member);
        } else {
            throw new ForbiddenException();
        }
    }
}
