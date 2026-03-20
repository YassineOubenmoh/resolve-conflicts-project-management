import type { Metadata } from "next";
import React from "react";

import ProjectRecap from "./ProjectRecap";

export const metadata: Metadata = {
  title:
    "Project Recap | TTM",
  description: "This is Next.js Home for TailAdmin Dashboard Template",
};

export default function ProjectRecapPage() {
  return (
    <div>
      <ProjectRecap />
    </div>
  );
}
