package org.teapot.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity implements Persistable<Long> {

    @Id
    @GeneratedValue
    @Setter
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDateTime;

    @Column(nullable = false)
    @LastModifiedDate
    @JsonIgnore
    private LocalDateTime lastModifiedDateTime;

    @PrePersist
    private void setCreationDateTime() {
        creationDateTime = LocalDateTime.now(ZoneId.of("UTC"));
    }

    @JsonIgnore
    @Transient
    public boolean isNew() {
        return getId() == null;
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
        final BaseEntity other = (BaseEntity) obj;
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
