package org.teapot.backend.model.organization;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.Owner;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
public class Organization extends Owner {

    @Getter @Setter
    private String fullName;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    @Getter @Setter
    private Set<Member> members = new HashSet<>();
}
