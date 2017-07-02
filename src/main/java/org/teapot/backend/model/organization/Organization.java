package org.teapot.backend.model.organization;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.teapot.backend.model.Board;
import org.teapot.backend.model.Owner;
import org.teapot.backend.util.ser.OrganizationSerializer;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@JsonSerialize(using = OrganizationSerializer.class)
public class Organization extends Owner {

    @Column
    private String fullName;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Member> members = new HashSet<>();

    public Organization() {
        super();
    }

    public Organization(String name,
                        LocalDateTime registrationDateTime,
                        Boolean isAvailable,
                        Set<Board> boards,
                        String fullName,
                        Set<Member> members) {
        super(name, registrationDateTime, isAvailable, boards);
        setFullName(fullName);
        setMembers(members);
    }

    public Organization(Long id,
                        String name,
                        LocalDateTime registrationDateTime,
                        Boolean isAvailable,
                        Set<Board> boards,
                        String fullName,
                        Set<Member> members) {
        super(id, name, registrationDateTime, isAvailable, boards);
        setFullName(fullName);
        setMembers(members);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<Member> getMembers() {
        return members;
    }

    public void setMembers(Set<Member> members) {
        this.members = members;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(fullName, members);
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
        final Organization other = (Organization) obj;
        return Objects.equals(this.fullName, other.fullName)
                && Objects.equals(this.members, other.members);
    }
}
