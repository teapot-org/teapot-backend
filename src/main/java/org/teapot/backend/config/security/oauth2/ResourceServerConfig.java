package org.teapot.backend.config.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.teapot.backend.config.security.expression.CustomWebSecurityExpressionHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.teapot.backend.controller.kanban.KanbanController.SINGLE_KANBAN_ENDPOINT;
import static org.teapot.backend.controller.kanban.TicketController.SINGLE_TICKET_ENDPOINT;
import static org.teapot.backend.controller.kanban.TicketListController.SINGLE_TICKET_LIST_ENDPOINT;
import static org.teapot.backend.controller.organization.MemberController.SINGLE_MEMBER_ENDPOINT;
import static org.teapot.backend.controller.organization.OrganizationController.SINGLE_ORGANIZATION_ENDPOINT;
import static org.teapot.backend.controller.user.UserController.SINGLE_USER_ENDPOINT;

@Configuration
@EnableResourceServer
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER - 1)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    public static final String RESOURCE_ID = "resource";

    private final CustomWebSecurityExpressionHandler expressionHandler;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.expressionHandler(expressionHandler)
                .resourceId(RESOURCE_ID);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new PutMethodDisallower(), BasicAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(GET, SINGLE_KANBAN_ENDPOINT).access("canRead(#id, 'Kanban')")
                .antMatchers(GET, SINGLE_TICKET_LIST_ENDPOINT).access("canRead(#id, 'TicketList')")
                .antMatchers(GET, SINGLE_TICKET_ENDPOINT).access("canRead(#id, 'Ticket')")
                .antMatchers(DELETE, SINGLE_USER_ENDPOINT).access("canDelete(#id, 'User')")
                .antMatchers(DELETE, SINGLE_ORGANIZATION_ENDPOINT).access("canDelete(#id, 'Organization')")
                .antMatchers(DELETE, SINGLE_MEMBER_ENDPOINT).access("canDelete(#id, 'Member')")
                .antMatchers(DELETE, SINGLE_KANBAN_ENDPOINT).access("canDelete(#id, 'Kanban')")
                .antMatchers(DELETE, SINGLE_TICKET_LIST_ENDPOINT).access("canDelete(#id, 'TicketList')")
                .antMatchers(DELETE, SINGLE_TICKET_ENDPOINT).access("canDelete(#id, 'Ticket')")
                .anyRequest().permitAll();
    }

    public static class PutMethodDisallower extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws ServletException, IOException {
            if (request.getMethod().equalsIgnoreCase("put")) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            } else {
                chain.doFilter(request, response);
            }
        }
    }
}
