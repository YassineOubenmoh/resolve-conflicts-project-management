import React, { useState, useEffect } from "react";
import Label from "../Label";
import Switch from "../switch/Switch";
import ComponentCard from "@/components/common/ComponentCard";
import { useFormContext } from "react-hook-form";

interface BesoinMOAProps {
  onChange?: (moas: string[]) => void;
}

const BesoinMOA: React.FC<BesoinMOAProps> = ({ onChange }) => {
  const [isSwitchOn, setIsSwitchOn] = useState(true);
  const [selectedMOA, setSelectedMOA] = useState<string[]>([]);
  const formContext = useFormContext();

  const moaOptions = [
    { value: "B2C", label: "MOA B2C" },
    { value: "B2B", label: "MOA B2B" },
    { value: "Data", label: "MOA Data" },
    { value: "IM", label: "MOA inwi Money" },
    { value: "WIN", label: "MOA Win" },
  ];

  const handleSwitchChange = (checked: boolean) => {
    setIsSwitchOn(checked);
    if (!checked) {
      setSelectedMOA([]);
      if (formContext) {
        formContext.setValue("moas", []);
      }
      if (onChange) {
        onChange([]);
      }
    }
  };

  const handleCheckboxChange = (value: string) => {
    const newSelectedMOA = selectedMOA.includes(value)
      ? selectedMOA.filter((item) => item !== value)
      : [...selectedMOA, value];

    setSelectedMOA(newSelectedMOA);

    if (formContext) {
      formContext.setValue("moas", newSelectedMOA);
    }

    if (onChange) {
      onChange(newSelectedMOA);
    }
  };

  useEffect(() => {
    if (formContext) {
      const currentMoas = formContext.getValues("moas");
      if (currentMoas && currentMoas.length > 0) {
        setSelectedMOA(currentMoas);
      }
    }
  }, [formContext]);

  return (

    <div style={{ position: "relative", paddingTop: "1.5rem" }}>
      <Label>Besoin MOA
      </Label>
      <ComponentCard>
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <p className="text-sm font-medium text-gray-700 dark:text-gray-300">
            Le projet a un besoin MOA
          </p>
          <Switch
            label=""
            defaultChecked={isSwitchOn}
            onChange={handleSwitchChange}
            customColor="#ab3c73"
          />
        </div>

        <div className="mb-4">
          <p className="text-sm font-medium text-gray-700 dark:text-gray-300">
            Choisissez les MOA que vous souhaitez contacter :
          </p>
        </div>



        {/* MOA Options */}

        <ComponentCard>
          <div className="space-y-3">
            {moaOptions.map((option) => (
              <div key={option.value} className="flex items-center gap-3">
                <input
                  type="checkbox"
                  id={option.value}
                  checked={selectedMOA.includes(option.value)}
                  onChange={() => handleCheckboxChange(option.value)}
                  className="w-4 h-4 accent-[#ab3c73]" // Control width, height, and color
                />
                <label
                  htmlFor={option.value}
                  className="flex items-center gap-2 text-sm font-medium text-gray-700 dark:text-gray-300"
                >
                  <span
                    className="flex items-center justify-center px-2 py-1 text-white rounded-md"
                    style={{ width: "3rem", alignItems: "center", backgroundColor: "#f3d1e6", color: "#ab3c73", }}
                  >
                    {option.value}
                  </span>
                  {option.label}
                </label>
              </div>
            ))}
          </div> </ComponentCard>
      </ComponentCard>
    </div>
  );
};

export default BesoinMOA;