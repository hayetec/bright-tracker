import {
    useEffect,
    useState,
    type FormEvent,
} from "react";
import {
    createGuardian,
    deleteGuardian,
    getGuardians,
    updateGuardian,
} from "../api/guardiansApi";
import type { Guardian } from "../types/guardians";
import { useAuth } from "../auth/AuthProvider";


export default function GuardiansPage() {
    const [guardians, setGuardians] = useState<Guardian[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [searchTerm, setSearchTerm] = useState("");
    const [showAddForm, setShowAddForm] = useState(false);
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [email, setEmail] = useState("");
    const [editingGuardianId, setEditingGuardianId] =
        useState<number | null>(null);
    const { isAdmin } = useAuth();

    useEffect(() => {
        async function loadGuardians() {
            try {
                const data = await getGuardians();
                setGuardians(data);
            } catch (error) {
                setError(
                    error instanceof Error
                        ? error.message
                        : "Unable to load guardians",
                );
            } finally {
                setLoading(false);
            }
        }

        loadGuardians();
    }, []);

    const filteredGuardians = guardians.filter((guardian) => {
        const searchableText =
            `${guardian.firstName} ${guardian.lastName} ${guardian.email} ${guardian.phoneNumber}`
                .toLowerCase();

        return searchableText.includes(
            searchTerm.trim().toLowerCase(),
        );
    });

    if (loading) {
        return <p>Loading guardians...</p>;
    }

    async function handleSubmitGuardian(
        event: FormEvent<HTMLFormElement>,
    ) {
        event.preventDefault();

        try {
            if (editingGuardianId !== null) {
                const updatedGuardian = await updateGuardian(
                    editingGuardianId,
                    {
                        firstName,
                        lastName,
                        phoneNumber,
                        email: email.trim() || null,
                    },
                );

                setGuardians((currentGuardians) =>
                    currentGuardians.map((guardian) =>
                        guardian.id === editingGuardianId
                            ? updatedGuardian
                            : guardian,
                    ),
                );
            } else {
                const newGuardian = await createGuardian({
                    firstName,
                    lastName,
                    phoneNumber,
                    email: email.trim() || null,
                });

                setGuardians((currentGuardians) => [
                    ...currentGuardians,
                    newGuardian,
                ]);
            }

            setFirstName("");
            setLastName("");
            setPhoneNumber("");
            setEmail("");
            setEditingGuardianId(null);
            setShowAddForm(false);
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : "Unable to save guardian",
            );
        }
    }

    async function handleDeleteGuardian(guardian: Guardian) {
        const confirmed = window.confirm(
            `Delete ${guardian.firstName} ${guardian.lastName}?`,
        );

        if (!confirmed) {
            return;
        }

        try {
            await deleteGuardian(guardian.id);

            setGuardians((currentGuardians) =>
                currentGuardians.filter(
                    (currentGuardian) =>
                        currentGuardian.id !== guardian.id,
                ),
            );
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : "Unable to delete guardian",
            );
        }
    }

    if (error) {
        return <p>{error}</p>;
    }

    return (
        <div className="guardians-page">
            <div className="guardians-header">
                <div>
                    <h2>Guardians</h2>
                    <p>View and manage student guardians.</p>
                </div>

                <strong>
                    {guardians.length}{" "}
                    {guardians.length === 1 ? "guardian" : "guardians"}
                </strong>
            </div>

            <div className="guardians-filters">
                <input
                    type="search"
                    placeholder="Search guardians..."
                    value={searchTerm}
                    onChange={(event) =>
                        setSearchTerm(event.target.value)
                    }
                />
            </div>

            {isAdmin && (
                <div className="guardians-actions">
                    <button
                        type="button"
                        onClick={() => setShowAddForm((current) => !current)}
                    >
                        {showAddForm ? "Cancel" : "Add Guardian"}
                    </button>
                </div>
            )}

            {isAdmin && showAddForm && (
                <form
                    className="guardian-form"
                    onSubmit={handleSubmitGuardian}
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
                        type="tel"
                        placeholder="Phone number"
                        value={phoneNumber}
                        onChange={(event) => setPhoneNumber(event.target.value)}
                        required
                    />

                    <input
                        type="email"
                        placeholder="Email (optional)"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                    />

                    <button type="submit">
                        {editingGuardianId !== null
                            ? "Update Guardian"
                            : "Save Guardian"}
                    </button>
                </form>
            )}

            <div className="guardians-table-wrapper">
                <table className="guardians-table">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Phone</th>
                        <th>Email</th>
                        {isAdmin && <th>Actions</th>}
                    </tr>
                    </thead>

                    <tbody>
                    {filteredGuardians.map((guardian) => (
                        <tr key={guardian.id}>
                            <td>
                                <strong>
                                    {guardian.firstName}{" "}
                                    {guardian.lastName}
                                </strong>
                            </td>

                            <td>{guardian.phoneNumber}</td>
                            <td>{guardian.email}</td>
                            {isAdmin && (
                                <td>
                                    <button
                                        type="button"
                                        onClick={() => {
                                            setEditingGuardianId(guardian.id);
                                            setFirstName(guardian.firstName);
                                            setLastName(guardian.lastName);
                                            setPhoneNumber(guardian.phoneNumber);
                                            setEmail(guardian.email ?? "");
                                            setShowAddForm(true);
                                        }}
                                    >
                                        Edit
                                    </button>
                                    <button
                                        type="button"
                                        onClick={() => handleDeleteGuardian(guardian)}
                                    >
                                        Delete
                                    </button>
                                </td>
                            )}
                        </tr>
                    ))}

                    {filteredGuardians.length === 0 && (
                        <tr>
                            <td colSpan={isAdmin ? 4 : 3}>
                                No guardians found.
                            </td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}