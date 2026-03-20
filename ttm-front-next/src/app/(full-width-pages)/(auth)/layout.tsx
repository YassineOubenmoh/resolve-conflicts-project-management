import ThemeTogglerTwo from "@/components/common/ThemeTogglerTwo";

import { ThemeProvider } from "@/context/ThemeContext";
import Image from "next/image";
import React from "react";

export default function AuthLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="relative p-6 bg-white z-1 dark:bg-gray-900 sm:p-0">
      <ThemeProvider>
        <div className="relative flex lg:flex-row w-full h-screen justify-center flex-col  dark:bg-gray-900 sm:p-0">
          {children}
          <div
          style={{ backgroundColor: "#281932", width: "40%" }}
          className=" w-full h-full dark:bg-white/5 lg:grid items-center hidden">

            <div>
              <Image

                style={{ 
                  maxWidth: "80%",
                  paddingLeft: "3rem",
                  paddingRight: "3rem",
                  position: "relative", 
                  left: "12%",
                  }}

                src="/images/icons/ttm-icons/ttm-logo-v2.png"
                alt="auth-illustration"
                width={800}
                height={800}
              
              />
            </div>


            




            
          </div>
         
        </div>
      </ThemeProvider>
    </div>
  );
}
