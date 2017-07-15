package org.teapot.backend.model.kanban;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.OwnerItem;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Kanban extends OwnerItem {

    @Getter
    @Setter
    private String title;

    public Kanban(String title, Owner owner) {
        super(owner);
        setTitle(title);
    }
}
