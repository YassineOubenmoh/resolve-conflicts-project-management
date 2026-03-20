import Link from "next/link";
import React from "react";
import Button from "../ui/button/Button";


interface BreadcrumbProps {
  pageTitle: string;
}

const PageBreadcrumb: React.FC<BreadcrumbProps> = ({ pageTitle }) => {
  return (
    <div
      style={{ position: "relative", paddingRight: "5rem" }}

      className="flex flex-wrap items-center justify-between gap-3 mb-6">
      <h2
        style={{ position: "relative", paddingLeft: "5rem" }}
        className="text-xl font-semibold text-gray-800 dark:text-white/90"
        x-text="pageName"
      >
        {pageTitle}
      </h2>

      {/* Button */}
      <Link href="/project-recap" passHref>
        <Button
          style={{
            backgroundColor: "#ab3c73",
            color: "white",
            width: "5.5rem",


          }}

        >
          Valider
        </Button>
      </Link>

    </div>
  );
};

export default PageBreadcrumb;
