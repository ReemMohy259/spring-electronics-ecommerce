package com.voltedge.keycloak.listener;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class RegisterClientRoleAssignerProviderFactory implements EventListenerProviderFactory {

    public static final String PROVIDER_ID = "register-client-role-assigner";

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new RegisterClientRoleAssignerProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
