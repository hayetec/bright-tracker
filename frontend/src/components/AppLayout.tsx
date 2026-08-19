import { NavLink, Outlet } from "react-router-dom";
import { useAuth } from "../auth/AuthProvider";

export default function AppLayout() {
    const { username, roles, logout } = useAuth();

    return (
        <div>
            <header>
                <h1>Bright Tracker</h1>

                <nav>
                    <NavLink to="/">Dashboard</NavLink>
                    {" | "}
                    <NavLink to="/meals">Meals</NavLink>
                    {" | "}
                    <NavLink to="/students">Students</NavLink>
                    {" | "}
                    <NavLink to="/classrooms">Classrooms</NavLink>
                    {" | "}
                    <NavLink to="/guardians">Guardians</NavLink>
                    {" | "}
                    <NavLink to="/staff">Staff</NavLink>
                </nav>

                <p>
                    Logged in as: {username} | Role: {roles.join(", ") || "No role"}
                </p>

                <button onClick={logout}>Logout</button>
            </header>

            <main>
                <Outlet />
            </main>
        </div>
    );
}