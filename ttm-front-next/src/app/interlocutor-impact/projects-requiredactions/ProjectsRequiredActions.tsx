'use client';

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { getUsernameFromToken } from '@/app/services/SpocAffectationProjectService';
import { getUserByUsername } from '@/axios/UsersApis';
import { getGateProjectsByDepartment } from '@/axios/InterlocutorApis';
import { GateProjectFront } from '@/types/interlocutor';
import { ChevronDown, CheckCircle } from 'lucide-react';
import Badge from '@/components/ui/badge/Badge';

const getUserDepartment = async (): Promise<string | null> => {
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

export default function ProjectsRequiredActions() {
  const router = useRouter();
  const [projects, setProjects] = useState<GateProjectFront[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [expanded, setExpanded] = useState<Record<string, boolean>>({});

  useEffect(() => {
    const fetchProjects = async () => {
      setLoading(true);
      try {
        const department = await getUserDepartment();
        if (!department) return;

        const data = await getGateProjectsByDepartment(department);
        setProjects(data);
      } catch (error) {
        console.error('Error fetching projects:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchProjects();
  }, []);

  const toggleExpand = (key: string) => {
    setExpanded((prev) => ({ ...prev, [key]: !prev[key] }));
  };


  const filteredProjects = projects.filter((project) =>
    project.titleProject.toLowerCase().includes(searchTerm.toLowerCase())
  );


  const handleNewImpactClick = (projectId: number, gate: string) => {
    router.push(`/interlocutor-impact/add-impact/${projectId}/${gate}`);
  };



  return (
    <div className="space-y-6 px-4 max-w-5xl mx-auto">
      <input
        type="text"
        placeholder="Search by project title..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-900 dark:text-white dark:border-white/[0.1]"
      />

      {loading ? (
        <div className="text-center text-gray-500 dark:text-gray-400">Loading...</div>
      ) : filteredProjects.length === 0 ? (
        <div className="text-center text-gray-500 dark:text-gray-400">Aucun résultat trouvé.</div>
      ) : (
        <div className="space-y-4">
          {filteredProjects.map((project, index) => {
            const key = `${project.projectId}-${project.gate}-${index}`;
            const isOpen = expanded[key];

            return (
              <div
                key={key}
                className="relative rounded-xl border border-gray-200 bg-white shadow-sm dark:border-white/[0.1] dark:bg-white/[0.02]"
              >
                {project.currentGate && (
                  <div className="absolute top-3 right-3 flex items-center gap-1 px-3 py-1 bg-green-100 text-green-700 rounded-full text-sm font-medium">
                    <CheckCircle className="w-4 h-4" />
                    In Progress
                  </div>
                )}

                <button
                  onClick={() => toggleExpand(key)}
                  className="w-full flex items-center justify-between px-6 pt-8 pb-4 text-left font-semibold text-lg hover:bg-gray-50 dark:hover:bg-white/[0.04] transition"
                  aria-expanded={isOpen}
                  aria-controls={`project-actions-${key}`}
                >
                  <div>
                    <div style={{ color: '#B12B89' }}>{project.titleProject}</div>
                    <div className="text-sm text-gray-500 dark:text-gray-400">Gate: {project.gate}</div>
                  </div>
                  <ChevronDown
                    className={`h-5 w-5 transition-transform ${isOpen ? 'rotate-180' : ''}`}
                  />
                </button>

                {isOpen && (
                  <div id={`project-actions-${key}`} className="px-6 pb-4 pt-2">
                    {project.requiredActions.length === 0 ? (
                      <div className="text-sm text-gray-500">Aucune action requise pour ce projet.</div>
                    ) : (
                      <div className="flex flex-wrap gap-2 mb-4">
                        {project.requiredActions.map((action, idx) => (
                          <Badge
                            key={idx}
                            size="sm"
                            variant="light"
                            className="text-sm"
                            style={{
                              backgroundColor: '#F8CEEB',
                              color: '#B12B89',
                            }}
                          >
                            {action}
                          </Badge>
                        ))}
                      </div>
                    )}
                    <button
                      onClick={() => handleNewImpactClick(project.projectId, project.gate)}
                      className="px-4 py-2 bg-[#B12B89] text-white rounded-md hover:bg-[#9c246f] transition"
                    >
                      New Impact
                    </button>

                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
