package com.serguni.utils;

import com.serguni.clients.KeycloakAdminClient;
import com.serguni.dto.RegistrationForm;
import com.serguni.exceptions.IndividualRegisteredAlready;
import com.serguni.exceptions.RegistrationFailed;
import com.serguni.vars.KeycloakProps;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

@ApplicationScoped
public class KeycloakRegisterUtil {

    @Inject
    KeycloakAdminClient keycloakAdminClient;
    @Inject
    KeycloakProps keycloakProps;

    public String checkIndividualRegisteredAlready(UsersResource usersResource, String username, String email) {
        StringJoiner sj = new StringJoiner(",");

        if (!usersResource.searchByUsername(username, true).isEmpty()) {
            sj.add("username");
        }

        if (!usersResource.searchByEmail(email, true).isEmpty()) {
           sj.add("email");
        }

        return sj.toString();
    }
    public String register(RegistrationForm registrationForm) throws RegistrationFailed {

        //Define user
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(registrationForm.getLogin());
        user.setFirstName(registrationForm.getFirstName());
        user.setLastName(registrationForm.getLastName());
        user.setEmail(registrationForm.getEmail());
        user.setAttributes(Collections.singletonMap("phoneNumber", List.of(registrationForm.getPhoneNumber())));


        // Create credentials
        CredentialRepresentation password = new CredentialRepresentation();
        password.setTemporary(false);
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(registrationForm.getPassword());

        user.setCredentials(List.of(password));

        // Get realm
        RealmResource realmResource =  keycloakAdminClient.getKeycloak().realm(keycloakProps.realm());
        UsersResource usersResource = realmResource.users();

        String conflictFields = checkIndividualRegisteredAlready(usersResource, registrationForm.getLogin(), registrationForm.getEmail());

        if (!conflictFields.isEmpty()) {
            throw new IndividualRegisteredAlready(conflictFields);
        }

        // Create user (requires manage-users role)
        try (Response response = usersResource.create(user)) {
            switch (response.getStatus()) {
                case 201 -> {
                    // TODO: рассмотреть варианты получше как достать id
                    String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                    return userId;
                }
                case 409 -> throw new IndividualRegisteredAlready("User is registered already.");
                default -> throw new RegistrationFailed("Register error. Code: " + response.getStatus());
            }
        }
    }
}
