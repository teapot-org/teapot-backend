package org.teapot.backend.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.*;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.teapot.backend.controller.AbstractController;
import org.teapot.backend.model.user.User;
import org.teapot.backend.model.user.UserAuthority;
import org.teapot.backend.repository.user.UserRepository;
import org.teapot.backend.util.VerificationMailSender;

import java.time.LocalDate;
import java.util.Arrays;

@RepositoryRestController
public class UserController extends AbstractController {

    public static final String USERS_ENDPOINT = "/users";
    public static final String SINGLE_USER_ENDPOINT = USERS_ENDPOINT + "/{id}";

    @Autowired
    private Environment env;

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private VerificationMailSender verificationMailSender;

    @PreAuthorize("isAnonymous() or hasRole('ADMIN')")
    @PostMapping(USERS_ENDPOINT)
    public ResponseEntity<?> registerUser(
            @RequestBody Resource<User> requestResource,
            PersistentEntityResourceAssembler assembler,
            WebRequest request,
            Authentication auth
    ) {
        User user = requestResource.getContent();

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new DataIntegrityViolationException("Already exists");
        }

        boolean isVerificationEnabled = Arrays
                .stream(env.getActiveProfiles())
                .anyMatch("verification"::equalsIgnoreCase);

        if ((auth == null) && isVerificationEnabled) {
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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(SINGLE_USER_ENDPOINT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(
            @PathVariable Long id,
            @RequestBody Resource<User> requestResource
    ) {
        User existingUser = userRepository.findOne(id);
        if (existingUser == null) {
            throw new ResourceNotFoundException();
        }

        User user = requestResource.getContent();

        user.setId(id);
        user.setActivated(existingUser.getActivated());

        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN') or @userRepository.findByEmail(authentication?.name)?.id == #id")
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
            @RequestParam(required = false) String description,
            Authentication auth
    ) {
        User user = userRepository.findOne(id);
        if (user == null) {
            throw new ResourceNotFoundException();
        }

        if (auth.getAuthorities().contains(UserAuthority.ADMIN)) {
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

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(SINGLE_USER_ENDPOINT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        User user = userRepository.findOne(id);
        if (user == null) {
            throw new ResourceNotFoundException();
        }

        userRepository.delete(id);
    }
}
