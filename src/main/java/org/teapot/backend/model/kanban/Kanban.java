package org.teapot.backend.model.kanban;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.AbstractPersistable;
import org.teapot.backend.model.Owner;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Kanban extends AbstractPersistable {

    @Getter
    @Setter
    private String title;

    @ManyToOne
    @Getter
    @Setter
    private Owner owner;
}
