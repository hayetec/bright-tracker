import {
    useEffect,
    useState,
    type FormEvent,
} from "react";
import {
    createStudent,
    deleteStudent,
    getStudents,
    updateStudent,
} from "../api/studentsApi";
import type { Student } from "../types/students";
import { useAuth } from "../auth/AuthProvider";


export default function StudentsPage() {
    const { isAdmin } = useAuth();
    const [students, setStudents] = useState<Student[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [searchTerm, setSearchTerm] = useState("");
    const [showAddForm, setShowAddForm] = useState(false);
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [dateOfBirth, setDateOfBirth] = useState("");
    const [gradeLevel, setGradeLevel] = useState("");
    const [editingStudentId, setEditingStudentId] =
        useState<number | null>(null);

    useEffect(() => {
        async function loadStudents() {
            try {
                const data = await getStudents();
                setStudents(data);
            } catch (error) {
                setError(
                    error instanceof Error
                        ? error.message
                        : "Unable to load students",
                );
            } finally {
                setLoading(false);
            }
        }

        loadStudents();
    }, []);

    const filteredStudents = students.filter((student) => {
        const fullName =
            `${student.firstName} ${student.lastName}`.toLowerCase();

        return fullName.includes(searchTerm.trim().toLowerCase());
    });

    if (loading) {
        return <p>Loading students...</p>;
    }

    async function handleSubmitStudent(
        event: FormEvent<HTMLFormElement>,
    ) {
        event.preventDefault();

        try {
            if (editingStudentId !== null) {
                const updatedStudent = await updateStudent(
                    editingStudentId,
                    {
                        firstName,
                        lastName,
                        dateOfBirth,
                        gradeLevel,
                        status: "ACTIVE",
                    },
                );

                setStudents((currentStudents) =>
                    currentStudents.map((student) =>
                        student.id === editingStudentId
                            ? updatedStudent
                            : student,
                    ),
                );
            } else {
                const newStudent = await createStudent({
                    firstName,
                    lastName,
                    dateOfBirth,
                    gradeLevel,
                    status: "ACTIVE",
                });

                setStudents((currentStudents) => [
                    ...currentStudents,
                    newStudent,
                ]);
            }

            setFirstName("");
            setLastName("");
            setDateOfBirth("");
            setGradeLevel("");
            setEditingStudentId(null);
            setShowAddForm(false);
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : "Unable to save student",
            );
        }
    }

    async function handleDeleteStudent(studentId: number) {
        const confirmed = window.confirm(
            "Are you sure you want to delete this student?",
        );

        if (!confirmed) {
            return;
        }

        try {
            await deleteStudent(studentId);

            setStudents((currentStudents) =>
                currentStudents.filter(
                    (student) => student.id !== studentId,
                ),
            );
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : "Unable to delete student",
            );
        }
    }

    if (error) {
        return <p>{error}</p>;
    }

    return (
        <div className="students-page">
            <div className="students-filters">
                <input
                    type="search"
                    placeholder="Search students..."
                    value={searchTerm}
                    onChange={(event) => setSearchTerm(event.target.value)}
                />
            </div>

            <div className="students-header">
                <div>
                    <h2>Students</h2>
                    <p>View and manage enrolled students.</p>
                </div>

                <strong>
                    {students.length} {students.length === 1 ? "student" : "students"}
                </strong>
            </div>

            {isAdmin && (
                <div className="students-actions">
                    <button
                        type="button"
                        onClick={() => {
                            setShowAddForm((current) => !current);
                            setEditingStudentId(null);
                            setFirstName("");
                            setLastName("");
                            setDateOfBirth("");
                            setGradeLevel("");
                        }}
                    >
                        {showAddForm ? "Cancel" : "Add Student"}
                    </button>
                </div>
            )}

            {isAdmin && showAddForm && (
                <form
                    className="student-form"
                    onSubmit={handleSubmitStudent}
                >
                    <input
                        type="text"
                        placeholder="First name"
                        value={firstName}
                        onChange={(event) => setFirstName(event.target.value)}
                        required
                    />

                    <input
                        type="text"
                        placeholder="Last name"
                        value={lastName}
                        onChange={(event) => setLastName(event.target.value)}
                        required
                    />

                    <input
                        type="date"
                        value={dateOfBirth}
                        onChange={(event) => setDateOfBirth(event.target.value)}
                        required
                    />

                    <input
                        type="text"
                        placeholder="Grade level"
                        value={gradeLevel}
                        onChange={(event) => setGradeLevel(event.target.value)}
                        required
                    />

                    <button type="submit">
                        {editingStudentId !== null
                            ? "Update Student"
                            : "Save Student"}
                    </button>
                </form>
            )}

            <div className="students-table-wrapper">
                <table className="students-table">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Date of Birth</th>
                        <th>Age Group</th>
                        <th>Status</th>
                        {isAdmin && <th>Actions</th>}
                    </tr>
                    </thead>

                    <tbody>
                    {filteredStudents.map((student) => (
                        <tr key={student.id}>
                            <td>
                                <strong>
                                    {student.firstName} {student.lastName}
                                </strong>
                            </td>
                            <td>{student.dateOfBirth}</td>
                            <td>{student.gradeLevel}</td>
                            <td>{student.status}</td>
                            {isAdmin && (
                                <td>
                                    <button
                                        type="button"
                                        onClick={() => {
                                            setEditingStudentId(student.id);
                                            setFirstName(student.firstName);
                                            setLastName(student.lastName);
                                            setDateOfBirth(student.dateOfBirth);
                                            setGradeLevel(student.gradeLevel);
                                            setShowAddForm(true);
                                        }}
                                    >
                                        Edit
                                    </button>

                                    <button
                                        type="button"
                                        onClick={() => handleDeleteStudent(student.id)}
                                    >
                                        Delete
                                    </button>
                                </td>
                            )}
                        </tr>
                    ))}

                    {filteredStudents.length === 0 && (
                        <tr>
                            <td colSpan={isAdmin ? 5 : 4}>No students found.</td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}