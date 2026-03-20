"use client";
import React, { useState, useEffect, useMemo } from "react";
import Image from "next/image";
import ComponentCard from "@/components/common/ComponentCard";
import Label from "../Label";
import { useFormContext } from "react-hook-form";

interface TrackingProps {
  onChange?: (trackingId: number) => void;
}

export default function Tracking({ onChange }: TrackingProps) {
  const [selectedTrack, setSelectedTrack] = useState<string | null>(null);
  const formContext = useFormContext();

  const trackingOptions = useMemo(() => [
    { id: 1, label: "Full-track", icon: "/images/icons/tracking-icons/full-track.png" },
    { id: 2, label: "Fast-track", icon: "/images/icons/tracking-icons/fast-track.png" },
    { id: 3, label: "Super-f-track", icon: "/images/icons/tracking-icons/super-f-track.png" },
    { id: 4, label: "Hyper-f-track", icon: "/images/icons/tracking-icons/hyper-f-track.png" },
  ], []);

  const handleCardClick = (track: string, trackingId: number) => {
    setSelectedTrack(track);

    if (formContext) {
      formContext.setValue("trackingId", trackingId);
    }

    if (onChange) {
      onChange(trackingId);
    }
  };

  useEffect(() => {
    if (formContext) {
      const currentTrackingId = formContext.getValues("trackingId");
      if (currentTrackingId) {
        const trackOption = trackingOptions.find(option => option.id === currentTrackingId);
        if (trackOption) {
          setSelectedTrack(trackOption.label);
        }
      }
    }
  }, [formContext, trackingOptions]);

  return (
    <div style={{ position: "relative", paddingTop: "1rem" }}>
      <Label>Tracking</Label>
      <ComponentCard className="flex items-center justify-center">
        <div className="flex items-center space-x-2">
          {trackingOptions.map((option, index) => (
            <ComponentCard
              key={index}
              style={{
                height: "7rem", // Card height
                width: "7rem", // Card width
                display: "flex",
                flexDirection: "column",
                justifyContent: "center", // Center icon and text vertically
                alignItems: "center", // Center icon and text horizontally
                padding: "1rem",
                cursor: "pointer", // Add pointer cursor for better UX
                border: selectedTrack === option.label ? "2px solid #829fe8" : "1px solid #e5e7eb", // Add blue border if selected
                borderRadius: "0.5rem", // Rounded corners
              }}
              onClick={() => handleCardClick(option.label, option.id)}
            >
              {/* Icon */}
              <Image
                className={`${option.label === "Full-track" ? "w-18 h-18" : "w-23 h-23"
                  }`} // Dynamically assign className
                style={{
                  position: "relative",
                  bottom: "0.3rem",
                  left:
                    option.label === "Super-f-track" || option.label === "Hyper-f-track"
                      ? "0.3rem"
                      : "",

                  maxWidth: "300rem",
                }}
                src={option.icon}
                alt={option.label}
                width={80} // Icon width
                height={60} // Icon height
              />
              {/* Label */}
              <p
                style={{
                  position: "relative",
                  bottom: "2rem",
                  fontSize: "12px",
                  textAlign: "center",
                  whiteSpace: "nowrap",

                  top:
                    option.label === "Full-track" ? "-22px" : "",

                }}
              >
                {option.label}
              </p>
            </ComponentCard>
          ))}
        </div>
      </ComponentCard>
    </div>
  );
}
