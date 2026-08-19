import keycloak from "../auth/keycloak";

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

    return fetch(path, {
        ...options,
        headers,
    });
}