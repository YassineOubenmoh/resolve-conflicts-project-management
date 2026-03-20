'use client';
import '@/app/project-recap/ProjectRecap.css'; // Import the CSS file for styling
import { useSearchParams } from "next/navigation";
import { useEffect, useRef, useState } from "react";
import Documents from "./(recap-components)/Documents";
import RequiredActions from "./(recap-components)/RequiredActions";
import ViewGlobal from "./(recap-components)/ViewGlobal";
import "./ProjectRecap.css"; // Import the CSS file for styling

import { getProjectById } from "@/axios/ProjectApis";

import PassageGate from "../(owner)/project-list/(projectList-details)/PassageGate";


import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';


interface Project {
  id: number;
  owner: string;
  title: string;
  description: string;
  launchDate: Date;
  passageDate: Date;
  departments: string[];
  ownerMoa: string[];
  tracking: string;
  gate: string;
}


const ProjectRecap = () => {
  const [activeLink, setActiveLink] = useState("/summary");
  const [selectedSection, setSelectedSection] = useState('Vue Globale');
  const inputRef = useRef<HTMLInputElement>(null);


  const searchParams = useSearchParams();
  const projectId = searchParams.get("id"); // Retrieve the 'id' from query parameters
  const [project, setProject] = useState<Project | null>(null);
  const [open, setOpen] = useState(true);
  const handleClose = () => {
    setOpen(false);
  };



  useEffect(() => {
    const fetchProject = async () => {
      if (!projectId) {
        console.error("Project ID is missing");
        return;
      }

      try {
        const response = await getProjectById(projectId);
        const projectData = response.data;

        const mappedProject: Project = {
          id: projectData.id,
          owner: projectData.ownerFullName,
          title: projectData.title,
          description: projectData.description,
          launchDate: new Date(projectData.dateStartTtm),
          passageDate: new Date(), // Placeholder, adjust if backend adds it
          departments: projectData.departments || [],
          ownerMoa: projectData.moas || [],
          tracking: projectData.trackingId?.toString() || "",
          gate: projectData.currentGate || "",
        };

        setProject(mappedProject);
      } catch (error) {
        console.error("Error fetching project:", error);
      }
    };

    fetchProject();
  }, [projectId]);


  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if ((event.metaKey || event.ctrlKey) && event.key === "k") {
        event.preventDefault();
        inputRef.current?.focus();
      }
    };

    document.addEventListener("keydown", handleKeyDown);

    return () => {
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, []);



  if (!project) {
    return <div>
      <Backdrop
        sx={(theme) => ({ color: '#ab3c73', backgroundColor: '#FFF', zIndex: theme.zIndex.drawer + 1 })}
        open={open}
        onClick={handleClose}
      >
        <CircularProgress color="inherit" />
      </Backdrop>
    </div>;
  }


  return (
    <div className="campaign-details"
      style={{
        paddingLeft: "6rem",
        paddingRight: "6rem",
        paddingTop: "2rem",

      }}>

      <div className="flex gap-2.5 text-nowrap" >

        <div>
          <h2 className='pb-2.5 text-[18px] font-bold '> Sujet : #{project.id} </h2>
        </div>

        <div>

          <p className='relative top-0.5 text-sm '>  Créé le {project.launchDate.toLocaleDateString()} </p>

        </div>

      </div>



      <div className="h-14 top-0 flex w-full bg-white rounded-lg border-gray-200 dark:border-gray-800 dark:bg-gray-900 lg:border">
        <div className="text-nowrap flex items-center justify-between w-full lg:px-6" >

          {/* Centered Navigation Links */}
          <nav className="flex-1 flex justify-center items-center gap-6" >
            <ol
              style={{ position: "relative", paddingRight: "0.5rem", cursor: "pointer" }}
              className={`nav-link ${activeLink === "/summary" ? "active" : ""}`}
              onClick={() => {
                setActiveLink("/summary")
                setSelectedSection('Vue Globale')
              }}
            >
              Summary
            </ol>

            <ol
              style={{ position: "relative", cursor: "pointer" }}

              className={`nav-link Attachments ${activeLink === "Attachments" ? "active" : ""}`}
              onClick={() => {
                setActiveLink("Attachments")
                setSelectedSection('Pièces jointes et retour')
              }}
            >
              Attachments and Return
            </ol>

            <ol
              style={{ position: "relative", paddingRight: "0.5rem", cursor: "pointer" }}
              className={`nav-link ${activeLink === "Passage validation" ? "active" : ""}`}
              onClick={() => {
                setActiveLink("Passage validation")
                setSelectedSection('Validation de passage')
              }}
            >
              Passage validation
            </ol>

            <ol
              style={{ position: "relative", paddingRight: "0.5rem", cursor: "pointer" }}
              className={`nav-link ${activeLink === "/singin" ? "active" : ""}`}
              onClick={() => {
                setActiveLink("/singin")
                setSelectedSection('Actions Requises')
              }}
            >
              Required Actions
            </ol>
          </nav>
        </div>
      </div>


      <div>
        <div className="mt-5 text-nowrap" >
          {selectedSection === 'Vue Globale' && <ViewGlobal projectDetails={project} />}
          {selectedSection === 'Actions Requises' && <RequiredActions />}
          {selectedSection === 'Validation de passage' && <PassageGate projectDetails={project} />}
          {selectedSection === 'Pièces jointes et retour' && <Documents />}
        </div>

      </div>

    </div>
  );
};

export default ProjectRecap;