package org.teapot.backend.model.meta;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "property")
public class TeapotProperty extends AbstractPersistable<Long> {

    @Column(nullable = false, unique = true)
    private String name;

    private String value;

    public TeapotProperty() {
    }

    public TeapotProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }
}
