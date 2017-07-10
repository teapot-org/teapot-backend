package org.teapot.backend.model.meta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "property")
@NoArgsConstructor
@AllArgsConstructor
public class TeapotProperty extends AbstractPersistable {

    @Column(nullable = false, unique = true)
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String value;
}
