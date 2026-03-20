'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { getActionById } from '@/axios/InterlocutorApis';
import { Action } from '@/types/interlocutor';

import './FeedbackDetails.css';

const FeedbackDetail = () => {
  const params = useParams();
  const router = useRouter();

  const [action, setAction] = useState<Action | null>(null);
  const [loading, setLoading] = useState(true);
  const [isClosing, setIsClosing] = useState(false);

  useEffect(() => {
    const fetchAction = async () => {
      try {
        const id = Number(params.actionId);
        if (!isNaN(id)) {
          const result = await getActionById(id);
          setAction(result);
        } else {
          throw new Error('Invalid action ID');
        }
      } catch (err) {
        console.error('Failed to fetch action:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchAction();
  }, [params.actionId]);

  const handleClose = () => {
    setIsClosing(true);
    setTimeout(() => {
      router.back();
    }, 500);
  };

  const renderValidationBadge = (status: string) => {
    let label = '';
    let color = '';

    switch (status) {
      case 'ACCEPTER':
        label = 'Accepted';
        color = 'bg-green-100 text-green-700 border-green-300';
        break;
      case 'REFUSER':
        label = 'Rejected';
        color = 'bg-red-100 text-red-700 border-red-300';
        break;
      case 'A_MODIFIER':
        label = 'To Modify';
        color = 'bg-yellow-100 text-yellow-700 border-yellow-300';
        break;
      default:
        label = status;
        color = 'bg-gray-100 text-gray-700 border-gray-300';
    }

    return (
      <span
        className={`px-3 py-1 text-xs font-semibold rounded-full border ${color}`}
      >
        {label}
      </span>
    );
  };

  if (loading) return <div className="text-center mt-10 text-gray-500">Loading...</div>;
  if (!action) return <div className="text-center mt-10 text-red-500">Impact not found</div>;

  return (
    <div
      className={`fixed inset-0 z-50 bg-black bg-opacity-40 flex items-center justify-center transition-opacity duration-500 ease-in-out ${
        isClosing ? 'animate-fadeOut' : 'animate-fadeIn'
      }`}
      onClick={handleClose}
    >
      <div
        className={`bg-white rounded-2xl p-8 w-full max-w-xl shadow-2xl relative transform transition-transform duration-500 ease-in-out ${
          isClosing ? 'animate-slideDown' : 'animate-slideUp'
        }`}
        onClick={(e) => e.stopPropagation()}
      >
        <button
          onClick={handleClose}
          className="absolute top-3 right-4 text-gray-400 hover:text-gray-700 text-2xl font-bold transition-opacity"
        >
          ×
        </button>

        <h2 className="text-xl font-semibold mb-6 text-center" style={{ color: '#B12B89' }}>
            {action.responseToActionLabel}
        </h2>


        <div className="grid grid-cols-2 gap-x-4 gap-y-4 text-gray-700 text-sm">
        
          <div className="font-medium">Validation Status:</div>
          <div>{renderValidationBadge(action.validationStatus)}</div>

          <div className="font-medium">Justification:</div>
          <div>{action.justificationStatus}</div>

          <div className="font-medium">Last Modified:</div>
          <div>{new Date(action.lastModifiedAt).toLocaleDateString()}</div>
        </div>
      </div>
    </div>
  );
};

export default FeedbackDetail;
