export type StudentAllergy = {
    id: number;
    studentId: number;
    allergen: string;
    severity: string;
    notes: string | null;
    createdAt: string | null;
    updatedAt: string | null;
};