import type { Metadata } from "next";
import React from "react";

import CreateProject from "./CreateProject";

export const metadata: Metadata = {
  title:
    "Create Project | TTM",
  description: "This is Next.js Home for TailAdmin Dashboard Template",
};

export default function Ecommerce() {
  return (
    <div>
      <CreateProject />
    </div>
  );
}
