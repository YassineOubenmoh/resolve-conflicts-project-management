import React from "react";

interface ComponentCardProps {
  children: React.ReactNode;
  className?: string; // Additional custom classes for styling
  style?: React.CSSProperties; // Additional custom styles
  onClick?: () => void; 

}

const ComponentCard: React.FC<ComponentCardProps> = ({
  children,
  onClick,
  className = "",
  style,
}) => {
  return (
    <div
      onClick={onClick}
      className={`rounded-md border bg-white  ${className}`}
      style={style}
    >
      {/* Card Body */}
      <div className="p-4 sm:p-6">
        <div className="space-y-6">{children}</div>
      </div>
    </div>
  );
};

export default ComponentCard;
