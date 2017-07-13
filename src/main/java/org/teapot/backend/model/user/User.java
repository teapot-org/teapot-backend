package org.teapot.backend.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.organization.Member;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
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

    @OneToOne(mappedBy = "user")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    @Getter
    @Setter
    private VerificationToken verificationToken;

    @OneToMany(mappedBy = "user")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Getter
    @Setter
    private Set<Member> members = new HashSet<>();

    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encode(password);
    }

    @Override
    public String getType() {
        return "user";
    }
}
