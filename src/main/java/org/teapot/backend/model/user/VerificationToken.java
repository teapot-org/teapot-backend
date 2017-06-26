package org.teapot.backend.model.user;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_token")
public class VerificationToken {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true, length = 32, updatable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expireDateTime = LocalDateTime.now().plusDays(1);

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    public VerificationToken() {
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpireDateTime() {
        return expireDateTime;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpireDateTime(LocalDateTime expireDateTime) {
        this.expireDateTime = expireDateTime;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
