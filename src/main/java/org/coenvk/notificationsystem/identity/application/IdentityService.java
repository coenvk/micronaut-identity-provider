package org.coenvk.notificationsystem.identity.application;

import io.micronaut.context.annotation.Bean;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import org.coenvk.notificationsystem.identity.contracts.SignUpRequest;
import org.coenvk.notificationsystem.identity.contracts.SignUpResponse;
import org.coenvk.notificationsystem.identity.domain.Identity;
import org.coenvk.notificationsystem.identity.infrastructure.IdentityRepository;
import org.jasypt.util.password.StrongPasswordEncryptor;
import reactor.core.publisher.Mono;

@Bean
public class IdentityService {

    private final IdentityRepository _identityRepository;
    private final StrongPasswordEncryptor _passwordEncryptor;

    public IdentityService(IdentityRepository identityRepository, StrongPasswordEncryptor passwordEncryptor) {
        _identityRepository = identityRepository;
        _passwordEncryptor = passwordEncryptor;
    }

    public Mono<SignUpResponse> signUp(SignUpRequest request) {

        if (_identityRepository.existsByEmail(request.getEmail())) {
            return Mono.error(new HttpStatusException(HttpStatus.CONFLICT, "User already exists!"));
        }

        var identity = new Identity();
        identity.setEmail(request.getEmail());
        identity.setUsername(request.getUsername());
        identity.setPasswordHash(_passwordEncryptor.encryptPassword(request.getPassword()));

        return Mono.from(_identityRepository.save(identity))
                .map(x -> new SignUpResponse(x.getEmail(), x.getUsername()));
    }
}
