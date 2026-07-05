package com.voltedge.keycloak.authenticator;

import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.events.Errors;
import org.keycloak.models.*;

import java.util.Map;

public class LoginClientRoleEnforcerAuthenticator implements Authenticator {

    private static final Map<String, String> CLIENT_REQUIRED_ROLE = Map.of(
        "voltedge-customer", "CUSTOMER",
        "voltedge-merchant", "MERCHANT",
        "voltedge-admin", "ADMIN"
    );

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        RealmModel realm = context.getRealm();
        UserModel user = context.getUser();
        ClientModel client = context.getAuthenticationSession().getClient();

        String requiredRoleName = CLIENT_REQUIRED_ROLE.get(client.getClientId());

        if (requiredRoleName == null) {
            context.success();
            return;
        }

        RoleModel requiredRole = realm.getRole(requiredRoleName);

        if (requiredRole == null || user == null || !user.hasRole(requiredRole)) {
            context.getEvent().error("access_denied");
            Response errorResponse = context.form()
                .setError("This account is not registered for this application.")
                .createErrorPage(Response.Status.FORBIDDEN);
            context.getEvent().error(Errors.ACCESS_DENIED);
            context.failure(AuthenticationFlowError.ACCESS_DENIED, errorResponse);
            return;
        }

        context.success();
    }

    @Override
    public void action(AuthenticationFlowContext context) {
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void close() {
    }
}
