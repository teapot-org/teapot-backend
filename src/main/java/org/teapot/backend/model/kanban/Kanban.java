package org.teapot.backend.model.kanban;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.AbstractPersistable;
import org.teapot.backend.model.Owner;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(mappedBy = "kanban", cascade = CascadeType.REMOVE)
    @OrderColumn
    private List<TicketList> ticketLists;

    public Kanban(String title, Owner owner) {
        setTitle(title);
        setOwner(owner);
    }
}
