package com.voltedge.keycloak.listener;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;

import java.util.Map;

public class RegisterClientRoleAssignerProvider implements EventListenerProvider {

    private static final Logger LOGGER = Logger.getLogger(RegisterClientRoleAssignerProvider.class);

    private static final Map<String, String> CLIENT_TO_ROLE = Map.of(
        "voltedge-customer", "CUSTOMER",
        "voltedge-merchant", "MERCHANT"
    );

    private final KeycloakSession session;

    public RegisterClientRoleAssignerProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType() != EventType.REGISTER) {
            return;
        }

        String clientId = event.getClientId();
        String userId = event.getUserId();

        if (clientId == null || userId == null) {
            LOGGER.warnf("REGISTER event missing clientId or userId, skipping. event=%s", event);
            return;
        }

        RealmModel realm = session.getContext().getRealm();
        if (realm == null) {
            realm = session.realms().getRealm(event.getRealmId());
        }

        UserModel user = session.users().getUserById(realm, userId);
        if (user == null) {
            LOGGER.warnf("Could not resolve user %s for REGISTER event", userId);
            return;
        }

        String roleName = CLIENT_TO_ROLE.get(clientId);
        if (roleName == null) {
            LOGGER.warnf("REGISTER event from unmapped client '%s' for user %s — no role assigned", clientId, userId);
            return;
        }

        RoleModel role = realm.getRole(roleName);
        if (role == null) {
            LOGGER.errorf("Role '%s' does not exist in realm '%s' — cannot assign to user %s",
                roleName, realm.getName(), userId);
            return;
        }

        user.grantRole(role);
        LOGGER.infof("Granted role '%s' to user %s (registered via client '%s')",
            roleName, userId, clientId);
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
    }

    @Override
    public void close() {
    }
}
