package org.teapot.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.teapot.backend.dto.Link;
import org.teapot.backend.repository.meta.TeapotPropertyRepository;

@Component
public class LinkBuilder {

    @Autowired
    private TeapotPropertyRepository propertyRepository;

    public Link format(String relativePathFormat, Object... args) {
        return new Link(propertyRepository.findByName("site-uri").getValue()
                + String.format(relativePathFormat, args));
    }
}
