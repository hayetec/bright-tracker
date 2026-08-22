import {
    useEffect,
    useState,
    type FormEvent,
} from "react";
import {
    createClassroom,
    deleteClassroom,
    getClassrooms,
    updateClassroom,
} from "../api/classroomsApi";
import type { Classroom } from "../types/classrooms";
import { useAuth } from "../auth/AuthProvider";

export default function ClassroomsPage() {
    const { isAdmin } = useAuth();
    const [classrooms, setClassrooms] = useState<Classroom[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [searchTerm, setSearchTerm] = useState("");
    const [showAddForm, setShowAddForm] = useState(false);
    const [name, setName] = useState("");
    const [gradeLevel, setGradeLevel] = useState("");
    const [roomNumber, setRoomNumber] = useState("");
    const [capacity, setCapacity] = useState("");
    const [editingClassroomId, setEditingClassroomId] =
        useState<number | null>(null);

    useEffect(() => {
        async function loadClassrooms() {
            try {
                const data = await getClassrooms();
                setClassrooms(data);
            } catch (error) {
                setError(
                    error instanceof Error
                        ? error.message
                        : "Unable to load classrooms",
                );
            } finally {
                setLoading(false);
            }
        }

        loadClassrooms();
    }, []);

    const filteredClassrooms = classrooms.filter((classroom) => {
        const searchableText =
            `${classroom.name} ${classroom.gradeLevel} ${classroom.roomNumber} ${classroom.status}`
                .toLowerCase();

        return searchableText.includes(
            searchTerm.trim().toLowerCase(),
        );
    });

    async function handleSubmitClassroom(
        event: FormEvent<HTMLFormElement>,
    ) {
        event.preventDefault();

        try {
            const request = {
                name,
                gradeLevel,
                roomNumber,
                capacity: Number(capacity),
                status: "ACTIVE",
            };

            if (editingClassroomId !== null) {
                const updatedClassroom = await updateClassroom(
                    editingClassroomId,
                    request,
                );

                setClassrooms((currentClassrooms) =>
                    currentClassrooms.map((classroom) =>
                        classroom.id === editingClassroomId
                            ? updatedClassroom
                            : classroom,
                    ),
                );
            } else {
                const newClassroom = await createClassroom(request);

                setClassrooms((currentClassrooms) => [
                    ...currentClassrooms,
                    newClassroom,
                ]);
            }

            setName("");
            setGradeLevel("");
            setRoomNumber("");
            setCapacity("");
            setEditingClassroomId(null);
            setShowAddForm(false);
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : "Unable to save classroom",
            );
        }
    }

    async function handleDeleteClassroom(classroom: Classroom) {
        const confirmed = window.confirm(
            `Delete ${classroom.name}? Make sure all student enrollments and staff assignments have been removed first.`,
        );

        if (!confirmed) {
            return;
        }

        try {
            await deleteClassroom(classroom.id);

            setClassrooms((currentClassrooms) =>
                currentClassrooms.filter(
                    (currentClassroom) =>
                        currentClassroom.id !== classroom.id,
                ),
            );
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : "Unable to delete classroom",
            );
        }
    }

    if (loading) {
        return <p>Loading classrooms...</p>;
    }

    if (error) {
        return <p>{error}</p>;
    }

    return (
        <div className="classrooms-page">
            <div className="classrooms-header">
                <div>
                    <h2>Classrooms</h2>
                    <p>View and manage school classrooms.</p>
                </div>

                <strong>
                    {classrooms.length}{" "}
                    {classrooms.length === 1 ? "classroom" : "classrooms"}
                </strong>
            </div>

            {isAdmin && (
                <div className="classrooms-actions">
                    <button
                        type="button"
                        onClick={() => setShowAddForm((current) => !current)}
                    >
                        {showAddForm ? "Cancel" : "Add Classroom"}
                    </button>
                </div>
            )}

            {isAdmin && showAddForm && (
                <form
                    className="classroom-form"
                    onSubmit={handleSubmitClassroom}
                >
                    <input
                        type="text"
                        placeholder="Classroom name"
                        value={name}
                        onChange={(event) => setName(event.target.value)}
                        required
                    />

                    <input
                        type="text"
                        placeholder="Age group"
                        value={gradeLevel}
                        onChange={(event) => setGradeLevel(event.target.value)}
                        required
                    />

                    <input
                        type="text"
                        placeholder="Room number"
                        value={roomNumber}
                        onChange={(event) => setRoomNumber(event.target.value)}
                        required
                    />

                    <input
                        type="number"
                        placeholder="Capacity"
                        value={capacity}
                        onChange={(event) => setCapacity(event.target.value)}
                        min="1"
                        required
                    />

                    <button type="submit">
                        {editingClassroomId !== null
                            ? "Update Classroom"
                            : "Save Classroom"}
                    </button>
                </form>
            )}

            <div className="classrooms-filters">
                <input
                    type="search"
                    placeholder="Search classrooms..."
                    value={searchTerm}
                    onChange={(event) =>
                        setSearchTerm(event.target.value)
                    }
                />
            </div>

            <div className="classrooms-table-wrapper">
                <table className="classrooms-table">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Age Group</th>
                        <th>Room</th>
                        <th>Capacity</th>
                        <th>Status</th>
                        {isAdmin && <th>Actions</th>}
                    </tr>
                    </thead>

                    <tbody>
                    {filteredClassrooms.map((classroom) => (
                        <tr key={classroom.id}>
                            <td>
                                <strong>{classroom.name}</strong>
                            </td>
                            <td>{classroom.gradeLevel}</td>
                            <td>{classroom.roomNumber}</td>
                            <td>{classroom.capacity}</td>
                            <td>{classroom.status}</td>
                            {isAdmin && (
                                <td>
                                    <button
                                        type="button"
                                        onClick={() => {
                                            setEditingClassroomId(classroom.id);
                                            setName(classroom.name);
                                            setGradeLevel(classroom.gradeLevel);
                                            setRoomNumber(classroom.roomNumber);
                                            setCapacity(String(classroom.capacity));
                                            setShowAddForm(true);
                                        }}
                                    >
                                        Edit
                                    </button>

                                    <button
                                        type="button"
                                        onClick={() => handleDeleteClassroom(classroom)}
                                    >
                                        Delete
                                    </button>
                                </td>
                            )}
                        </tr>
                    ))}

                    {filteredClassrooms.length === 0 && (
                        <tr>
                            <td colSpan={isAdmin ? 6 : 5}>
                                No classrooms found.
                            </td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}