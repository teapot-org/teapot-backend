package org.teapot.backend.config.data;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.stereotype.Component;

@Component
@EnableJpaAuditing
public class SpringDataRestCustomization extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.getCorsRegistry().addMapping("/**").allowedMethods("*");
    }

//    @Component
//    @Order(SecurityProperties.IGNORED_ORDER)
//    public class DisablePutMethodFilter extends OncePerRequestFilter {
//        @Override
//        protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain)
//                throws ServletException, IOException {
//            if (req.getMethod().equalsIgnoreCase("PUT")) {
//                resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
//            } else {
//                filterChain.doFilter(req, resp);
//            }
//        }
//    }
}
