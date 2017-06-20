package org.teapot.backend.model.meta;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "property")
public class TeapotProperty implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String value;

    public TeapotProperty() {
    }

    public TeapotProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
