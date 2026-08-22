import {
    useEffect,
    useState,
    type FormEvent,
} from "react";
import {
    createStaff,
    deleteStaff,
    getStaff,
    updateStaff,
} from "../api/staffApi";
import type { Staff } from "../types/staff";
import { useAuth } from "../auth/AuthProvider";

export default function StaffPage() {
    const { isAdmin } = useAuth();
    const [staff, setStaff] = useState<Staff[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [searchTerm, setSearchTerm] = useState("");
    const [showAddForm, setShowAddForm] = useState(false);
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [role, setRole] = useState("TEACHER");
    const [editingStaffId, setEditingStaffId] =
        useState<number | null>(null);

    useEffect(() => {
        async function loadStaff() {
            try {
                const data = await getStaff();
                setStaff(data);
            } catch (error) {
                setError(
                    error instanceof Error
                        ? error.message
                        : "Unable to load staff",
                );
            } finally {
                setLoading(false);
            }
        }

        loadStaff();
    }, []);

    const filteredStaff = staff.filter((member) => {
        const searchableText =
            `${member.firstName} ${member.lastName} ${member.email} ${member.phoneNumber} ${member.role} ${member.status}`
                .toLowerCase();

        return searchableText.includes(
            searchTerm.trim().toLowerCase(),
        );
    });

    if (loading) {
        return <p>Loading staff...</p>;
    }

    async function handleSubmitStaff(
        event: FormEvent<HTMLFormElement>,
    ) {
        event.preventDefault();

        try {
            const request = {
                firstName,
                lastName,
                email,
                phoneNumber,
                role,
                status: "ACTIVE",
            };

            if (editingStaffId !== null) {
                const updatedStaff = await updateStaff(
                    editingStaffId,
                    request,
                );

                setStaff((currentStaff) =>
                    currentStaff.map((member) =>
                        member.id === editingStaffId
                            ? updatedStaff
                            : member,
                    ),
                );
            } else {
                const newStaff = await createStaff(request);

                setStaff((currentStaff) => [
                    ...currentStaff,
                    newStaff,
                ]);
            }

            setFirstName("");
            setLastName("");
            setEmail("");
            setPhoneNumber("");
            setRole("TEACHER");
            setEditingStaffId(null);
            setShowAddForm(false);
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : "Unable to save staff",
            );
        }
    }

    async function handleDeleteStaff(member: Staff) {
        const confirmed = window.confirm(
            `Delete ${member.firstName} ${member.lastName}?`,
        );

        if (!confirmed) {
            return;
        }

        try {
            await deleteStaff(member.id);

            setStaff((currentStaff) =>
                currentStaff.filter(
                    (currentMember) =>
                        currentMember.id !== member.id,
                ),
            );
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : "Unable to delete staff",
            );
        }
    }

    if (error) {
        return <p>{error}</p>;
    }

    return (
        <div className="staff-page">
            <div className="staff-header">
                <div>
                    <h2>Staff</h2>
                    <p>View and manage school staff.</p>
                </div>

                <strong>
                    {staff.length}{" "}
                    {staff.length === 1 ? "staff member" : "staff members"}
                </strong>
            </div>

            {isAdmin && (
                <div className="staff-actions">
                    <button
                        type="button"
                        onClick={() => setShowAddForm((current) => !current)}
                    >
                        {showAddForm ? "Cancel" : "Add Staff"}
                    </button>
                </div>
            )}

            {isAdmin && showAddForm && (
                <form
                    className="staff-form"
                    onSubmit={handleSubmitStaff}
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
                        type="email"
                        placeholder="Email"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                        required
                    />

                    <input
                        type="tel"
                        placeholder="Phone number"
                        value={phoneNumber}
                        onChange={(event) => setPhoneNumber(event.target.value)}
                        required
                    />

                    <select
                        value={role}
                        onChange={(event) => setRole(event.target.value)}
                    >
                        <option value="TEACHER">Teacher</option>
                        <option value="TEACHER_AIDE">Teacher Aide</option>
                        <option value="ADMIN">Admin</option>
                    </select>

                    <button type="submit">
                        {editingStaffId !== null
                            ? "Update Staff"
                            : "Save Staff"}
                    </button>
                </form>
            )}

            <div className="staff-filters">
                <input
                    type="search"
                    placeholder="Search staff..."
                    value={searchTerm}
                    onChange={(event) =>
                        setSearchTerm(event.target.value)
                    }
                />
            </div>

            <div className="staff-table-wrapper">
                <table className="staff-table">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Role</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Status</th>
                        {isAdmin && <th>Actions</th>}
                    </tr>
                    </thead>

                    <tbody>
                    {filteredStaff.map((member) => (
                        <tr key={member.id}>
                            <td>
                                <strong>
                                    {member.firstName} {member.lastName}
                                </strong>
                            </td>
                            <td>{member.role}</td>
                            <td>{member.email}</td>
                            <td>{member.phoneNumber}</td>
                            <td>{member.status}</td>
                            {isAdmin && (
                                <td>
                                    <button
                                        type="button"
                                        onClick={() => {
                                            setEditingStaffId(member.id);
                                            setFirstName(member.firstName);
                                            setLastName(member.lastName);
                                            setEmail(member.email);
                                            setPhoneNumber(member.phoneNumber);
                                            setRole(member.role);
                                            setShowAddForm(true);
                                        }}
                                    >
                                        Edit
                                    </button>

                                    <button
                                        type="button"
                                        onClick={() => handleDeleteStaff(member)}
                                    >
                                        Delete
                                    </button>
                                </td>
                            )}
                        </tr>
                    ))}

                    {filteredStaff.length === 0 && (
                        <tr>
                            <td colSpan={isAdmin ? 6 : 5}>
                                No staff found.
                            </td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}