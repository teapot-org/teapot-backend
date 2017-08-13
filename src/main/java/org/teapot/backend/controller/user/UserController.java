package org.teapot.backend.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.*;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.teapot.backend.controller.AbstractController;
import org.teapot.backend.model.user.User;
import org.teapot.backend.model.user.UserAuthority;
import org.teapot.backend.repository.user.UserRepository;
import org.teapot.backend.util.PagedResourcesAssemblerHelper;
import org.teapot.backend.util.VerificationMailSender;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.teapot.backend.model.user.UserAuthority.ADMIN;
import static org.teapot.backend.util.SecurityUtils.getAuthenticatedUser;

@RepositoryRestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController extends AbstractController {

    public static final String USERS_ENDPOINT = "/users";
    public static final String SINGLE_USER_ENDPOINT = USERS_ENDPOINT + "/{id:\\d+}";

    private final Environment env;
    private final UserRepository userRepository;
    private final PagedResourcesAssemblerHelper<User> helper;

    @Autowired(required = false)
    private VerificationMailSender verificationMailSender;

    @PreAuthorize("canCreate(#resource?.content)")
    @PostMapping(USERS_ENDPOINT)
    public ResponseEntity<?> registerUser(
            @RequestBody Resource<User> resource,
            PersistentEntityResourceAssembler assembler,
            WebRequest request
    ) {
        User user = resource.getContent();

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new DataIntegrityViolationException("Already exists");
        }

        boolean isVerificationEnabled = Arrays
                .stream(env.getActiveProfiles())
                .anyMatch("verification"::equalsIgnoreCase);

        User authenticatedUser = getAuthenticatedUser();
        if ((authenticatedUser == null) && isVerificationEnabled) {
            user.setActivated(false);
            user = userRepository.save(user);
            verificationMailSender.createTokenAndSend(user, request.getLocale());
        } else {
            user.setActivated(true);
            userRepository.save(user);
        }

        PersistentEntityResource responseResource = assembler.toResource(user);
        HttpHeaders headers = headersPreparer.prepareHeaders(responseResource);
        addLocationHeader(headers, assembler, user);

        return ControllerUtils.toResponseEntity(HttpStatus.CREATED, headers, responseResource);
    }

    @PreAuthorize("canEdit(#id, 'User')")
    @PatchMapping(SINGLE_USER_ENDPOINT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchUser(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) UserAuthority authority,
            @RequestParam(required = false) LocalDate birthday,
            @RequestParam(required = false) String description
    ) {
        User user = userRepository.findOne(id);
        if (user == null) {
            throw new ResourceNotFoundException();
        }

        if (ADMIN.equals(getAuthenticatedUser().getAuthority())) {
            if (name != null) user.setName(name);
            if (email != null) user.setEmail(email);
            if (password != null) user.setPassword(password);
            if (available != null) user.setAvailable(available);
            if (firstName != null) user.setFirstName(firstName);
            if (lastName != null) user.setLastName(lastName);
            if (authority != null) user.setAuthority(authority);
            if (birthday != null) user.setBirthday(birthday);
            if (description != null) user.setDescription(description);
        } else {
            if (name != null) user.setName(name);
            if ((available != null) && (!available)) user.setAvailable(false);
            if (firstName != null) user.setFirstName(firstName);
            if (lastName != null) user.setLastName(lastName);
            if (birthday != null) user.setBirthday(birthday);
        }

        userRepository.save(user);
    }

    @GetMapping(USERS_ENDPOINT + "/search/find-by")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam String q,
            Pageable pageable,
            PersistentEntityResourceAssembler assembler
    ) {
        Page<User> searchResult = userRepository.findAll((root, qr, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            String query = "%" + q.toLowerCase() + "%";
            if (firstName != null) {
                predicates.add(cb.like(cb.lower(root.get("firstName")), query));
            }
            if (lastName != null) {
                predicates.add(cb.like(cb.lower(root.get("lastName")), query));
            }
            if (name != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), query));
            }
            if (email != null) {
                predicates.add(cb.like(cb.lower(root.get("email")), query));
            }

            return cb.or(predicates.toArray(new Predicate[predicates.size()]));
        }, pageable);

        return ResponseEntity.ok(helper.toResource(User.class, searchResult, assembler));
    }
}
