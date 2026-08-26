import Keycloak from "keycloak-js";

const keycloak = new Keycloak({
    url: import.meta.env.VITE_KEYCLOAK_URL ?? "http://localhost:8081",
    realm: "bright-tracker",
    clientId: "bright-tracker-web",
});

export default keycloak;