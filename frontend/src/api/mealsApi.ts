import { apiFetch } from "./apiClient";
import type { MealDashboard } from "../types/meals";

export type MealRecordRequest = {
    amSnackEaten: boolean;
    lunchEaten: boolean;
    pmSnackEaten: boolean;
};

export async function getMealDashboard(
    date: string,
): Promise<MealDashboard> {
    const response = await apiFetch(
        `/api/meal-dashboard?date=${encodeURIComponent(date)}`,
    );

    if (!response.ok) {
        throw new Error(`Failed to load meal dashboard: ${response.status}`);
    }

    return response.json();
}

export async function saveMealRecord(
    studentId: number,
    date: string,
    mealRecord: MealRecordRequest,
): Promise<void> {
    const updateResponse = await apiFetch(
        `/api/students/${studentId}/meals/${date}`,
        {
            method: "PUT",
            body: JSON.stringify(mealRecord),
        },
    );

    if (updateResponse.ok) {
        return;
    }

    if (updateResponse.status !== 404) {
        throw new Error(
            `Failed to update meal record: ${updateResponse.status}`,
        );
    }

    const createResponse = await apiFetch(
        `/api/students/${studentId}/meals`,
        {
            method: "POST",
            body: JSON.stringify({
                recordDate: date,
                ...mealRecord,
            }),
        },
    );

    if (!createResponse.ok) {
        throw new Error(
            `Failed to create meal record: ${createResponse.status}`,
        );
    }
}