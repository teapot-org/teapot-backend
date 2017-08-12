package org.teapot.backend.config.security.expression;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;
import org.teapot.backend.repository.kanban.KanbanRepository;
import org.teapot.backend.repository.kanban.TicketListRepository;
import org.teapot.backend.repository.kanban.TicketRepository;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;
import org.teapot.backend.repository.user.UserRepository;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomWebSecurityExpressionHandler extends OAuth2WebSecurityExpressionHandler {

    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final MemberRepository memberRepository;
    private final KanbanRepository kanbanRepository;
    private final TicketListRepository ticketListRepository;
    private final TicketRepository ticketRepository;

    @Override
    protected SecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, FilterInvocation fi) {
        final CustomSecurityExpressionRoot root = new CustomSecurityExpressionRoot(authentication,
                userRepository,
                organizationRepository,
                memberRepository,
                kanbanRepository,
                ticketListRepository,
                ticketRepository);

        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(trustResolver);
        return root;
    }
}
