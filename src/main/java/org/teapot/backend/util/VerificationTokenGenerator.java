package org.teapot.backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.teapot.backend.model.user.VerificationToken;
import org.teapot.backend.repository.meta.TeapotPropertyRepository;
import org.teapot.backend.repository.user.VerificationTokenRepository;

import java.time.LocalDateTime;

@Component
@Profile("verification")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class VerificationTokenGenerator {

    private final TeapotPropertyRepository propertyRepository;
    private final VerificationTokenRepository tokenRepository;
    private final RandomSequenceGenerator sequenceGenerator;

    public VerificationToken generateToken() {
        VerificationToken token = new VerificationToken();

        Integer verificationTokenExpireDays =
                Integer.valueOf(propertyRepository
                        .findByName("verification-token-expire-days").getValue());

        if (verificationTokenExpireDays == null) {
            verificationTokenExpireDays = 1;
        }

        while (true) {
            String tokenString = sequenceGenerator.generateSequence(32);

            if (tokenRepository.findByToken(tokenString) == null) {
                token.setExpireDateTime(LocalDateTime.now().plusDays(verificationTokenExpireDays));
                token.setToken(tokenString);

                return token;
            }
        }
    }
}
