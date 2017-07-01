package org.teapot.backend.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Owner extends AbstractPersistable<Long> {

    @Column(unique = true, nullable = false, length = 32)
    private String name;

    private LocalDateTime registrationDateTime;

    @OneToMany(mappedBy = "owner")
    private List<Board> boards;

    public Owner() {
    }

    public Owner(String name,
                 LocalDateTime registrationDateTime,
                 List<Board> boards) {
        setName(name);
        setRegistrationDateTime(registrationDateTime);
        setBoards(boards);
    }

    public Owner(Long id,
                 String name,
                 LocalDateTime registrationDateTime,
                 List<Board> boards) {
        setId(id);
        setName(name);
        setRegistrationDateTime(registrationDateTime);
        setBoards(boards);
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getRegistrationDateTime() {
        return registrationDateTime;
    }

    public void setRegistrationDateTime(LocalDateTime registrationDateTime) {
        this.registrationDateTime = registrationDateTime;
    }

    public List<Board> getBoards() {
        return boards;
    }

    public void setBoards(List<Board> boards) {
        this.boards = boards;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(
                name,
                registrationDateTime,
                boards
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final Owner other = (Owner) obj;
        return Objects.equals(this.name, other.name)
                && Objects.equals(this.registrationDateTime, other.registrationDateTime)
                && Objects.equals(this.boards, other.boards);
    }
}