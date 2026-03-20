"use client";
import React, { useState } from "react";
import Image from "next/image"; // Import the Next.js Image component
import ComponentCard from "@/components/common/ComponentCard";
import Label from "../Label";

export default function Gates() {
  const [selectedGate, setSelectedGate] = useState<string | null>(null); // State for selected card

  const gateOptions = [
    { label: "T-2", icon: "/images/icons/gates-icons/T-2.png" },
    { label: "T-1", icon: "/images/icons/gates-icons/T-1.png" },
    { label: "T0", icon: "/images/icons/gates-icons/T0.png" },
    { label: "T6", icon: "/images/icons/gates-icons/T0.png" },
    { label: "T3", icon: "/images/icons/gates-icons/T0.png" },
    { label: "T4", icon: "/images/icons/gates-icons/T0.png" },
    { label: "T5", icon: "/images/icons/gates-icons/T0.png" },
  ];

  const handleCardClick = (gate: string) => {
    setSelectedGate(gate); // Update state with the selected gate
    console.log("Clicked gate:", gate); // Log the clicked card
  };

  return (
    <div style={{ position: "relative", paddingTop: "1rem" }}>
      <Label>Gate de démarrage</Label>
      <div className="grid grid-cols-3 gap-4 mt-4"> {/* Grid layout */}
        {gateOptions.map((option, index) => (
          <ComponentCard
            className="grid grid-cols-3 gap-4 mt-4"
            key={index}
            style={{
              height: "3rem", // Card height
              width: "10rem", // Card width
              display: "flex",
              flexDirection: "row",
              justifyContent: "left", // Center icon and text horizontally
              alignItems: "normal", // Center icon and text vertically
              padding: "0.5rem",
              cursor: "pointer", // Add pointer cursor for better UX
              border: selectedGate === option.label ? "2px solid #007bff" : "1px solid #e5e7eb", // Add blue border if selected
              borderRadius: "0.5rem", // Rounded corners
              backgroundColor: selectedGate === option.label ? "#f0f8ff" : "white", // Highlight selected card
            }}
            onClick={() => handleCardClick(option.label)} // Add onClick event
          >
            {/* Icon */}
            <Image
              src={option.icon}
              alt={option.label}
              width={60} // Icon width
              height={60} // Icon height
              style={{ position: "relative", bottom: "1.9rem", right: "1rem" }} // Add spacing between icon and text
            />
            {/* Label */}
            <p
              style={{
                fontSize: "14px", // Adjust font size
                position: "relative",
                bottom: "6.4rem",
                left: "4rem"

              }}
            >
              {option.label}
            </p>
          </ComponentCard>
        ))}
      </div>

    </div>
  );
}