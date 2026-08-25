import { apiFetch } from "./apiFetch";
import type { StudentGuardian } from "../types/studentGuardians";

export async function getStudentGuardians(
    studentId: number,
): Promise<StudentGuardian[]> {
    const response = await apiFetch(
        `/api/students/${studentId}/guardians`,
    );

    if (!response.ok) {
        throw new Error(
            `Failed to load student guardians: ${response.status}`,
        );
    }

    return response.json();
}