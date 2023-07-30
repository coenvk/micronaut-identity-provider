package org.coenvk.notificationsystem.identity.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.jdbc.annotation.JoinColumn;
import io.micronaut.data.jdbc.annotation.JoinTable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@MappedEntity("role")
public class Role {

    @Id
    @GeneratedValue
    @NonNull
    private UUID id;

    @NonNull
    @NotBlank
    private String name;

    @JoinTable(name = "role_has_claim", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "claim_id"))
    @Relation(value = Relation.Kind.MANY_TO_MANY, cascade = Relation.Cascade.PERSIST)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Set<Claim> claims = new HashSet<>();
}
