package com.serguni.vars;


import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigMapping(prefix = "keycloak")
public interface KeycloakProps {
    @WithName("server-url")
    String serverUrl();

    @WithName("realm")
    String realm();

    @WithName("client-id")
    String clientId();

    @WithName("client-secret")
    String clientSecret();

    @WithName("admin-username")
    String adminUsername();

    @WithName("admin-password")
    String adminPassword();
}
