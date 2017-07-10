package org.teapot.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
    private Set<Board> boards = new HashSet<>();

    @PreRemove
    private void detachBoards() {
        boards.forEach(board -> board.setOwner(null));
    }
}
