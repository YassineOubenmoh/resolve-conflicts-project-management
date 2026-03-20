"use client";
import Input from "@/components/form/input/InputField";
import Label from "@/components/form/Label";
import { EyeCloseIcon, EyeIcon } from "@/icons";
import CheckIcon from '@mui/icons-material/Check';
import Alert from '@mui/material/Alert';
import CircularProgress from '@mui/material/CircularProgress';
import Stack from '@mui/material/Stack';
import axios from "axios";
import Link from "next/link";
import { useRouter } from 'next/navigation';
import { useState } from "react";
import Button from "../ui/button/Button";




const Signup: React.FC = () => {

  const [showPassword, setShowPassword] = useState(false);

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    username: "",
    department: "",
    email: "",
    password: "",
  });

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);


  const router = useRouter();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSignup = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true); // Start loading

    try {
      console.log("Payload sent:", JSON.stringify(formData));

      await axios.post("http://localhost:8087/api/internal/auth/signup", formData, {
        headers: {
          "Content-Type": "application/json",
        },
      });

      setSuccess("You have been signed up successfully.");
      setError("");

      router.push('/signin');

    } catch (err: unknown) {
      setError("Signup failed... please try again!");
      console.error(err);
      setSuccess("");
    } finally {
      setLoading(false); // Stop loading
    }
  };

  return (
    <div
      className="pb-12 pt-6 flex flex-col flex-1 lg:w-1/2 w-full overflow-y-auto no-scrollbar">


      {success && (
        <div className=" pb-8 relative bottom-12 ">
          <Alert icon={<CheckIcon fontSize="inherit" />} severity="success">
            You have been registered successfully
          </Alert>
        </div>
      )}

      {error && (
        <div className="  relative bottom-6 ">
          <Stack >

            <Alert severity="error">{error}</Alert>
          </Stack>
        </div>
      )}

      <div className="flex flex-col justify-center flex-1 w-full max-w-md mx-auto">
        <div>
          <div className="mb-2 sm:mb-2">
            <h1 className="mb-2 font-semibold text-gray-800 text-title-sm dark:text-white/90 sm:text-title-md">
              Sign Up
            </h1>

          </div>

          {/* Divider */}
          <div className="relative py-3 sm:py-5">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-gray-200 dark:border-gray-800" />
            </div>
            <div className="relative flex justify-center text-[18px]">
              <span className="p-2 text-gray-400 bg-white dark:bg-gray-900 sm:px-5 sm:py-2">
                Join us
              </span>
            </div>
          </div>


          <div>

            <form onSubmit={handleSignup}>
              <div className="space-y-5">
                <div className="grid grid-cols-1 gap-5 sm:grid-cols-2">
                  {/* <!-- First Name --> */}
                  <div className="sm:col-span-1">
                    <Label>
                      First Name<span className="text-error-500">*</span>
                    </Label>
                    <Input
                      type="text"
                      id="firstName"
                      name="firstName"
                      placeholder="Enter your first name"
                      onChange={handleChange}
                    />
                  </div>
                  {/* <!-- Last Name --> */}
                  <div className="sm:col-span-1">
                    <Label>
                      Last Name<span className="text-error-500">*</span>
                    </Label>
                    <Input
                      type="text"
                      id="lastName"
                      name="lastName"
                      placeholder="Enter your last name"
                      onChange={handleChange}
                    />
                  </div>
                </div>
                <div className="grid grid-cols-1 gap-5 sm:grid-cols-2">
                  {/* <!-- UserName --> */}
                  <div className="sm:col-span-1">
                    <Label>
                      Username<span className="text-error-500">*</span>
                    </Label>
                    <Input
                      type="text"
                      id="useraame"
                      name="username"
                      placeholder="Enter your username"
                      onChange={handleChange}
                    />
                  </div>
                  {/* <!-- Last Name --> */}
                  <div className="sm:col-span-1">
                    <Label>
                      Department<span className="text-error-500">*</span>
                    </Label>
                    <Input
                      type="text"
                      id="department"
                      name="department"
                      placeholder="Enter your department"
                      onChange={handleChange}
                    />
                  </div>
                </div>
                {/* <!-- Email --> */}
                <div>
                  <Label>
                    Email<span className="text-error-500">*</span>
                  </Label>
                  <Input
                    type="email"
                    id="email"
                    name="email"
                    placeholder="Enter your email"

                    onChange={handleChange} />
                </div>
                {/* <!-- Password --> */}
                <div>
                  <Label>
                    Password<span className="text-error-500">*</span>
                  </Label>
                  <div className="relative">
                    <Input
                      placeholder="Enter your password"
                      onChange={handleChange}
                      type={showPassword ? "text" : "password"}
                      id="password"
                      name="password"
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

                {/* <!-- Button --> */}
                <div>
                  <Button
                    style={{ backgroundColor: "#ab3c73" }}
                    className="flex items-center justify-center w-full px-4 py-3 text-sm font-medium text-white transition rounded-lg  shadow-theme-xs hover:bg-brand-600"
                    type="submit"
                    disabled={loading} // Optional: disable button while loading
                  >
                    {loading ? <CircularProgress size={20} color="inherit" /> : "Sign Up"}

                  </Button>
                </div>
              </div>
            </form>

            <div className="mt-5">
              <p className="text-sm font-normal text-center text-gray-700 dark:text-gray-400 sm:text-start">
                Already have an account?
                <Link
                  style={{ paddingLeft: "0.5rem" }}
                  href="/signin"
                  className="text-brand-500 hover:text-brand-600 dark:text-brand-400"
                >
                  Sign In
                </Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Signup;
