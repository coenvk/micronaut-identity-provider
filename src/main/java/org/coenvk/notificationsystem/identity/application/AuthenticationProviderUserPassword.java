package org.coenvk.notificationsystem.identity.application;

import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.coenvk.notificationsystem.identity.domain.Role;
import org.coenvk.notificationsystem.identity.infrastructure.IdentityRepository;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {

    private final IdentityRepository _identityRepository;
    private final StrongPasswordEncryptor _passwordEncryptor;

    public AuthenticationProviderUserPassword(IdentityRepository identityRepository,
                                              StrongPasswordEncryptor passwordEncryptor) {
        _identityRepository = identityRepository;
        _passwordEncryptor = passwordEncryptor;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest,
                                                          AuthenticationRequest<?, ?> authenticationRequest) {

        return Flux.create(emitter -> {
            var usernameOrEmail = authenticationRequest.getIdentity().toString();
            var optionalIdentity = _identityRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);

            if (optionalIdentity.isEmpty()) {
                emitter.error(AuthenticationResponse.exception());
                return;
            }

            var identity = optionalIdentity.get();

            if (!_passwordEncryptor.checkPassword(authenticationRequest.getSecret().toString(),
                    identity.getPasswordHash())) {
                emitter.error(AuthenticationResponse.exception());
                return;
            }

            var roles = identity.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            var roleClaims = identity.getRoles().stream()
                    .flatMap(role -> role.getClaims().stream());

            var claims = Stream.concat(roleClaims, identity.getClaims().stream());

            var attributes = claims
                    .map(claim -> Map.entry(claim.getClaimType(), (Object) claim.getClaimValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            emitter.next(AuthenticationResponse.success(identity.getEmail(), roles, attributes));
            emitter.complete();
        }, FluxSink.OverflowStrategy.ERROR);
    }
}
