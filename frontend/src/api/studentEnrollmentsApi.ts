import { apiFetch } from "./apiFetch";
import type { StudentEnrollment } from "../types/studentEnrollments";

export async function getClassroomEnrollments(
    classroomId: number,
): Promise<StudentEnrollment[]> {
    const response = await apiFetch(
        `/api/classrooms/${classroomId}/students`,
    );

    if (!response.ok) {
        throw new Error(
            `Failed to load classroom enrollments: ${response.status}`,
        );
    }

    return response.json();
}