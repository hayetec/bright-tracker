import Keycloak from "keycloak-js";

const keycloak = new Keycloak({
    url: "http://localhost:8081",
    realm: "bright-tracker",
    clientId: "bright-tracker-web",
});

export default keycloak;