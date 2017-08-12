package org.teapot.backend.config.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.teapot.backend.model.user.User;
import org.teapot.backend.model.user.UserAuthority;

import java.util.Collection;
import java.util.Collections;

@Data
public class AuthenticatedRequestContext implements UserDetails {

    private @NonNull User user;

    @Override
    @JsonIgnore
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return user.getActivated();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getAvailable();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(user.getAuthority());
    }

    public boolean hasRole(String role) {
        return getAuthorities().contains(UserAuthority.valueOf(role));
    }
}
