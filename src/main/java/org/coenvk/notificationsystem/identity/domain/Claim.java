package org.coenvk.notificationsystem.identity.domain;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@MappedEntity("claim")
public class Claim {

    @Id
    @GeneratedValue
    @NonNull
    private UUID id;

    @NonNull
    @NotBlank
    private String claimType;

    @NonNull
    @NotBlank
    private String claimValue;
}
