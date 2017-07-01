package org.teapot.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.teapot.backend.model.user.VerificationToken;

import javax.transaction.Transactional;

@Transactional
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);
}
