package org.teapot.backend.model.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.teapot.backend.model.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
@Data
@ToString(exclude = "user")
@EqualsAndHashCode(callSuper = true, exclude = "user")
@NoArgsConstructor
public class VerificationToken extends AbstractPersistable<Long> {

    @Column(nullable = false, unique = true, length = 32, updatable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expireDateTime = LocalDateTime.now().plusDays(1);

    @OneToOne
    private User user;
}
