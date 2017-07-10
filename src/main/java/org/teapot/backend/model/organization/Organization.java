package org.teapot.backend.model.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.teapot.backend.model.Owner;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends Owner {

    @Getter
    @Setter
    private String fullName;

    @OneToMany(mappedBy = "organization")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Getter
    @Setter
    private Set<Member> members = new HashSet<>();
}
