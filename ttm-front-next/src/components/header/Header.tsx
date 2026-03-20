
"use client";

import Link from "next/link";

import { jwtDecode } from "jwt-decode";
import Image from "next/image";
import React, { useEffect, useRef, useState } from "react";
import './HeaderStyle.css';
import NotificationDropdown from "./NotificationDropdown";
import UserDropdown from "./UserDropdown";


type DecodedToken = {
  realm_access?: {
    roles: string[];
  };
};

const Header: React.FC = () => {
  const [activeLink, setActiveLink] = useState("home");


  const inputRef = useRef<HTMLInputElement>(null);




  const handleAccueilRedirect = (e: React.MouseEvent<HTMLAnchorElement>) => {
    e.preventDefault(); // Stop default navigation

    try {
      const accessToken = localStorage.getItem("accessToken");
      if (!accessToken) return;

      const decoded = jwtDecode<DecodedToken>(accessToken);
      const roles = decoded?.realm_access?.roles || [];

      const redirectionMap: { [key: string]: string } = {
        OWNER: "/project-list",
        SPOC: "/spoc/projects-affected",
        ADMIN: "/admin/users"
      };

      const destination = roles.find(role => redirectionMap[role]);

      if (destination) {
        window.location.href = redirectionMap[destination];
      } else if (
        !roles.some(role =>
          ["OWNER", "SPOC", "INTERLOCUTEUR_SIGNIALE_IMPACT", "INTERLOCUTEUR_RETOUR_IMPACT"].includes(role)
        )
      ) {
        alert("You are not allowed to access the platform yet. Please try again later!");
      }
    } catch (err) {
      console.error("Error during redirection:", err);
    }
  };


  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if ((event.metaKey || event.ctrlKey) && event.key === "k") {
        event.preventDefault();
        inputRef.current?.focus();
      }
    };

    document.addEventListener("keydown", handleKeyDown);

    return () => {
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, []);

  return (
    <>
      <header className="sticky top-0 flex w-full bg-white border-gray-200 z-99999 dark:border-gray-800 dark:bg-gray-900 lg:border-b headerStyle">
        <div className="flex items-center justify-between w-full lg:px-6">

          {/* Centered Navigation Links */}
          <nav className="flex-1 flex justify-center items-center gap-6">
            <Link

              href="/not-found"
              style={{ position: "relative", left: "20rem", paddingRight: "1.4rem" }}
              className={`nav-link ${activeLink === "home" ? "active" : ""}`}
              onClick={(e) => {
                setActiveLink("home");
                handleAccueilRedirect(e);

              }}          >
              Home
            </Link>

            <Link
              style={{ position: "relative", left: "20rem", paddingRight: "1.4rem" }}
              href="/current-projects-list"
              className={`nav-link ${activeLink === "/current-projects-list" ? "active" : ""}`}
              onClick={() => setActiveLink("/current-projects-list")}
            >
              Current Projects
            </Link>

            <Link
              style={{ position: "relative", left: "20rem" }}

              href="#contacts"
              className={`nav-link contacts ${activeLink === "#contacts" ? "active" : ""}`}
              onClick={() => setActiveLink("#contacts")}
            >
              Contact Us
            </Link>
          </nav>

          <Image
            src="/images/icons/ttm-icons/ttm-logo-v1.png"
            alt="auth-illustration"
            width={800}
            height={800}
            className="w-20 h-20 relative right-7/12 "

          />


          {/* Right Section */}
          <div className="flex items-center gap-4">
            <div className="relative pr-2 flex items-center gap-2 2xsm:gap-3">

              {/* Chatbot Button (round with image filling it) */}
              <button
                onClick={() => window.location.href = "/askinwiai/"}
                className="w-10 h-10 rounded-full overflow-hidden bg-gray-100 hover:bg-gray-200 dark:bg-white/[0.05] dark:hover:bg-white/[0.1] transition"
                title="Ask INWI AI"
              >
                <Image
                  src="/assets/chatbot.png"
                  alt="Chatbot"
                  width={40}
                  height={40}
                  className="object-cover w-full h-full"
                  priority
                />
              </button>

              {/* Notification Menu */}
              <NotificationDropdown />
            </div>

            {/* User Dropdown */}
            <UserDropdown />
          </div>





        </div>

      </header>

    </>

  );
};

export default Header;
