package org.coenvk.notificationsystem.identity.infrastructure;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import org.coenvk.notificationsystem.identity.domain.RefreshToken;

@JdbcRepository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByRefreshToken(@NonNull @NotBlank UUID refreshToken);

    long updateByUsername(@NonNull @NotBlank String username,
                          boolean revoked);
}
