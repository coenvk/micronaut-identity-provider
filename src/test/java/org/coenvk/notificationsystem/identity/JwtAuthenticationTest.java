package org.coenvk.notificationsystem.identity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

@MicronautTest
public class JwtAuthenticationTest {

    @Inject
    @Client("/")
    private HttpClient client;

    @Test
    void givenSecureUrl_WithoutAuthentication_ReturnsUnauthorized() {
        Executable func = () -> client.toBlocking().exchange(HttpRequest.GET("/").accept(MediaType.TEXT_PLAIN));
        var e = assertThrows(HttpClientResponseException.class, func);
        assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
    }

    @Test
    void givenSuccessfulAuthentication_AccessTokenAndRefreshTokenIssued() {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("sherlock", "password");
        HttpRequest<UsernamePasswordCredentials> request = HttpRequest.POST("/login", creds);
        BearerAccessRefreshToken token = client.toBlocking().retrieve(request,
                BearerAccessRefreshToken.class);

        assertEquals("sherlock", token.getUsername());
        assertNotNull(token.getAccessToken());
        assertNotNull(token.getRefreshToken());

        var parsedJwt = assertDoesNotThrow(() -> JWTParser.parse(token.getAccessToken()));
        assertInstanceOf(SignedJWT.class, parsedJwt);
    }

    @Test
    void givenSuccessfulAuthentication_JwtIssued() {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("sherlock", "password");
        HttpRequest<UsernamePasswordCredentials> request = HttpRequest.POST("/login", creds);
        HttpResponse<BearerAccessRefreshToken> rsp = client.toBlocking().exchange(request,
                BearerAccessRefreshToken.class);

        assertEquals(HttpStatus.OK, rsp.status());

        BearerAccessRefreshToken bearerAccessRefreshToken = rsp.body();
        assertNotNull(bearerAccessRefreshToken);
        assertEquals("sherlock", bearerAccessRefreshToken.getUsername());
        assertNotNull(bearerAccessRefreshToken.getAccessToken());

        var parsedJwt = assertDoesNotThrow(() -> JWTParser.parse(bearerAccessRefreshToken.getAccessToken()));
        assertInstanceOf(SignedJWT.class, parsedJwt);

        MutableHttpRequest<Object> requestWithAuthorization = HttpRequest.GET("/")
                .accept(MediaType.TEXT_PLAIN)
                .bearerAuth(bearerAccessRefreshToken.getAccessToken());

        HttpResponse<String> response = client.toBlocking().exchange(requestWithAuthorization, String.class);

        assertEquals(HttpStatus.OK, response.status());
        assertEquals("sherlock", response.body());
    }
}
