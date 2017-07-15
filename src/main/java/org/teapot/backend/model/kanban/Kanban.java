package org.teapot.backend.model.kanban;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.OwnerItem;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Kanban extends OwnerItem {

    @Getter
    @Setter
    private String title;

    @OneToMany(mappedBy = "kanban", cascade = CascadeType.REMOVE)
    @OrderColumn
    private List<TicketList> ticketLists;

    public Kanban(String title) {
        setTitle(title);
    }

    public Kanban(String title, Owner owner) {
        super(owner);
        setTitle(title);
    }
}
