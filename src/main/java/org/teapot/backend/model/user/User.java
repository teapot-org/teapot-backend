package org.teapot.backend.model.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.teapot.backend.model.Board;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.util.ser.UserSerializer;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "user")
@JsonSerialize(using = UserSerializer.class)
public class User extends Owner {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private Boolean isActivated = false;

    @Column(length = 32)
    private String firstName;

    @Column(length = 32)
    private String lastName;

    @Enumerated
    private UserAuthority authority = UserAuthority.USER;

    private LocalDate birthday;

    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private VerificationToken verificationToken;

    @OneToMany(mappedBy = "user")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Member> members;

    public User() {
        super();
    }

    public User(String name,
                LocalDateTime registrationDateTime,
                Set<Board> boards,
                String email,
                String password,
                Boolean isAvailable,
                Boolean isActivated,
                String firstName,
                String lastName,
                UserAuthority authority,
                LocalDate birthday,
                String description) {
        super(name, registrationDateTime, isAvailable, boards);
        setEmail(email);
        setPassword(password);
        setAvailable(isAvailable);
        setActivated(isActivated);
        setFirstName(firstName);
        setLastName(lastName);
        setAuthority(authority);
        setBirthday(birthday);
        setDescription(description);
    }


    public User(Long id,
                String ownerName,
                LocalDateTime registrationDateTime,
                Set<Board> boards,
                String email,
                String password,
                Boolean isAvailable,
                Boolean isActivated,
                String firstName,
                String lastName,
                UserAuthority authority,
                LocalDate birthday,
                String description) {
        super(id, ownerName, registrationDateTime, isAvailable, boards);
        setEmail(email);
        setPassword(password);
        setAvailable(isAvailable);
        setActivated(isActivated);
        setFirstName(firstName);
        setLastName(lastName);
        setAuthority(authority);
        setBirthday(birthday);
        setDescription(description);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encode(password);
    }

    public Boolean isActivated() {
        return isActivated;
    }

    public void setActivated(Boolean activated) {
        isActivated = activated;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserAuthority getAuthority() {
        return authority;
    }

    public void setAuthority(UserAuthority authority) {
        this.authority = authority;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public VerificationToken getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(VerificationToken verificationToken) {
        this.verificationToken = verificationToken;
    }

    public Set<Member> getMembers() {
        return members;
    }

    public void setMembers(Set<Member> members) {
        this.members = members;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(
                email,
                password,
                isActivated,
                firstName,
                lastName,
                authority,
                birthday,
                description,
                verificationToken,
                members
        );
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
        final User other = (User) obj;
        return Objects.equals(this.email, other.email)
                && Objects.equals(this.password, other.password)
                && Objects.equals(this.isActivated, other.isActivated)
                && Objects.equals(this.firstName, other.firstName)
                && Objects.equals(this.lastName, other.lastName)
                && Objects.equals(this.authority, other.authority)
                && Objects.equals(this.birthday, other.birthday)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.verificationToken, other.verificationToken)
                && Objects.equals(this.members, other.members);
    }
}
