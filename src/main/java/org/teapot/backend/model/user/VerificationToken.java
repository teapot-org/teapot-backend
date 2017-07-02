package org.teapot.backend.model.user;

import org.teapot.backend.model.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class VerificationToken extends AbstractPersistable<Long> {

    @Column(nullable = false, unique = true, length = 32, updatable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expireDateTime = LocalDateTime.now().plusDays(1);

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    public VerificationToken() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    public LocalDateTime getExpireDateTime() {
        return expireDateTime;
    }

    public void setExpireDateTime(LocalDateTime expireDateTime) {
        this.expireDateTime = expireDateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
