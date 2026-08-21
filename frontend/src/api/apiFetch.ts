import keycloak from "../auth/keycloak";

export async function apiFetch(
    input: RequestInfo | URL,
    init: RequestInit = {},
): Promise<Response> {
    await keycloak.updateToken(30);

    const headers = new Headers(init.headers);

    if (keycloak.token) {
        headers.set("Authorization", `Bearer ${keycloak.token}`);
    }

    return fetch(input, {
        ...init,
        headers,
    });
}