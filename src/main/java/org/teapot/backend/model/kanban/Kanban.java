package org.teapot.backend.model.kanban;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.OwnerItem;
import org.teapot.backend.model.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
public class Kanban extends OwnerItem {

    @Getter
    @Setter
    private String title;

    @OneToMany(mappedBy = "kanban", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderColumn(name = "position")
    @Getter
    @RestResource(path = "ticket-lists")
    private List<TicketList> ticketLists = new ArrayList<>();

    @ManyToOne
    @Getter
    @Setter
    private Project project;

    @Enumerated
    @Getter
    @Setter
    private KanbanAccess access = KanbanAccess.PUBLIC;

    @ManyToMany
    @Getter
    private Set<User> contributors = new HashSet<>();

    public Kanban(String title) {
        setTitle(title);
    }

    public Kanban(String title, Owner owner) {
        super(owner);
        setTitle(title);
    }

    public Kanban(String title, Owner owner, KanbanAccess access) {
        super(owner);
        setTitle(title);
        setAccess(access);
    }

    public Kanban(String title, Owner owner, Project project) {
        this(title, owner);
        setProject(project);
    }

    public void addTicketList(TicketList ticketList) {
        if (!ticketLists.contains(ticketList)) {
            ticketList.setKanban(this);
            ticketLists.add(ticketList);
        }
    }
}
