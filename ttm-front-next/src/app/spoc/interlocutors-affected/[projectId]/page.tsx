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

export default function InterlocutorsAffectation() {
  const [interlocutorSignalingImpactOptions, setInterlocutorSignalingImpactOptions] = useState<OptionType[]>([]);
  const [interlocutorRespondingImpactOptions, setInterlocutorRespondingImpactOptions] = useState<OptionType[]>([]);
  const [department, setDepartment] = useState<string | null>(null);

  const [selectedSignalingUsername, setSelectedSignalingUsername] = useState<string | null>(null);
  const [selectedRespondingUsername, setSelectedRespondingUsername] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const [project, setProject] = useState<ProjectDto | null>(null);

  const { projectId } = useParams();

  // Fetch project
  useEffect(() => {
    const fetchProject = async () => {
      if (!projectId) return;
      try {
        const { data } = await getProjectByProjectId(Number(projectId));
        setProject(data);
        setSelectedSignalingUsername(data?.interlocutorSignalingImpactUsername || null);
        setSelectedRespondingUsername(data?.interlocutorRespondingImpactUsername || null);
      } catch (error) {
        console.error("Failed to fetch project:", error);
      }
    };

    fetchProject();
  }, [projectId]);

  // Fetch user department
  useEffect(() => {
    const fetchUserDepartment = async () => {
      const username = getUsernameFromToken();
      if (!username) return;

      try {
        const { data: user } = await getUserByUsername(username);
        setDepartment(user.department);
      } catch (error) {
        console.error("Failed to fetch user:", error);
      }
    };

    fetchUserDepartment();
  }, []);

  // Fetch interlocutors options
  useEffect(() => {
    const fetchInterlocutors = async () => {
      if (!department) return;

      try {
        const [signalingRes, respondingRes] = await Promise.all([
          getInterlocutorsSignalingImpactByDepartment(department),
          getInterlocutorsRespondingImpactByDepartment(department),
        ]);

        const signalingOptions = signalingRes.data.map((user: UserDto) => ({
          value: user.username,
          label: `${user.firstName} ${user.lastName}`,
        }));

        const respondingOptions = respondingRes.data.map((user: UserDto) => ({
          value: user.username,
          label: `${user.firstName} ${user.lastName}`,
        }));

        setInterlocutorSignalingImpactOptions(signalingOptions);
        setInterlocutorRespondingImpactOptions(respondingOptions);
      } catch (error) {
        console.error("Failed to fetch interlocutors:", error);
      }
    };

    fetchInterlocutors();
  }, [department]);

  // Fetch interlocutors from localStorage
  useEffect(() => {
    const savedSignalingUsername = localStorage.getItem("interlocutorSignalingImpact");
    const savedRespondingUsername = localStorage.getItem("interlocutorRespondingImpact");

    if (savedSignalingUsername) {
      setSelectedSignalingUsername(savedSignalingUsername);
    }
    if (savedRespondingUsername) {
      setSelectedRespondingUsername(savedRespondingUsername);
    }
  }, []);

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
    } catch (error) {
      console.error("Erreur lors de l'affectation :", error);
      alert("Une erreur est survenue lors de l'affectation.");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 p-4">
      <ComponentCard className="w-full max-w-lg bg-white p-8 rounded-xl shadow-xl flex flex-col space-y-6">
        
        {/* Title and Interlocutors Header */}
        <div className="text-left text-[#9A2F8A]">
          {project?.title && (
            <h1 className="text-xl font-bold mb-2">
              {project.title}
            </h1>

          )}

          {/* Display interlocutors from localStorage */}
          {selectedSignalingUsername && (
            <p className="text-sm">Interlocuteur Signalant Impact : {selectedSignalingUsername}</p>
          )}
          {selectedRespondingUsername && (
            <p className="text-sm">Interlocuteur Retours Impacts : {selectedRespondingUsername}</p>
          )}
        </div>

        {/* Signaling Interlocutor */}
        <div>
          <Label>Interlocuteur Signalant Impact</Label>
          <div className="relative">
            <Select
              options={interlocutorSignalingImpactOptions}
              placeholder="Sélectionnez un interlocuteur"
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
              placeholder="Sélectionnez un interlocuteur"
              onChange={(value) => setSelectedRespondingUsername(value)}
              className="dark:bg-dark-900"
            />
            <span className="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none text-gray-500">
              <ChevronDownIcon />
            </span>
          </div>
        </div>

        {/* Submit Button */}
        <div className="flex justify-center">
          <button
            onClick={handleSubmit}
            className="text-white px-6 py-3 rounded-lg hover:bg-[#B12B89] transition-all ease-in-out duration-200"
            style={{ backgroundColor: '#B12B89' }}
          >
            Confirmer
          </button>
        </div>

        {/* Success Message */}
        {successMessage && (
          <div className="mt-4 p-4 bg-green-100 text-green-800 rounded-lg shadow-lg transition-opacity duration-300">
            {successMessage}
          </div>
        )}
      </ComponentCard>
    </div>
  );
}
