package org.teapot.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class User {

    private Long id;

    private String username;

    private String password;

    private Boolean available;

    private String firstName;

    private String lastName;

    private LocalDateTime registrationDate;

    private LocalDate birthday;

    private User() {
    }

    public User(Long id,
                String username,
                String password,
                Boolean available,
                String firstName,
                String lastName,
                LocalDateTime registrationDate,
                LocalDate birthday) {
        this.id = id;
        this.username = username;
        this.password = password;

        this.available = available;
        this.firstName = firstName;
        this.lastName = lastName;
        this.registrationDate = registrationDate;
        this.birthday = birthday;
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

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
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
}
