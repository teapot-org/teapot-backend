package org.teapot.backend.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.kanban.Ticket;
import org.teapot.backend.model.organization.Member;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@NoArgsConstructor
public class User extends Owner {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private String email;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Getter
    private String password;

    @Getter
    @Setter
    private Boolean activated = false;

    @Column(length = 32)
    @Getter
    @Setter
    private String firstName;

    @Column(length = 32)
    @Getter
    @Setter
    private String lastName;

    @Enumerated
    @Getter
    @Setter
    private UserAuthority authority = UserAuthority.USER;

    @Getter
    @Setter
    private LocalDate birthday;

    @Getter
    @Setter
    private String description;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private VerificationToken verificationToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @RestResource(exported = false)
    private Set<Member> members;

    @ManyToMany(mappedBy = "contributors")
    @RestResource(exported = false)
    private Set<Kanban> contributedKanbans;

    @ManyToMany(mappedBy = "contributors")
    @RestResource(exported = false)
    private Set<Ticket> contributedTickets;

    @PreRemove
    private void detachKanbansAndTickets() {
        if (contributedKanbans != null) {
            contributedKanbans.forEach(kanban -> kanban.getContributors().remove(this));
        }
        if (contributedTickets != null) {
            contributedTickets.forEach(ticket -> ticket.getContributors().remove(this));
        }
    }

    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encode(password);
    }

    @Override
    public String getType() {
        return "user";
    }
}
