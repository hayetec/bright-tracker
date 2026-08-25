export type StudentGuardian = {
    id: number;
    studentId: number;
    guardianId: number;
    relationship: string;
    isPrimaryContact: boolean;
    isEmergencyContact: boolean;
    createdAt: string | null;
};