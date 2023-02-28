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
import java.util.Map;

@ApplicationScoped
public class KeycloakRegisterUtil {

    @Inject
    KeycloakAdminClient keycloakAdminClient;
    @Inject
    KeycloakProps keycloakProps;

    public boolean isIndividualRegisteredAlready(UsersResource usersResource, String username, String email) {
        return !(usersResource.searchByEmail(username, true).isEmpty() &&
                usersResource.searchByEmail(email, true).isEmpty());
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

        if (isIndividualRegisteredAlready(usersResource, registrationForm.getLogin(), registrationForm.getEmail())) {
            throw new IndividualRegisteredAlready("Пользователь уже существует");
        }

        // Create user (requires manage-users role)
        try (Response response = usersResource.create(user)) {
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                System.out.println("RESPONSE STATUS = " + response.getStatus());
                throw new RegistrationFailed();
            }
            // TODO: рассмотреть варианты получше
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            return userId;
        }
    }
}
