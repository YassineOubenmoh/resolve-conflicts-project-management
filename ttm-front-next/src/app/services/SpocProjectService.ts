export interface Project {
    id: number;
    ownerUsername: string;
    ownerFullName: string;
    title: string;
    description: string;
    marketType: string;
    projectType: string;
    ttmComitteeSubCategory: string;
    subcategoryCommercialCodir: string;
    isConfidential: boolean;
    dateStartTtm: string;
    expressionOfNeed: string;
    briefCommunication: string;
    briefCDG: string;
    regulatoryBrief: string;
    attachedDocuments: string[];
    comments: string[];
    dateCreation: string;
    moas: string[];
    trackingId: number;
    gateProjectIds: number[];
    currentGate: string;
    requiredActions: string | null;
    departments: string[];
    assignedToInterlocutors: string
  }
  

  export interface ProjectDto {
    id: number;
    ownerUsername: string;
    ownerFullName: string;
    title: string;
    description: string;
    marketType: string;
    projectType: string;
    ttmComitteeSubCategory: string;
    subcategoryCommercialCodir: string;
    isConfidential: boolean;
    dateStartTtm: string;
    expressionOfNeed: string;
    briefCommunication: string;
    briefCDG: string;
    regulatoryBrief: string;
    attachedDocuments: string[];
    comments: string[];
    dateCreation: string;
    moas: string[];
    trackingId: number;
    gateProjectIds: number[];
    currentGate: string;
    requiredActions: string | null;
    departments: string[];
  }

  export async function fetchProjects(): Promise<Project[]> {


    const token = localStorage.getItem("accessToken");


    const res = await fetch("http://localhost:8080/project/all", {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });

    if (!res.ok) {
      throw new Error("Failed to fetch projects");
    }
  
    return res.json();
  }


  export const fetchFilteredProjects = async (
    projectType?: string | null,
    marketType?: string | null
  ): Promise<Project[]> => {
    const token = localStorage.getItem("accessToken");
    const params = new URLSearchParams();
    if (projectType) params.append("projectType", projectType);
    if (marketType) params.append("marketType", marketType);
  
    const response = await fetch(`http://localhost:8080/project/filter?${params.toString()}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      throw new Error("Failed to fetch filtered projects");
    }
  
    return await response.json();
  };
  


  // Add this to SpocProjectService.ts
export async function fetchProjectById(id: string | string[] | undefined): Promise<Project | null> {
  if (!id) return null;
  
  const token = localStorage.getItem("accessToken");

  const res = await fetch(`http://localhost:8080/project/find/${id}`, {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });

  if (!res.ok) throw new Error("Failed to fetch project");

  return res.json();
}

 
  