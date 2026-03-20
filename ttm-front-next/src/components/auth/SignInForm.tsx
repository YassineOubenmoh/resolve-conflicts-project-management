"use client";

import { forgotPassword, login } from "@/axios/UsersApis";
import Checkbox from "@/components/form/input/Checkbox";
import Input from "@/components/form/input/InputField";
import Label from "@/components/form/Label";
import Button from "@/components/ui/button/Button";
import { EyeCloseIcon, EyeIcon } from "@/icons";
import CloseIcon from '@mui/icons-material/Close';
import Alert from '@mui/material/Alert';
import CircularProgress from '@mui/material/CircularProgress';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import IconButton from '@mui/material/IconButton';
import Stack from '@mui/material/Stack';
import { jwtDecode } from "jwt-decode";
import Link from "next/link";
import React, { useState } from "react";

type DecodedToken = {
  realm_access?: {
    roles: string[];
  };
};

export default function SignInForm() {
  const [showPassword, setShowPassword] = useState(false);
  const [isChecked, setIsChecked] = useState(false);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  // States for forgot password popup
  const [forgotPasswordOpen, setForgotPasswordOpen] = useState(false);
  const [email, setEmail] = useState("");
  const [resetEmailSent, setResetEmailSent] = useState(false);
  const [resetEmailLoading, setResetEmailLoading] = useState(false);
  const [resetEmailError, setResetEmailError] = useState("");

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true); // Start loading

    setError("");

    try {
      const response = await login(username, password);

      console.log("Response data:", response.data);

      const { accessToken, refreshToken } = response.data;

      // Store tokens in localStorage
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);

      console.log("Access Token:", localStorage.getItem("accessToken"));
      console.log("Refresh Token:", localStorage.getItem("refreshToken"));

      const decoded = jwtDecode<DecodedToken>(accessToken as string);
      const roles = decoded?.realm_access?.roles || [];

      const redirectionMap: { [key: string]: string } = {
        OWNER: "/project-list",
        //SPOC: "/spoc/interlocutors-affected"
        SPOC: "/spoc/dashboard",
        INTERLOCUTEUR_SIGNALE_IMPACT: "/interlocutor-impact/dashboard",
        INTERLOCUTEUR_RETOUR_IMPACT: "/interlocutor-feedback/dashboard",
        ADMIN: "/admin/users"
      };

      const destination = roles.find(role => redirectionMap[role]);

      if (destination) {
        window.location.href = redirectionMap[destination];
      } else if (
        !roles.some(role =>
          ["ADMIN", "OWNER", "SPOC", "INTERLOCUTEUR_SIGNIALE_IMPACT", "INTERLOCUTEUR_RETOUR_IMPACT"].includes(role)
        )
      ) {
        setError("You are not allowed to access the platform yet... please try again later!");
      }

    } catch (err: unknown) {
      setError("username or password are incorrect");
      console.error(err);

    } finally {
      setLoading(false); // Stop loading
    }
  };

  // Handler for opening the forgot password popup
  const handleForgotPasswordClick = (e: React.MouseEvent) => {
    e.preventDefault();
    setForgotPasswordOpen(true);
  };

  // Handler for closing the forgot password popup
  const handleCloseForgotPassword = () => {
    setForgotPasswordOpen(false);
    // Reset states when closing the popup
    setEmail("");
    setResetEmailSent(false);
    setResetEmailError("");
  };

  // Handler for sending password reset email
  const handleSendResetEmail = async () => {
    if (!email) {
      setResetEmailError("Please enter your email address");
      return;
    }

    setResetEmailLoading(true);
    setResetEmailError("");

    try {
      // Based on the controller method using @RequestParam
      // Using path variables as you mentioned in your comment
      await forgotPassword(email);

      setResetEmailSent(true);
    } catch (err) {
      setResetEmailError("Failed to send reset email. Please try again.");
      console.error(err);
    } finally {
      setResetEmailLoading(false);
    }
  };

  return (
    <div className="relative pb-12 flex flex-col flex-1 lg:w-1/2 w-full">
      <div className="flex flex-col justify-center flex-1 w-full max-w-md mx-auto">
        <div>
          {error && (
            <div className="pb-11 relative right-40 w-[171.5%]">
              <Stack>
                <Alert severity="error">{error}</Alert>
              </Stack>
            </div>
          )}

          <div className="mb-3 sm:mb-5">
            <h1 className="mb-2 font-semibold text-gray-800 text-title-sm dark:text-white/90 sm:text-title-md">
              Sign In
            </h1>
            <p className="text-sm text-gray-500 dark:text-gray-400">
              Enter your username and password to sign in!
            </p>
          </div>

          {/* Divider */}
          <div className="relative py-3 sm:py-5">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-gray-200 dark:border-gray-800" />
            </div>
            <div className="relative flex justify-center text-[18px]">
              <span className="p-2 text-gray-400 bg-white dark:bg-gray-900 sm:px-5 sm:py-2">
                welcome
              </span>
            </div>
          </div>

          {/* Login Form */}
          <form onSubmit={handleLogin}>
            <div className="space-y-6">
              <div>
                <Label>
                  Username <span className="text-error-500">*</span>
                </Label>
                <Input
                  placeholder="Enter your username"
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                />
              </div>

              <div>
                <Label>
                  Password <span className="text-error-500">*</span>
                </Label>
                <div className="relative">
                  <Input
                    type={showPassword ? "text" : "password"}
                    placeholder="Enter your password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                  />
                  <span
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute z-30 -translate-y-1/2 cursor-pointer right-4 top-1/2"
                  >
                    {showPassword ? (
                      <EyeIcon className="fill-gray-500 dark:fill-gray-400" />
                    ) : (
                      <EyeCloseIcon className="fill-gray-500 dark:fill-gray-400" />
                    )}
                  </span>
                </div>
              </div>

              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <Checkbox checked={isChecked} onChange={setIsChecked} />
                  <span className="block font-normal text-gray-700 text-theme-sm dark:text-gray-400">
                    Keep me logged in
                  </span>
                </div>
                <a
                  href="#"
                  onClick={handleForgotPasswordClick}
                  className="text-sm text-brand-500 hover:text-brand-600 dark:text-brand-400"
                >
                  Forgot password?
                </a>
              </div>

              {/* Login Button */}
              <div>
                <Button
                  style={{ backgroundColor: "#ab3c73" }}
                  className="w-full"
                  size="sm"
                  type="submit"
                  disabled={loading}
                >
                  {loading ? <CircularProgress size={20} color="inherit" /> : "Sign In"}
                </Button>
              </div>
            </div>

            {/* Footer */}
            <div className="mt-5">
              <p className="text-sm font-normal text-center text-gray-700 dark:text-gray-400 sm:text-start">
                Don&apos;t have an account?{" "}
                <Link
                  href="/signup"
                  className="pl-2 text-brand-500 hover:text-brand-600 dark:text-brand-400"
                >
                  Sign Up
                </Link>
              </p>
            </div>
          </form>
        </div>
      </div>

      {/* Forgot Password Dialog */}
      <Dialog
        open={forgotPasswordOpen}
        onClose={handleCloseForgotPassword}
        fullWidth
        maxWidth="xs"
      >
        <DialogTitle>
          Forgot Password
          <IconButton
            aria-label="close"
            onClick={handleCloseForgotPassword}
            sx={{
              position: 'absolute',
              right: 8,
              top: 8,
            }}
          >
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <DialogContent>
          {resetEmailSent ? (
            <div className="py-4">
              <Alert severity="success">
                Password reset email has been sent! Please check your inbox.
              </Alert>
            </div>
          ) : (
            <div className="py-4">
              <p className="mb-4 text-sm text-gray-600">
                Enter your email address below and we&apos;ll send you a link to reset your password.
              </p>
              <div className="mb-4">
                <Label>
                  Email <span className="text-error-500">*</span>
                </Label>
                <Input
                  type="email"
                  placeholder="Enter your email address"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
              {resetEmailError && (
                <Alert severity="error" className="mb-4">
                  {resetEmailError}
                </Alert>
              )}
            </div>
          )}
        </DialogContent>
        <DialogActions sx={{ padding: '0 24px 20px 24px' }}>
          {!resetEmailSent && (
            <Button
              style={{ backgroundColor: "#ab3c73" }}
              className="w-full"
              size="sm"
              onClick={handleSendResetEmail}
              disabled={resetEmailLoading}
            >
              {resetEmailLoading ? (
                <CircularProgress size={20} color="inherit" />
              ) : (
                "Send Reset Link"
              )}
            </Button>
          )}
          {resetEmailSent && (
            <Button
              style={{ backgroundColor: "#ab3c73" }}
              className="w-full"
              size="sm"
              onClick={handleCloseForgotPassword}
            >
              Close
            </Button>
          )}
        </DialogActions>
      </Dialog>
    </div>
  );
}