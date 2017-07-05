package org.teapot.backend.model.organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.teapot.backend.model.AbstractPersistable;
import org.teapot.backend.model.user.User;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "organization_member")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"user", "organization"})
@NoArgsConstructor
@AllArgsConstructor
public class Member extends AbstractPersistable<Long> {

    @ManyToOne
    private User user;

    @Enumerated
    private MemberStatus status;

    @ManyToOne
    private Organization organization;

    private LocalDate admissionDate;
}
