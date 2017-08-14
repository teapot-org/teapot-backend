package org.teapot.backend.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.teapot.backend.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class VerificationToken extends BaseEntity {

    @Column(nullable = false, unique = true, length = 32, updatable = false)
    @Getter
    @Setter
    private String token;

    @Column(nullable = false)
    @Getter
    @Setter
    private LocalDateTime expireDateTime = LocalDateTime.now().plusDays(1);

    @OneToOne(optional = false)
    @Getter
    @Setter
    private User user;
}
