"use client";

import { FileText } from "lucide-react";
import React, { useEffect, useState } from "react";
import Image from "next/image";
import {
  Table,
  TableBody,
  TableCell,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Trash2 } from "lucide-react";
import {
  getInterlocutorsData,
  getUserByUsername,
  removeAffectationByProjectId,
  exportInterlocutorsToCSV
} from "@/axios/UsersApis";



import { InterlocutorDto } from "@/types/project";
import { getUsernameFromToken } from "@/app/services/SpocAffectationProjectService";

import { motion, AnimatePresence } from "framer-motion";

import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';

export default function InterlocutorsAffected() {
  const [searchTerm, setSearchTerm] = useState("");
  const [data, setData] = useState<InterlocutorDto[]>([]);
  const [filteredData, setFilteredData] = useState<InterlocutorDto[]>([]);
  const [department, setDepartment] = useState<string | null>(null);
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(null);
  const [flashMessage, setFlashMessage] = useState<string | null>(null);

  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(true);

  const [currentPage, setCurrentPage] = useState(1);
  const documentsPerPage = 10;


  const handleClose = () => {
    setOpen(false);
  };


  useEffect(() => {
    const fetchUserData = async () => {
      const username = getUsernameFromToken();
      if (!username) return;

      try {
        const { data: user } = await getUserByUsername(username);
        if (user?.department) {
          setDepartment(user.department);
        }
      } catch (error) {
        console.error("Error fetching user:", error);
      }
    };

    fetchUserData();
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      if (!department) return;

      setLoading(true);
      try {
        const response = await getInterlocutorsData(department);
        setData(response.data);
      } catch (error) {
        console.error("Failed to fetch interlocutor data", error);
      } finally {
        setLoading(false);
        setOpen(false);
      }
    };

    fetchData();
  }, [department]);

  useEffect(() => {
    const term = searchTerm.toLowerCase();
    const filtered = data.filter((entry) => {
      const signalingName = `${entry.interlocutorSignalingFirstName} ${entry.interlocutorSignalingLastName}`.toLowerCase();
      const respondingName = `${entry.interlocutorRespondingFirstName} ${entry.interlocutorRespondingLastName}`.toLowerCase();
      const project = entry.projectName?.toLowerCase?.() ?? '';

      return (
        signalingName.includes(term) ||
        respondingName.includes(term) ||
        project.includes(term)
      );
    });
    setFilteredData(filtered);
  }, [searchTerm, data]);

  const handleDelete = (projectId: number) => {
    setSelectedProjectId(projectId);
  };

  const confirmDelete = async () => {
    if (selectedProjectId === null) return;

    setLoading(true);
    try {
      await removeAffectationByProjectId(selectedProjectId);
      setFlashMessage("Affectation supprimée avec succès");

      const response = await getInterlocutorsData(department!);
      setData(response.data);
      setSelectedProjectId(null);

      setTimeout(() => setFlashMessage(null), 3000);
    } catch (error) {
      console.error("Error deleting affectation:", error);
    } finally {
      setLoading(false);
      setOpen(false);
    }
  };


  const totalPages = Math.ceil(filteredData.length / documentsPerPage);
  const startIndex = (currentPage - 1) * documentsPerPage;
  const currentInterlocutors = filteredData.slice(
    startIndex,
    startIndex + documentsPerPage
  );



  // CSV Export handler
    const handleDownloadCSV = () => {
      if (!department) {
        alert("Department not set yet.");
        return;
      }
      exportInterlocutorsToCSV(department).catch(() =>
        alert("Erreur lors du téléchargement du CSV")
      );
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
      {flashMessage && (
        <div className="mb-4 p-4 text-sm text-green-800 bg-green-100 border border-green-200 rounded-lg">
          {flashMessage}
        </div>
      )}

      <AnimatePresence>
        {selectedProjectId !== null && (
          <motion.div
            className="fixed inset-0 z-50 flex items-center justify-center bg-black/30"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          >
            <motion.div
              className="p-6 w-full max-w-md bg-white rounded-lg shadow-lg z-50"
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.8, opacity: 0 }}
              transition={{ type: "spring", stiffness: 300, damping: 20 }}
            >
              <p className="mb-4 text-red-700 font-medium text-center">
                Are you sure to continue the suppression ?
              </p>
              <div className="flex justify-center gap-4">
                <button
                  onClick={confirmDelete}
                  className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700 text-sm"
                >
                  Delete
                </button>
                <button
                  onClick={() => setSelectedProjectId(null)}
                  className="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400 text-sm"
                >
                  Cancel
                </button>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      <div className="relative flex justify-between items-center px-4 w-full mb-4">
        <div className="w-2/5">
          <div className="p-4 flex items-center">
            <input
              type="text"
              placeholder="Rechercher par mot clés..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="bg-white w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-blue-500 text-sm dark:bg-white/[0.05] dark:text-white"
            />
            <Image
              src="/images/icons/search.png"
              alt="search-bar"
              width={40}
              height={40}
              className="w-4 h-4 relative right-8"
            />
          </div>
        </div>

        {/* Export CSV Button aligned to the extreme right */}
        <button
          onClick={handleDownloadCSV}
          className="px-4 py-2 text-sm text-white border border-gray-300 rounded-lg shadow-md hover:opacity-90 transition flex items-center gap-2"
          style={{ backgroundColor: "#B12B89" }}
        >
          <FileText size={18} />
          Export CSV
        </button>
      </div>


      <div className="overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.03]">
        <div className="w-full overflow-x-auto">
          <div className="min-w-[700px] mx-auto">
            <Table>
              <TableHeader className="border-b border-gray-100 dark:border-white/[0.05]">
                <TableRow className="bg-gray-50 dark:bg-white/[0.03]">
                  {["Interlocuteur Impacts", "Interlocuteur Retours", "Projet", "Actions"].map((head, idx) => (
                    <TableCell
                      key={idx}
                      isHeader
                      className="px-4 py-2 font-semibold text-gray-600 text-sm text-start dark:text-gray-400"
                    >
                      {head}
                    </TableCell>
                  ))}
                </TableRow>
              </TableHeader>

              <TableBody className="divide-y divide-gray-100 dark:divide-white/[0.05]">
                {currentInterlocutors.length > 0 ? (
                  currentInterlocutors.map((entry, index) => {
                    const signalingName = `${entry.interlocutorSignalingFirstName} ${entry.interlocutorSignalingLastName}`;
                    const respondingName = `${entry.interlocutorRespondingFirstName} ${entry.interlocutorRespondingLastName}`;

                    return (
                      <TableRow
                        key={index}
                        className="hover:bg-gray-50 dark:hover:bg-white/[0.05]"
                      >
                        <TableCell className="px-4 py-3 text-gray-700 text-sm dark:text-white/90">
                          {signalingName}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-600 text-sm dark:text-gray-400">
                          {respondingName}
                        </TableCell>
                        <TableCell className="px-4 py-3 text-gray-600 text-sm dark:text-gray-400">
                          {entry.projectName}
                        </TableCell>
                        <TableCell className="px-4 py-3">
                          <div className="flex items-center gap-4">
                            <button
                              onClick={() => handleDelete(entry.projectId)}
                              className="text-red-600 hover:text-red-800"
                              title="Supprimer"
                            >
                              <Trash2 className="w-4 h-4" />
                            </button>
                          </div>
                        </TableCell>
                      </TableRow>
                    );
                  })
                ) : (
                  <TableRow>
                    <TableCell
                      colSpan={4}
                      className="px-4 py-6 text-center text-gray-500 dark:text-gray-400"
                    >
                      Aucun résultat trouvé.
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </div>
        </div>
      </div>



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
                  : "bg-white text-[#B12B89] border-[#B12B89] hover:bg-[#B12B89]/10"
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
