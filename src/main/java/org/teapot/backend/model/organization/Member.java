package org.teapot.backend.model.organization;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.teapot.backend.model.AbstractPersistable;
import org.teapot.backend.model.user.User;
import org.teapot.backend.util.ser.MemberSerializer;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "organization_member")
@JsonSerialize(using = MemberSerializer.class)
public class Member extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Enumerated
    private MemberStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Organization organization;

    private LocalDate admissionDate;

    public Member() {
    }

    public Member(User user,
                  MemberStatus status,
                  Organization organization,
                  LocalDate admissionDate) {
        setUser(user);
        setStatus(status);
        setOrganization(organization);
        setAdmissionDate(admissionDate);
    }

    public Member(Long id,
                  User user,
                  MemberStatus status,
                  Organization organization,
                  LocalDate admissionDate) {
        setId(id);
        setUser(user);
        setStatus(status);
        setOrganization(organization);
        setAdmissionDate(admissionDate);
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }
}
