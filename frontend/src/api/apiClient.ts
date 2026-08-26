import keycloak from "../auth/keycloak";

const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL ?? "";

export async function apiFetch(
    path: string,
    options: RequestInit = {},
): Promise<Response> {
    await keycloak.updateToken(30);

    const headers = new Headers(options.headers);

    headers.set("Authorization", `Bearer ${keycloak.token}`);

    if (options.body) {
        headers.set("Content-Type", "application/json");
    }

    return fetch(`${API_BASE_URL}${path}`, {
        ...options,
        headers,
    });
}