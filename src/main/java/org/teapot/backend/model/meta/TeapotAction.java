package org.teapot.backend.model.meta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "action")
@NoArgsConstructor
@AllArgsConstructor
public class TeapotAction extends AbstractPersistable {

    @Column(nullable = false, unique = true, length = 32)
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String usage;

    @Lob
    @Column(length = 512)
    @Getter
    @Setter
    private String manual;
}
