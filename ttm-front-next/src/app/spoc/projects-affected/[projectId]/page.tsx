'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { fetchProjectById, Project } from '@/app/services/SpocProjectService';
import './ProjectDetails.css';

import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';

const ProjectDetail = () => {
  const { projectId } = useParams();
  const router = useRouter();

  const [project, setProject] = useState<Project | null>(null);
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(true);
  const [isClosing, setIsClosing] = useState(false);

  const handleClose2 = () => {
    setOpen(false);
  };

  useEffect(() => {
    if (projectId) {
      setLoading(true);
      fetchProjectById(projectId.toString())
        .then(setProject)
        .catch((err) => console.error('Failed to fetch project:', err))
        .finally(() => {
          setLoading(false);
        });
    }
  }, [projectId]);

  const handleClose = () => {
    setIsClosing(true);
    setTimeout(() => {
      router.back();
    }, 500);
  };

  if (loading) {
    return (
      <Backdrop
        sx={{
          color: '#ab3c73',
          backgroundColor: '#FFF',
          zIndex: (theme) => theme.zIndex.drawer + 1,
        }}
        open={open}
        onClick={handleClose2}
      >
        <CircularProgress color="inherit" />
      </Backdrop>
    );
  }

  if (!project) return <div>Project not found</div>;

  return (
    <div
      className={`fixed inset-0 z-50 bg-[rgba(0,0,0,0.4)] flex items-center justify-center animate-fadeIn ${
        isClosing ? 'animate-fadeOut' : ''
      }`}
    >
      <div
        className={`bg-white rounded-xl p-6 w-full max-w-xl shadow-lg relative animate-slideUp ${
          isClosing ? 'animate-slideDown' : ''
        }`}
        onClick={(e) => e.stopPropagation()}
      >
        {/* Close Button */}
        <button
          onClick={handleClose}
          className="absolute top-3 right-4 text-gray-500 hover:text-gray-800 text-2xl font-bold"
        >
          ×
        </button>

        {/* Title */}
        <h1 className="text-2xl font-bold mb-4 text-left text-[#A03A96]">
          {project.title}
        </h1>

        {/* Confidential Label */}
        {project.isConfidential && (
          <div className="mb-4">
            <span className="inline-block bg-red-100 text-red-600 text-xs font-medium px-3 py-1 rounded-full shadow-sm">
              🚫 Confidential
            </span>
          </div>
        )}

        {/* Info Grid */}
        <div className="grid grid-cols-2 gap-x-3 gap-y-2 text-gray-700 text-sm mb-4">
          <div className="font-medium">Propriétaire:</div>
          <div>{project.ownerFullName}</div>

          <div className="font-medium">Type de Marché:</div>
          <div>{project.marketType}</div>

          <div className="font-medium">Type de Projet:</div>
          <div>{project.projectType}</div>

          <div className="font-medium">Confidentiel:</div>
          <div>{project.isConfidential ? 'Oui' : 'Non'}</div>

          <div className="font-medium">Date de Début:</div>
          <div>{new Date(project.dateStartTtm).toLocaleDateString()}</div>
        </div>

        {/* Description */}
        {project.description && (
          <div className="text-gray-700 text-sm">
            <h2 className="font-semibold mb-1">Description:</h2>
            <p>{project.description}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProjectDetail;
