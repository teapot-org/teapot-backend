package org.teapot.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.teapot.backend.model.user.User;
import org.teapot.backend.model.user.VerificationToken;
import org.teapot.backend.repository.meta.TeapotPropertyRepository;
import org.teapot.backend.repository.user.VerificationTokenRepository;

import java.util.Locale;

@Component
public class VerificationMailSender {

    @Autowired
    private TeapotPropertyRepository propertyRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private VerificationTokenGenerator generator;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    public void createTokenAndSend(User user, Locale locale) {
        new Thread(() -> {
            VerificationToken verificationToken = generator.generateToken();
            verificationToken.setUser(user);
            tokenRepository.save(verificationToken);
            user.setVerificationToken(verificationToken);

            String confirmUrl = "https://"
                    + propertyRepository.findByName("site-uri").getValue()
                    + "/confirmRegistration?token="
                    + verificationToken.getToken();

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject(messages.getMessage(
                    "mail.confirm.subject", null, locale));
            mailMessage.setText(messages.getMessage(
                    "mail.confirm.text", new String[]{confirmUrl}, locale));
            mailSender.send(mailMessage);
        }).start();
    }
}
