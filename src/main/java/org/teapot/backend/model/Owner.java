package org.teapot.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
public abstract class Owner extends BaseEntity {

    @Column(unique = true, nullable = false, length = 32)
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Boolean available = true;

    @OneToMany(mappedBy = "owner")
    private Set<OwnerItem> ownerItems;

    @PreRemove
    private void detachOwnerItems() {
        if (ownerItems != null) {
            ownerItems.forEach(item -> item.setOwner(null));
        }
    }

    public final String getType() {
        return getClass().getSimpleName();
    }
}
