package org.teapot.backend.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "authority")
public class UserAuthority implements GrantedAuthority {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String authority;

    public UserAuthority() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authority);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final UserAuthority other = (UserAuthority) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.authority, other.authority);
    }
}
