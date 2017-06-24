package org.teapot.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.teapot.backend.repository.meta.TeapotPropertyRepository;

import java.util.Arrays;
import java.util.Objects;

@Component
public class LinkBuilder {

    @Autowired
    private TeapotPropertyRepository propertyRepository;

    public String format(String relativePathFormat, Object... args) {
        if (Arrays.stream(args).anyMatch(Objects::isNull)) {
            return null;
        } else {
            return propertyRepository.findByName("site-uri").getValue()
                    + String.format(relativePathFormat, args);
        }
    }
}
