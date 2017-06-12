package org.teapot.backend.test.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.teapot.backend.test.AbstractIT;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;


public abstract class AbstractControllerIT extends AbstractIT {

    protected MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    protected MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private void setConverters(HttpMessageConverter<?>[] converters) {
        mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .findAny().get();
        Assert.assertNotNull(mappingJackson2HttpMessageConverter);
    }

    @Autowired
    public void setMockMvc(WebApplicationContext webApplicationContext) throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    protected String json(Object object) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(
                object, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
