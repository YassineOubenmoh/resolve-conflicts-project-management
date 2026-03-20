/* eslint-disable @typescript-eslint/no-explicit-any */
import axios from "axios";
import { setupAuthInterceptors } from "./authInterceptor";

export const projectsApi = axios.create({
  baseURL: "http://localhost:8080",
});


setupAuthInterceptors(projectsApi);

// API Methods

// Project Apis

export const getProjects = async () => {
  const { data } = await projectsApi.get("/project/all");
  return Array.isArray(data) ? data : []; // Ensure the response is an array
};


export const getOwnerProjects = async (ownerName: string) => {
  if (!ownerName || ownerName === "undefined") {
    throw new Error("Invalid owner name");
  }
  const { data } = await projectsApi.get(`/project/ownerprojects/${ownerName}`);
  return Array.isArray(data) ? data : [];
};


export const addProject = (project: unknown) => {
  return projectsApi.post(`/add`, project);
};

export const getProjectById = (id: string) => {
  return projectsApi.get(`/project/find/${id}`);
};



export const createProject = async (projectData: Record<string, unknown>, formData: FormData) => {
  try {
    // Ensure we're using a valid FormData object
    const finalFormData = formData instanceof FormData ? formData : new FormData();

    // Make sure projectData is included in formData as 'projectDto'
    if (!finalFormData.has('projectDto')) {
      finalFormData.append('projectDto', JSON.stringify(projectData));
    }

    // Configure headers properly for multipart/form-data
    const config = {
      headers: {
        // Let the browser set the Content-Type with boundary
        // The browser will automatically set the correct Content-Type with boundary
      }
    };

    console.log("Preparing FormData to send to API");

    // Debug logging - check FormData contents
    for (const [key, value] of finalFormData.entries()) {
      if (value instanceof File) {
        console.log(`FormData entry - ${key}: File: ${value.name} (${value.size} bytes, type: ${value.type})`);
      } else {
        try {
          console.log(`FormData entry - ${key}: ${typeof value === 'string' ? value.substring(0, 100) + '...' : value}`);
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
        } catch (e) {
          console.log(`FormData entry - ${key}: [Complex value]`);
        }
      }
    }

    // Ensure the endpoint is correct
    const endpoint = "project/add";

    console.log(`Calling API endpoint: ${endpoint}`);

    // Make the API call
    const response = await projectsApi.post(endpoint, finalFormData, config);
    console.log("API response received:", response);
    return response;
  } catch (error) {
    console.error("Error in createProject API call:", error);

    // Enhanced error logging
    if (error && typeof error === 'object' && 'response' in error) {
      const axiosError = error as {
        response?: {
          status?: number,
          data?: object,
          headers?: { [key: string]: string }
        },
        request?: object,
        message?: string
      };

      console.error("API Response Status:", axiosError.response?.status);
      console.error("API Response Data:", axiosError.response?.data);
      console.error("API Response Headers:", axiosError.response?.headers);
    }

    throw error;
  }
};








// Departments Apis

export const getDepartments = async () => {
  const { data } = await projectsApi.get("/departement/all");
  return Array.isArray(data) ? data : [];
};








// Tracking & Gates Apis

export const getTrackingById = (id: string) => {
  return projectsApi.get(`/tracking/find/${id}`);
};


export const getProjectsNotAffected = (department: string) => {
  return projectsApi.get(`/project/not-affected-projects`, {
    params: { department },
  });
};

export const getInterlocutorsSignalingImpactByDepartment = (department: string) => {
  return projectsApi.get(`/project/interlocutors-impact`, {
    params: { department },
  });
};

export const getInterlocutorsRespondingImpactByDepartment = (department: string) => {
  return projectsApi.get(`/project/interlocutors-response`, {
    params: { department },
  });
};


export const getNextGateByProjectProjectId = (projectId: number) => {
  return projectsApi.get(`/project/nextgate/${projectId}`, {
    validateStatus: () => true // Accept all status codes and don't throw e
  });
};


export const getCurrentGateIdByProjectId = (projectId: number) => {
  return projectsApi.get(`/gate-project/current-gate/${projectId}`, {
    validateStatus: () => true // Accept all status codes, don't throw
  });
};

export const UpdateCurrentGateProject = (gateId: number, data: any) => {
  return projectsApi.put(`/gate-project/update/${gateId}`, data);
};


export const moveToNextGate = (projectId: number) => {
  return projectsApi.put(`/project/next-gate/${projectId}`);
};


export const getGatesResponseByProjectId = (projectId: number) => {
  return projectsApi.get(`/gate-project/project-gates-responses/${projectId}`);
};


export const affectGatesToDepartment = async (departmentId: number, gateIds: number[]) => {
  const response = await projectsApi.post(`/departement-gateproject/affect-set-gates/${departmentId}`, gateIds);
  return response.data;
};





// Affectations Apis 
export const affectProjectToInterlocutorSignalingImpact = (
  projectId: number,
  username: string
) => {
  return projectsApi.post(`/project/affect-signal/${projectId}/${username}`);
};

export const affectProjectToInterlocutorRespondingImpact = (
  username: string,
  projectId: number
) => {
  return projectsApi.post(`/project/affect-respond/${username}/${projectId}`);
};



export const getProjectsForSpoc = (department: string) => {
  return projectsApi.get(`/project/spoc-projects/${department}`);
};



export const filterProjectsForSpoc = (
  department: string,
  projectType?: string,
  marketType?: string
) => {
  // Create an object to hold query parameters, only add them if they are provided
  const params: Record<string, string> = { department };

  if (projectType) {
    params.projectType = projectType;
  }

  if (marketType) {
    params.marketType = marketType;
  }

  // Make the API request with the query parameters
  return projectsApi.get("/project/filter-spoc", { params });
};



//deja éxisté
export const getProjectByProjectId = (id: number) => {
  return projectsApi.get(`project/find/${id}`);
};



export const exportSpocProjectsToCSV = async (department: string) => {
  try {
    const response = await projectsApi.get(`project/spoc-projects/export/${department}`, {
      responseType: 'blob', // important for file download
    });

    // Create a blob from the response data
    const blob = new Blob([response.data], { type: 'text/csv' });

    // Create a link element to trigger download
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = downloadUrl;

    // Set the file name from backend or default
    link.download = 'spoc-projects.csv';

    // Append link, click and remove
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    // Release the object URL
    window.URL.revokeObjectURL(downloadUrl);

    return true; // or return response if you want
  } catch (error) {
    console.error('Failed to download CSV:', error);
    throw error;
  }
};
