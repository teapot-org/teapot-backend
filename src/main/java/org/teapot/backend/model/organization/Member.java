package org.teapot.backend.model.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.BaseEntity;
import org.teapot.backend.model.user.User;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "organization_member")
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {

    @ManyToOne(optional = false)
    @Getter
    @Setter
    private User user;

    @Enumerated
    @Getter
    @Setter
    private MemberStatus status;

    @ManyToOne(optional = false)
    @Getter
    @Setter
    private Organization organization;
}
