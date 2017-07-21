package org.teapot.backend.model.kanban;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.AbstractPersistable;
import org.teapot.backend.model.user.User;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
public class Ticket extends AbstractPersistable {

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description;

    @ManyToOne(optional = false)
    @Getter
    @Setter
    @RestResource(path = "ticket-list")
    private TicketList ticketList;

    @ManyToMany
    @Getter
    private Set<User> contributors = new HashSet<>();

    public Ticket(String title, String description, TicketList ticketList) {
        setTitle(title);
        setDescription(description);
        setTicketList(ticketList);
    }
}
