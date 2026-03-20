"use client";

import ComponentCard from "@/components/common/ComponentCard";
import DatePicker from "@/components/form/date-picker";
import BesoinMOA from "@/components/form/form-elements/BesoinMOA";
import DepartmentSelector from "@/components/form/form-elements/DepartmentSelector";&q(())
import DropzoneComponent from "@/components/form/form-elements/DropZone";
import Tracking from "@/components/form/form-elements/Tracking";
import FileInput from "@/components/form/input/FileInput";
import Input from "@/components/form/input/InputField";
import TextArea from "@/components/form/input/TextArea";
import Label from "@/components/form/Label";
import Select from "@/components/form/Select";
import Switch from "@/components/form/switch/Switch";
import { ChevronDownIcon } from "@/icons";
import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect, useState } from "react";
import { Controller, FormProvider, useForm } from "react-hook-form";
import * as z from "zod";
import { createProject } from "../../axios/ProjectApis";
import Alert from '@mui/material/Alert';


const formSchema = z.object({
  title: z.string().min(1, { message: "Project title is required" }),
  description: z.string().min(1, { message: "Description is required" }),
  marketType: z.string().min(1, { message: "Market type could not be empty" }),
  projectType: z.string().min(1, { message: "Project type could not be empty" }),
  ttmComitteeSubCategory: z.string().min(1, { message: "This field is required" }),
  subcategoryCommercialCodir: z.string().min(1, { message: "This field is required" }),
  isConfidential: z.boolean(),
  dateStartTtm: z.date({ message: "Date is required" }).nullable(),
  comments: z.array(z.string().min(1, { message: "Comment is required" })),
  moas: z.array(z.string()).min(1, { message: "At least one MOA is required" }),
  trackingId: z.number(),
  departments: z.array(z.string().min(1, { message: "Department is required" })),
});


type FormValues = z.infer<typeof formSchema>;

const CreateProject = () => {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [files, setFiles] = useState<FormData | null>(null);
  const [alertMessage, setAlertMessage] = useState<string | null>(null);
  const [alertSeverity, setAlertSeverity] = useState<'success' | 'error' | null>(null);


  // Initialize form with FormProvider once
  const methods = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      title: "",
      description: "",
      marketType: "",
      projectType: "",
      ttmComitteeSubCategory: "",
      subcategoryCommercialCodir: "",
      isConfidential: true,
      dateStartTtm: null,
      comments: [],
      moas: [],
      departments: [],
      trackingId: 1,
    },
    mode: "onChange"
  });


  const {
    register,
    control,
    handleSubmit,
    formState: { errors }
  } = methods;


  const SCC = [
    { value: "1", label: "Offre" },
    { value: "2", label: "Service" },
    { value: "3", label: "Promotion" },
    { value: "4", label: "Animation" },
    { value: "5", label: "Amélioration" },
  ];

  const marketTypeOptions = [
    { value: "prépayé", label: "Prépayé" },
    { value: "option2", label: "Option2" },
    { value: "option3", label: "Option3" },
  ];

  const projectTypeOptions = [
    {
      value: "codir",
      label: "Codir commercial/ Comité TTM / Comité Experience Client (à checker)",
    },
    { value: "option2", label: "Option2" },
    { value: "option3", label: "Option3" },
  ];

  // Updated DropzoneComponent handling in the form submission function
  const handleFormSubmit = async (data: FormValues) => {
    if (isSubmitting) return;
    setIsSubmitting(true);
    console.log("Form submitted with data:", data);

    try {
      // Get file inputs
      const expressionOfNeed = document.getElementById('expressionOfNeed') as HTMLInputElement;
      const briefCommunication = document.getElementById('briefCommunication') as HTMLInputElement;
      const briefCDG = document.getElementById('briefCDG') as HTMLInputElement;
      const regulatoryBrief = document.getElementById('regulatoryBrief') as HTMLInputElement;


      // Create new FormData for file uploads
      const formData = new FormData();


      // Add files from the DropzoneComponent
      if (files) {
        for (const [key, value] of files.entries()) {
          formData.append(key, value); // Append each file to the FormData
        }
      }

      // Add files if they exist
      if (expressionOfNeed?.files?.[0]) {
        formData.append('expressionOfNeed', expressionOfNeed.files[0]);
      }

      if (briefCommunication?.files?.[0]) {
        formData.append('briefCommunication', briefCommunication.files[0]);
      }

      if (briefCDG?.files?.[0]) {
        formData.append('briefCDG', briefCDG.files[0]);
      }

      if (regulatoryBrief?.files?.[0]) {
        formData.append('regulatoryBrief', regulatoryBrief.files[0]);
      }

      // Handle the attached documents from DropzoneComponent
      if (files !== null) {

        // First, inspect what's actually in the files FormData
        for (const [key, value] of files.entries()) {
          if (value instanceof File) {

            // Append each file with the exact field name expected by the backend
            // This is the key fix - using the correct field name without an index
            formData.append('attachedDocuments', value);
          } else {
            console.log(`Found non-file entry with key '${key}': ${value}`);
          }
        }
      }

      // Ensure comments is an array
      const comments = Array.isArray(data.comments) ? data.comments : [];

      // Create the project data object that matches the API expectations
      const projectData = {
        title: data.title,
        description: data.description,
        marketType: data.marketType,
        projectType: data.projectType,
        ttmComitteeSubCategory: data.ttmComitteeSubCategory,
        subcategoryCommercialCodir: data.subcategoryCommercialCodir,
        isConfidential: data.isConfidential,
        dateStartTtm: data.dateStartTtm ? data.dateStartTtm.toISOString() : null,
        comments: comments,
        moas: data.moas,
        trackingId: data.trackingId || 1, // Default to 1 if not selected
        departments: data.departments
      };

      // Log the data being sent to the API for debugging
      console.log("Sending project data:", projectData);

      // Add the project data to the FormData as a JSON string
      formData.append('projectDto', JSON.stringify(projectData));



      // Call the API with the FormData
      const response = await createProject(projectData, formData);
      console.log("API response:", response);

      if (response && response.status === 201) {
        // Show success message and redirect to success page instead of project list
        setAlertMessage("The project has been created successfully!");
        setAlertSeverity("success");

        setTimeout(() => {
          // Store project ID if needed for the success page
          const projectId = response.data?.id;
          if (projectId) {
            localStorage.setItem('createdProjectId', projectId);
          }

          // Redirect to the success page instead of project list
          window.location.href = "/affect-gates-to-departments";
        }, 2000); // Reduced delay to 2 seconds
      } else {
        throw new Error("Unexpected response from server");
      }
    } catch (error) {
      console.error("Error creating project:", error);
      let errorMessage = "Could not creating the project, try again!";

      if (error instanceof Error) {
        errorMessage = error.message;
      } else if (typeof error === 'object' && error !== null && 'response' in error) {
        const axiosError = error as { response?: { data?: { message?: string } } };
        if (axiosError.response?.data?.message) {
          errorMessage = axiosError.response.data.message;
        }
      }

      setAlertMessage(errorMessage);
      setAlertSeverity("error");
    }
    finally {
      setIsSubmitting(false);
    }
  };


  // Auto-dismiss the Alert
  useEffect(() => {
    if (alertMessage) {
      const timer = setTimeout(() => {
        setAlertMessage(null);
        setAlertSeverity(null);
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [alertMessage]);






  return (
    <FormProvider {...methods}>

      {alertMessage && alertSeverity && (
        <div className="mb-4">
          <Alert severity={alertSeverity}>{alertMessage}</Alert>
        </div>
      )}

      <form onSubmit={handleSubmit(handleFormSubmit)} className="relative py-8 px-8 sm:px-6 lg:px-8 max-w-7xl mx-auto">
        <div
          style={{ position: "relative", paddingRight: "5rem" }}
          className="flex flex-wrap items-center justify-between gap-3 mb-6">
          <h2
            style={{ position: "relative", paddingLeft: "5rem" }}
            className="text-xl font-semibold text-gray-800 dark:text-white/90"
            x-text="pageName"
          >
            New Project
          </h2>

          {/* Submit Button */}
          <button
            type="submit"
            style={{
              backgroundColor: "#ab3c73",
              color: "white",
              width: "5.5rem",
              padding: "0.5rem 1rem",
              borderRadius: "0.375rem",
              border: "none",
              cursor: isSubmitting ? "not-allowed" : "pointer",
              opacity: isSubmitting ? 0.7 : 1,
            }}
            disabled={isSubmitting}

          >
            {isSubmitting ? "Valider..." : "Valider"}
          </button>
        </div>



        <div className="mt-6 grid gap-6 md:grid-cols-2">
          {/* Left Side */}
          <div className="space-y-6">
            <ComponentCard>


              {/* Title field */}
              <Label>Title</Label>
              <input
                {...register("title")}
                placeholder="Entrer le titre de votre projet"
                className={`w-full px-4 py-3 text-sm text-gray-900 placeholder-gray-400 
    bg-white border border-gray-200 rounded-xl outline-none 
    transition-all duration-200 
    focus:border-blue-500 focus:ring-2 focus:ring-blue-100 
    hover:border-gray-300
    ${errors.title ? "border-red-500 focus:border-red-500 focus:ring-red-100" : ""}`}
              />
              {errors.title && (
                <p className="text-red-600 text-sm relative bottom-4">{errors.title.message}</p>
              )}


              <Label className={`text-gray-900  pt-4 ${errors.description ? "border-red-500 focus:border-red-500 focus:ring-red-100" : ""}
                        `}>Description</Label>
              <TextArea
                className={`text-gray-900  pt-4 ${errors.description ? "border-red-500 focus:border-red-500 focus:ring-red-100" : ""}`}
                {...register("description")}
                rows={6}
                placeholder="Décrivez votre projet brièvement"

              />
              {errors.description && (
                <p className="text-red-600 text-sm relative bottom-4">{errors.description.message}</p>
              )}

              <Label className="pt-4">Sous catégorie Comité TTM</Label>
              <Controller
                control={control}
                name="ttmComitteeSubCategory"
                render={({ field }) => (
                  <Select
                    {...field}
                    options={SCC}
                    placeholder="Sélectionner"
                    className="dark:bg-dark-900"
                    icon={<ChevronDownIcon />}
                  />

                )}
              />
              {errors.ttmComitteeSubCategory && (
                <p className="text-red-600 text-sm relative bottom-4">{errors.ttmComitteeSubCategory.message}</p>
              )}

              <Label className="pt-4">Type de marché</Label>
              <Controller
                control={control}
                name="marketType"
                render={({ field }) => (
                  <Select
                    {...field}
                    options={marketTypeOptions}
                    placeholder="Sélectionner"
                    className="dark:bg-dark-900"
                    icon={<ChevronDownIcon />}
                  />
                )}
              />
              {errors.marketType && (
                <p className="text-red-600 text-sm relative bottom-4">{errors.marketType.message}</p>
              )}

              <Label className="pt-4">Type de Projet</Label>
              <Controller
                control={control}
                name="projectType"
                render={({ field }) => (
                  <Select
                    {...field}
                    options={projectTypeOptions}
                    placeholder="Sélectionner"
                    className="dark:bg-dark-900"
                    icon={<ChevronDownIcon />}
                  />
                )}
              />
              {errors.projectType && (
                <p className="text-red-600 text-sm relative bottom-4">{errors.projectType.message}</p>
              )}

              <Label className="pt-4">Sous catégorie Codir Commercial</Label>
              <Controller
                control={control}
                name="subcategoryCommercialCodir"
                render={({ field }) => (
                  <Select
                    {...field}
                    options={SCC}
                    placeholder="Sélectionner"
                    className="dark:bg-dark-900"
                    icon={<ChevronDownIcon />}
                  />
                )}
              />
              {errors.subcategoryCommercialCodir && (
                <p className="text-red-600 text-sm relative bottom-4">{errors.subcategoryCommercialCodir.message}</p>
              )}

              <Label className="pt-4">Confidentiel</Label>
              <Controller
                control={control}
                name="isConfidential"
                render={({ field }) => (
                  <div className="relative">
                    <Input disabled value="Le projet est confidentiel" className="pr-16" />
                    <div className="absolute inset-y-0 right-4 flex items-center">
                      <Switch label="" {...field} customColor="#ab3c73" />
                    </div>
                  </div>
                )}
              />

              <BesoinMOA />
              <Tracking />
              <DepartmentSelector />
            </ComponentCard>
          </div>

          {/* Right Side */}
          <div className="space-y-6">
            <ComponentCard>
              <Controller
                control={control}
                name="dateStartTtm"
                render={({ field }) => (
                  <DatePicker
                    id="date-picker"
                    label="Passage en instance (ex: T-2, T-1...)"
                    placeholder="Date de passage en instance"
                    defaultDate={field.value as Date | undefined}
                    onChange={(date) => {

                      // Ensure we're passing a Date object, not an array
                      if (date instanceof Date) {
                        field.onChange(date);
                      } else if (Array.isArray(date) && date.length > 0 && date[0] instanceof Date) {

                        // If it's an array of dates (some date pickers return this), take the first one
                        field.onChange(date[0]);
                      } else if (typeof date === 'string') {

                        // If it's a string, try to parse it
                        field.onChange(new Date(date));
                      }
                    }}
                  />
                )}
              />
              {errors.dateStartTtm && (
                <p className="text-red-600 text-sm relative bottom-4">{errors.dateStartTtm.message}</p>
              )}
              <p className="text-sm text-gray-500 relative bottom-4 ml-2 mt-1">
                Cette date n&apos;est pas définitive, elle doit être validée par le PMO
              </p>

              <Label className="pt-4">Expression de Besoin / FIB</Label>
              <FileInput id="expressionOfNeed" />


              <Label className="pt-4">Brief Communication</Label>
              <FileInput id="briefCommunication" />

              <Label className="pt-4">Brief CDG</Label>
              <FileInput id="briefCDG" />

              <Label className="pt-4">Brief Réglementaire</Label>
              <FileInput id="regulatoryBrief" />

              <Label className="pt-4">Joindre des documents</Label>
              <DropzoneComponent id="attachedDocuments" label="" setFiles={setFiles} />

              <Label className="pt-4">Commentaires</Label>

              <Controller
                control={control}
                name="comments"
                render={({ field }) => (
                  <TextArea
                    className="text-gray-900 "
                    {...field}
                    onChange={(e) => {
                      const comments = e.target.value
                        .split('\n')
                        .filter(comment => comment.trim() !== '');
                      field.onChange(comments);
                    }}
                    rows={6}
                    placeholder="Insérer vos commentaires ici (un commentaire par ligne)"
                  />
                )}
              />
              {errors.comments && (
                <p className="text-red-600 text-sm relative bottom-4">{errors.comments.message}</p>
              )}
            </ComponentCard>
          </div>
        </div>

      </form>
    </FormProvider>
  );
};

export default CreateProject;
