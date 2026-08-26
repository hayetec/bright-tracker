import keycloak from "../auth/keycloak";

const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL ?? "";

export async function apiFetch(
    input: RequestInfo | URL,
    init: RequestInit = {},
): Promise<Response> {
    await keycloak.updateToken(30);

    const headers = new Headers(init.headers);

    if (keycloak.token) {
        headers.set("Authorization", `Bearer ${keycloak.token}`);
    }

    const url =
        typeof input === "string"
            ? `${API_BASE_URL}${input}`
            : input;

    return fetch(url, {
        ...init,
        headers,
    });
}