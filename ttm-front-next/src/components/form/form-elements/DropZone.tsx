// Modified DropzoneComponent.tsx
"use client";
import React, { useState } from "react";
import { useDropzone } from "react-dropzone";
import Image from "next/image";

interface DropzoneProps {
  label: string;
  id?: string;
  setFiles: (files: FormData) => void;
}

const DropzoneComponent: React.FC<DropzoneProps> = ({ label, id, setFiles }) => {
  const [fileList, setFileList] = useState<File[]>([]);
  const onDrop = (acceptedFiles: File[]) => {
    console.log("Files dropped:", acceptedFiles);
    // Update local state to show uploaded files
    setFileList(prev => [...prev, ...acceptedFiles]);
    // Create a new FormData object
    const formData = new FormData();
    // Append each file with a consistent key name
    acceptedFiles.forEach(file => {
      formData.append('attachedDocuments', file); // Use the same key for all files
    });
    console.log(`Added ${acceptedFiles.length} files to FormData`);
    // Update parent component state
    setFiles(formData);
  };

  const { getRootProps, getInputProps } = useDropzone({
    onDrop,
    accept: {
      "application/pdf": [],
      "application/vnd.ms-powerpoint": [],
      "application/vnd.openxmlformats-officedocument.presentationml.presentation": [],
      "application/msword": [],
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document": [],
      "application/vnd.ms-excel": [],
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": [],
    },
  });

  // Function to display file size in a readable format
  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  return (
    <div>
      {/* Label for the dropzone */}
      <label className="block mb-2 text-sm font-medium text-gray-700 dark:text-gray-300">
        {label}
      </label>

      <div
        {...getRootProps()}
        className={`transition border border-gray-300 border-dashed cursor-pointer dark:hover:border-brand-500 dark:border-gray-700 rounded-xl hover:border-brand-500 p-6 bg-gray-50 dark:bg-gray-900`}
      >
        {/* Hidden Input */}
        <input id={id} {...getInputProps()} />

        <div className="flex flex-col items-center text-center">
          {/* Icon */}
          <div className="mb-[22px] flex justify-center">
            <div className="flex h-[68px] w-[68px] items-center justify-center rounded-full bg-gray-200 text-gray-700 dark:bg-gray-800 dark:text-gray-400">
              <svg
                className="fill-current"
                width="29"
                height="28"
                viewBox="0 0 29 28"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  fillRule="evenodd"
                  clipRule="evenodd"
                  d="M14.5019 3.91699C14.2852 3.91699 14.0899 4.00891 13.953 4.15589L8.57363 9.53186C8.28065 9.82466 8.2805 10.2995 8.5733 10.5925C8.8661 10.8855 9.34097 10.8857 9.63396 10.5929L13.7519 6.47752V18.667C13.7519 19.0812 14.0877 19.417 14.5019 19.417C14.9161 19.417 15.2519 19.0812 15.2519 18.667V6.48234L19.3653 10.5929C19.6583 10.8857 20.1332 10.8855 20.426 10.5925C20.7188 10.2995 20.7186 9.82463 20.4256 9.53184L15.0838 4.19378C14.9463 4.02488 14.7367 3.91699 14.5019 3.91699ZM5.91626 18.667C5.91626 18.2528 5.58047 17.917 5.16626 17.917C4.75205 17.917 4.41626 18.2528 4.41626 18.667V21.8337C4.41626 23.0763 5.42362 24.0837 6.66626 24.0837H22.3339C23.5766 24.0837 24.5839 23.0763 24.5839 21.8337V18.667C24.5839 18.2528 24.2482 17.917 23.8339 17.917C23.4197 17.917 23.0839 18.2528 23.0839 18.667V21.8337C23.0839 22.2479 22.7482 22.5837 22.3339 22.5837H6.66626C6.25205 22.5837 5.91626 22.2479 5.91626 21.8337V18.667Z"
                />
              </svg>
            </div>
          </div>

          {/* Main Text */}
          <p className="text-gray-700 dark:text-gray-300 font-medium">
            Déposez ou glissez vos documents ici
          </p>

          {/* Supported Formats */}
          <p className="mt-2 text-sm text-gray-500 dark:text-gray-400">
            Formats supportés :{" "}
          </p>

          <div>
            <span className="inline-flex items-center gap-2">
              {/* PDF Icon */}
              <span className="flex items-center gap-1">
                <Image
                  src="/images/icons/pdf.png"
                  alt="PDF"
                  width={200}
                  height={200}
                  className="w-4.5 h-4.5"
                />
              </span>

              {/* Word Icon */}
              <span className="flex items-center gap-1">
                <Image
                  src="/images/icons/word-icon.png"
                  alt="Word"
                  width={200}
                  height={200}
                  className="w-5 h-5"
                />
              </span>

              {/* Excel Icon */}
              <span className="flex items-center gap-1">
                <Image
                  src="/images/icons/excel.jpg"
                  alt="Excel"
                  width={200}
                  height={200}
                  className="w-5 h-5"
                />
              </span>

              {/* PowerPoint Icon */}
              <span className="flex items-center gap-1">
                <Image
                  src="/images/icons/ppt-icon.jpg"
                  alt="PowerPoint"
                  width={200}
                  height={200}
                  className="w-5 h-5"
                />
              </span>
            </span>
          </div>
        </div>
      </div>

      {/* Display uploaded files */}
      {fileList.length > 0 && (
        <div className="mt-4">
          <h4 className="font-medium text-gray-700 dark:text-gray-300 mb-2">Documents téléchargés:</h4>
          <ul className="space-y-2">
            {fileList.map((file, index) => (
              <li key={index} className="flex items-center justify-between p-2 bg-gray-100 dark:bg-gray-800 rounded">
                <div className="flex items-center">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  <span className="text-sm truncate max-w-xs">{file.name}</span>
                </div>
                <span className="text-xs text-gray-500">{formatFileSize(file.size)}</span>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default DropzoneComponent;