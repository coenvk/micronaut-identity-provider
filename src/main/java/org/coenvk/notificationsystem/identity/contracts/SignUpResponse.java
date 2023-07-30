package org.coenvk.notificationsystem.identity.contracts;

import io.micronaut.core.annotation.NonNull;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignUpResponse {

    @NonNull
    @NotBlank
    private String email;

    @NonNull
    @NotBlank
    private String username;
}
