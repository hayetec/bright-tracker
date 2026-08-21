import { apiFetch } from "./apiFetch";
import type { Student } from "../types/students";

export type StudentRequest = {
    firstName: string;
    lastName: string;
    dateOfBirth: string;
    gradeLevel: string;
    status: string;
};

export async function getStudents(): Promise<Student[]> {
    const response = await apiFetch("/api/students");

    if (!response.ok) {
        throw new Error(
            `Failed to load students: ${response.status}`,
        );
    }

    return response.json();
}

export async function createStudent(
    student: StudentRequest,
): Promise<Student> {
    const response = await apiFetch("/api/students", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(student),
    });

    if (!response.ok) {
        const body = await response.text();

        throw new Error(
            `Failed to create student: ${response.status} - ${body}`,
        );
    }

    return response.json();
}

export async function updateStudent(
    studentId: number,
    student: StudentRequest,
): Promise<Student> {
    const response = await apiFetch(`/api/students/${studentId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(student),
    });

    if (!response.ok) {
        const body = await response.text();

        throw new Error(
            `Failed to update student: ${response.status} - ${body}`,
        );
    }

    return response.json();
}

export async function deleteStudent(
    studentId: number,
): Promise<void> {
    const response = await apiFetch(`/api/students/${studentId}`, {
        method: "DELETE",
    });

    if (!response.ok) {
        const body = await response.text();

        throw new Error(
            `Failed to delete student: ${response.status} - ${body}`,
        );
    }
}


