import { jwtDecode } from "jwt-decode";
import Cookies from "js-cookie";

interface DecodedToken {
    email: string;
    role: string;
    exp: number;
}

export const getToken = (): string | null => {
    return Cookies.get("token") || null;
};


export const decodeToken = (): DecodedToken | null => {
    const token = getToken();
    if (!token) return null;

    try {
        return jwtDecode<DecodedToken>(token);
    } catch (error) {
        console.error("Invalid token:", error);
        return null;
    }
};


export const isAuthenticated = (): boolean => {
    const decoded = decodeToken();
    return !!decoded && decoded.exp * 1000 > Date.now();
};


export const getUserRole = (): string | null => {
    const decoded = decodeToken();
    return decoded ? decoded.role : null;
};


export const logout = () => {
    Cookies.remove("token");
    window.location.href = "/sign-in";
};
