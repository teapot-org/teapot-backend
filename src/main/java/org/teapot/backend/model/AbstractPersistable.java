package org.teapot.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.Identifiable;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class AbstractPersistable<T extends Serializable>
        extends org.springframework.data.jpa.domain.AbstractPersistable<T> implements Identifiable<T> {

    @JsonIgnore
    @Override
    public boolean isNew() {
        return super.isNew();
    }

    @Override
    public void setId(T id) {
        super.setId(id);
    }
}
