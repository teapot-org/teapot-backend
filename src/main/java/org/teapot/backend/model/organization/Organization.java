package org.teapot.backend.model.organization;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.teapot.backend.model.Board;
import org.teapot.backend.model.Owner;
import org.teapot.backend.util.ser.MemberSerializer;
import org.teapot.backend.util.ser.OrganizationSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@JsonSerialize(using = OrganizationSerializer.class)
public class Organization extends Owner {

    @Column
    private String fullName;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Member> members = new HashSet<>();

    public Organization() {
        super();
    }

    public Organization(String name,
                        LocalDateTime registrationDateTime,
                        List<Board> boards,
                        String fullName,
                        Set<Member> members) {
        super(name, registrationDateTime, boards);
        setFullName(fullName);
        setMembers(members);
    }

    public Organization(Long id,
                        String name,
                        LocalDateTime registrationDateTime,
                        List<Board> boards,
                        String fullName,
                        Set<Member> members) {
        super(id, name, registrationDateTime, boards);
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
