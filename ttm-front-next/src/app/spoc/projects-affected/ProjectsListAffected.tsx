"use client";

import { FileText } from "lucide-react";

import React, { useState, useEffect } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import Badge from "@/components/ui/badge/Badge";
import { Shield } from "lucide-react";
import { BookOpenCheck } from "lucide-react";
import Image from "next/image";
import { Dropdown } from "@/components/ui/dropdown/Dropdown";
import Link from "next/link";
import { Eye, Rocket } from "lucide-react";
import { Project } from "@/app/services/SpocProjectService";
import {
  filterProjectsForSpoc,
  affectProjectToInterlocutorRespondingImpact
} from "@/axios/ProjectApis"; // Import the filter method
import { getUsernameFromToken } from "@/app/services/SpocAffectationProjectService";
import { getUserByUsername, keepProjectForSpoc } from "@/axios/UsersApis";

import { ChevronDownIcon } from "lucide-react";

import Select from "@/components/form/Select";

import { UserDto } from "@/app/services/SpocAffectationProjectService";

import { getInterlocutorsRespondingImpactByDepartment,
  exportSpocProjectsToCSV
 } from "@/axios/ProjectApis";

import { Lock } from "lucide-react";

import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';

import './ProjectAffected.css'; // Import the CSS file


export const getUserDepartment = async (): Promise<string | null> => {
  const username = getUsernameFromToken();
  if (!username) return null;

  try {
    const { data: user } = await getUserByUsername(username);
    return user?.department || null;
  } catch (error) {
    console.error("Failed to fetch user department:", error);
    return null;
  }
};


type OptionType = {
  value: string;
  label: string;
};

export default function ProjectsList() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [projectTypeOpen, setProjectTypeOpen] = useState(false);
  const [marketTypeOpen, setMarketTypeOpen] = useState(false);
  const [selectedProjectType, setSelectedProjectType] = useState<string | null>(null);
  const [selectedMarketType, setSelectedMarketType] = useState<string | null>(null);

  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const [selectedSpocInterlocutorUsername, setSelectedSpocInterlocutorUsername] = useState<string | null>(null)
  const [interlocutorRespondingImpactOptions, setInterlocutorRespondingImpactOptions] = useState<OptionType[]>([]);

  const [takenProjects, setTakenProjects] = useState<number[]>([]);

  const [department, setDepartment] = useState<string | null>(null);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(null);

  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(true);

  const [currentPage, setCurrentPage] = useState(1);
  const projectsPerPage = 10;
  

  const handleClose = () => {
    setOpen(false);
  };

  // Fetch user department
  useEffect(() => {
    const fetchUserData = async () => {
      const username = getUsernameFromToken();
      if (!username) return;

      try {
        const { data: user } = await getUserByUsername(username);
        if (!user?.department) return;
        setDepartment(user.department);
      } catch (error) {
        console.error("Error fetching user:", error);
      }
    };

    fetchUserData();
  }, []);

  useEffect(() => {

    if (!department) return;

    const fetchDropdownData = async () => {

      try {
        const responses = await Promise.all([
          getInterlocutorsRespondingImpactByDepartment(department),
        ]);

        // Assuming each response has a `.data` property which is an array of UserDto
        const users: UserDto[] = responses.flatMap((res) => res.data);

        setInterlocutorRespondingImpactOptions(
          users.map((u) => ({
            value: u.username,
            label: `${u.firstName} ${u.lastName}`,
          }))
        );
      } catch (err) {
        console.error("Failed to fetch dropdown data:", err);
      }
    };

    fetchDropdownData();
  }, [department]);


  useEffect(() => {
    const fetchProjects = async () => {
      setLoading(true);
      const dept = await getUserDepartment();
      if (!dept) return;


      try {
        // Convert selectedProjectType and selectedMarketType to undefined if they are null
        const { data } = await filterProjectsForSpoc(
          dept,
          selectedProjectType || undefined, // Converts null to undefined
          selectedMarketType || undefined    // Converts null to undefined
        );
        setProjects(data);
      } catch (error) {
        console.error("Error fetching projects:", error);
      } finally {
        setLoading(false);
        setOpen(false);
      }
    };

    fetchProjects();
  }, [selectedProjectType, selectedMarketType]);


  const filteredProjects = projects.filter((project) =>
    project.title.toLowerCase().includes(searchTerm.toLowerCase())
  );



  const totalPages = Math.ceil(filteredProjects.length / projectsPerPage);
  const startIndex = (currentPage - 1) * projectsPerPage;
  const currentProjects = filteredProjects.slice(
    startIndex,
    startIndex + projectsPerPage
  );


  const handleKeepProject = async (projectId: number) => {
    const username = getUsernameFromToken();
    if (!username) {
      console.error("No username found in token.");
      return;
    }

    try {
      const response = await keepProjectForSpoc(username, projectId);
      console.log("Project kept for SPOC:", response);
      // Optionally refresh list or show a message
    } catch (error) {
      console.error("Failed to keep project for SPOC:", error);
    }
  };

  const handleInterlocutorSpocAffectation = async (projectId: number) => {
    if (!projectId || !selectedSpocInterlocutorUsername) {
      alert("Veuillez sélectionner tous les champs requis.");
      return;
    }

    try {
      await Promise.all([
        affectProjectToInterlocutorRespondingImpact(selectedSpocInterlocutorUsername, Number(projectId)),
      ]);

      setSuccessMessage("Affectation réussie !");
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err) {
      console.error("Erreur lors de l'affectation :", err);
      alert("Une erreur est survenue lors de l'affectation.");
    }

  }



  const confirmAction = async () => {
    if (selectedProjectId !== null) {
      try {
        await handleKeepProject(selectedProjectId);
        await handleInterlocutorSpocAffectation(selectedProjectId);
      } catch (error) {
        console.error("Failed to assign project:", error);
      } finally {
        setConfirmOpen(false);
        setSelectedProjectId(null);

        setSuccessMessage("Affectation done successfully !")
      }
    }
  };




  // CSV Export handler
  const handleDownloadCSV = () => {
    if (!department) {
      alert("Department not set yet.");
      return;
    }
    exportSpocProjectsToCSV(department).catch(() =>
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
    <div className="pt-3.5 pb-24">




      {/* Search and Filter Bar */}
<div className="relative flex justify-between items-center px-4 w-full mb-4">
  <div className="w-2/5">
    <div className="p-4 flex items-center">
      <input
        type="text"
        placeholder="Search by project..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        className="bg-white w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-blue-500 text-sm dark:bg-white/[0.05] dark:text-white"
      />
      <Image
        src={"/images/icons/search.png"}
        alt="search-bar"
        width={40}
        height={40}
        className="w-4 h-4 relative right-8"
      />
    </div>
  </div>

  <div className="flex gap-4">
    {/* Project Type Dropdown */}
    <div className="relative">
      <button
        className="dropdown-toggle px-4 py-2 text-sm border rounded-lg dark:text-white"
        onClick={() => {
          setProjectTypeOpen(!projectTypeOpen);
          setMarketTypeOpen(false);
        }}
      >
        {selectedProjectType || "Project Type"}
      </button>
      <Dropdown isOpen={projectTypeOpen} onClose={() => setProjectTypeOpen(false)}>
        <ul className="p-2">
          {[
            "Comité TTM",
            "Comité Go-To-Market",
            "Comité Technique",
            "Comité Innovation",
            "Codir Marketing",
          ].map((type) => (
            <li
              key={type}
              onClick={() => {
                setSelectedProjectType(type);
                setProjectTypeOpen(false);
              }}
              className="py-1 px-2 hover:bg-gray-100 cursor-pointer"
            >
              {type}
            </li>
          ))}
          <li
            onClick={() => {
              setSelectedProjectType(null);
              setProjectTypeOpen(false);
            }}
            className="py-1 px-2 text-red-500 hover:bg-gray-100 cursor-pointer"
          >
            Réinitialiser
          </li>
        </ul>
      </Dropdown>
    </div>

    {/* Market Type Dropdown + Export CSV Button Container */}
    <div className="relative flex items-center gap-2">
      <button
        className="dropdown-toggle px-4 py-2 text-sm border rounded-lg dark:text-white"
        onClick={() => {
          setMarketTypeOpen(!marketTypeOpen);
          setProjectTypeOpen(false);
        }}
      >
        {selectedMarketType || "Market Type"}
      </button>
      <Dropdown isOpen={marketTypeOpen} onClose={() => setMarketTypeOpen(false)}>
        <ul className="p-2">
          {["Prepayé", "Postpayé", "Home"].map((type) => (
            <li
              key={type}
              onClick={() => {
                setSelectedMarketType(type);
                setMarketTypeOpen(false);
              }}
              className="py-1 px-2 hover:bg-gray-100 cursor-pointer"
            >
              {type}
            </li>
          ))}
          <li
            onClick={() => {
              setSelectedMarketType(null);
              setMarketTypeOpen(false);
            }}
            className="py-1 px-2 text-red-500 hover:bg-gray-100 cursor-pointer"
          >
            Réinitialiser
          </li>
        </ul>
      </Dropdown>

      {/* CSV Download Button */}
      <button
        onClick={handleDownloadCSV}
        className="px-4 py-2 text-sm text-white border border-gray-300 rounded-lg shadow-md hover:opacity-90 transition flex items-center gap-2"
        style={{ backgroundColor: "#B12B89" }}
      >
        <FileText size={18} />
        Export CSV
      </button>
    </div>
  </div>
</div>


      {/* Table */}
      <div className="overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.03]">
        <div className="w-full overflow-x-auto">
          <div className="min-w-[1050px] mx-auto">
            <Table>
              <TableHeader className="border-b border-gray-100 dark:border-white/[0.05]">
                <TableRow className="bg-gray-50 dark:bg-white/[0.03]">
                  {[
                    "Title",
                    "Owner Project",
                    "Market Type",
                    "Project Type",
                    "Confidentiality",
                    "Starting Date",
                    "Affectation",
                    "Details",
                  ].map((head, idx) => (
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
                {currentProjects.length === 0 ? (
                  <TableRow>
                    <TableCell
                      colSpan={8}
                      className="px-4 py-4 text-center text-gray-500 dark:text-gray-400"
                    >
                      No result was found !
                    </TableCell>
                  </TableRow>
                ) : (
                  currentProjects.map((project) => {
                    const statut = project.assignedToInterlocutors;
                    const formattedStatut =
                      statut === "ASSIGNED"
                        ? "Assigned"
                        : statut === "NOT_ASSIGNED"
                          ? "Not Assigned"
                          : statut === "RESERVED"
                            ? "Reserved"
                            : "";


                    return (
                      <TableRow
                        key={project.id}
                        className="hover:bg-gray-50 dark:hover:bg-white/[0.05]"
                      >
                        <TableCell className="px-4 py-4 text-start text-gray-700 text-sm dark:text-white/90">
                          {project.title}
                        </TableCell>
                        <TableCell className="px-4 py-4 text-start text-gray-500 text-sm dark:text-gray-400">
                          {project.ownerFullName}
                        </TableCell>
                        <TableCell className="px-4 py-4 text-start text-gray-500 text-sm dark:text-gray-400">
                          {project.marketType}
                        </TableCell>
                        <TableCell className="px-4 py-4 text-start text-gray-500 text-sm dark:text-gray-400">
                          {project.projectType}
                        </TableCell>
                        <TableCell className="px-4 py-4 text-start">
                          <Badge
                            size="sm"
                            variant="light"
                            className={`p-1 ${project.isConfidential
                                ? "bg-red-100 text-red-600"
                                : "bg-green-100 text-green-600"
                              } dark:bg-white/[0.05]`}
                          >
                            {project.isConfidential ? (
                              <Shield size={16} className="text-red-600" />
                            ) : (
                              <BookOpenCheck size={16} className="text-green-600" />
                            )}
                          </Badge>
                        </TableCell>


                        <TableCell className="px-4 py-4 text-start text-gray-500 text-sm dark:text-gray-400">
                          {new Date(project.dateStartTtm).toLocaleDateString()}
                        </TableCell>
                        <TableCell className="px-4 py-4 text-start">
                          <Badge
                            size="sm"
                            variant="light"
                            className={`text-sm ${formattedStatut === "Assigned"
                                ? "bg-blue-100 text-blue-600"
                                : formattedStatut === "Reserved"
                                  ? "bg-yellow-100 text-yellow-600"
                                  : formattedStatut === "Not Assigned"
                                    ? "bg-red-100 text-red-600"
                                    : "bg-gray-100 text-gray-600"
                              } dark:bg-white/[0.05]`}
                          >
                            {formattedStatut}
                          </Badge>
                        </TableCell>

                        <TableCell className="px-4 py-4 text-start flex gap-2 items-center">
                          <Link href={`/spoc/projects-affected/${project.id}`}>
                            <Eye className="w-5 h-5 text-gray-500 hover:text-blue-500 cursor-pointer" />
                          </Link>


                          {statut === "NOT_ASSIGNED" && !takenProjects.includes(project.id) && (
                            <Link href={`/spoc/affect-interlocutors/${project.id}`}>
                              <button
                                type="button"
                                className="p-1 rounded transition-all duration-200 hover:scale-110 hover:shadow-[0_0_10px_#B12B89] focus:outline-none"
                              >
                                <Rocket
                                  className="w-5 h-5 transition-colors duration-200"
                                  style={{ color: "#B12B89" }}
                                />
                              </button>
                            </Link>
                          )}

                          {/* Optional: Show a disabled Rocket if taken */}
                          {takenProjects.includes(project.id) && (
                            <button
                              type="button"
                              className="p-1 rounded opacity-50 cursor-not-allowed"
                              disabled
                            >
                              <Rocket className="w-5 h-5" style={{ color: "#B12B89" }} />
                            </button>
                          )}

                          {/* Add Lock icon */}

                          {statut === "NOT_ASSIGNED" && (
                            <button
                              type="button"
                              onClick={() => {
                                setTakenProjects((prev) => [...prev, project.id]);
                                setSelectedProjectId(project.id);
                                setConfirmOpen(true);
                              }}
                              className="p-1 rounded transition-all duration-200 hover:scale-110 hover:shadow-[0_0_10px_#B12B89] focus:outline-none"
                            >
                              <Lock
                                className="w-5 h-5 cursor-pointer"
                                style={{ color: "#9A2F8A" }}
                              />
                            </button>
                          )}






                        </TableCell>
                      </TableRow>
                    );
                  })
                )}
              </TableBody>
            </Table>
          </div>




          {confirmOpen && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
              <div className="bg-white dark:bg-gray-900 p-6 rounded-xl shadow-lg max-w-sm w-full relative">
                <label className="block mb-2 text-gray-800 dark:text-gray-100" htmlFor="interlocutor">
                  Interlocutor
                </label>

                <div className="relative mb-4">
                  <Select
                    options={interlocutorRespondingImpactOptions}
                    placeholder="Select an interlocutor"
                    onChange={(value) => setSelectedSpocInterlocutorUsername(value)}
                    className="text-sm"
                  />
                  <span className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 pointer-events-none">
                    <ChevronDownIcon className="w-4 h-4" />
                  </span>
                </div>

                <p className="mb-4 text-gray-800 dark:text-gray-100">
                  Do you want to handle impacts by yourself?
                </p>

                <div className="flex justify-end gap-4">
                  <button
                    onClick={() => {
                      setConfirmOpen(false);
                      setSelectedProjectId(null);
                    }}
                    className="px-4 py-2 bg-gray-300 text-gray-800 rounded hover:bg-gray-400 transition"
                  >
                    Cancel
                  </button>
                  <button
                    onClick={confirmAction}
                    className="px-4 py-2 bg-[#B12B89] text-white rounded hover:opacity-90 transition"
                  >
                    Confirm
                  </button>
                </div>
              </div>
            </div>

          )}

          {successMessage && (
            <div className="fixed bottom-6 left-1/2 transform -translate-x-1/2 bg-green-100 text-green-800 px-6 py-3 rounded-lg shadow-lg transition-opacity duration-300 z-50">
              {successMessage}
            </div>
          )}


        </div>
      </div>



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
