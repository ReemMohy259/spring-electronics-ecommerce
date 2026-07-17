package com.electronics.config;

import com.electronics.service.CurrentUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationConverter jwtAuthenticationConverter,
        CurrentUserProvisioningFilter currentUserProvisioningFilter) {

        return http.cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(
                auth -> auth
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/actuator/health",
                        "/api/v1/categories/**",
                        "/api/v1/products/**",
                        "/api/v1/chat",
                        "/api/v1/stripe/**")
                    .permitAll()
                    .requestMatchers("/api/v1/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/v1/merchant/**")
                    .hasRole("MERCHANT")
                    .requestMatchers("/api/v1/customer/**")
                    .hasRole("CUSTOMER")
                    .anyRequest()
                    .authenticated())
            .oauth2ResourceServer(
                oauth -> oauth
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
            .addFilterAfter(currentUserProvisioningFilter, BearerTokenAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CurrentUserProvisioningFilter currentUserProvisioningFilter(
        CurrentUserService currentUserService) {
        return new CurrentUserProvisioningFilter(currentUserService);
    }
}