/* eslint-disable @typescript-eslint/no-explicit-any */
import { Project } from "@/app/services/SpocProjectService";
import axios from 'axios';
import { setupAuthInterceptors } from "./authInterceptor";




export const usersApi = axios.create({
    baseURL: 'http://localhost:8087/api/internal',
});


// Access & refresh token configuration
setupAuthInterceptors(usersApi);

// API Calls

export const getUsers = async () => {
    const { data } = await usersApi.get("/users");
    return Array.isArray(data) ? data : []; // Ensure the response is an array
};

export const getUserByUsername = (username: string) => {
    return usersApi.get(`/users/by-username/${username}`);
};

export const login = (username: string, password: string) => {
    return usersApi.post("/auth/login", {
        username,
        password,
    });
};

export const assignRole = (payload: unknown) => {
    return usersApi.post("/users/roles/assign", payload);
};

export const addProject = (project: Project) => {
    return usersApi.post(`/add`, project);
};

export const forgotPassword = (email: string) => {
    return usersApi.post(`/auth/password/forgot`, null, {
        params: { email },
    });
};

export const resetPassword = (payload: { oldPassword: string; newPassword: string }) => {
    return usersApi.post("/auth/password/reset", payload);
};

export const getInterlocutorsByDepartment = (department: string) => {
    return usersApi.get(`/interlocutors-dep`, {
        params: { department }
    });
};

export const getInterlocutorsData = (department: string) => {
    return usersApi.get(`/interlocutors-affectations`, {
        params: { department }
    });
};

export const removeAffectationByProjectId = (projectId: number) => {
    return usersApi.put(`/remove-affectation/${projectId}`);
};

export const keepProjectForSpoc = (username: string, projectId: number) => {
    return usersApi.put(`/spoc-keep`, null, {
        params: {
            username,
            projectId
        }
    });
};



export const exportInterlocutorsToCSV = async (department: string) => {
  try {
    const response = await usersApi.get(`/interlocutors-affectations/export/${department}`, {
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