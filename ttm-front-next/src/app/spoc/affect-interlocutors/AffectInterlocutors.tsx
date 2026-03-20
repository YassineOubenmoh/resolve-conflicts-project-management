'use client';

import { useEffect, useState } from "react";
import ComponentCard from "@/components/common/ComponentCard";
import Label from "@/components/form/Label";
import Select from "@/components/form/Select";
import { ChevronDownIcon } from "lucide-react";

import {
  getInterlocutorsSignalingImpactByDepartment,
  getInterlocutorsRespondingImpactByDepartment,
  affectProjectToInterlocutorSignalingImpact,
  affectProjectToInterlocutorRespondingImpact,
  getProjectByProjectId,
} from "@/axios/ProjectApis";

import { getUserByUsername } from "@/axios/UsersApis";
import { getUsernameFromToken, UserDto } from "@/app/services/SpocAffectationProjectService";
import { ProjectDto } from "@/app/services/SpocProjectService";
import { useParams } from 'next/navigation';

type OptionType = {
  value: string;
  label: string;
};

export default function AffectInterlocutors() {
  const [interlocutorSignalingImpactOptions, setInterlocutorSignalingImpactOptions] = useState<OptionType[]>([]);
  const [interlocutorRespondingImpactOptions, setInterlocutorRespondingImpactOptions] = useState<OptionType[]>([]);
  const [department, setDepartment] = useState<string | null>(null);

  const [selectedSignalingUsername, setSelectedSignalingUsername] = useState<string | null>(null);
  const [selectedRespondingUsername, setSelectedRespondingUsername] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const [project, setProject] = useState<ProjectDto | null>(null);

  const { projectId } = useParams();

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

  // Fetch dropdown options based on department
  useEffect(() => {
    if (!department) return;

    const fetchDropdownData = async () => {
      try {
        const [signalingRes, respondingRes] = await Promise.all([
          getInterlocutorsSignalingImpactByDepartment(department),
          getInterlocutorsRespondingImpactByDepartment(department),
        ]);

        const signaling = signalingRes.data as UserDto[];
        const responding = respondingRes.data as UserDto[];

        setInterlocutorSignalingImpactOptions(
          signaling.map((u) => ({
            value: u.username,
            label: `${u.firstName} ${u.lastName}`,
          }))
        );

        setInterlocutorRespondingImpactOptions(
          responding.map((u) => ({
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

  // Fetch project details
  useEffect(() => {
    const fetchProject = async () => {
      if (!projectId) return;
      try {
        const { data } = await getProjectByProjectId(Number(projectId));
        setProject(data);
      } catch (err) {
        console.error("Error fetching project:", err);
      }
    };

    fetchProject();
  }, [projectId]);

  const handleSubmit = async () => {
    if (!projectId || !selectedSignalingUsername || !selectedRespondingUsername) {
      alert("Veuillez sélectionner tous les champs requis.");
      return;
    }

    try {
      await Promise.all([
        affectProjectToInterlocutorSignalingImpact(Number(projectId), selectedSignalingUsername),
        affectProjectToInterlocutorRespondingImpact(selectedRespondingUsername, Number(projectId)),
      ]);

      setSuccessMessage("Affectation réussie !");
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err) {
      console.error("Erreur lors de l'affectation :", err);
      alert("Une erreur est survenue lors de l'affectation.");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 p-4">
      <ComponentCard className="w-full max-w-lg bg-white p-8 rounded-xl shadow-xl flex flex-col space-y-6">
        {/* Signaling Interlocutor */}
        <div>
          <Label>Interlocuteur Signalant Impact</Label>
          <div className="relative">
            <Select
              options={interlocutorSignalingImpactOptions}
              placeholder="Select an interlocutor"
              onChange={(value) => setSelectedSignalingUsername(value)}
              className="dark:bg-dark-900"
            />
            <span className="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none text-gray-500">
              <ChevronDownIcon />
            </span>
          </div>
        </div>

        {/* Responding Interlocutor */}
        <div>
          <Label>Interlocuteur Retours Impacts</Label>
          <div className="relative">
            <Select
              options={interlocutorRespondingImpactOptions}
              placeholder="Select an interlocutor"
              onChange={(value) => setSelectedRespondingUsername(value)}
              className="dark:bg-dark-900"
            />
            <span className="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none text-gray-500">
              <ChevronDownIcon />
            </span>
          </div>
        </div>

        {/* Project Title Display */}
        {project?.title && (
          <h1 className="text-xl font-bold mt-4 text-center">
            Projet sélectionné : {project.title}
          </h1>
        )}

        {/* Submit Button */}
        <div className="flex justify-center">
          <button
            onClick={handleSubmit}
            className="text-white px-6 py-3 rounded-lg hover:bg-[#B12B89] transition-all ease-in-out duration-200"
            style={{ backgroundColor: '#B12B89' }}
          >
            Valider
          </button>
        </div>

        {/* Success Message */}
        {successMessage && (
          <div
            className="mt-4 p-4 bg-green-100 text-green-800 rounded-lg shadow-lg transform transition-opacity duration-300"
            style={{
              opacity: successMessage ? 1 : 0,
              transition: 'opacity 0.5s ease-in-out',
            }}
          >
            {successMessage}
          </div>
        )}
      </ComponentCard>
    </div>
  );
}
