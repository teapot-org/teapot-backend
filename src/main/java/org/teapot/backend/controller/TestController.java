package org.teapot.backend.controller;

import org.springframework.hateoas.UriTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class TestController {

    @GetMapping("/test")
    public void test() {
        Map<String, Object> variables = new LinkedHashMap<String, Object>() {{
            put("page", 1);
            put("size", 20);
            put("sort", "lalala");
        }};
        UriTemplate uriTemplate = new UriTemplate("http://localhost:8080/owners{?page,size,sort}");

        URI expanded = uriTemplate.expand(variables);

        System.out.println();
    }
}
