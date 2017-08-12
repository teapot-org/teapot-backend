package org.teapot.backend.controller.meta;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.teapot.backend.model.user.User;
import org.teapot.backend.model.user.VerificationToken;
import org.teapot.backend.repository.user.UserRepository;
import org.teapot.backend.repository.user.VerificationTokenRepository;
import org.teapot.backend.util.VerificationMailSender;

import java.time.LocalDateTime;
import java.util.Arrays;

@Controller
@RequestMapping("/actions")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TeapotActionController {

    private final Environment env;
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;

    @Autowired(required = false)
    private VerificationMailSender verificationMailSender;

    @PostMapping("/activate")
    public void activate(
            @RequestParam("token") String tokenString,
            WebRequest request
    ) {
        boolean isVerificationEnabled = Arrays
                .stream(env.getActiveProfiles())
                .anyMatch("verification"::equalsIgnoreCase);

        if (!isVerificationEnabled) {
            return;
        }

        VerificationToken token = tokenRepository.findByToken(tokenString);
        if (token == null) {
            throw new ResourceNotFoundException();
        }

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
