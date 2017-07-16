package org.teapot.backend.model.kanban;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.OwnerItem;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import java.util.Set;

@Entity
@NoArgsConstructor
public class Project extends OwnerItem {

    @Getter
    @Setter
    private String title;

    @OneToMany(mappedBy = "project")
    private Set<Kanban> kanbans;

    public Project(String title) {
        setTitle(title);
    }

    @PreRemove
    private void detachKanbans() {
        kanbans.forEach(kanban -> kanban.setProject(null));
    }
}
