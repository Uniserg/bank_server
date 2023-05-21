//package com.serguni.graph;
//
//import com.serguni.clients.KeycloakAdminClient;
//import com.serguni.vars.KeycloakProps;
//import io.quarkus.test.junit.QuarkusTest;
//import org.apache.commons.configuration2.PropertiesConfiguration;
//import org.junit.jupiter.api.Test;
//
//import javax.inject.Inject;
//
//@QuarkusTest
//public class KeycloakRequestTest {
//    @Inject
//    KeycloakAdminClient keycloakAdminClient;
//
//    @Test
//    public void testRequest() {
//        keycloakAdminClient
//                .getKeycloak()
//                .realm(KeycloakProps.realm)
//                .users().count();
//    }
//}
