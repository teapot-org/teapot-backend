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
@Table(name = "action")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TeapotAction extends AbstractPersistable<Long> {

    @Column(nullable = false, unique = true, length = 32)
    private String name;

    private String usage;

    @Lob
    @Column(length = 512)
    private String manual;
}
