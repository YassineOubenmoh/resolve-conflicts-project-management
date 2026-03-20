'use client';

import React, { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import Image from 'next/image';
import { Download, MessageCircleMore, User } from 'lucide-react';

import Backdrop from "@mui/material/Backdrop";
import CircularProgress from "@mui/material/CircularProgress";

import { getActionsByRequiredAction } from '@/axios/InterlocutorApis';
import { downloadFile } from '@/axios/DocumentApis';
import { Action } from '@/types/interlocutor';
import FeedbackInterlocutorLayout from '../../FeedbackInterlocutorLayout';

const ImpactAdded = () => {
  const params = useParams();
  const router = useRouter();
  const requiredActionId = Number(params?.requiredActionId);
  const [searchTerm, setSearchTerm] = useState('');
  const [actions, setActions] = useState<Action[]>([]);
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(true);

  useEffect(() => {
    const fetchActions = async () => {
      if (!requiredActionId || isNaN(requiredActionId)) return;

      setLoading(true);
      try {
        const data = await getActionsByRequiredAction(requiredActionId);
        setActions(data || []);
      } catch (error) {
        console.error('Error fetching actions:', error);
        setActions([]);
      } finally {
        setLoading(false);
        setOpen(false)
      }
    };

    fetchActions();
  }, [requiredActionId]);

  const filteredActions = actions.filter((action) =>
    action.actionLabel.toLowerCase().includes(searchTerm.toLowerCase())
  );


  if (loading) {
        return (
          <Backdrop
            sx={(theme) => ({
              color: "#ab3c73",
              backgroundColor: "#FFF",
              zIndex: theme.zIndex.drawer + 1,
            })}
            open={open}
          >
            <CircularProgress color="inherit" />
          </Backdrop>
        );
      }


  return (
    <FeedbackInterlocutorLayout>
      <div className="pt-6 px-4 pb-24">
        {/* Search Bar */}
        <div className="flex justify-between items-center mb-6">
          <div className="w-full sm:w-2/5">
            <div className="relative p-4 flex items-center">
              <input
                type="text"
                placeholder="Rechercher par titre..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full px-4 py-2 border border-gray-200 rounded-lg bg-white text-sm focus:outline-none focus:ring-1 focus:ring-blue-500 dark:bg-white/[0.05] dark:text-white"
              />
              <Image
                src="/images/icons/search.png"
                alt="Search icon"
                width={16}
                height={16}
                className="absolute right-6"
              />
            </div>
          </div>
        </div>

        {/* Cards Grid */}
        {loading ? (
          <div className="text-center text-gray-500 dark:text-gray-400">Loading actions...</div>
        ) : filteredActions.length === 0 ? (
          <div className="text-center text-gray-500 dark:text-gray-400">No actions found.</div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 justify-center">
            {filteredActions.map((action, index) => (
              <div
                key={index}
                className="relative bg-white dark:bg-white/[0.03] border border-gray-200 dark:border-white/[0.05] rounded-2xl shadow-lg p-5 hover:shadow-xl transition-shadow flex flex-col justify-between min-h-[200px]"
              >
                {/* Validation Status */}
                <div className="absolute top-4 left-4">
                  {action.validationStatus === 'ACCEPTER' ||
                  action.validationStatus === 'REFUSER' ||
                  action.validationStatus === 'A_MODIFIER' ? (
                    <span
                      className={`inline-block px-2 py-1 rounded-full text-xs font-semibold
                        ${
                          action.validationStatus === 'ACCEPTER'
                            ? 'bg-green-100 text-green-800'
                            : action.validationStatus === 'REFUSER'
                            ? 'bg-red-100 text-red-800'
                            : 'bg-yellow-100 text-yellow-800'
                        }
                      `}
                    >
                      {action.validationStatus === 'ACCEPTER'
                        ? 'Accepted'
                        : action.validationStatus === 'REFUSER'
                        ? 'Rejected'
                        : 'To Modify'}
                    </span>
                  ) : (
                    <span className="inline-block px-2 py-1 rounded-full text-xs font-semibold bg-gray-100 text-gray-600">
                      No feedback added
                    </span>
                  )}
                </div>

                {/* Icons */}
                <div className="absolute top-4 right-4 flex gap-3">
                  <button
                    onClick={() => downloadFile(action.actionDocument)}
                    className="text-blue-600 hover:text-blue-800 transition-transform transform hover:scale-110 focus:outline-none disabled:opacity-50"
                    disabled={!action.actionDocument}
                    title={action.actionDocument ? 'Download' : 'No file'}
                  >
                    <Download className="w-5 h-5" />
                  </button>


                  <button
                    onClick={() =>
                      action?.id &&
                      router.push(`/interlocutor-feedback/add-feedback/${action.id}`)
                    }
                    className={`transition-transform transform hover:scale-110 focus:outline-none ${
                      action.validationStatus
                        ? 'text-gray-400 cursor-not-allowed'
                        : 'text-blue-600 hover:text-blue-800'
                    }`}
                    disabled={!!action.validationStatus}
                    title={
                      action.validationStatus ? 'Feedback already added' : 'Add New Feedback'
                    }
                  >
                    <MessageCircleMore className="w-5 h-5" />
                  </button>



                </div>

                {/* Action Label */}
                <div className="mt-10 mb-0">
                  <h2 className="text-lg font-semibold" style={{ color: '#B12B89' }}>
                    {action.actionLabel || '—'}
                  </h2>
                </div>

                {/* Footer: Created By & Date */}
                <div className="mt-2 pt-2 border-t border-gray-100 dark:border-white/[0.08] text-sm text-gray-600 dark:text-gray-300 flex justify-between items-center">
                  <div className="flex items-center gap-1">
                    <User className="w-4 h-4 text-gray-500 dark:text-white" />
                    <span>{action.actionCreatedBy || '—'}</span>
                  </div>
                  <div>
                    {action.createdAt
                      ? new Date(action.createdAt).toISOString().slice(0, 16).replace('T', ' ')
                      : '—'}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </FeedbackInterlocutorLayout>
  );
};

export default ImpactAdded;
