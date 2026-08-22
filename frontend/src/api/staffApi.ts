import { apiFetch } from "./apiFetch";
import type { Staff } from "../types/staff";

export type StaffRequest = {
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber: string;
    role: string;
    status: string;
};

export async function getStaff(): Promise<Staff[]> {
    const response = await apiFetch("/api/staff");

    if (!response.ok) {
        throw new Error(
            `Failed to load staff: ${response.status}`,
        );
    }

    return response.json();
}

export async function createStaff(
    staff: StaffRequest,
): Promise<Staff> {
    const response = await apiFetch("/api/staff", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(staff),
    });

    if (!response.ok) {
        const body = await response.text();

        throw new Error(
            `Failed to create staff: ${response.status} - ${body}`,
        );
    }

    return response.json();
}

export async function updateStaff(
    staffId: number,
    staff: StaffRequest,
): Promise<Staff> {
    const response = await apiFetch(`/api/staff/${staffId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(staff),
    });

    if (!response.ok) {
        const body = await response.text();

        throw new Error(
            `Failed to update staff: ${response.status} - ${body}`,
        );
    }

    return response.json();
}

export async function deleteStaff(
    staffId: number,
): Promise<void> {
    const response = await apiFetch(`/api/staff/${staffId}`, {
        method: "DELETE",
    });

    if (!response.ok) {
        const body = await response.text();

        throw new Error(
            `Failed to delete staff: ${response.status} - ${body}`,
        );
    }
}