'use client';

import { useRouter } from 'next/navigation';

import React, { useState, useEffect } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import Image from 'next/image';
import { Download, Eye, Pencil, User, X } from 'lucide-react';
import Badge from '@/components/ui/badge/Badge';
import { downloadFile } from '@/axios/DocumentApis';
import {
  getImpactAndReturnsByInterlocutor,
  getRequiredActionById,
  updateImpact,
} from '@/axios/InterlocutorApis';
import { Action, RequiredActionDto, ActionDto } from '@/types/interlocutor';
import ComponentCard from '@/components/common/ComponentCard';
import Label from '@/components/form/Label';

import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';



import Link from 'next/link';

export default function RetursImpacts() {
  const [searchTerm, setSearchTerm] = useState('');
  const [actions, setActions] = useState<Action[]>([]);
  const [requiredActionsMap, setRequiredActionsMap] = useState<Record<number, RequiredActionDto | null>>({});
  const [editingAction, setEditingAction] = useState<Action | null>(null);
  const [impactTitle, setImpactTitle] = useState('');
  const [comments, setComments] = useState('');
  const [file, setFile] = useState<File | null>(null);
  const [errorMessage, setErrorMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  const router = useRouter();

  const [open, setOpen] = useState(true);
  

  const [currentPage, setCurrentPage] = useState(1);
  const feedbacksPerPage = 10;

  const handleClose = () => {
    setOpen(false);
  };
  

  useEffect(() => {
    const fetchActions = async () => {
      setLoading(true)
      try {
        const fetchedActions: Action[] = await getImpactAndReturnsByInterlocutor();
        setActions(fetchedActions);

        const uniqueRequiredActionIds = Array.from(
          new Set(
            fetchedActions
              .map((a) => a.requiredActionId)
              .filter((id): id is number => id !== null && id !== undefined)
          )
        );

        const requiredActionsData = await Promise.all(
          uniqueRequiredActionIds.map((id) => getRequiredActionById(id))
        );

        const map: Record<number, RequiredActionDto | null> = {};
        uniqueRequiredActionIds.forEach((id, idx) => {
          map[id] = requiredActionsData[idx];
        });

        setRequiredActionsMap(map);
      } catch (error) {
        console.error('Error fetching actions or required actions:', error);
      } finally {
        setLoading(false);
        setOpen(false);
      }
    };

    fetchActions();
  }, []);

  const filteredActions = actions.filter((action) =>
    action.actionLabel.toLowerCase().includes(searchTerm.toLowerCase())
  );



  const totalPages = Math.ceil(filteredActions.length / feedbacksPerPage);
  const startIndex = (currentPage - 1) * feedbacksPerPage;
  const currentActions = filteredActions.slice(
    startIndex,
    startIndex + feedbacksPerPage
  );



  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files?.length) {
      setFile(e.target.files[0]);
    }
  };

  const openEditForm = (action: Action) => {
    setEditingAction(action);
    setImpactTitle(action.actionLabel || '');
    setComments('');
    setFile(null);
    setErrorMessage('');
    setSuccessMessage('');
  };

  const closeEditForm = () => {
    setEditingAction(null);
    setImpactTitle('');
    setComments('');
    setFile(null);
    setErrorMessage('');
    setSuccessMessage('');

    //router.push('/spoc/impact-documents');
  };

  const handleSubmit = async () => {
  if (!impactTitle) {
    setErrorMessage('Le titre impact est obligatoire.');
    return;
  }

  if (!file) {
    setErrorMessage('Veuillez sélectionner un document à joindre.');
    return;
  }

  if (!editingAction) {
    setErrorMessage('Aucune action sélectionnée pour la mise à jour.');
    return;
  }

  setLoading(true);
  setErrorMessage('');
  setSuccessMessage('');

  try {
    const actionDto: ActionDto = {
      actionLabel: impactTitle,
      comments: comments ? [comments] : [],
      requiredActionId: editingAction.requiredActionId,
    };

    await updateImpact(editingAction.id, actionDto, file);

    setSuccessMessage('Mise à jour réussie ! / Impact Updated Successfully');
    closeEditForm();

    const updated = await getImpactAndReturnsByInterlocutor();
    setActions(updated);

    setTimeout(() => {
      router.push('/spoc/impact-documents');
    }, 300); // Delay to show spinner
  } catch (error) {
    setErrorMessage('Erreur lors de la mise à jour. Veuillez réessayer.');
    console.error(error);
  } finally {
    setLoading(false);
    setOpen(false);
  }
};





  if (loading) {
  return (
    <div>
      <Backdrop
        sx={(theme) => ({
          color: '#ab3c73',
          backgroundColor: '#FFF',
          zIndex: theme.zIndex.drawer + 1,
        })}
        open={open}
        onClick={handleClose}
      >
        <CircularProgress color="inherit" />
      </Backdrop>
    </div>
  );
}




  return (
    <div className="pt-6 px-4 pb-24">
      {/* Search bar */}
      <div className="relative flex justify-between items-center px-4 w-full mb-4">
        <div className="w-2/5">
          <div className="p-4 flex items-center">
            <input
              type="text"
              placeholder="Rechercher par titre..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="bg-white w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-blue-500 text-sm dark:bg-white/[0.05] dark:text-white"
            />
            <Image
              src="/images/icons/search.png"
              alt="search"
              width={40}
              height={40}
              className="w-4 h-4 relative right-8"
            />
          </div>
        </div>
      </div>

      {/* Table */}
      <div className="overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.03]">
        <div className="w-full overflow-x-auto">
          <div className="min-w-[700px] mx-auto">
            <Table>
              <TableHeader className="border-b border-gray-100 dark:border-white/[0.05]">
                <TableRow className="bg-gray-50 dark:bg-white/[0.03]">
                  {['Required Action', 'Signaled Impact', 'Validation Status', 'Validated By', 'Download', 'Details'].map(
                    (head, idx) => (
                      <TableCell
                        key={idx}
                        isHeader
                        className="px-4 py-2 font-semibold text-gray-600 text-sm text-start dark:text-gray-400"
                      >
                        {head}
                      </TableCell>
                    )
                  )}
                </TableRow>
              </TableHeader>
              <TableBody className="divide-y divide-gray-100 dark:divide-white/[0.05]">
                {currentActions.length ? (
                  currentActions.map((action, index) => (
                    <TableRow key={index} className="hover:bg-gray-50 dark:hover:bg-white/[0.05]">
                      <TableCell className="px-4 py-3 text-sm text-gray-700 dark:text-white/90">
                        {action.requiredActionId && requiredActionsMap[action.requiredActionId] ? (
                          <Badge
                            size="sm"
                            style={{ color: '#B12B89', backgroundColor: '#F8CEEB' }}
                            className="text-sm"
                          >
                            {requiredActionsMap[action.requiredActionId]?.requiredAction}
                          </Badge>
                        ) : (
                          'Loading...'
                        )}
                      </TableCell>
                      <TableCell className="px-4 py-3 text-sm">{action.actionLabel}</TableCell>
                      <TableCell className="px-4 py-4 text-start">
                        <Badge
                          size="sm"
                          variant="light"
                          className={`text-sm ${
                            action.validationStatus === 'ACCEPTER'
                              ? 'bg-green-100 text-green-600'
                              : action.validationStatus === 'A_MODIFIER'
                              ? 'bg-yellow-100 text-yellow-600'
                              : action.validationStatus === 'REFUSER'
                              ? 'bg-red-100 text-red-600'
                              : 'bg-gray-100 text-gray-600'
                          } dark:bg-white/[0.05]`}
                        >
                          {action.validationStatus === 'ACCEPTER'
                            ? 'Accepted'
                            : action.validationStatus === 'A_MODIFIER'
                            ? 'To Modify'
                            : action.validationStatus === 'REFUSER'
                            ? 'Rejected'
                            : 'No feedback'}
                        </Badge>
                      </TableCell>

                      <TableCell className="px-4 py-3 text-sm">
                        <div className="flex items-center gap-2">
                          {action.validatedBy && <User className="w-4 h-4" />}
                          <Badge
                            size="sm"
                            variant="light"
                            className="text-gray-600 bg-gray-100 dark:bg-white/[0.05]"
                          >
                            {action.validatedBy || 'Not validated yet'}
                          </Badge>
                        </div>
                      </TableCell>
                      <TableCell className="px-4 py-3 text-sm text-blue-600 text-center">
                        <button
                          onClick={() => downloadFile(action.responseDocument)}
                          disabled={!action.responseDocument}
                          className={`flex items-center ${
                            !action.responseDocument ? 'cursor-not-allowed opacity-50' : ''
                          }`}
                        >
                          <Download className="w-4 h-4" />
                        </button>
                      </TableCell>


                      <TableCell className="px-4 py-3 text-sm">
                        <div className="flex items-center gap-4">

                          {(action.validationStatus != null && action.justificationStatus != null && action.lastModifiedAt != null) && (
                            <Link href={`/spoc/returns-impacts/${action.id}`}>
                              <Eye className="w-4 h-4 cursor-pointer" />
                            </Link>
                          )}

                          {!action.validationStatus && (
                            <Pencil
                              className="w-4 h-4 text-yellow-600 cursor-pointer"
                              onClick={() => openEditForm(action)}
                            />
                          )}

                        </div>
                      </TableCell>



                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={6} className="text-center py-4 text-gray-500">
                      No data found.
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </div>
        </div>
      </div>




      {/* Edit Modal */}
      {editingAction && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40 p-4">
          <ComponentCard className="max-w-xl w-full bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
            <div className="flex justify-between items-center mb-4">
              
              <X className="w-5 h-5 cursor-pointer" onClick={closeEditForm} />
            </div>

            {/* Display Impact Title */}
            <div className="mb-4">
              <h3 className="text-xl font-bold text-gray-800 dark:text-gray-100 text-center">
                {impactTitle}
              </h3>
            </div>

            {errorMessage && <p className="text-red-600 mb-2">{errorMessage}</p>}
            {successMessage && <p className="text-green-600 mb-2">{successMessage}</p>}

            {/* Editable Comments */}
            <div className="mb-4">
              <Label htmlFor="comments">Comments</Label>
              <textarea
                id="comments"
                value={comments}
                onChange={(e) => setComments(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#B12B89]"
              />
            </div>

            {/* File Upload */}
            <div className="mb-4">
              <Label htmlFor="fileUpload">Upload Document</Label>
              <input
                id="fileUpload"
                type="file"
                onChange={handleFileChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-md bg-white file:mr-4 file:py-2 file:px-4 file:border-0 file:text-sm file:font-semibold file:bg-[#B12B89] file:text-white hover:file:bg-[#9d2476] cursor-pointer"
              />
            </div>

            {/* Action Buttons */}
            <div className="flex justify-end space-x-4">
              <button
                onClick={closeEditForm}
                className="px-4 py-2 rounded-md text-white bg-gray-500 hover:bg-gray-600 transition"
              >
                Cancel
              </button>

              <button
                onClick={handleSubmit}
                disabled={loading}
                className="px-4 py-2 rounded-md text-white bg-[#B12B89] hover:bg-[#9d2476] transition disabled:opacity-50"
              >
                {loading ? 'Update...' : 'To update'}
              </button>
            </div>
          </ComponentCard>
        </div>
      )}



      {/* Pagination */}
        {totalPages > 1 && (
          <div className="mt-8 flex justify-center items-center flex-wrap gap-3">
            {Array.from({ length: totalPages }, (_, index) => {
              const isActive = currentPage === index + 1;
              return (
                <button
                  key={index + 1}
                  onClick={() => setCurrentPage(index + 1)}
                  className={`px-4 py-2 text-sm font-medium rounded-full border transition-all duration-300 ease-in-out shadow-sm ${
                    isActive
                      ? "bg-[#B12B89] text-white border-[#B12B89] scale-105 shadow-lg"
                      : "bg-white text-[#B12B89] border-[#B12B89] hover:bg-[#B12B89]/10 hover:scale-105"
                  }`}
                >
                  {index + 1}
                </button>
              );
            })}
          </div>
        )}


    </div>
  );
}
