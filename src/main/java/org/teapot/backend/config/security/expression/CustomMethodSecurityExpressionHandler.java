package org.teapot.backend.config.security.expression;

import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.stereotype.Component;
import org.teapot.backend.repository.kanban.KanbanRepository;
import org.teapot.backend.repository.kanban.TicketListRepository;
import org.teapot.backend.repository.kanban.TicketRepository;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;
import org.teapot.backend.repository.user.UserRepository;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomMethodSecurityExpressionHandler extends OAuth2MethodSecurityExpressionHandler {

    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final MemberRepository memberRepository;
    private final KanbanRepository kanbanRepository;
    private final TicketListRepository ticketListRepository;
    private final TicketRepository ticketRepository;

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, MethodInvocation invocation) {
        final CustomSecurityExpressionRoot root = new CustomSecurityExpressionRoot(authentication,
                userRepository,
                organizationRepository,
                memberRepository,
                kanbanRepository,
                ticketListRepository,
                ticketRepository);

        root.setThis(invocation.getThis());
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());

        return root;
    }
}
