"use client";
import { jwtDecode } from "jwt-decode";
import { useEffect, useState } from "react";


interface JwtPayload {
  name?: string;
  email?: string;
  given_name?: string;
  family_name?: string;
  department?: string;
}

export default function UserInfoCard() {
  const [user, setUser] = useState<JwtPayload>({});



  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    console.log("Token:", localStorage.getItem("accessToken"));

    if (token) {
      try {
        const decoded = jwtDecode<JwtPayload>(token);

        setUser(decoded);
      } catch (err) {
        console.error("Failed to decode JWT:", err);
      }
    }
  }, []);



  return (
    <div className="p-5 border border-gray-200 rounded-2xl dark:border-gray-800 lg:p-6">
      <div className="flex flex-col gap-6 lg:flex-row lg:items-start lg:justify-between">
        <div>
          <h4 className="text-lg font-semibold text-gray-800 dark:text-white/90 lg:mb-6">
            Personal Information
          </h4>

          <div className="grid grid-cols-1 gap-4 lg:grid-cols-2 lg:gap-7 2xl:gap-x-32">
            <div>
              <p className="mb-2 text-xs leading-normal text-gray-500 dark:text-gray-400">
                First Name
              </p>
              <p className="text-sm font-medium text-gray-800 dark:text-white/90">
                {user.given_name || "User First Name"}
              </p>
            </div>

            <div>
              <p className="mb-2 text-xs leading-normal text-gray-500 dark:text-gray-400">
                Last Name
              </p>
              <p className="text-sm font-medium text-gray-800 dark:text-white/90">
                {user.family_name || "User Last Name"}
              </p>
            </div>

            <div>
              <p className="mb-2 text-xs leading-normal text-gray-500 dark:text-gray-400">
                Email address
              </p>
              <p className="text-sm font-medium text-gray-800 dark:text-white/90">
                {user.email || "User Email"}
              </p>
            </div>

            <div>
              <p className="mb-2 text-xs leading-normal text-gray-500 dark:text-gray-400">
                Departemnt
              </p>
              <p className="text-sm font-medium text-gray-800 dark:text-white/90">
                {user.department || "User Department"}
              </p>
            </div>
          </div>
        </div>


      </div>


    </div>
  );
}
