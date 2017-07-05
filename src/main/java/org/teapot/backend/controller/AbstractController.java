package org.teapot.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.HttpHeadersPreparer;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.hateoas.UriTemplate;
import org.springframework.http.HttpHeaders;

public abstract class AbstractController {

    @Autowired
    protected HttpHeadersPreparer headersPreparer;

    protected void addLocationHeader(HttpHeaders headers,
                                     PersistentEntityResourceAssembler assembler,
                                     Object source) {
        String selfLink = assembler.getSelfLinkFor(source).getHref();
        headers.setLocation(new UriTemplate(selfLink).expand());
    }
}
