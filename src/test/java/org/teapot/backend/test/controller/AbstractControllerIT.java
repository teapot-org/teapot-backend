package org.teapot.backend.test.controller;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.teapot.backend.model.BaseEntity;
import org.teapot.backend.model.user.User;
import org.teapot.backend.model.user.UserAuthority;
import org.teapot.backend.repository.user.UserRepository;
import org.teapot.backend.test.AbstractIT;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractControllerIT extends AbstractIT {

    private static final String CLIENT_ID = "client";
    private static final String CLIENT_SECRET = "secret";
    protected MediaType contentType = MediaType.parseMediaType("application/hal+json;charset=utf-8");

    protected MockMvc mockMvc;

    protected JacksonJsonParser parser = new JacksonJsonParser();

    protected ObjectMapper mapper;

    @Autowired
    protected EntityLinks entityLinks;

    @Autowired
    protected UserRepository userRepository;

    protected User userWithAdminRole = new User();
    protected User userWithUserRole = new User();

    protected String adminAccessToken;
    protected String userAccessToken;

    @BeforeClass
    public static void setupRequestAttributes() {
        HttpServletRequest mockRequest = new MockHttpServletRequest();
        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
    }

    @AfterClass
    public static void resetRequestAttributes() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Autowired
    public void setMockMvcAndMapper(WebApplicationContext wac, FilterChainProxy filter) throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .addFilters(filter)
                .build();

        mapper = Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(MapperFeature.USE_ANNOTATIONS)
                .build()
                .registerModule(new Jackson2HalModule());
    }

    @SuppressWarnings("unchecked")
    protected String json(Object object) throws IOException {
        return mapper.writeValueAsString(object);
    }

    protected String obtainAccessToken(String email, String password) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", email);
        params.add("password", password);

        String resultString = mockMvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic(CLIENT_ID, CLIENT_SECRET))
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn().getResponse().getContentAsString();


        return parser.parseMap(resultString).get("access_token").toString();
    }

    @Before
    public void obtainAccessTokens() throws Exception {
        userWithAdminRole.setEmail("admin@auth.com");
        userWithAdminRole.setName("administrator");
        userWithAdminRole.setPassword("pass");
        userWithAdminRole.setAuthority(UserAuthority.ADMIN);
        userWithAdminRole.setActivated(true);
        userRepository.save(userWithAdminRole);
        adminAccessToken = obtainAccessToken(userWithAdminRole.getEmail(), "pass");

        userWithUserRole.setEmail("user@auth.com");
        userWithUserRole.setName("user");
        userWithUserRole.setPassword("pass");
        userWithUserRole.setAuthority(UserAuthority.USER);
        userWithUserRole.setActivated(true);
        userRepository.save(userWithUserRole);
        userAccessToken = obtainAccessToken(userWithUserRole.getEmail(), "pass");
    }

    protected String linkFor(BaseEntity object) {
        return (object == null) || object.isNew()
                ? null
                : entityLinks.linkToSingleResource(object.getClass(), object.getId()).getHref();
    }
}
