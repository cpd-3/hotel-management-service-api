package com.cpd.hotel_system.hotel_management_service_api.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloakSecurityUtil {
    Keycloak keycloak;

    @Value("${keycloak.config.server-url}")
    private String serverUrl;
    @Value("${keycloak.config.realm}")
    private String realm;
    @Value("${keycloak.config.client-id}")
    private String clientId;
    @Value("${keycloak.config.grant-type}")
    private String grantType;
    @Value("${keycloak.config.name}")
    private String username;
    @Value("${keycloak.config.password}")
    private String password;
    @Value("${keycloak.config.secret}")
    private String secret;

    public Keycloak getKeycloakInstance() {
        if(keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .grantType(grantType)
                    .username(username)
                    .password(password)
                    .clientSecret(secret)
                    .build();
        }
        return keycloak;
    }

}
