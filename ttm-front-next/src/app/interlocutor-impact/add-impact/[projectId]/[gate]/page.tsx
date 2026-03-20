'use client';

import React, { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { ChevronDownIcon } from 'lucide-react';

import ComponentCard from '@/components/common/ComponentCard';
import Label from '@/components/form/Label';
import Select from '@/components/form/Select';

import { getProjectByProjectId } from '@/axios/ProjectApis';
import { getRequiredActionsByProjectIdAndGate, sendAction } from '@/axios/InterlocutorApis';

import { ProjectDto } from '@/app/services/SpocProjectService';
import { RequiredActionDto, ActionDto } from '@/types/interlocutor';

const AddImpact = () => {
  const [project, setProject] = useState<ProjectDto | null>(null);
  const [requiredActions, setRequiredActions] = useState<RequiredActionDto[]>([]);
  const [selectedActionId, setSelectedActionId] = useState<string>('');
  const [impactTitle, setImpactTitle] = useState('');
  const [comments, setComments] = useState('');
  const [file, setFile] = useState<File | null>(null);
  const [successMessage, setSuccessMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const params = useParams();
  const projectId = Number(params?.projectId);
  const gate = params?.gate?.toString();

  useEffect(() => {
    const fetchData = async () => {
      if (!projectId || !gate) return;

      try {
        const projectResponse = await getProjectByProjectId(projectId);
        setProject(projectResponse.data);

        const actions = await getRequiredActionsByProjectIdAndGate(projectId, gate);
        setRequiredActions(actions);
      } catch (error) {
        console.error('Error loading data:', error);
      }
    };

    fetchData();
  }, [projectId, gate]);

  const handleSubmit = async () => {
  setErrorMessage('');
  setSuccessMessage('');

  if (!selectedActionId) return setErrorMessage('Please select a required action.');
  if (!impactTitle.trim()) return setErrorMessage('Please enter an impact title.');
  if (!file) return setErrorMessage('Please upload a file.');

  // Build ActionDto without the file name
  const actionDto: ActionDto = {
    actionLabel: impactTitle,
    comments: comments ? [comments] : [], // comments as array of strings
    requiredActionId: Number(selectedActionId),
    // Remove actionDocument here; backend gets file separately
  };

  try {
    setLoading(true);
    await sendAction(actionDto, file);
    setSuccessMessage('Impact successfully added!');
    setSelectedActionId('');
    setImpactTitle('');
    setComments('');
    setFile(null);
  } catch (error) {
    console.error('Error submitting action:', error);
    setErrorMessage('Failed to submit impact.');
  } finally {
    setLoading(false);
  }
};


  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0] || null;
    setFile(selectedFile);
  };

  const actionOptions = requiredActions.map((action) => ({
    label: action.requiredAction,
    value: action.id.toString(),
  }));

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 p-4">
      <ComponentCard className="w-full max-w-lg bg-white p-8 rounded-xl shadow-xl flex flex-col space-y-6">
        {project ? (
          <h2 className="text-lg font-bold bg-gradient-to-r from-[#B12B89] to-pink-800 text-transparent bg-clip-text">
            {project.title}
          </h2>
        ) : (
          <p className="text-center text-gray-500">Loading project...</p>
        )}

        <div>
          <Label>Required Action</Label>
          <Select
            options={actionOptions}
            placeholder="Select a required action"
            onChange={setSelectedActionId}
            defaultValue={selectedActionId}
            icon={<ChevronDownIcon size={18} className="text-gray-400" />}
          />
        </div>

        <div>
          <Label>Impact Title</Label>
          <input
            type="text"
            value={impactTitle}
            onChange={(e) => setImpactTitle(e.target.value)}
            placeholder="Enter the title of the impact"
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-900 dark:text-white"
          />
        </div>

        <div>
          <Label>Comments</Label>
          <textarea
            value={comments}
            onChange={(e) => setComments(e.target.value)}
            rows={4}
            placeholder="Add your comments here..."
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-900 dark:text-white"
          />
        </div>

        <div>
          <Label>Upload File</Label>
          <label
            htmlFor="fileInput"
            className="relative flex items-center justify-center w-full h-40 p-4 border-2 border-dashed border-gray-300 rounded-lg cursor-pointer hover:border-[#B12B89] hover:bg-[#fdf4fa] transition duration-200"
          >
            <div className="text-center text-gray-500 pointer-events-none">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="mx-auto mb-2 h-10 w-10 text-[#B12B89]"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M7 16V4m0 0L3 8m4-4l4 4m5 0h2a2 2 0 012 2v10a2 2 0 01-2 2h-6a2 2 0 01-2-2h8m-6 0v-4"
                />
              </svg>
              <p className="text-sm">Click or drag a file to upload</p>
              <p className="text-xs text-gray-400">Accepted formats: PDF, DOCX, PNG...</p>
            </div>
            <input
              id="fileInput"
              type="file"
              onChange={handleFileChange}
              className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
            />
          </label>

          {file && (
            <p className="mt-2 text-sm text-gray-600 dark:text-gray-300">
              Selected file: <span className="font-medium">{file.name}</span>
            </p>
          )}
        </div>


        {errorMessage && (
          <div className="p-3 bg-red-100 text-red-800 rounded-md text-sm shadow">
            {errorMessage}
          </div>
        )}

        <div className="flex justify-center">
          <button
            onClick={handleSubmit}
            disabled={loading}
            className={`text-white px-6 py-3 rounded-lg transition-all ease-in-out duration-200 ${loading ? 'bg-[#a02472]' : 'hover:bg-[#B12B89]'}`}
            style={{ backgroundColor: '#B12B89' }}
          >
            {loading ? 'Submitting...' : 'Valider'}
          </button>
        </div>

        {successMessage && (
          <div className="mt-4 p-4 bg-green-100 text-green-800 rounded-lg shadow-lg transform transition-opacity duration-300">
            {successMessage}
          </div>
        )}
      </ComponentCard>
    </div>
  );
};

export default AddImpact;
