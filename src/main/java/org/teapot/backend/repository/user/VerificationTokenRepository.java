package org.teapot.backend.repository.user;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.teapot.backend.model.user.VerificationToken;
import org.teapot.backend.repository.BaseEntityRepository;

@RepositoryRestResource(exported = false)
public interface VerificationTokenRepository extends BaseEntityRepository<VerificationToken> {

    VerificationToken findByToken(String token);
}
