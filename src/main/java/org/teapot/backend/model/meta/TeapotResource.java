package org.teapot.backend.model.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.teapot.backend.model.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "resource")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TeapotResource extends AbstractPersistable<Long> {

    @Column(nullable = false, unique = true, length = 32)
    private String name;

    @Column(nullable = false, unique = true)
    private String uri;

    @Lob
    @Column(length = 512)
    private String description;
}
