package org.teapot.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.kanban.Kanban;

import javax.persistence.*;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
public abstract class Owner extends AbstractPersistable {

    @Column(unique = true, nullable = false, length = 32)
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Boolean available = true;

    @OneToMany(mappedBy = "owner")
    private Set<Kanban> kanbans;

    @PreRemove
    private void detachKanbans() {
        if (kanbans != null) {
            kanbans.forEach(kanban -> kanban.setOwner(null));
        }
    }

    public abstract String getType();
}
