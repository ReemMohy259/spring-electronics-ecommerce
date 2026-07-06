
# ---- Stage 1: build the SPI jar ----
FROM maven:3.9-eclipse-temurin-21 AS spi-builder
WORKDIR /spi
COPY keycloak/spi/pom.xml .
COPY keycloak/spi/src ./src
RUN mvn -B -q clean package -DskipTests

# ---- Stage 2: augment Keycloak ----
FROM quay.io/keycloak/keycloak:26.6 AS builder

ENV KC_DB=postgres
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

COPY --from=spi-builder /spi/target/*.jar /opt/keycloak/providers/
COPY keycloak/themes/ /opt/keycloak/themes/

WORKDIR /opt/keycloak
RUN /opt/keycloak/bin/kc.sh build

# ---- Stage 3: final runtime image ----
FROM quay.io/keycloak/keycloak:26.6
COPY --from=builder /opt/keycloak/ /opt/keycloak/
COPY keycloak/realm-config/import/ /opt/keycloak/data/import/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start-dev", "--import-realm", "--spi-theme-static-max-age=-1", "--spi-theme-cache-themes=false", "--spi-theme-cache-templates=false"]
