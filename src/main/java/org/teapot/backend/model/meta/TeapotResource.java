package org.teapot.backend.model.meta;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;

@Entity
@Table(name = "resource")
public class TeapotResource extends AbstractPersistable<Long> {

    @Column(nullable = false, unique = true, length = 32)
    private String name;

    @Column(nullable = false, unique = true)
    private String uri;

    @Lob
    @Column(length = 512)
    private String description;

    public TeapotResource() {
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
