package org.teapot.backend.model.meta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "resource")
@NoArgsConstructor
@AllArgsConstructor
public class TeapotResource extends BaseEntity {

    @Column(nullable = false, unique = true, length = 32)
    @Getter
    @Setter
    private String name;

    @Column(nullable = false, unique = true)
    @Getter
    @Setter
    private String uri;

    @Lob
    @Column(length = 512)
    @Getter
    @Setter
    private String description;
}
