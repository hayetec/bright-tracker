import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App";
import keycloak from "./auth/keycloak";
import { AuthProvider } from "./auth/AuthProvider";
import "./index.css";

keycloak
    .init({
        onLoad: "login-required",
        pkceMethod: "S256",
    })
    .then((authenticated) => {
        if (!authenticated) {
            return;
        }

        createRoot(document.getElementById("root")!).render(
            <StrictMode>
                <AuthProvider>
                    <App />
                </AuthProvider>
            </StrictMode>,
        );
    })
    .catch((error) => {
        console.error("Keycloak initialization failed:", error);
    });