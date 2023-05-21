package com.serguni.clients;

import com.serguni.vars.KeycloakProps;
import org.keycloak.admin.client.Keycloak;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class KeycloakAdminClient {
    @Inject
    KeycloakProps keycloakProps;

    Keycloak keycloak;

    public Keycloak getKeycloak() {
        if (keycloak == null || keycloak.isClosed()) {
            keycloak = Keycloak.getInstance(
                    keycloakProps.serverUrl(),
                    keycloakProps.realm(),
                    keycloakProps.adminUsername(),
                    keycloakProps.adminPassword(),
                    keycloakProps.clientId(),
                    keycloakProps.clientSecret());
        }

        return keycloak;
    }

    public String getAccessToken(String username, String password) {
        return Keycloak.getInstance(
                keycloakProps.serverUrl(),
                keycloakProps.realm(),
                username,
                password,
                keycloakProps.clientId(),
                keycloakProps.clientSecret()).tokenManager().getAccessToken().getToken();
    }
}
