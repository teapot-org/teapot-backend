package org.teapot.backend.model.kanban;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.AbstractPersistable;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TicketList extends AbstractPersistable {

    @Getter
    @Setter
    private String title;

    @ManyToOne(optional = false)
    @Getter
    @Setter
    private Kanban kanban;

    @OneToMany(mappedBy = "ticketList", cascade = CascadeType.REMOVE)
    @OrderColumn
    private List<Ticket> tickets;

    public TicketList(String title, Kanban kanban) {
        setTitle(title);
        setKanban(kanban);
    }
}
