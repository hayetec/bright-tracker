import { useEffect, useState } from "react";
import {
    getMealDashboard,
    saveMealRecord,
} from "../api/mealsApi";
import { useAuth } from "../auth/AuthProvider";
import MealProgressCard from "../components/MealProgressCard";
import type {
    MealDashboard,
    MealDashboardStudent,
} from "../types/meals";

function getTodayDate(): string {
    const now = new Date();

    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, "0");
    const day = String(now.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
}

export default function MealsPage() {
    const { isAdmin } = useAuth();

    const [dashboard, setDashboard] = useState<MealDashboard | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedClassroom, setSelectedClassroom] = useState("ALL");
    const [updatingStudentId, setUpdatingStudentId] = useState<number | null>(
        null,
    );

    const date = getTodayDate();

    const classrooms = Array.from(
        new Set(
            dashboard?.students
                .map((student) => student.classroomName)
                .filter((name): name is string => Boolean(name)),
        ),
    ).sort();

    const filteredStudents =
        dashboard?.students.filter((student) => {
            const fullName =
                `${student.firstName} ${student.lastName}`.toLowerCase();

            const matchesSearch = fullName.includes(
                searchTerm.trim().toLowerCase(),
            );

            const matchesClassroom =
                selectedClassroom === "ALL" ||
                student.classroomName === selectedClassroom;

            return matchesSearch && matchesClassroom;
        }) ?? [];

    useEffect(() => {
        async function loadDashboard() {
            try {
                const data = await getMealDashboard(date);
                setDashboard(data);
            } catch (error) {
                setError(
                    error instanceof Error
                        ? error.message
                        : "Unable to load meal dashboard",
                );
            } finally {
                setLoading(false);
            }
        }

        loadDashboard();
    }, [date]);

    async function toggleMeal(
        student: MealDashboardStudent,
        meal: "amSnackEaten" | "lunchEaten" | "pmSnackEaten",
    ) {
        if (!isAdmin) {
            return;
        }

        try {
            setUpdatingStudentId(student.studentId);

            await saveMealRecord(student.studentId, date, {
                amSnackEaten:
                    meal === "amSnackEaten"
                        ? !student.amSnackEaten
                        : student.amSnackEaten,

                lunchEaten:
                    meal === "lunchEaten"
                        ? !student.lunchEaten
                        : student.lunchEaten,

                pmSnackEaten:
                    meal === "pmSnackEaten"
                        ? !student.pmSnackEaten
                        : student.pmSnackEaten,
            });

            const refreshedDashboard = await getMealDashboard(date);
            setDashboard(refreshedDashboard);
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : "Unable to update meal record",
            );
        } finally {
            setUpdatingStudentId(null);
        }
    }

    if (loading) {
        return <p>Loading meals...</p>;
    }

    if (error) {
        return <p>{error}</p>;
    }

    if (!dashboard) {
        return <p>No meal data available.</p>;
    }

    return (
        <div className="meals-page">
            <div className="meals-header">
                <div>
                    <h2>Today's Meals</h2>
                    <p>{dashboard.date}</p>
                </div>

                <strong>{dashboard.totalStudents} students</strong>
            </div>

            <div className="meal-progress-grid">
                <MealProgressCard
                    title="AM Snack"
                    eaten={dashboard.amSnack.eaten}
                    remaining={dashboard.amSnack.remaining}
                    total={dashboard.totalStudents}
                />

                <MealProgressCard
                    title="Lunch"
                    eaten={dashboard.lunch.eaten}
                    remaining={dashboard.lunch.remaining}
                    total={dashboard.totalStudents}
                />

                <MealProgressCard
                    title="PM Snack"
                    eaten={dashboard.pmSnack.eaten}
                    remaining={dashboard.pmSnack.remaining}
                    total={dashboard.totalStudents}
                />
            </div>

            <div className="meal-filters">
                <input
                    type="search"
                    placeholder="Search students..."
                    value={searchTerm}
                    onChange={(event) => setSearchTerm(event.target.value)}
                />

                <select
                    value={selectedClassroom}
                    onChange={(event) => setSelectedClassroom(event.target.value)}
                >
                    <option value="ALL">All classrooms</option>

                    {classrooms.map((classroom) => (
                        <option key={classroom} value={classroom}>
                            {classroom}
                        </option>
                    ))}
                </select>
            </div>

            <div className="meal-table-wrapper">
                <table className="meal-table">
                    <thead>
                    <tr>
                        <th>Student</th>
                        <th>Classroom</th>
                        <th>AM Snack</th>
                        <th>Lunch</th>
                        <th>PM Snack</th>
                    </tr>
                    </thead>

                    <tbody>
                    {filteredStudents.map((student) => (
                        <tr key={student.studentId}>
                            <td>
                                <strong>
                                    {student.firstName} {student.lastName}
                                </strong>

                                {student.hasAllergies && (
                                    <span className="allergy-badge">Allergy</span>
                                )}
                            </td>

                            <td>{student.classroomName ?? "Unassigned"}</td>

                            <td>
                                <MealStatus
                                    eaten={student.amSnackEaten}
                                    editable={isAdmin}
                                    disabled={updatingStudentId === student.studentId}
                                    onClick={() =>
                                        toggleMeal(student, "amSnackEaten")
                                    }
                                />
                            </td>

                            <td>
                                <MealStatus
                                    eaten={student.lunchEaten}
                                    editable={isAdmin}
                                    disabled={updatingStudentId === student.studentId}
                                    onClick={() =>
                                        toggleMeal(student, "lunchEaten")
                                    }
                                />
                            </td>

                            <td>
                                <MealStatus
                                    eaten={student.pmSnackEaten}
                                    editable={isAdmin}
                                    disabled={updatingStudentId === student.studentId}
                                    onClick={() =>
                                        toggleMeal(student, "pmSnackEaten")
                                    }
                                />
                            </td>
                        </tr>
                    ))}

                    {filteredStudents.length === 0 && (
                        <tr>
                            <td colSpan={5}>No students found.</td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

type MealStatusProps = {
    eaten: boolean;
    editable: boolean;
    disabled: boolean;
    onClick: () => void;
};

function MealStatus({
                        eaten,
                        editable,
                        disabled,
                        onClick,
                    }: MealStatusProps) {
    if (!editable) {
        return (
            <span
                className={
                    eaten
                        ? "meal-status eaten"
                        : "meal-status not-eaten"
                }
            >
        {eaten ? "✓ Eaten" : "Not eaten"}
      </span>
        );
    }

    return (
        <button
            type="button"
            className={
                eaten
                    ? "meal-button eaten"
                    : "meal-button not-eaten"
            }
            disabled={disabled}
            onClick={onClick}
        >
            {eaten ? "✓ Eaten" : "Mark eaten"}
        </button>
    );
}