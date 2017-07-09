package org.teapot.backend.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@ToString(exclude = "boards")
@EqualsAndHashCode(callSuper = true, exclude = "boards")
@NoArgsConstructor
@AllArgsConstructor
public abstract class Owner extends AbstractPersistable<Long> {

    @Column(unique = true, nullable = false, length = 32)
    private String name;

    private LocalDateTime registrationDateTime;

    private Boolean available = true;

    @OneToMany(mappedBy = "owner")
    private Set<Board> boards = new HashSet<>();

    @PreRemove
    private void detachBoards() {
        boards.forEach(board -> board.setOwner(null));
    }
}
