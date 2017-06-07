package org.teapot.backend.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "user")
public class User implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false, length = 16)
    private String password;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "first_name", length = 32)
    private String firstName;

    @Column(name = "last_name", length = 32)
    private String lastName;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    private LocalDate birthday;

    private String description;

    public User() {
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

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        User user = (User) o;

        if ((id != null) ? !id.equals(user.id) : (user.id != null)) {
            return false;
        }

        if (!username.equals(user.username)) {
            return false;
        }

        if (!password.equals(user.password)) {
            return false;
        }

        if ((isAvailable != null) ? !isAvailable.equals(user.isAvailable) :
                (user.isAvailable != null)) {

            return false;
        }

        if ((firstName != null) ? !firstName.equals(user.firstName) :
                (user.firstName != null)) {

            return false;
        }

        if ((lastName != null) ? !lastName.equals(user.lastName) :
                (user.lastName != null)) {

            return false;
        }

        if ((registrationDate != null) ?
                !registrationDate.equals(user.registrationDate) :
                (user.registrationDate != null)) {

            return false;
        }

        if ((birthday != null) ? !birthday.equals(user.birthday) :
                (user.birthday != null)) {

            return false;
        }

        return (description != null) ? description.equals(user.description) :
                (user.description == null);
    }

    @Override
    public int hashCode() {
        int result = (id != null) ? id.hashCode() : 0;

        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();

        result = 31 * result + ((isAvailable != null) ?
                isAvailable.hashCode() : 0);

        result = 31 * result + ((firstName != null) ?
                firstName.hashCode() : 0);

        result = 31 * result + ((lastName != null) ?
                lastName.hashCode() : 0);

        result = 31 * result + ((registrationDate != null) ?
                registrationDate.hashCode() : 0);

        result = 31 * result + ((birthday != null) ?
                birthday.hashCode() : 0);

        result = 31 * result + ((description != null) ?
                description.hashCode() : 0);

        return result;
    }
}
