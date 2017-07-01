package org.teapot.backend.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.teapot.backend.util.ser.BoardSerializer;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
@JsonSerialize(using = BoardSerializer.class)
public class Board extends AbstractPersistable<Long> {

    private String title;

    @ManyToOne(optional = false)
    private Owner owner;

    public Board() {
    }

    public Board(String title, Owner owner) {
        this.title = title;
        this.owner = owner;
    }

    public Board(Long id, String title, Owner owner) {
        setId(id);
        setTitle(title);
        setOwner(owner);
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(title);
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
        final Board other = (Board) obj;
        return Objects.equals(this.title, other.title);
    }
}
