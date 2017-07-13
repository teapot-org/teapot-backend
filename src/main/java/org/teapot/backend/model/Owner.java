package org.teapot.backend.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = User.class, name = "user"),
        @JsonSubTypes.Type(value = Organization.class, name = "organization")
})
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
public abstract class Owner extends AbstractPersistable {

    @Column(unique = true, nullable = false, length = 32)
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Boolean available = true;

    @OneToMany(mappedBy = "owner")
    @Getter
    @Setter
    private Set<Kanban> kanbans = new HashSet<>();

    @PreRemove
    private void detachBoards() {
        kanbans.forEach(kanban -> kanban.setOwner(null));
    }

    public abstract String getType();
}
