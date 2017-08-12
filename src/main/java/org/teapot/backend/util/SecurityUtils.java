package org.teapot.backend.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.teapot.backend.config.security.model.AuthenticatedRequestContext;
import org.teapot.backend.model.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtils {

    private static AuthenticatedRequestContext getAuthenticatedRequestContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = (authentication != null)
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;

        return (principal instanceof AuthenticatedRequestContext)
                ? ((AuthenticatedRequestContext) principal)
                : null;
    }

    public static User getAuthenticatedUser() {
        AuthenticatedRequestContext principal = getAuthenticatedRequestContext();
        return (principal != null) ? principal.getUser() : null;
    }

    public static void loginAs(User user) {
        AuthenticatedRequestContext principal = new AuthenticatedRequestContext(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal, principal.getPassword(), principal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}

