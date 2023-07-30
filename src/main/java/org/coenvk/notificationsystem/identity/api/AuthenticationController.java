package org.coenvk.notificationsystem.identity.api;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.coenvk.notificationsystem.identity.application.IdentityService;
import org.coenvk.notificationsystem.identity.contracts.SignUpRequest;
import org.coenvk.notificationsystem.identity.contracts.SignUpResponse;
import reactor.core.publisher.Mono;

@Controller
public class AuthenticationController {

    private final IdentityService _identityService;

    public AuthenticationController(IdentityService identityService) {
        _identityService = identityService;
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post("/signup")
    public Mono<SignUpResponse> signUp(@Body SignUpRequest request) {

        return _identityService.signUp(request);
    }
}
