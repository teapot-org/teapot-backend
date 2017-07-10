package org.teapot.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Board extends AbstractPersistable {

    @Getter
    @Setter
    private String title;

    @ManyToOne
    @Getter
    @Setter
    private Owner owner;
}
