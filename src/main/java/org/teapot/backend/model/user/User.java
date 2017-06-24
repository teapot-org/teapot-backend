package org.teapot.backend.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.teapot.backend.util.ser.UserSerializer;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;


@Entity
@Table(name = "user")
@JsonSerialize(using = UserSerializer.class)
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false, length = 32)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "is_activated")
    private Boolean isActivated = false;

    @Column(name = "first_name", length = 32)
    private String firstName;

    @Column(name = "last_name", length = 32)
    private String lastName;

    @Enumerated
    private UserAuthority authority = UserAuthority.USER;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    private LocalDate birthday;

    private String description;

    @JsonIgnore
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private VerificationToken verificationToken;

    public User() {
    }

    public User(Long id,
                String username,
                String email,
                String password,
                Boolean isAvailable,
                Boolean isActivated,
                String firstName,
                String lastName,
                UserAuthority authority,
                LocalDate registrationDate,
                LocalDate birthday,
                String description,
                VerificationToken verificationToken) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isAvailable = isAvailable;
        this.isActivated = isActivated;
        this.firstName = firstName;
        this.lastName = lastName;
        this.authority = authority;
        this.registrationDate = registrationDate;
        this.birthday = birthday;
        this.description = description;
        this.verificationToken = verificationToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
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
        return Objects.hash(
                id,
                username,
                email,
                password,
                isAvailable,
                isActivated,
                firstName,
                lastName,
                authority,
                registrationDate,
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
        final User other = (User) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.username, other.username)
                && Objects.equals(this.email, other.email)
                && Objects.equals(this.password, other.password)
                && Objects.equals(this.isAvailable, other.isAvailable)
                && Objects.equals(this.isActivated, other.isActivated)
                && Objects.equals(this.firstName, other.firstName)
                && Objects.equals(this.lastName, other.lastName)
                && Objects.equals(this.authority, other.authority)
                && Objects.equals(this.registrationDate, other.registrationDate)
                && Objects.equals(this.birthday, other.birthday)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.verificationToken, other.verificationToken);
    }
}
