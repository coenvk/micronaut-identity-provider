package org.coenvk.notificationsystem.identity.application;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Prototype;
import org.jasypt.util.password.StrongPasswordEncryptor;

@Factory
public class PasswordEncoder {

    @Prototype
    public StrongPasswordEncryptor passwordEncryptor() {
        return new StrongPasswordEncryptor();
    }
}
