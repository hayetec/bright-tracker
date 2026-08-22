import { apiFetch } from "./apiFetch";
import type { Guardian } from "../types/guardians";

export type GuardianRequest = {
    firstName: string;
    lastName: string;
    phoneNumber: string;
    email?: string | null;
};

export async function getGuardians(): Promise<Guardian[]> {
    const response = await apiFetch("/api/guardians");

    if (!response.ok) {
        throw new Error(
            `Failed to load guardians: ${response.status}`,
        );
    }

    return response.json();
}

export async function createGuardian(
    guardian: GuardianRequest,
): Promise<Guardian> {
    const response = await apiFetch("/api/guardians", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(guardian),
    });

    if (!response.ok) {
        const body = await response.text();

        throw new Error(
            `Failed to create guardian: ${response.status} - ${body}`,
        );
    }

    return response.json();
}

export async function updateGuardian(
    guardianId: number,
    guardian: GuardianRequest,
): Promise<Guardian> {
    const response = await apiFetch(`/api/guardians/${guardianId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(guardian),
    });

    if (!response.ok) {
        const body = await response.text();

        throw new Error(
            `Failed to update guardian: ${response.status} - ${body}`,
        );
    }

    return response.json();
}

export async function deleteGuardian(
    guardianId: number,
): Promise<void> {
    const response = await apiFetch(`/api/guardians/${guardianId}`, {
        method: "DELETE",
    });

    if (!response.ok) {
        const body = await response.text();

        throw new Error(
            `Failed to delete guardian: ${response.status} - ${body}`,
        );
    }
}