package org.teapot.backend.model.organization;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.teapot.backend.model.Owner;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@ToString(exclude = "members")
@EqualsAndHashCode(callSuper = true, exclude = "members")
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends Owner {

    @Column
    private String fullName;

    @OneToMany(mappedBy = "organization")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Singular
    private Set<Member> members = new HashSet<>();
}
