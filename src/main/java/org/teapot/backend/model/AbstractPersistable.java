package org.teapot.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractPersistable implements Persistable<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime creationDateTime;

    @Column(nullable = false)
    @LastModifiedDate
    @JsonIgnore
    private LocalDateTime lastModifiedDateTime;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public LocalDateTime getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    @JsonIgnore
    @Transient
    public boolean isNew() {
        return id == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final AbstractPersistable other = (AbstractPersistable) obj;
        if (this.isNew() || other.isNew()) {
            return false;
        }
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + id;
    }
}
