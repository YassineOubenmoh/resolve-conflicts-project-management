"use client";
import React, { useState, useEffect, ChangeEvent, FormEvent } from "react";
import { useModal } from "../../hooks/useModal";
import { Modal } from "../ui/modal";
import Button from "../ui/button/Button";
import Input from "../form/input/InputField";
import Label from "../form/Label";
import { resetPassword } from '@/axios/UsersApis';
import { AlertCircle, CheckCircle2, Eye, EyeOff, Lock } from "lucide-react";

// Define API error type
interface ApiError {
  response?: {
    data?: {
      message?: string;
    }
  }
}

interface FormDataType {
  oldPassword: string;
  newPassword: string;
}

interface VisibilityState {
  oldPassword: boolean;
  newPassword: boolean;
}

interface AlertState {
  type: 'success' | 'error' | null;
  message: string;
  isVisible: boolean;
}

export default function UserAddressCard() {
  const { isOpen, openModal, closeModal } = useModal();

  const [formData, setFormData] = useState<FormDataType>({
    oldPassword: "",
    newPassword: ""
  });
  const [visibility, setVisibility] = useState<VisibilityState>({
    oldPassword: false,
    newPassword: false
  });
  const [alert, setAlert] = useState<AlertState>({
    type: null,
    message: "",
    isVisible: false
  });
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [passwordStrength, setPasswordStrength] = useState<number>(0);

  useEffect(() => {
    // Password strength criteria
    const calculateStrength = (password: string): number => {
      if (!password) return 0;

      let score = 0;
      if (password.length >= 8) score += 1;
      if (/[A-Z]/.test(password)) score += 1;
      if (/[a-z]/.test(password)) score += 1;
      if (/[0-9]/.test(password)) score += 1;
      if (/[^A-Za-z0-9]/.test(password)) score += 1;

      return score;
    };

    setPasswordStrength(calculateStrength(formData.newPassword));
  }, [formData.newPassword]);

  const handleInputChange = (e: ChangeEvent<HTMLInputElement>): void => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const toggleVisibility = (field: keyof VisibilityState): void => {
    setVisibility(prev => ({
      ...prev,
      [field]: !prev[field]
    }));
  };

  const showAlert = (type: 'success' | 'error', message: string, duration: number = 5000): void => {
    setAlert({
      type,
      message,
      isVisible: true
    });

    // Auto-hide the alert after duration
    if (duration) {
      setTimeout(() => {
        setAlert(prev => ({ ...prev, isVisible: false }));
      }, duration);
    }
  };

  const handleSave = async (): Promise<void> => {
    // Basic validation
    if (!formData.oldPassword) {
      showAlert("error", "Please enter your current password");
      return;
    }

    if (!formData.newPassword) {
      showAlert("error", "Please enter a new password");
      return;
    }

    if (passwordStrength < 3) {
      showAlert("error", "Please use a stronger password");
      return;
    }

    setIsSubmitting(true);

    try {
      await resetPassword({
        oldPassword: formData.oldPassword,
        newPassword: formData.newPassword
      });

      showAlert("success", "Password updated successfully!");

      // Reset form
      setFormData({ oldPassword: "", newPassword: "" });

      // Close modal after a brief delay so user can see success message
      setTimeout(() => {
        closeModal();
        setIsSubmitting(false);
      }, 1500);
    } catch (error: unknown) {
      // Type guard to check if the error matches our ApiError interface
      const isApiError = (err: unknown): err is ApiError => {
        return typeof err === 'object' && err !== null && 'response' in err;
      };

      let errorMessage = "Failed to update password. Please try again.";

      if (isApiError(error) && error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error instanceof Error) {
        errorMessage = error.message;
      }

      showAlert("error", errorMessage);
      setIsSubmitting(false);
    }
  };

  const resetForm = (): void => {
    setFormData({ oldPassword: "", newPassword: "" });
    setAlert({ type: null, message: "", isVisible: false });
  };

  // When modal closes, reset the form
  const handleCloseModal = (): void => {
    resetForm();
    closeModal();
  };

  const getStrengthColor = (): string => {
    if (passwordStrength <= 1) return "bg-red-500";
    if (passwordStrength <= 3) return "bg-yellow-500";
    return "bg-green-500";
  };

  const getStrengthText = (): string => {
    if (!formData.newPassword) return "";
    if (passwordStrength <= 1) return "Weak";
    if (passwordStrength <= 3) return "Medium";
    return "Strong";
  };

  return (
    <>
      <div className="p-5 border border-gray-200 rounded-2xl dark:border-gray-800 lg:p-6">
        <div className="flex flex-col gap-6 lg:flex-row lg:items-start lg:justify-between">
          <div>
            <h4 className="text-lg font-semibold text-gray-800 dark:text-white/90 lg:mb-6">
              Change Password
            </h4>
            <p className="text-sm font-medium text-gray-600 dark:text-white/90">
              To ensure the security of your account, we will ask you for your old password to set up a new one.
            </p>
          </div>
          <button
            onClick={openModal}
            className="flex w-full items-center justify-center gap-2 rounded-full border border-gray-300 bg-white px-6 py-2 text-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:hover:bg-white/[0.03] dark:hover:text-gray-200 lg:inline-flex lg:w-auto"
          >
            <Lock size={16} />
            Edit Password
          </button>
        </div>
      </div>

      <Modal isOpen={isOpen} onClose={handleCloseModal} className="max-w-[700px] m-4">
        <div className="relative w-full p-4 overflow-y-auto bg-white rounded-3xl dark:bg-gray-900 lg:p-11">
          <div className="px-2 pr-14">
            <h4 className="mb-2 text-2xl font-semibold text-gray-800 dark:text-white/90">Change Password</h4>
            <p className="mb-6 text-sm text-gray-500 dark:text-gray-400">Update your password to keep your account secure.</p>
          </div>

          {alert.isVisible && (
            <div className={`mb-6 px-4 py-3 rounded-lg flex items-center ${alert.type === 'success' ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200' :
              'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'
              }`}>
              {alert.type === 'success' ?
                <CheckCircle2 size={20} className="mr-2" /> :
                <AlertCircle size={20} className="mr-2" />
              }
              <span className="text-sm font-medium">{alert.message}</span>
            </div>
          )}

          <form className="flex flex-col" onSubmit={(e: FormEvent<HTMLFormElement>) => e.preventDefault()}>
            <div className="px-2">
              <div className="grid grid-cols-1 gap-x-6 gap-y-5 lg:grid-cols-2">
                <div className="py-4 relative">
                  <Label>Current Password</Label>
                  <Input
                    type={visibility.oldPassword ? "text" : "password"}
                    name="oldPassword"
                    value={formData.oldPassword}
                    onChange={handleInputChange}
                    placeholder="Enter current password"
                  />
                  <button
                    type="button"
                    className="absolute right-3 top-12 cursor-pointer text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300"
                    onClick={() => toggleVisibility("oldPassword")}
                  >
                    {visibility.oldPassword ?
                      <EyeOff size={18} className="relative top-1.5" /> :
                      <Eye size={18} className="relative top-1.5" />
                    }
                  </button>
                </div>

                <div className="py-4 relative">
                  <Label>New Password</Label>
                  <Input
                    type={visibility.newPassword ? "text" : "password"}
                    name="newPassword"
                    value={formData.newPassword}
                    onChange={handleInputChange}
                    placeholder="Enter new password"
                  />
                  <button
                    type="button"
                    className="absolute right-3 top-12 cursor-pointer text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300"
                    onClick={() => toggleVisibility("newPassword")}
                  >
                    {visibility.newPassword ?
                      <EyeOff size={18} className="relative top-1.5" /> :
                      <Eye size={18} className="relative top-1.5" />
                    }
                  </button>

                  {/* Password strength indicator */}
                  {formData.newPassword && (
                    <div className="mt-2">
                      <div className="h-1 w-full bg-gray-200 rounded-full overflow-hidden">
                        <div
                          className={`h-1 ${getStrengthColor()}`}
                          style={{ width: `${(passwordStrength / 5) * 100}%` }}
                        ></div>
                      </div>
                      <p className="text-xs mt-1 text-gray-500 dark:text-gray-400">
                        Password strength: <span className={passwordStrength <= 1 ? "text-red-500" : passwordStrength <= 3 ? "text-yellow-500" : "text-green-500"}>
                          {getStrengthText()}
                        </span>
                      </p>
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div className="flex items-center gap-3 px-2 mt-6 lg:justify-end">
              <Button
                size="sm"
                variant="outline"
                onClick={handleCloseModal}
                disabled={isSubmitting}
              >
                Cancel
              </Button>
              <Button
                size="sm"
                style={{ backgroundColor: "#ab3c73" }}
                onClick={handleSave}
                disabled={isSubmitting}
              >
                {isSubmitting ? "Saving..." : "Save Changes"}
              </Button>
            </div>
          </form>
        </div>
      </Modal>
    </>
  );
}