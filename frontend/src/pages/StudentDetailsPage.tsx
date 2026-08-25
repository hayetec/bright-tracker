import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getStudentById } from "../api/studentsApi";
import type { Student } from "../types/students";
import { getStudentGuardians } from "../api/studentGuardiansApi";
import { getGuardianById } from "../api/guardiansApi";
import type { Guardian } from "../types/guardians";
import type { StudentGuardian } from "../types/studentGuardians";
import { getStudentAllergies } from "../api/studentAllergiesApi";
import type { StudentAllergy } from "../types/studentAllergies";
import { getClassrooms } from "../api/classroomsApi";
import { getClassroomEnrollments } from "../api/studentEnrollmentsApi";
import type { Classroom } from "../types/classrooms";

type GuardianDetails = {
    relationship: StudentGuardian;
    guardian: Guardian;
};

export default function StudentDetailsPage() {
    const { studentId } = useParams();

    const [student, setStudent] = useState<Student | null>(null);
    const [guardians, setGuardians] = useState<GuardianDetails[]>([]);
    const [allergies, setAllergies] = useState<StudentAllergy[]>([]);
    const [classroom, setClassroom] = useState<Classroom | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        async function loadStudent() {
            if (!studentId) {
                setError("Student ID is missing.");
                setLoading(false);
                return;
            }

            try {
                const id = Number(studentId);

                const data = await getStudentById(id);
                setStudent(data);

                const relationships = await getStudentGuardians(id);

                const guardianDetails = await Promise.all(
                    relationships.map(async (relationship) => {
                        const guardian = await getGuardianById(
                            relationship.guardianId,
                        );

                        return {
                            relationship,
                            guardian,
                        };
                    }),
                );

                setGuardians(guardianDetails);
                const allergyData = await getStudentAllergies(id);
                setAllergies(allergyData);

                const classroomData = await getClassrooms();

                const enrollmentResults = await Promise.all(
                    classroomData.map(async (currentClassroom) => {
                        const enrollments = await getClassroomEnrollments(
                            currentClassroom.id,
                        );

                        return {
                            classroom: currentClassroom,
                            enrollments,
                        };
                    }),
                );

                const matchingClassroom = enrollmentResults.find(
                    ({ enrollments }) =>
                        enrollments.some(
                            (enrollment) => enrollment.studentId === id,
                        ),
                );

                setClassroom(matchingClassroom?.classroom ?? null);

            } catch (error) {
                if (
                    error instanceof Error &&
                    error.message.includes("404")
                ) {
                    setError("Student not found.");
                } else {
                    setError(
                        error instanceof Error
                            ? error.message
                            : "Unable to load student",
                    );
                }
            } finally {
                setLoading(false);
            }
        }

        loadStudent();
    }, [studentId]);

    if (loading) {
        return <p>Loading student...</p>;
    }

    if (error) {
        return <p>{error}</p>;
    }

    if (!student) {
        return <p>Student not found.</p>;
    }

    return (
        <div className="student-details-page">
            <Link to="/students">
                ← Back to Students
            </Link>

            <div className="student-details-header">
                <h2>
                    {student.firstName} {student.lastName}
                </h2>
            </div>

            <section className="student-details-section">
                <h3>Student Information</h3>

                <dl>
                    <dt>Date of Birth</dt>
                    <dd>{student.dateOfBirth}</dd>

                    <dt>Age Group</dt>
                    <dd>{student.gradeLevel}</dd>

                    <dt>Status</dt>
                    <dd>{student.status}</dd>
                </dl>
            </section>
            <section className="student-details-section">
                <h3>Classroom Enrollment</h3>

                {classroom ? (
                    <div className="student-classroom-card">
                        <strong>{classroom.name}</strong>

                        <p>
                            Age Group: {classroom.gradeLevel}
                        </p>

                        <p>
                            Room: {classroom.roomNumber}
                        </p>

                        <p>
                            Capacity: {classroom.capacity}
                        </p>

                        <p>
                            Status: {classroom.status}
                        </p>
                    </div>
                ) : (
                    <p>Not enrolled in a classroom.</p>
                )}
            </section>
            <section className="student-details-section">
                <h3>Guardians</h3>

                {guardians.length === 0 ? (
                    <p>No guardians linked.</p>
                ) : (
                    <div className="student-guardians-list">
                        {guardians.map(({ relationship, guardian }) => (
                            <div
                                key={relationship.id}
                                className="student-guardian-card"
                            >
                                <strong>
                                    {guardian.firstName} {guardian.lastName}
                                </strong>

                                <p>{relationship.relationship}</p>

                                <p>{guardian.phoneNumber}</p>

                                <p>
                                    {guardian.email ?? "No email provided"}
                                </p>

                                {relationship.isPrimaryContact && (
                                    <p>Primary Contact</p>
                                )}

                                {relationship.isEmergencyContact && (
                                    <p>Emergency Contact</p>
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </section>
            <section
                id="allergies"
                className="student-details-section"
            >
                <h3>Allergies</h3>

                {allergies.length === 0 ? (
                    <p>No known allergies.</p>
                ) : (
                    <div className="student-allergies-list">
                        {allergies.map((allergy) => (
                            <div
                                key={allergy.id}
                                className="student-allergy-card"
                            >
                                <strong>{allergy.allergen}</strong>

                                <p>
                                    Severity: {allergy.severity}
                                </p>

                                <p>
                                    {allergy.notes ?? "No notes provided."}
                                </p>
                            </div>
                        ))}
                    </div>
                )}
            </section>
        </div>
    );
}