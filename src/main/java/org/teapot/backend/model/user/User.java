package org.teapot.backend.model.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.teapot.backend.model.Board;
import org.teapot.backend.model.Owner;
import org.teapot.backend.util.ser.UserSerializer;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "user")
@JsonSerialize(using = UserSerializer.class)
public class User extends Owner {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private Boolean isAvailable = true;

    private Boolean isActivated = false;

    @Column(length = 32)
    private String firstName;

    @Column(length = 32)
    private String lastName;

    @Enumerated
    private UserAuthority authority = UserAuthority.USER;

    private LocalDate birthday;

    private String description;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    private VerificationToken verificationToken;

    public User() {
        super();
    }

    public User(String name,
                LocalDateTime registrationDateTime,
                List<Board> boards,
                String email,
                String password, Boolean isAvailable,
                Boolean isActivated, String firstName,
                String lastName,
                UserAuthority authority,
                LocalDate birthday,
                String description) {
        super(name, registrationDateTime, boards);
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
                List<Board> boards,
                String email,
                String password, Boolean isAvailable,
                Boolean isActivated, String firstName,
                String lastName,
                UserAuthority authority,
                LocalDate birthday,
                String description) {
        super(id, ownerName, registrationDateTime, boards);
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
        this.password = password;
    }

    public Boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
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

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(
                email,
                password,
                isAvailable,
                isActivated,
                firstName,
                lastName,
                authority,
                birthday,
                description,
                verificationToken
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
                && Objects.equals(this.isAvailable, other.isAvailable)
                && Objects.equals(this.isActivated, other.isActivated)
                && Objects.equals(this.firstName, other.firstName)
                && Objects.equals(this.lastName, other.lastName)
                && Objects.equals(this.authority, other.authority)
                && Objects.equals(this.birthday, other.birthday)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.verificationToken, other.verificationToken);
    }
}
