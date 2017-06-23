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

    public OrganizationDto(Long id, String name, String fullName, LocalDate creationDate, String membersLink) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.creationDate = creationDate;
        this.members = new Link(membersLink);
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
