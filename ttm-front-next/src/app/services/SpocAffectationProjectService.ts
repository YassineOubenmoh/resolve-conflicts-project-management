// src/services/SpocService.ts
import { jwtDecode } from "jwt-decode";

export interface UserDto {
  firstName: string;
  lastName: string;
  username: string;
  department: string;
  email: string;
  roles: string[];
}

type JwtPayload = {
    preferred_username?: string;
    username?: string;
    sub?: string;
  };
  
  export const getUsernameFromToken = (): string | null => {
    const token = localStorage.getItem("accessToken");
    if (!token) return null;
  
    try {
      const decoded = jwtDecode<JwtPayload>(token);
      return decoded.preferred_username || decoded.username || decoded.sub || null;
    } catch (e) {
      console.error("Invalid token", e);
      return null;
    }
  };
  