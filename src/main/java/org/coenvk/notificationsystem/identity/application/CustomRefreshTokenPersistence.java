package org.coenvk.notificationsystem.identity.application;

import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.errors.IssuingAnAccessTokenErrorCode;
import io.micronaut.security.errors.OauthErrorResponseException;
import io.micronaut.security.token.event.RefreshTokenGeneratedEvent;
import io.micronaut.security.token.refresh.RefreshTokenPersistence;
import jakarta.inject.Singleton;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.coenvk.notificationsystem.identity.domain.RefreshToken;
import org.coenvk.notificationsystem.identity.infrastructure.RefreshTokenRepository;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Singleton
public class CustomRefreshTokenPersistence implements RefreshTokenPersistence {

    private final RefreshTokenRepository _repository;

    public CustomRefreshTokenPersistence(RefreshTokenRepository repository) {
        _repository = repository;
    }

    @Transactional
    @Override
    public void persistToken(RefreshTokenGeneratedEvent event) {

        if (event == null) {
            return;
        }

        if (event.getRefreshToken() == null || event.getAuthentication() == null
                || event.getAuthentication().getName() == null) {
            return;
        }

        var refreshToken = UUID.fromString(event.getRefreshToken());

        var entity = new RefreshToken();
        entity.setRefreshToken(refreshToken);
        entity.setUsername(event.getAuthentication().getName());
        entity.setRevoked(false);
        entity.setDateCreated(Instant.now());

        _repository.save(entity);
    }

    @Override
    public Publisher<Authentication> getAuthentication(String refreshTokenString) {
        final var refreshToken = UUID.fromString(refreshTokenString);

        return Flux.create(emitter -> {
            Optional<RefreshToken> optionalToken = _repository.findByRefreshToken(refreshToken);
            if (optionalToken.isPresent()) {
                RefreshToken token = optionalToken.get();
                if (token.getRevoked()) {
                    emitter.error(new OauthErrorResponseException(IssuingAnAccessTokenErrorCode.INVALID_GRANT,
                            "Refresh token revoked", null));
                } else {
                    emitter.next(Authentication.build(token.getUsername()));
                    emitter.complete();
                }
            } else {
                emitter.error(new OauthErrorResponseException(IssuingAnAccessTokenErrorCode.INVALID_GRANT,
                        "Refresh token not found", null));
            }
        }, FluxSink.OverflowStrategy.ERROR);
    }
}
