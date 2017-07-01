package org.teapot.backend.controller.meta;

import com.google.common.primitives.Longs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.teapot.backend.controller.exception.BadRequestException;
import org.teapot.backend.controller.exception.ConflictException;
import org.teapot.backend.controller.exception.ResourceNotFoundException;
import org.teapot.backend.model.meta.TeapotAction;
import org.teapot.backend.model.user.User;
import org.teapot.backend.model.user.VerificationToken;
import org.teapot.backend.repository.meta.TeapotActionRepository;
import org.teapot.backend.repository.meta.TeapotResourceRepository;
import org.teapot.backend.repository.user.UserRepository;
import org.teapot.backend.repository.user.VerificationTokenRepository;
import org.teapot.backend.util.VerificationMailSender;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/actions")
public class TeapotActionController {

    @Autowired
    private TeapotActionRepository actionRepository;

    @Autowired
    private TeapotResourceRepository resourceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private VerificationMailSender verificationMailSender;

    @GetMapping("/{nameOrId}")
    public TeapotAction getAction(@PathVariable String nameOrId) {
        Long id = Longs.tryParse(nameOrId);
        return Optional.ofNullable((id != null)
                ? actionRepository.findOne(id)
                : actionRepository.findByName(nameOrId))
                .orElseThrow(ResourceNotFoundException::new);
    }

    @GetMapping("/help")
    public Object help(
            @RequestParam(name = "resource", required = false) String resourceNameOrId,
            @RequestParam(name = "action", required = false) String actionNameOrId
    ) {
        if ((resourceNameOrId != null) && (actionNameOrId != null)) {
            throw new ConflictException();
        }

        if (resourceNameOrId != null) {
            Long id = Longs.tryParse(resourceNameOrId);
            return Optional.ofNullable((id != null)
                    ? resourceRepository.findOne(id)
                    : resourceRepository.findByName(resourceNameOrId))
                    .orElseThrow(ResourceNotFoundException::new);
        }

        if (actionNameOrId != null) {
            Long id = Longs.tryParse(actionNameOrId);
            return Optional.ofNullable((id != null)
                    ? actionRepository.findOne(id)
                    : actionRepository.findByName(actionNameOrId))
                    .orElseThrow(ResourceNotFoundException::new);
        }

        throw new BadRequestException();
    }

    @PostMapping("/activate")
    public void activate(
            @RequestParam("token") String tokenString,
            WebRequest request
    ) {
        VerificationToken token = Optional
                .ofNullable(tokenRepository.findByToken(tokenString))
                .orElseThrow(ResourceNotFoundException::new);
        User user = token.getUser();

        tokenRepository.delete(token);

        if (token.getExpireDateTime().isAfter(LocalDateTime.now())) {
            user.setActivated(true);
            userRepository.save(user);
        } else {
            verificationMailSender.createTokenAndSend(user, request.getLocale());
        }
    }
}
