import { apiFetch } from "./apiFetch";
import type { Classroom } from "../types/classrooms";

export type ClassroomRequest = {
    name: string;
    gradeLevel: string;
    roomNumber: string;
    capacity: number;
    status: string;
};

export async function getClassrooms(): Promise<Classroom[]> {
    const response = await apiFetch("/api/classrooms");

    if (!response.ok) {
        throw new Error(
            `Failed to load classrooms: ${response.status}`,
        );
    }

    return response.json();
}

export async function createClassroom(
    classroom: ClassroomRequest,
): Promise<Classroom> {
    const response = await apiFetch("/api/classrooms", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(classroom),
    });

    if (!response.ok) {
        const body = await response.text();

        throw new Error(
            `Failed to create classroom: ${response.status} - ${body}`,
        );
    }

    return response.json();
}

export async function updateClassroom(
    classroomId: number,
    classroom: ClassroomRequest,
): Promise<Classroom> {
    const response = await apiFetch(`/api/classrooms/${classroomId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(classroom),
    });

    if (!response.ok) {
        const body = await response.text();

        throw new Error(
            `Failed to update classroom: ${response.status} - ${body}`,
        );
    }

    return response.json();
}

export async function deleteClassroom(
    classroomId: number,
): Promise<void> {
    const response = await apiFetch(`/api/classrooms/${classroomId}`, {
        method: "DELETE",
    });

    if (!response.ok) {
        const body = await response.text();

        throw new Error(
            `Failed to delete classroom: ${response.status} - ${body}`,
        );
    }
}