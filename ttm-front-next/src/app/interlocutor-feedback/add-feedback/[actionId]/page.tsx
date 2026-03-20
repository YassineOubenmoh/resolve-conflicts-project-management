'use client';

import React, { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';

import { getActionById, sendFeedback } from '@/axios/InterlocutorApis';
import { Action, ReturnImpactDto } from '@/types/interlocutor';

import ComponentCard from '@/components/common/ComponentCard';
import Label from '@/components/form/Label';
import Select from '@/components/form/Select';

import { ChevronDownIcon } from 'lucide-react';
import FeedbackInterlocutorLayout from '../../FeedbackInterlocutorLayout';

import { useRouter } from 'next/navigation';


const AddFeedback = () => {
  const router = useRouter();

  const params = useParams();
  const actionId = Number(params?.actionId);

  const [action, setAction] = useState<Action | null>(null);
  const [validationStatus, setValidationStatus] = useState('');
  const [feedbackTitle, setFeedbackTitle] = useState('');
  const [justifications, setJustifications] = useState('');
  const [file, setFile] = useState<File | null>(null);

  const [loading, setLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    const fetchAction = async () => {
      if (!actionId) return;
      try {
        const data = await getActionById(actionId);
        setAction(data);
      } catch (err) {
        console.error('Failed to load action:', err);
        setErrorMessage('Unable to fetch action data.');
      }
    };
    fetchAction();
  }, [actionId]);

  const handleSubmit = async () => {
    setSuccessMessage('');
    setErrorMessage('');

    if (!validationStatus) return setErrorMessage('Please select a validation status.');
    if (!feedbackTitle.trim()) return setErrorMessage('Please enter a feedback title.');
    if (!file) return setErrorMessage('Please upload a file.');
    if (!action?.requiredActionId) return setErrorMessage('Missing required action ID.');

    const feedbackDto: ReturnImpactDto = {
      responseToActionLabel: feedbackTitle,
      validationStatus,
      justificationStatus: justifications,
      requiredActionId: action.requiredActionId,
    };

    try {
      setLoading(true);
      await sendFeedback(action.id, feedbackDto, file);
      setSuccessMessage('Feedback successfully submitted!');
      setValidationStatus('');
      setFeedbackTitle('');
      setJustifications('');
      setFile(null);

      router.push('/interlocutor-feedback/feedback-documents');

    } catch (err) {
      console.error('Feedback submission failed:', err);
      setErrorMessage('Failed to submit feedback.');
    } finally {
      setLoading(false);
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0] || null;
    setFile(selectedFile);
  };

  const validationStatusOptions = ['ACCEPTER', 'A_MODIFIER', 'REFUSER'].map((status) => ({
    label: status,
    value: status,
  }));

  const getBadge = (status: string) => {
    let text = '';
    let color = '';

    switch (status) {
      case 'ACCEPTER':
        text = 'Accepted';
        color = 'bg-green-100 text-green-800';
        break;
      case 'REFUSER':
        text = 'Rejected';
        color = 'bg-red-100 text-red-800';
        break;
      case 'A_MODIFIER':
        text = 'To Modify';
        color = 'bg-yellow-100 text-yellow-800';
        break;
      default:
        return null;
    }

    return (
      <span className={`inline-block px-3 py-1 text-sm font-semibold rounded-full ${color}`}>
        {text}
      </span>
    );
  };

  return (

    <FeedbackInterlocutorLayout>

    <div className="min-h-screen flex items-center justify-center bg-[#F9FAFB] p-4">
      <ComponentCard className="w-full max-w-lg bg-white p-8 rounded-xl shadow-xl flex flex-col space-y-6">
        {action ? (
          <h2 className="text-lg font-bold bg-gradient-to-r from-[#B12B89] to-pink-800 text-transparent bg-clip-text">
            {action.actionLabel}
          </h2>
        ) : (
          <p className="text-center text-gray-500">Loading action...</p>
        )}

        <div>
          <Label>Feedback Title</Label>
          <input
            type="text"
            value={feedbackTitle}
            onChange={(e) => setFeedbackTitle(e.target.value)}
            placeholder="Enter the feedback title"
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-pink-500 dark:bg-gray-900 dark:text-white"
          />
        </div>

        <div className="space-y-2">
          <Label>Validation Status</Label>
          <Select
            options={validationStatusOptions}
            placeholder="Select validation status"
            onChange={setValidationStatus}
            defaultValue={validationStatus}
            icon={<ChevronDownIcon size={18} className="text-gray-400" />}
          />
          {getBadge(validationStatus)}
        </div>

        <div>
          <Label>Justifications</Label>
          <textarea
            value={justifications}
            onChange={(e) => setJustifications(e.target.value)}
            rows={4}
            placeholder="Add your justifications..."
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-pink-500 dark:bg-gray-900 dark:text-white"
          />
        </div>

        <div>
          <Label>Upload File</Label>
          <div
            className="relative flex items-center justify-center w-full h-40 p-4 border-2 border-dashed border-gray-300 rounded-lg cursor-pointer hover:border-pink-600 hover:bg-pink-50 transition"
            onClick={() => document.getElementById('fileInput')?.click()}
          >
            <div className="text-center text-gray-500">
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
              <p className="text-xs text-gray-400">Accepted formats: PDF, DOCX, PPTX...</p>
            </div>
            <input
              id="fileInput"
              type="file"
              onChange={handleFileChange}
              className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
            />
          </div>
          {file && (
            <p className="mt-2 text-sm text-gray-700 dark:text-gray-300">
              Selected file: <span className="font-medium">{file.name}</span>
            </p>
          )}
        </div>

        {errorMessage && (
          <div className="p-3 bg-red-100 text-red-800 rounded-md text-sm shadow">{errorMessage}</div>
        )}

        <div className="flex justify-center">
          <button
            onClick={handleSubmit}
            disabled={loading}
            className={`text-white px-6 py-3 rounded-lg transition-all ${
              loading ? 'bg-pink-700 cursor-not-allowed' : 'bg-[#B12B89] hover:bg-pink-800'
            }`}
          >
            {loading ? 'Submitting...' : 'Valider'}
          </button>
        </div>

        {successMessage && (
          <div className="mt-4 p-4 bg-green-100 text-green-800 rounded-lg shadow">{successMessage}</div>
        )}
      </ComponentCard>
    </div>

    </FeedbackInterlocutorLayout>
  );
};

export default AddFeedback;
