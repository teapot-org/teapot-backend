package org.teapot.backend.model.kanban;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.AbstractPersistable;
import org.teapot.backend.model.organization.Member;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Ticket extends AbstractPersistable {

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description;

    @ManyToOne
    @Getter
    @Setter
    private TicketList ticketList;

    @ManyToMany
    @Getter
    @Setter
    private Set<Member> contributors = new HashSet<>();

    public Ticket(String title, String description) {
        setTitle(title);
        setDescription(description);
    }

    public Ticket(String title, String description, TicketList ticketList) {
        this(title, description);
        setTicketList(ticketList);
    }
}
