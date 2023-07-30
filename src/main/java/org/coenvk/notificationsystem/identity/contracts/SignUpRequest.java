package org.coenvk.notificationsystem.identity.contracts;

import io.micronaut.core.annotation.NonNull;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {

    @NonNull
    @NotBlank
    private String email;

    @NonNull
    @NotBlank
    private String username;

    @NonNull
    @NotBlank
    private String password;
}
