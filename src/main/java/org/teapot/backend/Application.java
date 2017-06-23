package org.teapot.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static final String SITE_URL = "http://localhost:8080";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
