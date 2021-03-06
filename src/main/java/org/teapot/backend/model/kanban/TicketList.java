package org.teapot.backend.model.kanban;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class TicketList extends BaseEntity {

    @Getter
    @Setter
    private String title;

    @ManyToOne(optional = false)
    @Getter
    @Setter
    private Kanban kanban;

    @Setter
    private Integer position;

    @OneToMany(mappedBy = "ticketList", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderColumn(name = "position")
    @Getter
    private List<Ticket> tickets = new ArrayList<>();

    public TicketList(String title) {
        setTitle(title);
        setKanban(kanban);
    }

    public void addTicket(Ticket ticket) {
        if (!tickets.contains(ticket)) {
            ticket.setTicketList(this);
            tickets.add(ticket);
        }
    }
}
