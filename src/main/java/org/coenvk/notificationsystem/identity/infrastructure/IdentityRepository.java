package org.coenvk.notificationsystem.identity.infrastructure;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.jpa.JpaSpecificationExecutor;
import io.micronaut.data.repository.reactive.ReactorPageableRepository;
import java.util.Optional;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import org.coenvk.notificationsystem.identity.domain.Identity;

@JdbcRepository
public interface IdentityRepository extends ReactorPageableRepository<Identity, UUID>, JpaSpecificationExecutor<Identity> {

    @Join(value = "roles", type = Join.Type.LEFT_FETCH)
    @Join(value = "roles.claims", type = Join.Type.LEFT_FETCH)
    @Join(value = "claims", type = Join.Type.LEFT_FETCH)
    Optional<Identity> findByUsernameOrEmail(@NotBlank String username, @NotBlank String email);

    boolean existsByEmail(@NotBlank String email);
}
