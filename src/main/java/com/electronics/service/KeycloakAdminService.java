package com.electronics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakAdminService {

    private final RestTemplate restTemplate;

    @Value("${app.keycloak.admin-server-url}")
    private String keycloakServerUrl;

    @Value("${app.keycloak.admin-username}")
    private String adminUsername;

    @Value("${app.keycloak.admin-password}")
    private String adminPassword;

    @Value("${app.keycloak.admin-realm}")
    private String realm;

    public void updateUser(String keycloakId, String firstName, String lastName) {
        String accessToken = getAdminAccessToken();

        String url = "%s/admin/realms/%s/users/%s".formatted(keycloakServerUrl, realm, keycloakId);

        Map<String, Object> body = new LinkedHashMap<>();
        if (firstName != null)
            body.put("firstName", firstName);
        if (lastName != null)
            body.put("lastName", lastName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
    }

    public long countUsersWithRealmRole(String roleName) {
        return getUsersWithRealmRole(roleName).size();
    }

    public List<Map<String, Object>> getUsersWithRealmRole(String roleName) {
        String accessToken = getAdminAccessToken();
        String url = "%s/admin/realms/%s/roles/%s/users?first=0&max=1000"
            .formatted(keycloakServerUrl, realm, roleName);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        ResponseEntity<List> response = restTemplate
            .exchange(url, HttpMethod.GET, new HttpEntity<>(headers), List.class);
        List<Map<String, Object>> users = response.getBody();
        return users == null ? List.of() : users;
    }

    private String getAdminAccessToken() {
        String url = "%s/realms/master/protocol/openid-connect/token".formatted(keycloakServerUrl);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", "admin-cli");
        body.add("username", adminUsername);
        body.add("password", adminPassword);
        body.add("grant_type", "password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate
            .exchange(url, HttpMethod.POST, entity, Map.class);

        return (String) response.getBody().get("access_token");
    }
}
