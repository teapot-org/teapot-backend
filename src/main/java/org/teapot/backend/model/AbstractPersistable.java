package org.teapot.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class AbstractPersistable<T extends Serializable>
        extends org.springframework.data.jpa.domain.AbstractPersistable<T> {

    @JsonIgnore
    @Override
    public boolean isNew() {
        return super.isNew();
    }
}
