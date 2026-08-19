import {
    createContext,
    useContext,
    type ReactNode,
} from "react";
import keycloak from "./keycloak";

type UserRole = "ADMIN" | "STAFF";

type AuthContextType = {
    username?: string;
    roles: UserRole[];
    isAdmin: boolean;
    isStaff: boolean;
    logout: () => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
    const parsedToken = keycloak.tokenParsed;

    const username =
        typeof parsedToken?.preferred_username === "string"
            ? parsedToken.preferred_username
            : undefined;

    const clientRoles =
        parsedToken?.resource_access?.["bright-tracker-api"]?.roles ?? [];

    const roles = clientRoles.filter(
        (role): role is UserRole =>
            role === "ADMIN" || role === "STAFF",
    );

    const logout = () => {
        keycloak.logout({
            redirectUri: window.location.origin,
        });
    };

    return (
        <AuthContext.Provider
            value={{
                username,
                roles,
                isAdmin: roles.includes("ADMIN"),
                isStaff: roles.includes("STAFF"),
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error("useAuth must be used inside AuthProvider");
    }

    return context;
}