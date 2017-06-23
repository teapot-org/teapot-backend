package org.teapot.backend.dto;

import java.time.LocalDate;

public class OrganizationDto {

    private Long id;

    private String name;

    private String fullName;

    private LocalDate creationDate;

    private Link members;

    public OrganizationDto() {
    }

    public OrganizationDto(String name, String fullName, LocalDate creationDate, Link members) {
        this.name = name;
        this.fullName = fullName;
        this.creationDate = creationDate;
        this.members = members;
    }

    public OrganizationDto(Long id, String name, String fullName, LocalDate creationDate, Link members) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.creationDate = creationDate;
        this.members = members;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Link getMembers() {
        return members;
    }

    public void setMembers(Link members) {
        this.members = members;
    }
}
