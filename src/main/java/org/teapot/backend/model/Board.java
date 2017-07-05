package org.teapot.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = "owner")
@NoArgsConstructor
@AllArgsConstructor
public class Board extends AbstractPersistable<Long> {

    private String title;

    @ManyToOne
    private Owner owner;
}
