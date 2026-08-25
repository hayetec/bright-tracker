import { apiFetch } from "./apiFetch";
import type { StudentAllergy } from "../types/studentAllergies";

export async function getStudentAllergies(
    studentId: number,
): Promise<StudentAllergy[]> {
    const response = await apiFetch(
        `/api/students/${studentId}/allergies`,
    );

    if (!response.ok) {
        throw new Error(
            `Failed to load student allergies: ${response.status}`,
        );
    }

    return response.json();
}