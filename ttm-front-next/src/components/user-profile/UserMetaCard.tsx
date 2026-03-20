"use client";
import { jwtDecode } from "jwt-decode";
import { useEffect, useState } from "react";

// Define the expected JWT structure
interface JwtPayload {
  name?: string;
  email?: string;
  given_name?: string;
  department?: string;
  realm_access?: {
    roles: string[];
  };
}

const TARGET_ROLES = ["OWNER", "SPOC", "ADMIN", "INTERLOCUTEUR_SIGNALE_IMPACT", "INTERLOCUTEUR_RETOUR_IMPACT"];

const getInitials = (name?: string) => {
  if (!name) return "U";

  const nameParts = name.split(" ");
  if (nameParts.length === 1) return nameParts[0][0].toUpperCase();

  // Get first letter of first name and first letter of last name
  return (nameParts[0][0] + nameParts[nameParts.length - 1][0]).toUpperCase();
};

export default function UserMetaCard() {
  const [user, setUser] = useState<JwtPayload>({});
  const [userRole, setUserRole] = useState<string | null>(null);

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      try {
        const decoded = jwtDecode<JwtPayload>(token);
        setUser(decoded);

        const roles = decoded.realm_access?.roles || [];
        const matchedRole = roles.find((role) => TARGET_ROLES.includes(role));
        if (matchedRole) {
          setUserRole(matchedRole);
        }
      } catch (err) {
        console.error("Failed to decode JWT:", err);
      }
    }
  }, []);

  return (
    <>
      <div className="p-5 border border-gray-200 rounded-2xl dark:border-gray-800 lg:p-6">
        <div className="flex flex-col gap-5 xl:flex-row xl:items-center xl:justify-between">
          <div className="flex flex-col items-center w-full gap-6 xl:flex-row">
            <div className="w-15 h-15 overflow-hidden border border-gray-200 rounded-full dark:border-gray-800 flex items-center justify-center bg-[#919191] text-white font-bold text-2xl">
              {getInitials(user.name)}
            </div>
            <div className="order-3 xl:order-2">
              <h4 className="mb-2 text-lg font-semibold text-center text-gray-800 dark:text-white/90 xl:text-left">
                {user.name || "User Name"}
              </h4>

              <div className="flex flex-col items-center gap-1 text-center xl:flex-row xl:gap-3 xl:text-left">
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  {userRole || "unknown role"}
                </p>
                <div className="hidden h-3.5 w-px bg-gray-300 dark:bg-gray-700 xl:block"></div>

                <p className="text-sm text-gray-500 dark:text-gray-400">
                  {user.department || "User's Department"}
                </p>
                <div className="hidden h-3.5 w-px bg-gray-300 dark:bg-gray-700 xl:block"></div>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  Casablanca, Morocco
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}