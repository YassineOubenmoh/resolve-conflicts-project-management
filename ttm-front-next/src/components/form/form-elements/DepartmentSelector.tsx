"use client";
import React, { useState, useEffect, useMemo } from "react";
import Label from "../Label";
import ComponentCard from "@/components/common/ComponentCard";
import { useFormContext } from "react-hook-form";

interface DepartmentSelectorProps {
  onChange?: (departments: string[]) => void;
}

const DepartmentSelector: React.FC<DepartmentSelectorProps> = ({ onChange }) => {
  const [selectedDepartments, setSelectedDepartments] = useState<string[]>([]);
  const formContext = useFormContext();

  const departmentOptions = useMemo(() => [
    { value: "digital&data", label: "Digital & Data" },
    { value: "finance", label: "Finance" },
    { value: "marketing", label: "Marketing" },
    { value: "sales", label: "Sales" },
    { value: "legal", label: "Legal" },
    { value: "hr", label: "Human Resources" },
  ], []);

  const handleCheckboxChange = (value: string) => {
    const newSelectedDepartments = selectedDepartments.includes(value)
      ? selectedDepartments.filter((item) => item !== value)
      : [...selectedDepartments, value];
    
    setSelectedDepartments(newSelectedDepartments);
    
    if (formContext) {
      formContext.setValue("departments", newSelectedDepartments);
    }
    
    if (onChange) {
      onChange(newSelectedDepartments);
    }
  };
  
  useEffect(() => {
    if (formContext) {
      const currentDepartments = formContext.getValues("departments");
      if (currentDepartments && currentDepartments.length > 0) {
        setSelectedDepartments(currentDepartments);
      }
    }
  }, [formContext]);

  return (
    <div style={{ position: "relative", paddingTop: "1.5rem" }}> 
      <Label>Départements concernés</Label>
      <ComponentCard>
        <div className="mb-4">
          <p className="text-sm font-medium text-gray-700 dark:text-gray-300">  
            Sélectionnez les départements concernés par ce projet:  
          </p>
        </div>      
       
        <ComponentCard>        
          <div className="space-y-3">
            {departmentOptions.map((option) => (
              <div key={option.value} className="flex items-center gap-3">
                <input
                  type="checkbox"
                  id={option.value}
                  checked={selectedDepartments.includes(option.value)}
                  onChange={() => handleCheckboxChange(option.value)}
                  className="w-4 h-4 accent-[#ab3c73]"
                />
                <label
                  htmlFor={option.value}
                  className="flex items-center gap-2 text-sm font-medium text-gray-700 dark:text-gray-300"
                >
                  {option.label}
                </label>
              </div>
            ))}
          </div>
        </ComponentCard>
      </ComponentCard>
    </div>
  );
};

export default DepartmentSelector;
