export type MealProgress = {
    eaten: number;
    remaining: number;
};

export type MealDashboardStudent = {
    studentId: number;
    firstName: string;
    lastName: string;
    classroomId: number | null;
    classroomName: string | null;
    hasAllergies: boolean;
    amSnackEaten: boolean;
    lunchEaten: boolean;
    pmSnackEaten: boolean;
};

export type MealDashboard = {
    date: string;
    totalStudents: number;
    amSnack: MealProgress;
    lunch: MealProgress;
    pmSnack: MealProgress;
    students: MealDashboardStudent[];
};