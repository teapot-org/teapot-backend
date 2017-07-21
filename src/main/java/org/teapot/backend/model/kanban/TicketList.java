package org.teapot.backend.model.kanban;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.AbstractPersistable;

import javax.persistence.*;
import java.util.Set;

@Entity
@NoArgsConstructor
public class TicketList extends AbstractPersistable {

    @Getter
    @Setter
    private String title;

    @ManyToOne(optional = false)
    @Getter
    @Setter
    private Kanban kanban;

    private Integer position;

    @OneToMany(mappedBy = "ticketList", cascade = CascadeType.REMOVE)
    @OrderColumn(name = "position")
    private Set<Ticket> tickets;

    public TicketList(String title) {
        setTitle(title);
        setKanban(kanban);
    }
}
