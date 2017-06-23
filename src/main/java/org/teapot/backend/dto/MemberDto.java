package org.teapot.backend.dto;

import org.teapot.backend.model.organization.MemberStatus;

import java.time.LocalDate;

public class MemberDto {

    private Long id;

    private Link user;

    private MemberStatus status;

    private Link organization;

    private LocalDate admissionDate;

    public MemberDto() {
    }

    public MemberDto(Link user, MemberStatus status, Link organization, LocalDate admissionDate) {
        this.user = user;
        this.status = status;
        this.organization = organization;
        this.admissionDate = admissionDate;
    }

    public MemberDto(Long id, String userLink, MemberStatus status, String organizationLink, LocalDate admissionDate) {
        this.id = id;
        this.user = new Link(userLink);
        this.status = status;
        this.organization = new Link(organizationLink);
        this.admissionDate = admissionDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Link getUser() {
        return user;
    }

    public void setUser(Link user) {
        this.user = user;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    public Link getOrganization() {
        return organization;
    }

    public void setOrganization(Link organization) {
        this.organization = organization;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }
}
