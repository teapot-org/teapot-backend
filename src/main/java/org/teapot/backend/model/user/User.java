package org.teapot.backend.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
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
@Data
@ToString(exclude = {"password", "verificationToken", "members"})
@EqualsAndHashCode(callSuper = true, exclude = "verificationToken")
@NoArgsConstructor
public class User extends Owner {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private Boolean activated = false;

    @Column(length = 32)
    private String firstName;

    @Column(length = 32)
    private String lastName;

    @Enumerated
    private UserAuthority authority = UserAuthority.USER;

    private LocalDate birthday;

    private String description;

    @OneToOne(mappedBy = "user")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private VerificationToken verificationToken;

    @OneToMany(mappedBy = "user")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Singular
    private Set<Member> members = new HashSet<>();

    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encode(password);
    }
}
